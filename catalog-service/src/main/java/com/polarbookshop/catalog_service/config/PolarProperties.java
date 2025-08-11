package com.polarbookshop.catalog_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "polar")  // 설정 데이터를 나타내는 클래스임을 표시, prefix와 필드 이름으로 속성 키 생성
public class PolarProperties {
    /**
     * A message to welcome users.
     */

    private String greeting; // 사용자 정의 속성인 polar.greeting(prefix + 필드명) 속성이 문자열로 인식되는 필드

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
