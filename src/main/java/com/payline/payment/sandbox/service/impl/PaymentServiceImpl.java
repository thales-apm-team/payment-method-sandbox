package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.MagicAmountEnumValue;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.Message;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentData3DS;
import com.payline.pmapi.bean.payment.response.PaymentModeCard;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.BankAccount;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.Card;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.BankTransfer;
import com.payline.pmapi.bean.payment.response.impl.*;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormField;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.bean.form.PartnerWidgetForm;
import com.payline.pmapi.bean.paymentform.bean.form.partnerwidget.PartnerWidgetOnPay;
import com.payline.pmapi.bean.paymentform.bean.form.partnerwidget.PartnerWidgetOnPayCallBack;
import com.payline.pmapi.bean.paymentform.bean.form.partnerwidget.PartnerWidgetScriptImport;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.service.PaymentService;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.payline.pmapi.bean.common.Message.MessageType.SUCCESS;

public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        this.verifyRequest( paymentRequest );
        return this.processRequest(paymentRequest);
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Order, etc.)
     * @param paymentRequest
     */
    private void verifyRequest( PaymentRequest paymentRequest){
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

    /**
     *
     */
    private PaymentResponse processRequest(PaymentRequest paymentRequest) {

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        PaymentResponse paymentResponse = null;

        BigInteger amount = paymentRequest.getAmount().getAmountInSmallestUnit();

        // PAYMENT RESPONSE REDIRECT
        if ("PAYMENT_RESPONSE_REDIRECT".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseRedirect(paymentRequest);
        }


        // PAYMENT RESPONSE FORM UPDATED
        if ("PAYMENT_RESPONSE_FORM_UPDATED".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseFormUpdated(paymentRequest);
        }

        // PAYMENT RESPONSE DO PAYMENT
        if ("PAYMENT_RESPONSE_DO_PAYMENT".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseDoPayment(paymentRequest);
        }

        // PAYMENT RESPONSE ACTIVE WAITING
        if ("PAYMENT_RESPONSE_ACTIVE_WAITING".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseActiveWaiting(paymentRequest);
        }

        // PAYMENT RESPONSE SUCCESS
        if ("PAYMENT_RESPONSE_SUCCESS".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseSuccess(paymentRequest);
        }

        // PAYMENT RESPONSE FAILURE
        if ("PAYMENT_RESPONSE_FAILURE".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseFailure(paymentRequest);
        }

        // GENERIC PLUGIN ERROR CASE
        if ("PAYMENT_RESPONSE_GENERIC_ERROR".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseGenericError(paymentRequest);
        }

        return paymentResponse;

    }

    /**
     *
     * @param paymentRequest
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseRedirect(PaymentRequest paymentRequest) {

        BigInteger amount = paymentRequest.getAmount().getAmountInSmallestUnit();

        PaymentResponseRedirect.RedirectionRequest redirectionRequest = null;
        String partnerTransactionId = paymentRequest.getTransactionId();
        String statusCode = "200";
        RequestContext requestContext = null;

        if (new BigInteger("10000").equals(amount)) {

            try {

                redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                        .aRedirectionRequest()
                        .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                        .withUrl(new URL("https", "www.google.com", "/fr"))
                        .build();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return PaymentResponseRedirect.PaymentResponseRedirectBuilder
                    .aPaymentResponseRedirect()
                    .withRedirectionRequest(redirectionRequest)
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        }

        if (new BigInteger("10001").equals(amount)) {

            try {

                redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                        .aRedirectionRequest()
                        .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                        .withUrl(new URL("https", "www.google.com", "/fr"))
                        .build();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return PaymentResponseRedirect.PaymentResponseRedirectBuilder
                    .aPaymentResponseRedirect()
                    .withRedirectionRequest(redirectionRequest)
                    .withPartnerTransactionId(partnerTransactionId)
                    .withStatusCode(statusCode)
                    .build();

        }

        if (new BigInteger("10002").equals(amount)) {

            try {

                redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                        .aRedirectionRequest()
                        .withUrl(new URL("https", "www.google.com", "/fr"))
                        .build();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Map<String, String> context = new HashMap<>();
            // TODO : Add data to the map if necessary

            requestContext = RequestContext.RequestContextBuilder.aRequestContext()
                    .withRequestData(context)
                    .build();

            return PaymentResponseRedirect.PaymentResponseRedirectBuilder.aPaymentResponseRedirect()
                    .withRedirectionRequest(redirectionRequest)
                    .withPartnerTransactionId(partnerTransactionId)
                    .withStatusCode(statusCode)
                    .withRequestContext(requestContext)
                    .build();

        }

        return null;

    }

    /**
     *
     * @param paymentRequest
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseFormUpdated(PaymentRequest paymentRequest) {

        BigInteger amount = paymentRequest.getAmount().getAmountInSmallestUnit();

        PaymentFormConfigurationResponse paymentFormConfigurationResponse = null;

        if (new BigInteger("10003").equals(amount)) {

            PartnerWidgetScriptImport scriptImport = null;

            try {

                scriptImport = PartnerWidgetScriptImport.WidgetPartnerScriptImportBuilder
                        .aWidgetPartnerScriptImport()
                        .withUrl(new URL("https://www.google.com"))
                        .withCache(true)
                        .withAsync(true)
                        .build();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            PartnerWidgetOnPay onPay = PartnerWidgetOnPayCallBack.WidgetContainerOnPayCallBackBuilder
                    .aWidgetContainerOnPayCallBack()
                    .withName("notUsedButMandatory")
                    .build();

            PartnerWidgetForm paymentForm = PartnerWidgetForm.WidgetPartnerFormBuilder.aWidgetPartnerForm()
                    .withDescription("")
                    .withScriptImport(scriptImport)
                    .withOnPay(onPay)
                    .build();

            paymentFormConfigurationResponse = PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                    .aPaymentFormConfigurationResponseSpecific()
                    .withPaymentForm(paymentForm)
                    .build();

            return PaymentResponseFormUpdated.PaymentResponseFormUpdatedBuilder
                    .aPaymentResponseFormUpdated()
                    .withPaymentFormConfigurationResponse(paymentFormConfigurationResponse)
                    .build();

        }

        if (new BigInteger("10004").equals(amount)) {

            List<PaymentFormField> customFields = new ArrayList<>();

            CustomForm customForm = CustomForm.builder()
                    .withDescription("")
                    .withCustomFields(customFields)
                    .withButtonText("Button")
                    .withDisplayButton(true)
                    .build();

            paymentFormConfigurationResponse = PaymentFormConfigurationResponseSpecific
                    .PaymentFormConfigurationResponseSpecificBuilder
                    .aPaymentFormConfigurationResponseSpecific()
                    .withPaymentForm(customForm)
                    .build();

            Map<String, String> requestContextMap = new HashMap<>();
            Map<String, String> requestSensitiveContext = paymentRequest.getRequestContext().getSensitiveRequestData();

            RequestContext requestContext = RequestContext
                    .RequestContextBuilder
                    .aRequestContext()
                    .withRequestData(requestContextMap)
                    .withSensitiveRequestData(requestSensitiveContext)
                    .build();

            return PaymentResponseFormUpdated.PaymentResponseFormUpdatedBuilder
                    .aPaymentResponseFormUpdated()
                    .withPaymentFormConfigurationResponse(paymentFormConfigurationResponse)
                    .withRequestContext(requestContext)
                    .build();

        }

        return null;

    }

    /**
     *
     * @param paymentRequest
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseDoPayment(PaymentRequest paymentRequest) {

        BigInteger amount = paymentRequest.getAmount().getAmountInSmallestUnit();

        String partnerTransactionId = paymentRequest.getTransactionId();

        if (new BigInteger("10005").equals(amount)) {

            Card card = Card.CardBuilder.aCard()
                    .withBrand("brand")
                    .withHolder("holder")
                    .withExpirationDate(YearMonth.of(20, 01))
                    .withPan("pan")
                    .withPanType(Card.PanType.CARD_PAN)
                    .build();

            PaymentData3DS paymentData3DS = PaymentData3DS.Data3DSBuilder.aData3DS()
                    .withCavv("cavv")
                    .withEci("eci")
                    .build();

            PaymentModeCard paymentModeCard = PaymentModeCard.PaymentModeCardBuilder.aPaymentModeCard()
                    .withPaymentDatas3DS(paymentData3DS)
                    .withCard(card)
                    .build();

            return PaymentResponseDoPayment.PaymentResponseDoPaymentBuilder
                    .aPaymentResponseDoPayment()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withPaymentMode(paymentModeCard)
                    .build();

        }

        return null;

    }

    /**
     *
     * @param paymentRequest
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseActiveWaiting(PaymentRequest paymentRequest) {

        BigInteger amount = paymentRequest.getAmount().getAmountInSmallestUnit();


        if (new BigInteger("10006").equals(amount)) {

            return new PaymentResponseActiveWaiting();

        }

        return null;

    }

    /**
     *
     * @param paymentRequest
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseSuccess(PaymentRequest paymentRequest) {

        BigInteger amount = paymentRequest.getAmount().getAmountInSmallestUnit();

        String partnerTransactionId = paymentRequest.getTransactionId();

        if (new BigInteger("10007").equals(amount)) {

            BankAccount owner = BankAccount.BankAccountBuilder.aBankAccount()
                    .withAccountNumber("")
                    .withBankCode("")
                    .withBankName("")
                    .withBic("bic")
                    .withCountryCode("FR")
                    .withHolder( "" )
                    .withIban("iban")
                    .build();

            BankTransfer transactionDetails = new BankTransfer( owner, null );

            return PaymentResponseSuccess.PaymentResponseSuccessBuilder
                    .aPaymentResponseSuccess()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withTransactionAdditionalData("transactionAdditionalData")
                    .withStatusCode("status")
                    .withMessage(new Message(SUCCESS, "Transaction acceptee"))
                    .withTransactionDetails(transactionDetails)
                    .build();

        }

        return null;

    }

    /**
     *
     * @param paymentRequest
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseFailure(PaymentRequest paymentRequest) {

        BigInteger amount = paymentRequest.getAmount().getAmountInSmallestUnit();

        String partnerTransactionId = paymentRequest.getTransactionId();

        if (new BigInteger("10090").equals(amount)) {

            return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .withErrorCode("no code transmitted")
                    .build();

        }

        if (new BigInteger("10091").equals(amount)) {

            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode("error message")
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();

        }

        return null;

    }

    /**
     *
     * @param paymentRequest
     * @return
     */
    private PaymentResponse processRequestWithPaymentResponseGenericError(PaymentRequest paymentRequest) {

        BigInteger amount = paymentRequest.getAmount().getAmountInSmallestUnit();

        if (new BigInteger("10098").equals(amount)) {
            // Return null
            // Nothing to do, the response object is already initialized to null
        }

        if (new BigInteger("10099").equals(amount)) {
            // Return exception
            throw new NullPointerException();
        }

        return null;

    }

}
