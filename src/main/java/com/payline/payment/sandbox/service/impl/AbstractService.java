package com.payline.payment.sandbox.service.impl;

import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;

import java.math.BigInteger;

public class AbstractService {

    /**
     *
     * @param request
     * @return
     */
    protected Object processRequestWithResponseGenericError(Object request) {

        BigInteger amount = null;

        if (request instanceof PaymentRequest) {
            amount = ((PaymentRequest) request).getAmount().getAmountInSmallestUnit();
        }

        if (request instanceof RedirectionPaymentRequest) {
            amount = ((RedirectionPaymentRequest) request).getAmount().getAmountInSmallestUnit();
        }

        if (request instanceof PaymentFormConfigurationRequest) {
            amount = ((PaymentFormConfigurationRequest) request).getAmount().getAmountInSmallestUnit();
        }

        if (request instanceof CaptureRequest) {
            amount = ((CaptureRequest) request).getAmount().getAmountInSmallestUnit();
        }

        if (new BigInteger("10098").equals(amount)
                || new BigInteger("20098").equals(amount)
                || new BigInteger("20198").equals(amount)
                || new BigInteger("30098").equals(amount)
                || new BigInteger("30198").equals(amount)
                || new BigInteger("30298").equals(amount)
                || new BigInteger("50098").equals(amount)) {
            // Return null
            // Nothing to do, the response object is already initialized to null
        }

        if (new BigInteger("10099").equals(amount)
                || new BigInteger("20099").equals(amount)
                || new BigInteger("20199").equals(amount)
                || new BigInteger("30099").equals(amount)
                || new BigInteger("30199").equals(amount)
                || new BigInteger("30299").equals(amount)
                || new BigInteger("50099").equals(amount)) {
            // Return exception
            throw new NullPointerException();
        }

        return null;

    }

}