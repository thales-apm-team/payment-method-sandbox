package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.MagicAmountEnumValue;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.service.RefundService;

import java.math.BigInteger;

public class RefundServiceImpl extends AbstractService implements RefundService {

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        this.verifyRequest(refundRequest);
        return this.processRequest(refundRequest);
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Order, etc.)
     * @param refundRequest
     */
    private void verifyRequest(RefundRequest refundRequest) {
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
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
     *
     * @param refundRequest
     * @return
     */
    private RefundResponse processRequest(RefundRequest refundRequest) {

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        RefundResponse refundResponse = null;

        BigInteger amount = refundRequest.getAmount().getAmountInSmallestUnit();


        // REFUND RESPONSE SUCCESS
        if ("REFUND_RESPONSE_SUCCESS".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            refundResponse = processRequestWithRefundResponseSuccess(refundRequest);
        }

        // REFUND RESPONSE FAILURE
        if ("REFUND_RESPONSE_FAILURE".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            refundResponse = processRequestWithRefundResponseFailure(refundRequest);
        }

        // GENERIC PLUGIN ERROR CASE
        if ("PAYMENT_RESPONSE_GENERIC_ERROR".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            refundResponse = (RefundResponse) processRequestWithResponseGenericError(refundRequest);
        }

        return null;

    }

    /**
     *
     * @param refundRequest
     * @return
     */
    private RefundResponse processRequestWithRefundResponseSuccess(RefundRequest refundRequest) {

        BigInteger amount = refundRequest.getAmount().getAmountInSmallestUnit();

        String partnerTransactionId = refundRequest.getTransactionId();
        String statusCode = "200";

        if (new BigInteger("70000").equals(amount)) {

            return RefundResponseSuccess
                    .RefundResponseSuccessBuilder
                    .aRefundResponseSuccess()
                    .withStatusCode(statusCode)
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        }

        return null;

    }

    /**
     *
     * @param refundRequest
     * @return
     */
    private RefundResponse processRequestWithRefundResponseFailure(RefundRequest refundRequest) {

        BigInteger amount = refundRequest.getAmount().getAmountInSmallestUnit();

        String partnerTransactionId = refundRequest.getTransactionId();

        if (new BigInteger("70090").equals(amount)) {

            return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                    .withErrorCode("Invalid data")
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        }

        if (new BigInteger("70091").equals(amount)) {

            return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                    .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                    .withErrorCode("PARTNER_UNKNOWN_ERROR")
                    .build();

        }

        return null;

    }


}