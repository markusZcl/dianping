package com.markus.dianping.recommend;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.*;

import java.io.IOException;


/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/17 11:46
 */
public class LRTrain {
    public static void main(String[] args) throws IOException {
        //初始化spark运行环境
        SparkSession spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();
        //将文件加载到内存
        JavaRDD<String> csvFile = spark.read().textFile("D:\\study\\project\\devtool\\data\\feature.csv").toJavaRDD();
        JavaRDD<Row> javaRDD = csvFile.map(new Function<String, Row>() {
            @Override
            public Row call(String s) throws Exception {
                s = s.replace("\"","");
                String[] strArr = s.split(",");
                return RowFactory.create(new Double(strArr[11]), Vectors.dense(Double.valueOf(strArr[0]),
                        Double.valueOf(strArr[1]),Double.valueOf(strArr[2]),
                        Double.valueOf(strArr[3]),Double.valueOf(strArr[4]),
                        Double.valueOf(strArr[5]),Double.valueOf(strArr[6]),
                        Double.valueOf(strArr[7]),Double.valueOf(strArr[8]),
                        Double.valueOf(strArr[9]),Double.valueOf(strArr[10])));
            }
        });
        StructType schema = new StructType(
                new StructField[]{
                        new StructField("label", DataTypes.DoubleType,false, Metadata.empty()),
                        new StructField("features",new VectorUDT(),false,Metadata.empty())
                }
        );
        Dataset<Row> data = spark.createDataFrame(javaRDD,schema);

        //分开训练和测试集
        Dataset<Row> []dataArr = data.randomSplit(new double[]{0.8,0.2});
        Dataset<Row> trainingData = dataArr[0];
        Dataset<Row> testingData = dataArr[1];
        LogisticRegression lr = new LogisticRegression().setMaxIter(10).setRegParam(0.3).setElasticNetParam(0.8).setFamily("multinomial");
        LogisticRegressionModel lrMode = lr.fit(trainingData);
        lrMode.save("D:\\study\\project\\devtool\\data\\lrModel");
        //测试评估
        Dataset<Row> predictions = lrMode.transform(testingData);

        //评价指标
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator();
        double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);
        System.out.println("accuracy="+accuracy);

    }
}
