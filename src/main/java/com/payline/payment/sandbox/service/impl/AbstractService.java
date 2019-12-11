package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.PluginUtils;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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

        if (request instanceof NotificationRequest) {
            try {
                amount = new BigInteger(PluginUtils.inputStreamToString(((NotificationRequest) request).getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (request instanceof CaptureRequest) {
            amount = ((CaptureRequest) request).getAmount().getAmountInSmallestUnit();
        }

        if (request instanceof ResetRequest) {
            amount = ((ResetRequest) request).getAmount().getAmountInSmallestUnit();
        }

        if (request instanceof RefundRequest) {
            amount = ((RefundRequest) request).getAmount().getAmountInSmallestUnit();
        }

        if (amount != null) {

            if (new BigInteger("10098").equals(amount)
                    || new BigInteger("20098").equals(amount)
                    || new BigInteger("20198").equals(amount)
                    || new BigInteger("30098").equals(amount)
                    || new BigInteger("30198").equals(amount)
                    || new BigInteger("30298").equals(amount)
                    || new BigInteger("40098").equals(amount)
                    || new BigInteger("50098").equals(amount)
                    || new BigInteger("60098").equals(amount)
                    || new BigInteger("70098").equals(amount)) {

                // Return null
                // Nothing to do, the response object is already initialized to null

            }

            if (new BigInteger("10099").equals(amount)
                    || new BigInteger("20099").equals(amount)
                    || new BigInteger("20199").equals(amount)
                    || new BigInteger("30099").equals(amount)
                    || new BigInteger("30199").equals(amount)
                    || new BigInteger("30299").equals(amount)
                    || new BigInteger("40099").equals(amount)
                    || new BigInteger("50099").equals(amount)
                    || new BigInteger("60099").equals(amount)
                    || new BigInteger("70099").equals(amount)) {

                // Return exception
                throw new NullPointerException();

            }

        }

        return null;

    }

}