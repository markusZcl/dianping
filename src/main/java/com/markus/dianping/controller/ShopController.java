package com.markus.dianping.controller;

import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.Common.CommonRes;
import com.markus.dianping.Common.EmBusinessError;
import com.markus.dianping.model.CategoryModel;
import com.markus.dianping.model.ShopModel;
import com.markus.dianping.service.CategoryService;
import com.markus.dianping.service.ShopService;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/10 15:11
 */
@Controller("/shop")
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private CategoryService categoryService;
    @RequestMapping("/recommend")
    @ResponseBody
    public CommonRes recommend(@RequestParam("longitude")BigDecimal longitude,
                               @RequestParam("latitude")BigDecimal latitude) throws BusinessException {
        if(longitude==null || latitude==null){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR);
        }
        List<ShopModel> recommend = shopService.recommend(longitude, latitude);
        return CommonRes.create(recommend);
    }
    @RequestMapping("/search")
    @ResponseBody
    public CommonRes search(@RequestParam("longitude")BigDecimal longitude,
                            @RequestParam("latitude")BigDecimal latitude,
                            @RequestParam("keyword")String keyword,
                            @RequestParam(value = "orderby",required = false)Integer orderBy,
                            @RequestParam(value = "categoryId",required = false)Integer categoryId,
                            @RequestParam(value = "tags",required = false)String tags) throws BusinessException, IOException {
        if(StringUtils.isEmpty(keyword) || longitude==null || latitude==null){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR);
        }
        //List<ShopModel> shopModelList = shopService.searchES(longitude,latitude,keyword,orderBy,categoryId,tags);
        List<CategoryModel> categoryModelList = categoryService.selectAll();
        //List<Map<String,Object>> tags1 = shopService.searchGroupByTags(keyword,categoryId,tags);
        Map<String,Object> result = shopService.searchES(longitude,latitude,keyword,orderBy,categoryId,tags);
        List<ShopModel> shopModelList = (List<ShopModel>) result.get("shop");
        List<Map<String,Object>> tags1 = (List<Map<String, Object>>) result.get("tags");
        Map<String,Object> modelMap = new HashMap<>();
        modelMap.put("category",categoryModelList);
        modelMap.put("shop",shopModelList);
        modelMap.put("tags",tags1);
        return CommonRes.create(modelMap);
    }
}
