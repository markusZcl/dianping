package com.markus.dianping.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.Common.EmBusinessError;
import com.markus.dianping.dal.CategoryModelMapper;
import com.markus.dianping.dal.SellerModelMapper;
import com.markus.dianping.dal.ShopModelMapper;
import com.markus.dianping.model.CategoryModel;
import com.markus.dianping.model.SellerModel;
import com.markus.dianping.model.ShopModel;
import com.markus.dianping.recommend.RecommendService;
import com.markus.dianping.recommend.RecommendSortService;
import com.markus.dianping.recommend.ShopSortModel;
import com.markus.dianping.service.ShopService;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/10 11:37
 */
@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopModelMapper shopModelMapper;
    @Autowired
    private SellerModelMapper sellerModelMapper;
    @Autowired
    private CategoryModelMapper categoryModelMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RecommendService recommendService;
    @Autowired
    private RecommendSortService recommendSortService;
    @Override
    public ShopModel create(ShopModel shopModel) throws BusinessException {
        shopModel.setCreatedAt(new Date());
        shopModel.setUpdatedAt(new Date());
        SellerModel sellerModel = sellerModelMapper.selectByPrimaryKey(shopModel.getSellerId());
        if(sellerModel == null){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR,"商户不存在");
        }
        if(sellerModel.getDisabledFlag() == 1){//1表示禁用
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR,"商户已禁用");
        }
        CategoryModel categoryModel = categoryModelMapper.selectByPrimaryKey(shopModel.getCategoryId());
        if(categoryModel == null){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR,"类目不存在");
        }
        shopModelMapper.insertSelective(shopModel);
        return get(shopModel.getId());
    }

    @Override
    public ShopModel get(Integer id) {
        ShopModel shopModel = shopModelMapper.selectByPrimaryKey(id);
        SellerModel sellerModel = sellerModelMapper.selectByPrimaryKey(shopModel.getSellerId());
        CategoryModel categoryModel = categoryModelMapper.selectByPrimaryKey(shopModel.getCategoryId());
        shopModel.setSellerModel(sellerModel);
        shopModel.setCategoryModel(categoryModel);
        return shopModel;
    }

    @Override
    public List<ShopModel> selectAll() {
        List<ShopModel> shopModelList = shopModelMapper.selectAll();
        shopModelList.forEach(shopModel -> {
            SellerModel sellerModel = sellerModelMapper.selectByPrimaryKey(shopModel.getSellerId());
            CategoryModel categoryModel = categoryModelMapper.selectByPrimaryKey(shopModel.getCategoryId());
            shopModel.setSellerModel(sellerModel);
            shopModel.setCategoryModel(categoryModel);
        });
        return shopModelList;
    }

    @Override
    public Integer shopCountAll() {
        return shopModelMapper.shopCountAll();
    }

    @Override
    public List<ShopModel> recommend(BigDecimal longitude, BigDecimal latitude) {
        List<Integer> shopIdList = recommendService.recall(148);
        shopIdList = recommendSortService.sort(shopIdList,148);
        List<ShopModel> shopModelList = shopIdList.stream().map(id->{
                ShopModel shopModel =  get(id);
                shopModel.setIconUrl("/static/image/firstpage/bar_o.png");
                shopModel.setDistance(100);
                return shopModel;
        }).collect(Collectors.toList());
//        return shopModelMapper.recommend(longitude,latitude);
        return shopModelList;
    }

    @Override
    public List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags) {
        return shopModelMapper.searchGroupByTags(keyword,categoryId,tags);
    }

    @Override
    public List<ShopModel> search(BigDecimal longitude, BigDecimal latitude, String keyword,Integer orderBy,Integer categoryId,String tags) {
        List<ShopModel> shopModelList = shopModelMapper.search(longitude,latitude,keyword,orderBy,categoryId,tags);
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerModelMapper.selectByPrimaryKey(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryModelMapper.selectByPrimaryKey(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public Map<String,Object> searchES(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderBy, Integer categoryId, String tags) throws IOException {
//        SearchRequest searchRequest = new SearchRequest("shop");//传入我没要搜索的索引名
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.matchQuery("name",keyword));
//        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        searchRequest.source(searchSourceBuilder);
//        List<Integer> shopIdList = new ArrayList<>();
//        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);//默认就是get请求
//        SearchHit[] hits = searchResponse.getHits().getHits();
//        for (SearchHit hit : hits) {
//            shopIdList.add(new Integer(hit.getSourceAsMap().get("id").toString()));
//        }
        Request request = new Request("GET","/shop/_search");//基于elasticsearch-rest-client的一个request
//        String reqJson = "{\n" +
//                "  \"_source\": \"*\",\n" +
//                "  \"script_fields\": {\n" +
//                "    \"distance\": {\n" +
//                "      \"script\": {\n" +
//                "         \"source\": \"haversin(lat,lon,doc['location'].lat,doc['location'].lon)\",\n" +
//                "        \"lang\": \"expression\",\n" +
//                "        \"params\": {\"lat\":"+latitude+",\"lon\":"+longitude+"}\n" +
//                "      }\n" +
//                "    }\n" +
//                "  },\n" +
//                "  \"query\": {\n" +
//                "    \"function_score\": {\n" +
//                "      \"query\": {\n" +
//                "        \"bool\": {\n" +
//                "          \"must\": [\n" +
//                "            {\"match\": {\"name\": {\"query\":\""+keyword+"\",\"boost\": 0.1}}},\n" +
//                "            {\"term\": {\"seller_disabled_flag\": 0}}\n" +
//                "          ]\n" +
//                "        }\n" +
//                "      },\n" +
//                "      \"functions\": [\n" +
//                "        {\n" +
//                "          \"gauss\": {\n" +
//                "            \"location\": {\n" +
//                "              \"origin\": \""+latitude+","+longitude+"\",\n" +
//                "              \"scale\": \"100km\",\n" +
//                "              \"offset\": \"0km\",\n" +
//                "              \"decay\": 0.5\n" +
//                "            }\n" +
//                "          },\n" +
//                "          \"weight\": 9\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"field_value_factor\": {\n" +
//                "            \"field\": \"seller_remark_score\"\n" +
//                "          },\n" +
//                "          \"weight\": 0.2\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"field_value_factor\": {\n" +
//                "            \"field\": \"remark_score\"\n" +
//                "          },\n" +
//                "          \"weight\": 0.1\n" +
//                "        }\n" +
//                "      ],\n" +
//                "      \"score_mode\": \"sum\",\n" +
//                "      \"boost_mode\": \"sum\"\n" +
//                "    }\n" +
//                "  },\n" +
//                "  \"sort\": [\n" +
//                "    {\n" +
//                "      \"_score\": {\n" +
//                "        \"order\": \"desc\"\n" +
//                "      }\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
        //上述String形式可扩展性太低了，接下来我们采用面向对象的编程思想
        JSONObject jsonObjectReq = new JSONObject();
        //构建source部分
        jsonObjectReq.put("_source","*");
        //构建自定义距离字段
        jsonObjectReq.put("script_fields",new JSONObject());
        jsonObjectReq.getJSONObject("script_fields").put("distance",new JSONObject());
        jsonObjectReq.getJSONObject("script_fields").getJSONObject("distance").put("script",new JSONObject());
        jsonObjectReq.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").put("source","haversin(lat,lon,doc['location'].lat,doc['location'].lon)");
        jsonObjectReq.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").put("lang","expression");
        jsonObjectReq.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").put("params",new JSONObject());
        jsonObjectReq.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").getJSONObject("params").put("lat",latitude);
        jsonObjectReq.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script").getJSONObject("params").put("lon",longitude);


        //构建query
        Map<String, Object> cixingMap = analyzeCategoryKeyword(keyword);
        boolean isAffectFilter = false;
        boolean isAffectOrder = true;

        jsonObjectReq.put("query",new JSONObject());
        //构建function_score
        jsonObjectReq.getJSONObject("query").put("function_score",new JSONObject());
        jsonObjectReq.getJSONObject("query").getJSONObject("function_score").put("query",new JSONObject());
        jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").put("bool",new JSONObject());
        jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").put("must",new JSONArray());
        jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").add(new JSONObject());
        //构建match query
        int queryIndex = 0;
        if(cixingMap.keySet().size()>0 && isAffectFilter){
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("bool",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").put("should", new JSONArray());
            int filterQueryIndex = 0;
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").add(new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .put("match",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .getJSONObject("match").put("name",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .getJSONObject("match").getJSONObject("name").put("query",keyword);
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .getJSONObject("match").getJSONObject("name").put("boost",0.1);

            for(String key : cixingMap.keySet()) {
                filterQueryIndex++;
                Integer cixingCategoryId = (Integer) cixingMap.get(key);
                jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").add(new JSONObject());
                jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .put("term", new JSONObject());
                jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .getJSONObject("term").put("category_id", new JSONObject());
                jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .getJSONObject("term").getJSONObject("category_id").put("value", cixingCategoryId);
                jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .getJSONObject("term").getJSONObject("category_id").put("boost", 0);
            }
        }else{
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("match",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").put("name",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("query",keyword);
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("boost",0.1);
        }
        queryIndex++;
        //构建第二个query的条件
        jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").add(new JSONObject());
        jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
        jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("seller_disabled_flag",0);

        if(tags!=null){
            queryIndex++;
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").add(new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("tags",tags);
        }

        if(categoryId!=null){
            queryIndex++;
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").add(new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("category_id",categoryId);
        }
        //构建functions
        jsonObjectReq.getJSONObject("query").getJSONObject("function_score").put("functions",new JSONArray());
        int functionIndex = 0;
        if(orderBy==null){
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("gauss",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss").put("location",new JSONObject());
            System.out.println(latitude+","+longitude);
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").put("origin",latitude+","+longitude);
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").put("scale","100km");
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").put("offset","0km");
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").put("decay",0.5);
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",9);
            functionIndex++;
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field","remark_score");
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",0.2);
            functionIndex++;
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field","seller_remark_score");
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",3);

            if(cixingMap.keySet().size()>0 && isAffectOrder){
                for (String key : cixingMap.keySet()) {
                    functionIndex++;
                    jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
                    jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("filter",new JSONObject());
                    jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("filter").put("term",new JSONObject());
                    jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("filter").getJSONObject("term").put("category_id",cixingMap.get(key));
                    jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",0.2);
                }
            }

            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").put("score_mode","sum");
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").put("boost_mode","sum");
        }else{
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor",new JSONObject());
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field","price_per_man");
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",1);
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").put("score_mode","sum");
            jsonObjectReq.getJSONObject("query").getJSONObject("function_score").put("boost_mode","replace");
        }

        //构建一个排序字段
        jsonObjectReq.put("sort",new JSONObject());
        jsonObjectReq.getJSONObject("sort").put("_score",new JSONObject());
        if(orderBy==null){
            jsonObjectReq.getJSONObject("sort").getJSONObject("_score").put("order","desc");
        }else{
            jsonObjectReq.getJSONObject("sort").getJSONObject("_score").put("order","asc");
        }

        //聚合字段
        jsonObjectReq.put("aggs",new JSONObject());
        jsonObjectReq.getJSONObject("aggs").put("group_by_tags",new JSONObject());
        jsonObjectReq.getJSONObject("aggs").getJSONObject("group_by_tags").put("terms",new JSONObject());
        jsonObjectReq.getJSONObject("aggs").getJSONObject("group_by_tags").getJSONObject("terms").put("field","tags");

        String reqJson = jsonObjectReq.toJSONString();
        System.out.println(reqJson);
        request.setJsonEntity(reqJson);
        Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());
        System.out.println(responseStr);
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
        List<ShopModel> shopModelList = new ArrayList<>();
        for(int i = 0;i<jsonArray.size();i++){
            JSONObject jsonObj = jsonArray.getJSONObject(i);//得到一条召回对象
            Integer id = new Integer(jsonObj.get("_id").toString());
            BigDecimal distance = new BigDecimal(jsonObj.getJSONObject("fields").getJSONArray("distance").get(0).toString());
            ShopModel shopModel = get(id);
            shopModel.setDistance(distance.multiply(new BigDecimal(1000).setScale(0,BigDecimal.ROUND_CEILING)).intValue());
            shopModelList.add(shopModel);
        }
        List<Map> tagsList = new ArrayList<>();
        JSONArray tagsJsonArr = jsonObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
        for(int i=0;i<tagsJsonArr.size();i++){
            JSONObject jsonObj = tagsJsonArr.getJSONObject(i);
            Map<String,Object> tagMap = new HashMap<>();
            tagMap.put("tags",jsonObj.getString("key"));
            tagMap.put("num",jsonObj.getInteger("doc_count"));
            tagsList.add(tagMap);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("shop",shopModelList);
        map.put("tags",tagsList);
        return map;
    }
    //构造分词函数识别器

    private Map<String,Object> analyzeCategoryKeyword(String keyword) throws IOException {
        Map<String,Object> result = new HashMap<>();
        Request request = new Request("GET","/shop/_analyze");
        request.setJsonEntity("{\"field\": \"name\",\"text\": \""+keyword+"\"}");
        Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray jsonArray = jsonObject.getJSONArray("tokens");
        for(int i=0;i<jsonArray.size();i++){
            String token = jsonArray.getJSONObject(i).getString("token");
            Integer categoryId = getCategoryIdByToken(token);
            if(categoryId!=null){
                result.put(token,categoryId);
            }
        }
        return result;
    }

    private Integer getCategoryIdByToken(String token){
        for(Integer key:categoryWorkMap.keySet()){
            List<String> tokenList = categoryWorkMap.get(key);
            if(tokenList.contains(token)){
                return key;
            }
        }
        return null;
    }

    private Map<Integer,List<String>> categoryWorkMap = new HashMap<>();

    @PostConstruct//意思就是在spring初始化bean完成后就可以加载这个方法
    public void init(){
        categoryWorkMap.put(1,new ArrayList<>());
        categoryWorkMap.get(1).add("吃饭");
        categoryWorkMap.get(1).add("下午茶");

        categoryWorkMap.put(2,new ArrayList<>());
        categoryWorkMap.get(2).add("休息");
        categoryWorkMap.get(2).add("睡觉");
        categoryWorkMap.get(2).add("住宿");
    }
}
