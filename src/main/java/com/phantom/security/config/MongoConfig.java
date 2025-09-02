package com.phantom.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    // MongoDB 연결에 대한 추가 설정이 필요하면 여기에 구현
    // 현재는 application.yml의 설정으로 충분함

}