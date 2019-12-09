package com.payline.payment.sandbox.service.impl;

import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.service.ResetService;

import java.math.BigInteger;

public class ResetServiceImpl extends AbstractService implements ResetService {

    @Override
    public ResetResponse resetRequest(ResetRequest resetRequest) {
        this.verifyRequest(resetRequest);
        // FIXME : No amount in request
        return null;
    }

    @Override
    public boolean canMultiple() {
        return false;
    }

    @Override
    public boolean canPartial() {
        return false;
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Order, etc.)
     * @param resetRequest
     */
    private void verifyRequest(ResetRequest resetRequest) {
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

}