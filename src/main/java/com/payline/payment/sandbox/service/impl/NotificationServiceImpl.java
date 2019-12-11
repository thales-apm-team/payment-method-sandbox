package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.MagicAmountEnumValue;
import com.payline.payment.sandbox.utils.PluginUtils;
import com.payline.pmapi.bean.common.*;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.TransactionStateChangedResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.NotificationService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

public class NotificationServiceImpl extends AbstractService implements NotificationService {

    @Override
    public NotificationResponse parse(NotificationRequest notificationRequest) {
        this.verifyRequest(notificationRequest);
        // FIXME : No amount in request
        return this.processRequest(notificationRequest);
    }

    @Override
    public void notifyTransactionStatus(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        this.verifyRequest(notifyTransactionStatusRequest);
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Order, etc.)
     * @param notificationRequest
     */
    private void verifyRequest(NotificationRequest notificationRequest) {
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Order, etc.)
     * @param notifyTransactionStatusRequest
     */
    private void verifyRequest(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

    /**
     *
     * @param notificationRequest
     * @return
     */
    private NotificationResponse processRequest(NotificationRequest notificationRequest) {

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        NotificationResponse notificationResponse = null;

        // There is no amount in this request, the magic amount is send in the notificationRequest.content
        BigInteger amount = null;

        try {

            amount = new BigInteger(PluginUtils.inputStreamToString(notificationRequest.getContent()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (amount != null) {

            // IGNORE NOTIFICATION RESPONSE
            if ("IGNORE_NOTIFICATION_RESPONSE".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
                notificationResponse = processRequestWithIgnoreNotificationResponse(notificationRequest);
            }

            // PAYMENT RESPONSE BY NOTIFICATION RESPONSE
            if ("PAYMENT_RESPONSE_BY_NOTIFICATION_RESPONSE".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
                notificationResponse = processRequestWithPaymentResponseByNotificationResponse(notificationRequest);
            }

            // TRANSACTION STATE CHANGED RESPONSE
            if ("TRANSACTION_STATE_CHANGED_RESPONSE".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
                notificationResponse = processRequestWithTransactionStateChangedResponse(notificationRequest);
            }

            // GENERIC PLUGIN ERROR CASE
            if ("NOTIFICATION_RESPONSE_GENERIC_ERROR".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
                notificationResponse = (NotificationResponse) processRequestWithResponseGenericError(notificationRequest);
            }

        }

        return notificationResponse;

    }

    /**
     *
     * @param notificationRequest
     * @return
     */
    private NotificationResponse processRequestWithIgnoreNotificationResponse(NotificationRequest notificationRequest) {

        // There is no amount in this request, the magic amount is send in the notificationRequest.content
        BigInteger amount = null;

        try {

            amount = new BigInteger(PluginUtils.inputStreamToString(notificationRequest.getContent()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (amount != null) {

            if (new BigInteger("40000").equals(amount)) {

                return new IgnoreNotificationResponse();

            }

            if (new BigInteger("40001").equals(amount)) {

                return IgnoreNotificationResponse.IgnoreNotificationResponseBuilder
                        .aIgnoreNotificationResponseBuilder()
                        .withHttpStatus(204)
                        .build();

            }

        }

        return null;

    }

    /**
     *
     * @param notificationRequest
     * @return
     */
    private NotificationResponse processRequestWithPaymentResponseByNotificationResponse(NotificationRequest notificationRequest) {

        // There is no amount in this request, the magic amount is send in the notificationRequest.content
        BigInteger amount = null;

        try {

            amount = new BigInteger(PluginUtils.inputStreamToString(notificationRequest.getContent()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        String partnerTransactionId = notificationRequest.getTransactionId();
        String statusCode = "200";

        if (amount != null) {

            if (new BigInteger("40002").equals(amount)) {

                PaymentResponse paymentResponse = PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                        .withStatusCode(statusCode)
                        .withTransactionDetails(new EmptyTransactionDetails())
                        .withPartnerTransactionId(partnerTransactionId)
                        .withMessage(new Message(Message.MessageType.SUCCESS, "Transaction acceptee"))
                        .build();

                TransactionCorrelationId transactionCorrelationId = TransactionCorrelationId.TransactionCorrelationIdBuilder.aCorrelationIdBuilder()
                        .withType(TransactionCorrelationId.CorrelationIdType.PARTNER_TRANSACTION_ID)
                        .withValue(partnerTransactionId)
                        .build();

                return PaymentResponseByNotificationResponse.PaymentResponseByNotificationResponseBuilder.aPaymentResponseByNotificationResponseBuilder()
                        .withPaymentResponse(paymentResponse)
                        .withTransactionCorrelationId(transactionCorrelationId)
                        .withHttpStatus(204)
                        .build();

            }

            if (new BigInteger("40003").equals(amount)) {

                PaymentResponse paymentResponse = PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                        .withStatusCode(statusCode)
                        .withPartnerTransactionId(partnerTransactionId)
                        .withTransactionDetails(new EmptyTransactionDetails())
                        .withTransactionAdditionalData("Transaction additional data")
                        .build();

                TransactionCorrelationId correlationId = TransactionCorrelationId.TransactionCorrelationIdBuilder
                        .aCorrelationIdBuilder()
                        .withType(TransactionCorrelationId.CorrelationIdType.PARTNER_TRANSACTION_ID)
                        .withValue(partnerTransactionId)
                        .build();

                return PaymentResponseByNotificationResponse.PaymentResponseByNotificationResponseBuilder
                        .aPaymentResponseByNotificationResponseBuilder()
                        .withPaymentResponse(paymentResponse)
                        .withTransactionCorrelationId(correlationId)
                        .build();

            }

        }

        return null;

    }

    /**
     *
     * @param notificationRequest
     * @return
     */
    private NotificationResponse processRequestWithTransactionStateChangedResponse(NotificationRequest notificationRequest) {

        // There is no amount in this request, the magic amount is send in the notificationRequest.content
        BigInteger amount = null;

        try {

            amount = new BigInteger(PluginUtils.inputStreamToString(notificationRequest.getContent()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        String partnerTransactionId = notificationRequest.getTransactionId();

        if (amount != null) {

            if (new BigInteger("40004").equals(amount)) {

                return TransactionStateChangedResponse.TransactionStateChangedResponseBuilder
                        .aTransactionStateChangedResponse()
                        .withPartnerTransactionId("UNKNOWN")
                        .withTransactionId(partnerTransactionId)
                        .withTransactionStatus(new SuccessTransactionStatus())
                        .withStatusDate(new Date())
                        .withHttpStatus(204)
                        .withAction(TransactionStateChangedResponse.Action.AUTHOR)
                        .withStatusDetails("Status details")
                        .build();

            }

            if (new BigInteger("40005").equals(amount)) {

                return TransactionStateChangedResponse.TransactionStateChangedResponseBuilder
                        .aTransactionStateChangedResponse()
                        .withPartnerTransactionId("UNKNOWN")
                        .withTransactionId(partnerTransactionId)
                        .withTransactionStatus(new FailureTransactionStatus(FailureCause.INTERNAL_ERROR))
                        .withStatusDate(new Date())
                        .withHttpStatus(204)
                        .build();

            }

        }

        return null;

    }

}