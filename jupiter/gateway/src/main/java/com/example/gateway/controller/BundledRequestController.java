package com.example.gateway.controller;

import com.example.gateway.helper.BundledRequest;
import com.example.gateway.helper.BundledResponse;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Map;

@RestController
@RequestMapping("${server.servlet.context-path}/api/bundled-request")
public class BundledRequestController {
    @Autowired
    private Environment environment;
    
    @Value("${server.servlet.context-path}")
    private String apiPrefix;
    
    private WebClient reactiveClient;
    
    @PostConstruct
    public void init() {
        this.reactiveClient = WebClient.create("http://localhost:" + environment.getProperty("server.port"));
    }
    
    @PostMapping
    public Mono<BundledResponse> aggregateResponses(@RequestBody Publisher<BundledRequest> requestStream, ServerHttpRequest request) {
        return Mono.from(requestStream)
                   .map(BundledRequest::getRequests)
                   .flatMapMany(Flux::fromIterable)
                   .flatMap(partialRequest -> reactiveClient.method(partialRequest.getHttpMethod())
                                                            .uri(partialRequest.getCompleteRequestUri(apiPrefix))
                                                            .headers(headers -> headers.addAll(request.getHeaders()))
                                                            .body(BodyInserters.fromObject(partialRequest.getBody()))
                                                            .exchange()
                                                            .flatMap(reactiveResponse -> reactiveResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}))
                                                            .map(apiResponse -> BundledResponse.PartialResponse.from(partialRequest.getEndpointUri(), apiResponse)))
                   .collectList()
                   .map(BundledResponse::new);
    }
}
