package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.MagicAmountEnumValue;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.Message;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentData3DS;
import com.payline.pmapi.bean.payment.response.PaymentModeCard;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.Card;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.Email;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseDoPayment;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.PaymentWithRedirectionService;

import java.math.BigInteger;
import java.time.YearMonth;

public class PaymentWithRedirectionServiceImpl extends AbstractService implements PaymentWithRedirectionService {

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        this.verifyRequest(redirectionPaymentRequest);
        return this.processRequest(redirectionPaymentRequest);
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        this.verifyRequest(transactionStatusRequest);
        return this.processRequest(transactionStatusRequest);
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to finalize a transaction after a redirection is filled in the request.
     *
     * @param redirectionPaymentRequest the request to verify
     */
    private void verifyRequest(RedirectionPaymentRequest redirectionPaymentRequest){
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to recover the state of a transaction when user session has expired is filled in the request.
     * (ex: transactionId)
     * @param redirectionPaymentRequest the request to verify
     */
    private void verifyRequest(TransactionStatusRequest redirectionPaymentRequest){
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

    /**
     *
     * @return
     */
    private PaymentResponse processRequest(Object request) {

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        PaymentResponse paymentResponse = null;
        BigInteger amount = null;

        if (request instanceof RedirectionPaymentRequest) {
            amount = ((RedirectionPaymentRequest)request).getAmount().getAmountInSmallestUnit();
        }

        if (request instanceof TransactionStatusRequest) {
            amount = ((TransactionStatusRequest)request).getAmount().getAmountInSmallestUnit();
        }

        // PAYMENT RESPONSE ON HOLD
        if ("PAYMENT_RESPONSE_ON_HOLD".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseOnHold(request);
        }

        // PAYMENT RESPONSE DO PAYMENT
        if ("PAYMENT_RESPONSE_DO_PAYMENT".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseDoPayment(request);
        }

        // PAYMENT RESPONSE SUCCESS
        if ("PAYMENT_RESPONSE_SUCCESS".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseSuccess(request);
        }

        // PAYMENT RESPONSE FAILURE
        if ("PAYMENT_RESPONSE_FAILURE".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseFailure(request);
        }

        // PAYMENT RESPONSE GENERIC ERROR
        if ("PAYMENT_RESPONSE_GENERIC_ERROR".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = (PaymentResponse) processRequestWithResponseGenericError(request);
        }

        return paymentResponse;

    }

    /**
     *
     * @param request
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseOnHold(Object request) {

        BigInteger amount = null;
        String partnerTransactionId = null;

        if (request instanceof RedirectionPaymentRequest) {
            amount = ((RedirectionPaymentRequest)request).getAmount().getAmountInSmallestUnit();
            partnerTransactionId = ((RedirectionPaymentRequest)request).getTransactionId();
        }

        if (request instanceof TransactionStatusRequest) {
            amount = ((TransactionStatusRequest)request).getAmount().getAmountInSmallestUnit();
            partnerTransactionId = ((TransactionStatusRequest)request).getTransactionId();
        }

        String status = "SUCCESS";

        if (new BigInteger("20000").equals(amount) || new BigInteger("20100").equals(amount)) {

            return PaymentResponseOnHold.PaymentResponseOnHoldBuilder
                    .aPaymentResponseOnHold()
                    .withOnHoldCause(OnHoldCause.ASYNC_RETRY)
                    .withBuyerPaymentId(new EmptyTransactionDetails())
                    .withPartnerTransactionId(partnerTransactionId)
                    .withStatusCode(status)
                    .build();

        }

        if (new BigInteger("20001").equals(amount) ||new BigInteger("20101").equals(amount) ) {

            return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                    .withStatusCode(status)
                    .build();

        }

        if (new BigInteger("20002").equals(amount)) {

            return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                    .build();

        }

        return null;

    }

    /**
     *
     * @param request
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseDoPayment(Object request) {

        BigInteger amount = null;
        String partnerTransactionId = null;

        if (request instanceof RedirectionPaymentRequest) {
            amount = ((RedirectionPaymentRequest)request).getAmount().getAmountInSmallestUnit();
            partnerTransactionId = ((RedirectionPaymentRequest)request).getTransactionId();
        }

        if (request instanceof TransactionStatusRequest) {
            amount = ((TransactionStatusRequest)request).getAmount().getAmountInSmallestUnit();
            partnerTransactionId = ((TransactionStatusRequest)request).getTransactionId();
        }

        if (new BigInteger("20003").equals(amount)) {

            Card card = Card.CardBuilder.aCard()
                    .withPan("pan")
                    .withHolder("")
                    .withExpirationDate(YearMonth.of(20, 01))
                    .withPanType(Card.PanType.TOKEN_PAN)
                    .build();

            PaymentData3DS paymentData3DS = PaymentData3DS.Data3DSBuilder.aData3DS()
                    .withEci("eci")
                    .withCavv("cavv")
                    .build();

            PaymentModeCard paymentModeCard = PaymentModeCard.PaymentModeCardBuilder.aPaymentModeCard()
                    .withCard(card)
                    .withPaymentDatas3DS(paymentData3DS)
                    .build();

            return PaymentResponseDoPayment.PaymentResponseDoPaymentBuilder.aPaymentResponseDoPayment()
                    .withPaymentMode(paymentModeCard)
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        }

        return null;

    }

    /**
     *
     * @param request
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseSuccess(Object request) {

        BigInteger amount = null;
        String partnerTransactionId = null;

        if (request instanceof RedirectionPaymentRequest) {
            amount = ((RedirectionPaymentRequest)request).getAmount().getAmountInSmallestUnit();
            partnerTransactionId = ((RedirectionPaymentRequest)request).getTransactionId();
        }

        if (request instanceof TransactionStatusRequest) {
            amount = ((TransactionStatusRequest)request).getAmount().getAmountInSmallestUnit();
            partnerTransactionId = ((TransactionStatusRequest)request).getTransactionId();
        }

        String statusCode = "200";

        if (new BigInteger("20004").equals(amount) || new BigInteger("20104").equals(amount)) {

            return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                    .withStatusCode(statusCode)
                    .withTransactionDetails(new EmptyTransactionDetails())
                    .withPartnerTransactionId(partnerTransactionId)
                    .withMessage(new Message(Message.MessageType.SUCCESS, "Transaction acceptee"))
                    .withTransactionAdditionalData("transactionAdditionalData")
                    .build();

        }

        if (new BigInteger("20005").equals(amount) || new BigInteger("20105").equals(amount)) {

            return PaymentResponseSuccess.PaymentResponseSuccessBuilder
                    .aPaymentResponseSuccess()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withStatusCode(statusCode)
                    .withTransactionAdditionalData("transactionAdditionalData")
                    .withTransactionDetails(new EmptyTransactionDetails())
                    .build();

        }

        if (new BigInteger("20006").equals(amount) || new BigInteger("20106").equals(amount)) {

            return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                    .withTransactionAdditionalData("transactionAdditionalData")
                    .withMessage(new Message(Message.MessageType.SUCCESS, "Transaction acceptee"))
                    .withPartnerTransactionId(partnerTransactionId)
                    .withTransactionDetails(new EmptyTransactionDetails())
                    .build();

        }

        if (new BigInteger("20007").equals(amount) || new BigInteger("20107").equals(amount)) {

            return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withStatusCode(statusCode)
                    .withTransactionDetails(Email.EmailBuilder.anEmail().withEmail("myemail@yopmail.fr").build())
                    .build();

        }

        return null;

    }

    /**
     *
     * @param request
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseFailure(Object request) {

        BigInteger amount = null;
        String partnerTransactionId = null;

        if (request instanceof RedirectionPaymentRequest) {
            amount = ((RedirectionPaymentRequest)request).getAmount().getAmountInSmallestUnit();
            partnerTransactionId = ((RedirectionPaymentRequest)request).getTransactionId();
        }

        if (request instanceof TransactionStatusRequest) {
            amount = ((TransactionStatusRequest)request).getAmount().getAmountInSmallestUnit();
            partnerTransactionId = ((TransactionStatusRequest)request).getTransactionId();
        }

        if (new BigInteger("20090").equals(amount) || new BigInteger("20190").equals(amount)) {

            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode("CANCELLED")
                    .withFailureCause(FailureCause.CANCEL)
                    .build();

        }

        if (new BigInteger("20091").equals(amount)) {

            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withFailureCause(FailureCause.INVALID_FIELD_FORMAT)
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        }

        if (new BigInteger("20092").equals(amount) || new BigInteger("20192").equals(amount)) {

            return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withFailureCause(FailureCause.COMMUNICATION_ERROR)
                    .withErrorCode("Invalid request")
                    .build();

        }

        return null;

    }

}
