package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.MagicAmountEnumValue;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.capture.response.CaptureResponse;
import com.payline.pmapi.bean.capture.response.impl.CaptureResponseFailure;
import com.payline.pmapi.bean.capture.response.impl.CaptureResponseSuccess;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.service.CaptureService;

import java.math.BigInteger;

public class CaptureServiceImpl extends AbstractService implements CaptureService {

    @Override
    public CaptureResponse captureRequest(CaptureRequest captureRequest) {
        this.verifyRequest(captureRequest);
        return this.processRequest(captureRequest);
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Order, etc.)
     * @param captureRequest
     */
    private void verifyRequest(CaptureRequest captureRequest) {
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
     * @param captureRequest
     * @return
     */
    private CaptureResponse processRequest(CaptureRequest captureRequest) {

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        CaptureResponse captureResponse = null;

        BigInteger amount = captureRequest.getAmount().getAmountInSmallestUnit();

        // CAPTURE RESPONSE SUCCESS
        if ("CAPTURE_RESPONSE_SUCCESS".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            captureResponse = processRequestWithCaptureResponseSuccess(captureRequest);
        }

        // CAPTURE RESPONSE FAILURE
        if ("CAPTURE_RESPONSE_FAILURE".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            captureResponse = processRequestWithCaptureResponseFailure(captureRequest);
        }

        // GENERIC PLUGIN ERROR CASE
        if ("PAYMENT_RESPONSE_GENERIC_ERROR".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            captureResponse = (CaptureResponse) processRequestWithResponseGenericError(captureRequest);
        }

        return captureResponse;

    }

    /**
     *
     * @param captureRequest
     * @return
     */
    private CaptureResponse processRequestWithCaptureResponseSuccess(CaptureRequest captureRequest) {

        BigInteger amount = captureRequest.getAmount().getAmountInSmallestUnit();

        String partnerTransactionId = captureRequest.getTransactionId();
        String statusCode = "FUNDED";

        if (new BigInteger("50000").equals(amount)) {

            return CaptureResponseSuccess.CaptureResponseSuccessBuilder.aCaptureResponseSuccess()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withStatusCode(statusCode)
                    .build();

        }

        return null;

    }

    /**
     *
     * @param captureRequest
     * @return
     */
    private CaptureResponse processRequestWithCaptureResponseFailure(CaptureRequest captureRequest) {

        BigInteger amount = captureRequest.getAmount().getAmountInSmallestUnit();

        String partnerTransactionId = captureRequest.getTransactionId();

        if (new BigInteger("50090").equals(amount)) {

            return CaptureResponseFailure.CaptureResponseFailureBuilder.aCaptureResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode("TRANSACTION STATUS NOT FAVORABLE")
                    .withFailureCause(FailureCause.REFUSED)
                    .build();

        }

        return null;

    }

}