package com.payline.payment.sandbox.service.impl;

import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.service.NotificationService;

public class NotificationServiceImpl extends AbstractService implements NotificationService {

    @Override
    public NotificationResponse parse(NotificationRequest notificationRequest) {
        this.verifyRequest(notificationRequest);
        // FIXME : No amount in request
        return null;
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

}