package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.MagicAmountEnumValue;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;
import com.payline.pmapi.service.ResetService;

import java.math.BigInteger;

public class ResetServiceImpl extends AbstractService implements ResetService {

    @Override
    public ResetResponse resetRequest(ResetRequest resetRequest) {
        this.verifyRequest(resetRequest);
        return this.processRequest(resetRequest);
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

    /**
     *
     * @param resetRequest
     * @return
     */
    private ResetResponse processRequest(ResetRequest resetRequest) {

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        ResetResponse resetResponse = null;

        BigInteger amount = resetRequest.getAmount().getAmountInSmallestUnit();

        // RESET RESPONSE SUCCESS
        if ("PAYMENT_RESPONSE_SUCCESS".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            resetResponse = processRequestWithPaymentResponseSuccess(resetRequest);
        }

        // RESET RESPONSE FAILURE
        if ("PAYMENT_RESPONSE_FAILURE".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            resetResponse = processRequestWithPaymentResponseFailure(resetRequest);
        }

        // GENERIC PLUGIN ERROR CASE
        if ("PAYMENT_RESPONSE_GENERIC_ERROR".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            resetResponse = (ResetResponse) processRequestWithResponseGenericError(resetRequest);
        }

        return resetResponse;

    }

    private ResetResponse processRequestWithPaymentResponseSuccess(ResetRequest resetRequest) {

        BigInteger amount = resetRequest.getAmount().getAmountInSmallestUnit();

        String partnerTransactionId = resetRequest.getTransactionId();
        String statusCode = "200";

        if (new BigInteger("60000").equals(amount)) {

            return ResetResponseSuccess
                    .ResetResponseSuccessBuilder
                    .aResetResponseSuccess()
                    .withStatusCode(statusCode)
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        }

        return null;

    }

    private ResetResponse processRequestWithPaymentResponseFailure(ResetRequest resetRequest) {

        BigInteger amount = resetRequest.getAmount().getAmountInSmallestUnit();

        String partnerTransactionId = resetRequest.getTransactionId();

        if (new BigInteger("60090").equals(amount)) {

            return ResetResponseFailure.ResetResponseFailureBuilder.aResetResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                    .withErrorCode("Empty partner response")
                    .build();

        }

        if (new BigInteger("60091").equals(amount)) {

            return ResetResponseFailure.ResetResponseFailureBuilder.aResetResponseFailure()
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .withErrorCode("XML RESPONSE PARSING FAILED")
                    .build();

        }

        return null;

    }

}