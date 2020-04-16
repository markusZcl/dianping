package com.markus.dianping.canal;

import com.alibaba.google.common.collect.Lists;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/15 21:54
 */
@Component
public class CanalClient implements DisposableBean {

    private CanalConnector canalConnector;
    @Bean
    public CanalConnector getCanalConnector(){
        canalConnector = CanalConnectors.newClusterConnector(Lists.newArrayList(
           new InetSocketAddress("127.0.0.1",11111)),
                "example","canal","canal"
        );
        canalConnector.connect();
        //指定filter，格式{database}.{table}
        canalConnector.subscribe();
        //回滚寻找上次中断的位置
        //对应我们的canalClient去消费我们的canal管道，
        // 其实是一个流式的操作，如果说对应的在流式操作的过程当中被中断了，对应的canal deployer会记录中断点，
        //当下次连接的时候，会继续从中断点开始消费
        canalConnector.rollback();
        return canalConnector;
    }
    //2.消息处理
    @Override
    public void destroy() throws Exception {
        //随着spring容器的销毁而断开连接
        if(canalConnector!=null){
            canalConnector.disconnect();
        }
    }
}
