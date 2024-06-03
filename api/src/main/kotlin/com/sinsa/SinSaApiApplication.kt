package com.sinsa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class SinSaApiApplication


fun main(args: Array<String>) {
    System.setProperty("spring.config.name", "application-api, application-persistence")
    runApplication<SinSaApiApplication>(*args)
}