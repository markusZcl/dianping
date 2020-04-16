package com.markus.dianping.recommend;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/16 23:18
 */
public class AlsRecallPredict {
    public static void main(String[] args) {
        //初始化spark运行环境
        SparkSession spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();

        //加载模型到内存
        ALSModel alsModel = ALSModel.load("D:\\study\\project\\devtool\\data\\alsModel");

        JavaRDD<String> csvFile = spark.read().textFile("D:///study/project/devtool/data/behavior.csv").toJavaRDD();
        JavaRDD<Object> javaRDD = csvFile.map(new Function<String, Object>() {
            @Override
            public Object call(String s) throws Exception {
                return Rating.parseRating(s);
            }
        });
        Dataset<Row> rating = spark.createDataFrame(javaRDD, Rating.class);

        //给5个用户做离线的召回结果预测
//        Dataset<Row>

    }
    public static class Rating implements Serializable {
        private Integer userId;
        private Integer shopId;
        private Integer rating;

        public static AlsRecallTrain.Rating parseRating(String str){
            str = str.replace("\"","");
            String[] strArr = str.split(",");
            int userId = Integer.parseInt(strArr[0]);
            int shopId = Integer.parseInt(strArr[1]);
            int rating = Integer.parseInt(strArr[2]);
            return new AlsRecallTrain.Rating(userId,shopId,rating);
        }

        public Rating(Integer userId, Integer shopId, Integer rating) {
            this.userId = userId;
            this.shopId = shopId;
            this.rating = rating;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getShopId() {
            return shopId;
        }

        public void setShopId(Integer shopId) {
            this.shopId = shopId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }
}
