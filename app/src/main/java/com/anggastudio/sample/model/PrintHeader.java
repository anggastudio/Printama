package com.anggastudio.sample.model;

public class PrintHeader {

    String merchantName;
    String merchantAddress1;
    String merchantAddress2;
    String merchantId;

    public PrintHeader() {
    }

    public PrintHeader(String merchantName, String merchantAddress1, String merchantAddress2) {
        this.merchantName = merchantName;
        this.merchantAddress1 = merchantAddress1;
        this.merchantAddress2 = merchantAddress2;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantAddress1() {
        return merchantAddress1;
    }

    public void setMerchantAddress1(String merchantAddress1) {
        this.merchantAddress1 = merchantAddress1;
    }

    public String getMerchantAddress2() {
        return merchantAddress2;
    }

    public void setMerchantAddress2(String merchantAddress2) {
        this.merchantAddress2 = merchantAddress2;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
