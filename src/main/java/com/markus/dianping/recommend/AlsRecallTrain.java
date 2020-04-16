package com.markus.dianping.recommend;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.Serializable;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/16 19:26
 */
//ALS召回算法的训练
public class AlsRecallTrain implements Serializable {
    public static void main(String[] args) throws IOException {
        //初始化spark运行环境
        SparkSession spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();
        JavaRDD<String> csvFile = spark.read().textFile("D:///study/project/devtool/data/behavior.csv").toJavaRDD();
        JavaRDD<Rating> ratingJavaRDD = csvFile.map(new Function<String, Rating>() {
            @Override
            public Rating call(String s) throws Exception {
                return Rating.parseRating(s);
            }
        });
        Dataset<Row> rating = spark.createDataFrame(ratingJavaRDD, Rating.class);
        //将所有的rating数据分成82份
        Dataset<Row>[] splits = rating.randomSplit(new double[]{0.8,0.2});
        Dataset<Row> trainingData = splits[0];
        Dataset<Row> testingData = splits[1];
        //过拟合：增大数据规模，减少Rank，增大正则化系数
        //负拟合：增加rank，减少正则化系数
        ALS als = new ALS().setMaxIter(10).setRank(4).setRegParam(0.01)
                .setUserCol("userId")
                .setItemCol("shopId")
                .setRatingCol("rating");
        //模型训练的过程
        ALSModel alsModel = als.fit(trainingData);
        //
        //alsModel.save("D:\\study\\project\\devtool\\data\\alsModel");
        //模型评测的过程
        Dataset<Row> predictions = alsModel.transform(testingData);

        //rmse 均方根误差，预测值与真实值的偏差的平方除以观测次数，开个根号
        //rmse越小，模型在测试数据值的表现更好，那么我们的离线指标也就越好
        RegressionEvaluator evaluator = new RegressionEvaluator().setMetricName("rmse")
                .setLabelCol("rating").setPredictionCol("prediction");
        double rmse = evaluator.evaluate(predictions);
        System.out.println("rmse="+rmse);
        alsModel.save("D:\\study\\project\\devtool\\data\\alsModel");
    }
    public static class Rating implements Serializable{
        private Integer userId;
        private Integer shopId;
        private Integer rating;

        public static Rating parseRating(String str){
            str = str.replace("\"","");
            String[] strArr = str.split(",");
            int userId = Integer.parseInt(strArr[0]);
            int shopId = Integer.parseInt(strArr[1]);
            int rating = Integer.parseInt(strArr[2]);
            return new Rating(userId,shopId,rating);
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
