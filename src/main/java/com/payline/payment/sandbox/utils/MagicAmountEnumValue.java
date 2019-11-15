package com.payline.payment.sandbox.utils;

import java.math.BigInteger;

public enum MagicAmountEnumValue {

    UNKNOWN_VALUE("", "", new BigInteger("-1")),

    PAYMENT_SERVICE__PAYMENT_RESPONSE_REDIRECT__10000 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_REDIRECT", new BigInteger("10000")),
    PAYMENT_SERVICE__PAYMENT_RESPONSE_REDIRECT__10001 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_REDIRECT", new BigInteger("10001")),
    PAYMENT_SERVICE__PAYMENT_RESPONSE_REDIRECT__10002 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_REDIRECT", new BigInteger("10002")),

    PAYMENT_SERVICE__PAYMENT_RESPONSE_FORM_UPDATED__10003 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_FORM_UPDATED", new BigInteger("10003")),
    PAYMENT_SERVICE__PAYMENT_RESPONSE_FORM_UPDATED__10004 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_FORM_UPDATED", new BigInteger("10004")),

    PAYMENT_SERVICE__PAYMENT_RESPONSE_DO_PAYMENT__10005 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_DO_PAYMENT", new BigInteger("10005")),

    PAYMENT_SERVICE__PAYMENT_RESPONSE_ACTIVE_WAITING__10006 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_ACTIVE_WAITING", new BigInteger("10006")),

    PAYMENT_SERVICE__PAYMENT_RESPONSE_SUCCESS__10007 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_SUCCESS", new BigInteger("10007")),

    PAYMENT_SERVICE__PAYMENT_RESPONSE_SUCCESS__10090("PAYMENT_SERVICE", "PAYMENT_RESPONSE_FAILURE", new BigInteger("10090")),
    PAYMENT_SERVICE__PAYMENT_RESPONSE_SUCCESS__10091 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_FAILURE", new BigInteger("10091")),

    PAYMENT_SERVICE__PAYMENT_RESPONSE_SUCCESS__10098 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_GENERIC_ERROR", new BigInteger("10098")),
    PAYMENT_SERVICE__PAYMENT_RESPONSE_SUCCESS__10099 ("PAYMENT_SERVICE", "PAYMENT_RESPONSE_GENERIC_ERROR", new BigInteger("10099"));


    private String service;
    private String response;
    private BigInteger amount;

    MagicAmountEnumValue(String service, String response, BigInteger amount) {
        this.service = service;
        this.response = response;
        this.amount = amount;
    }

    public String getService() {
        return service;
    }

    public String getResponse() {
        return response;
    }

    public BigInteger getAmount() {
        return amount;
    }


    public static MagicAmountEnumValue fromAmountValue(BigInteger amount) {

        for (MagicAmountEnumValue result : MagicAmountEnumValue.values()) {
            if (result.getAmount().equals(amount)) {
                return result;
            }
        }

        return MagicAmountEnumValue.UNKNOWN_VALUE;
    }

}