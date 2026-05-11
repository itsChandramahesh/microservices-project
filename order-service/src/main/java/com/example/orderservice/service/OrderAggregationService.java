package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.OrderRecord;
import com.example.orderservice.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderAggregationService {

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final String userServiceBaseUrl;
    private final String userServiceFallbackBaseUrl;
    private final String productServiceBaseUrl;
    private final String productServiceFallbackBaseUrl;
    private final String paymentServiceUrl;
    private final String paymentServiceFallbackUrl;
    private final String notificationServiceUrl;
    private final String notificationServiceFallbackUrl;
    private final String analyticsServiceUrl;
    private final String analyticsServiceFallbackUrl;

    public OrderAggregationService(
            RestTemplate restTemplate,
            OrderRepository orderRepository,
            @Value("${services.user.primary-base-url}") String userServiceBaseUrl,
            @Value("${services.user.fallback-base-url}") String userServiceFallbackBaseUrl,
            @Value("${services.product.primary-base-url}") String productServiceBaseUrl,
            @Value("${services.product.fallback-base-url}") String productServiceFallbackBaseUrl,
            @Value("${services.payment.primary-url}") String paymentServiceUrl,
            @Value("${services.payment.fallback-url}") String paymentServiceFallbackUrl,
            @Value("${services.notification.primary-url}") String notificationServiceUrl,
            @Value("${services.notification.fallback-url}") String notificationServiceFallbackUrl,
            @Value("${services.analytics.primary-url}") String analyticsServiceUrl,
            @Value("${services.analytics.fallback-url}") String analyticsServiceFallbackUrl) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
        this.userServiceBaseUrl = userServiceBaseUrl;
        this.userServiceFallbackBaseUrl = userServiceFallbackBaseUrl;
        this.productServiceBaseUrl = productServiceBaseUrl;
        this.productServiceFallbackBaseUrl = productServiceFallbackBaseUrl;
        this.paymentServiceUrl = paymentServiceUrl;
        this.paymentServiceFallbackUrl = paymentServiceFallbackUrl;
        this.notificationServiceUrl = notificationServiceUrl;
        this.notificationServiceFallbackUrl = notificationServiceFallbackUrl;
        this.analyticsServiceUrl = analyticsServiceUrl;
        this.analyticsServiceFallbackUrl = analyticsServiceFallbackUrl;
    }

    public Map<String, Object> placeOrder(OrderRequest orderRequest) {
        Long userId = orderRequest != null && orderRequest.getUserId() != null ? orderRequest.getUserId() : 1L;
        Long productId = orderRequest != null && orderRequest.getProductId() != null ? orderRequest.getProductId() : 101L;

        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> user = getObjectWithFallback(
                buildResourceUrl(userServiceBaseUrl, userId),
                buildResourceUrl(userServiceFallbackBaseUrl, userId));
        Map<String, Object> product = getObjectWithFallback(
                buildResourceUrl(productServiceBaseUrl, productId),
                buildResourceUrl(productServiceFallbackBaseUrl, productId));
        Map<String, Object> payment = postWithFallback(paymentServiceUrl, paymentServiceFallbackUrl);
        Map<String, Object> notification = postWithFallback(notificationServiceUrl, notificationServiceFallbackUrl);
        Map<String, Object> analytics = postWithFallback(analyticsServiceUrl, analyticsServiceFallbackUrl);

        OrderRecord orderRecord = new OrderRecord();
        orderRecord.setUserId(userId);
        orderRecord.setProductId(productId);
        orderRecord.setPaymentStatus(getStatusValue(payment));
        orderRecord.setNotificationStatus(getStatusValue(notification));
        orderRecord.setAnalyticsStatus(getStatusValue(analytics));
        orderRecord.setCreatedAt(LocalDateTime.now());

        OrderRecord savedOrder = orderRepository.save(orderRecord);

        response.put("orderId", savedOrder.getId());
        response.put("user", user);
        response.put("product", product);
        response.put("payment", payment);
        response.put("notification", notification);
        response.put("analytics", analytics);
        response.put("message", "Order placed successfully");
        return response;
    }

    private String buildResourceUrl(String baseUrl, Long id) {
        return baseUrl.endsWith("/") ? baseUrl + id : baseUrl + "/" + id;
    }

    private String getStatusValue(Map<String, Object> serviceResponse) {
        Object status = serviceResponse.get("status");
        return status != null ? status.toString() : "UNKNOWN";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getObjectWithFallback(String primaryUrl, String fallbackUrl) {
        try {
            return restTemplate.getForObject(primaryUrl, Map.class);
        } catch (RestClientException exception) {
            return restTemplate.getForObject(fallbackUrl, Map.class);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> postWithFallback(String primaryUrl, String fallbackUrl) {
        try {
            return restTemplate.postForObject(primaryUrl, HttpEntity.EMPTY, Map.class);
        } catch (RestClientException exception) {
            return restTemplate.postForObject(fallbackUrl, HttpEntity.EMPTY, Map.class);
        }
    }
}
