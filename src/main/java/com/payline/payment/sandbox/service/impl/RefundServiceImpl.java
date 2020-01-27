package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.PaymentResponseUtil;
import com.payline.payment.sandbox.utils.service.AbstractService;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.service.RefundService;

public class RefundServiceImpl extends AbstractService<RefundResponse> implements RefundService {

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        this.verifyRequest(refundRequest);

        String amount = refundRequest.getAmount().getAmountInSmallestUnit().toString();

        switch( amount ){
            /* ResetResponseSuccess */
            case "70000":
                return RefundResponseSuccess.RefundResponseSuccessBuilder.aRefundResponseSuccess()
                        .build();
            case "70001":
                return RefundResponseSuccess.RefundResponseSuccessBuilder.aRefundResponseSuccess()
                        .withPartnerTransactionId(PaymentResponseUtil.PARTNER_TRANSACTION_ID)
                        .withStatusCode("STATUS")
                        .build();

            /* ResetResponseFailure */
            case "70100":
                return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                        .build();
            case "70101":
                return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                        .withErrorCode("Error code less than 50 characters long")
                        .withFailureCause(FailureCause.REFUSED)
                        .withPartnerTransactionId( PaymentResponseUtil.PARTNER_TRANSACTION_ID )
                        .build();

            default:
                return super.generic( amount );
        }
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required in the request is filled.
     * @param refundRequest the request
     */
    private void verifyRequest(RefundRequest refundRequest) {
        if( refundRequest.getAmount() == null ){
            throw new IllegalArgumentException( "The RefundRequest is missing an amount" );
        }
    }

    @Override
    public boolean canMultiple() {
        return false;
    }

    @Override
    public boolean canPartial() {
        return false;
    }

}