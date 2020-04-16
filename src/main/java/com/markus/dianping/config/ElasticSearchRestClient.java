package com.markus.dianping.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/12 18:30
 */
@Configuration
public class ElasticSearchRestClient {
    @Value("${elasticsearch.ip}")
    String ipAddress;
    @Bean(name = "restHighLevelClient")
    public RestHighLevelClient restHighLevelClient(){
        String []address = ipAddress.split(":");
        String ip = address[0];
        int port = Integer.parseInt(address[1]);
        HttpHost httpHost = new HttpHost(ip,port,"http");
        return new RestHighLevelClient(RestClient.builder(new HttpHost[]{httpHost}));
    }
}
