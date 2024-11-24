package com.cnweb.bookingapi.controller;

import com.cnweb.bookingapi.payment.vnpay.VnpayService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final VnpayService vnpayService;
    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    // Chuyển hướng người dùng đến cổng thanh toán VNPAY
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestParam("amount") int amount,
                                         @RequestParam("orderInfo") String orderInfo,
                                         HttpServletRequest request) {
        String vnpayUrl = vnpayService.createOrder(request, amount, orderInfo, frontendBaseUrl);

        return ResponseEntity.ok(Collections.singletonMap("paymentUrl", vnpayUrl));
    }

    // Endpoint xử lý khi VnPay trả về kết quả thanh toán
    @GetMapping("/vnpay-payment-return")
    public ResponseEntity<?> paymentReturn(HttpServletRequest request) {
        int paymentStatus = vnpayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderInfo);
        response.put("totalPrice", totalPrice);
        response.put("paymentTime", paymentTime);
        response.put("transactionId", transactionId);
        response.put("paymentStatus", paymentStatus);

        return ResponseEntity.ok(response);
    }

}