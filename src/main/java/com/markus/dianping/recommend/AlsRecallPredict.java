package com.markus.dianping.recommend;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

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

        JavaRDD<String> csvFile = spark.read().textFile("D:\\study\\project\\devtool\\data\\behavior.csv").toJavaRDD();
        JavaRDD<Rating> javaRDD = csvFile.map(new Function<String, Rating>() {
            @Override
            public Rating call(String s) throws Exception {
                return Rating.parseRating(s);
            }
        });
        Dataset<Row> rating = spark.createDataFrame(javaRDD, Rating.class);

        //给5个用户做离线的召回结果预测
        Dataset<Row> users = rating.select(alsModel.getUserCol()).distinct().limit(5);
        Dataset<Row> userRecs = alsModel.recommendForUserSubset(users,20);//给特定用户数据集推荐20个数据集

        userRecs.foreachPartition(new ForeachPartitionFunction<Row>() {
            @Override
            public void call(Iterator<Row> iterator) throws Exception {
                Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dianping" +
                        "?useUnicode=true&characterEncoding=utf-8","root","root");
                PreparedStatement preparedStatement = connection.
                        prepareStatement("insert into recommend(id,recommend) values (?,?)");
                List<Map<String,Object>> data = new ArrayList<>();
                iterator.forEachRemaining(action->{
                    int userId = action.getInt(0);
                    List<GenericRowWithSchema> recommendation = action.getList(1);
                    List<Integer> shopIdList = new ArrayList<>();
                    recommendation.forEach(row->{
                        Integer shopId = row.getInt(0);
                        shopIdList.add(shopId);
                    });
                    String recommendData = StringUtils.join(shopIdList,",");
                    Map<String,Object> map = new HashMap<>();
                    map.put("userId",userId);
                    map.put("recommend",recommendData);
                    data.add(map);
                });
                data.forEach(stringObjectMap -> {
                    try {
                        preparedStatement.setInt(1, (Integer) stringObjectMap.get("userId"));
                        preparedStatement.setString(2, (String) stringObjectMap.get("recommend"));
                        preparedStatement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                preparedStatement.executeBatch();
                connection.close();
            }
        });
    }
    public static class Rating implements Serializable {
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
