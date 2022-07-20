/*******************************************************************************
 * Copyright(c) FriarTuck Pte Ltd ("FriarTuck"). All Rights Reserved.
 *  
 * This software is the confidential and proprietary information of FriarTuck. 
 * ("Confidential Information"). You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the license 
 * agreement you entered into with FriarTuck.
 * 
 * FriarTuck MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE 
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR NON-
 * INFRINGEMENT. FriarTuck SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE 
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 ******************************************************************************/
package com.example.gateway.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.Map;

/**
 * @author James Tran
 *
 */
@Getter
public class BundledResponse {
    private Map<String, Object> responses;
    private final long timestamp = System.currentTimeMillis();
    
    public BundledResponse(List<PartialResponse> partialResponses) {
        responses = StreamEx.of(partialResponses)
                            .map(PartialResponse::trim)
                            .toMap(PartialResponse::getEndpointUri, PartialResponse::getApiResponse);
    }
    
    @AllArgsConstructor(staticName="from") @Getter
    public static class PartialResponse {
        private String endpointUri;
        private Map<String, Object> apiResponse;
        
        private static final String[] REDUNDANT_KEYS = { "timestamp" };
        
        /**
         * Remove redundant information from individual ApiResponse to optimize payload size
         * 
         */
        public PartialResponse trim() {
            StreamEx.of(REDUNDANT_KEYS)
                    .forEach(key -> apiResponse.remove(key));
            
            return this;
        }
    }
}
