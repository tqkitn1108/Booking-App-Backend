package com.cnweb.bookingapi.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentDetails {
    private String cardholderName;
    private String cardNumber;
    private LocalDate expirationDate;
    private String cvv;
}
