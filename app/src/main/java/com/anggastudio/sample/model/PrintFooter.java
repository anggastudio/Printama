package com.anggastudio.sample.model;

public class PrintFooter {
    String initial;
    String paymentBy;
    String qrPayName;
    String powered;
    String issuer;
    String description;
    String environment;

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getPaymentBy() {
        return paymentBy;
    }

    public void setPaymentBy(String paymentBy) {
        this.paymentBy = paymentBy;
    }

    public String getPowered() {
        return powered;
    }

    public void setPowered(String powered) {
        this.powered = powered;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQrPayName() {
        return qrPayName;
    }

    public void setQrPayName(String qrPayName) {
        this.qrPayName = qrPayName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
