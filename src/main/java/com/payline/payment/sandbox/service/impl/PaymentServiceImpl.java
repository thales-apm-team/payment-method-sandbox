package com.payline.payment.sandbox.service.impl;

import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {
    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        this.verifyRequest( paymentRequest );

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        return null;
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Order, etc.)
     * @param paymentRequest
     */
    private void verifyRequest( PaymentRequest paymentRequest ){
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

}
