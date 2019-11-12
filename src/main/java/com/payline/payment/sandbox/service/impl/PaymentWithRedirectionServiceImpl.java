package com.payline.payment.sandbox.service.impl;

import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.service.PaymentWithRedirectionService;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        this.verifyRequest( redirectionPaymentRequest );

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        return null;
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        this.verifyRequest( transactionStatusRequest );

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        return null;
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to finalize a transaction after a redirection is filled in the request.
     *
     * @param redirectionPaymentRequest the request to verify
     */
    private void verifyRequest( RedirectionPaymentRequest redirectionPaymentRequest ){
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to recover the state of a transaction when user session has expired is filled in the request.
     * (ex: transactionId)
     * @param redirectionPaymentRequest the request to verify
     */
    private void verifyRequest( TransactionStatusRequest redirectionPaymentRequest ){
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }
}
