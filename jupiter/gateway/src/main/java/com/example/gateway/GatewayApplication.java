package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableEurekaClient
@EnableHystrix
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    @Order(0)
    public GlobalFilter processRequestResponseHeaders() {
        return (exchange, chain) -> {
            // Process request headers here

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Process response here
                ServerHttpResponse response       = exchange.getResponse();
                HttpHeaders responseHeader = response.getHeaders();

                if (response.getStatusCode().value() >= 400)
                    /*
                     * Cache-Control is ignore to prevent Chrome from swallowing response body when HTTP error status is returned
                     * @see https://stackoverflow.com/q/47692658/801434
                     *
                     */
                    responseHeader.remove("Cache-Control");
            }));
        };
    }

}
