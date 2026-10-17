package com.orderforge.order.client;

import com.orderforge.order.dto.ProductResponse;
import com.orderforge.order.exception.OrderApplicationException;
import com.orderforge.order.exception.OrderError;
import com.orderforge.order.exception.ProductServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProductClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public ProductClient(RestTemplate restTemplate,
                         @Value("${product-service.base-url}") String baseUrl,
                         ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
    }

    public ProductResponse getProductById(String productId) {
        ProductResponse product;
        try {
            product = restTemplate.getForObject(baseUrl + "/products/{productId}",
                    ProductResponse.class, productId);
        } catch (ResourceAccessException exception) {
            throw new OrderApplicationException(OrderError.PRODUCT_UNAVAILABLE,
                    "Product Service could not be reached or did not respond in time.", null, exception);
        } catch (RestClientResponseException exception) {
            throw mapDownstreamError(exception);
        } catch (RestClientException exception) {
            throw new ProductServiceException("Unable to read the Product Service response.", exception);
        }
        if (product == null || !productId.equals(product.id())
                || product.name() == null || product.name().isBlank()
                || product.unitPrice() == null || product.unitPrice().signum() < 0) {
            throw new ProductServiceException("Product Service returned incomplete or inconsistent product data.");
        }
        return product;
    }

    private OrderApplicationException mapDownstreamError(RestClientResponseException exception) {
        int status = exception.getStatusCode().value();
        OrderError error = switch (status) {
            case 404 -> OrderError.PRODUCT_NOT_FOUND;
            case 429, 503, 504 -> OrderError.PRODUCT_UNAVAILABLE;
            default -> OrderError.INVALID_PRODUCT_RESPONSE;
        };
        String detail = switch (error) {
            case PRODUCT_NOT_FOUND -> "The requested product does not exist.";
            case PRODUCT_UNAVAILABLE -> "Product Service is temporarily unavailable.";
            default -> "Product Service returned an unexpected error response.";
        };
        String downstreamCode = null;
        // Only forward public messages for explicitly recognized business errors.
        // Unknown codes, HTML, debug bodies, and infrastructure failures use our fallback.
        byte[] body = exception.getResponseBodyAsByteArray();
        if (status == 404 && body.length > 0 && body.length <= 8192) {
            try {
                var json = objectMapper.readTree(body);
                String code = json.path("code").asText("");
                String message = json.path("message").asText("");
                if ("PROD-001".equals(code)) {
                    downstreamCode = code;
                    if (!message.isBlank() && message.length() <= 300
                            && message.chars().noneMatch(Character::isISOControl)) {
                        detail = message;
                    }
                }
            } catch (RuntimeException ignored) {
                // A malformed downstream error must not replace the original failure.
            }
        }
        return new OrderApplicationException(error, detail, downstreamCode, exception);
    }
}
