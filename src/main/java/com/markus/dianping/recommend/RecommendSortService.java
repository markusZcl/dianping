package com.markus.dianping.recommend;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/17 12:57
 */
@Service
public class RecommendSortService {
    private SparkSession spark;
    private LogisticRegressionModel lrMode;

    @PostConstruct
    public void init(){
        spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();
        lrMode = LogisticRegressionModel.load("D:\\study\\project\\devtool\\data\\lrModel");
    }
    public List<Integer> sort(List<Integer> shopIdList,Integer userId){
        //需要根据lrMode所需要的11维的x，生成特征，然后调用其预测方法
        List<ShopSortModel> shopSortModels = new ArrayList<>();
        for (Integer shopId : shopIdList) {
            //造的假数据，可以从数据库或缓存中拿到对应的性别，年龄，评分，价格等做特征转化生成feature向量
            Vector vector = Vectors.dense(1,0,0,0,0,1,0.6,0,0,1,0);
            Vector result = lrMode.predictProbability(vector);
            double[] arr = result.toArray();//二维
            double score = arr[1];
            ShopSortModel shopSortModel = new ShopSortModel();
            shopSortModel.setShopId(shopId);
            shopSortModel.setScore(score);
            shopSortModels.add(shopSortModel);
        }
        shopSortModels.sort(new Comparator<ShopSortModel>() {
            @Override
            public int compare(ShopSortModel o1, ShopSortModel o2) {
                return -(int)(o1.getScore()-o2.getScore());
            }
        });
        return shopSortModels.stream().map(shopSortModel -> shopSortModel.getShopId()).collect(Collectors.toList());
    }
}
