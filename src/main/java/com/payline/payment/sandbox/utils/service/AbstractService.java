package com.payline.payment.sandbox.utils.service;

import com.payline.payment.sandbox.utils.PluginUtils;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;

import java.io.IOException;
import java.math.BigInteger;

public class AbstractService<T> {

    protected final static String PARTNER_TRANSACTION_ID = "PARTNER_ID.0123456789";

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

    protected T generic( String magicAmount ){
        // Remove the first number of the magic amount, representing the service
        // Remove the second and third number, representing the method called or a global use case
        String genericAmount = magicAmount.substring(3);

        if( "98".equals( genericAmount ) ){
            return null;
        }
        if( "99".equals( genericAmount ) ){
            throw new NullPointerException("Simulate a NullPointerException thrown by the plugin");
        }

        throw new IllegalArgumentException("Illegal (invalid or unknown) magic amount : " + magicAmount);
    }

}