package bean;

import java.time.LocalDateTime;

public class PaymentTransactionBean {

    private String sessionId;
    private String username;
    private double amount;
    private String currency;
    private String paymentStatus;
    private LocalDateTime createdAt;

    public PaymentTransactionBean(String sessionId, String username, double amount, String currency, String paymentStatus, LocalDateTime createdAt) {

        this.sessionId = sessionId;
        this.username = username;
        this.amount = amount;
        this.currency = currency;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;

    }

    public String getSessionId() { return sessionId; }

    public String getUsername() { return username; }

    public double getAmount() { return amount; }

    public String getCurrency() { return currency; }

    public String getPaymentStatus() { return paymentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setPaymentStatus(String s) { this.paymentStatus = s; }
}
