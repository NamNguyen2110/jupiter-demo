package com.example.gateway.helper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class BundledRequest {
    private List<RequestDetails> requests;
    
    @Getter @Setter
    public static class RequestDetails {
        private String endpointUri;
        private String httpMethod = HttpMethod.GET.name();
        private Map<String, String> params = new HashMap<>();
        private Map<String, String> body   = new HashMap<>();
        
        public HttpMethod getHttpMethod() {
            return HttpMethod.resolve(this.httpMethod);
        }
        
        public String getCompleteRequestUri(String apiPrefix) {
            // Make best effort to reformat the given URI into proper format first
            String formattedUri = (this.endpointUri.startsWith("/")) ? this.endpointUri : "/" + this.endpointUri;
            formattedUri        = (formattedUri.startsWith(apiPrefix)) ? formattedUri : apiPrefix + "/" + formattedUri;
            
            // Build the complete URI next
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(formattedUri);
            params.forEach(builder::queryParam);
            
            return builder.toUriString();
        }
    }
}
