package com.sinsa.config

import com.querydsl.sql.H2Templates
import com.querydsl.sql.SQLTemplates
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryDSLConfig {

    @Bean
    fun h2Template(): SQLTemplates {
        return H2Templates.builder().build()
    }
}