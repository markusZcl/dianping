package com.markus.dianping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.markus.dianping"})
@MapperScan("com.markus.dianping.dal")
//开启注解
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
public class DianpingApplication {
    public static void main(String[] args) {
        SpringApplication.run(DianpingApplication.class, args);
    }

}
