package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.PaymentResponseUtil;
import com.payline.payment.sandbox.utils.service.AbstractService;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseActiveWaiting;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFormUpdated;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormField;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormInputFieldCheckbox;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormInputFieldSelect;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormInputFieldText;
import com.payline.pmapi.bean.paymentform.bean.field.specific.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentServiceImpl extends AbstractService<PaymentResponse> implements PaymentService {

    private CustomForm customForm;
    private boolean isPaymentFormUpdatedFilled = false;
    /**------------------------------------------------------------------------------------------------------------------*/
    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        this.verifyRequest(paymentRequest);

        String amount = paymentRequest.getAmount().getAmountInSmallestUnit().toString();

        /* PaymentResponseSuccess */
        if ("10000".equals(amount) || amount.matches("^[3-9][0-9]*$") || ("10300".equals(amount) && isPaymentFormUpdatedFilled)) {
            // TODO: manage the case where the amount is "103XX" and the form has been updated (PAYLAPMEXT-207)
            String transactionAdditionalData = new String();

            PaymentResponseSuccess.PaymentResponseSuccessBuilder builder = PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                    .withPartnerTransactionId(PaymentResponseUtil.PARTNER_TRANSACTION_ID)
                    .withTransactionDetails(new EmptyTransactionDetails());

            // If the payment form contains data, put them into transaction additional data
            if (paymentRequest.getPaymentFormContext() != null
                    && paymentRequest.getPaymentFormContext().getPaymentFormParameter() != null
                    && !paymentRequest.getPaymentFormContext().getPaymentFormParameter().isEmpty()) {

                transactionAdditionalData = paymentRequest.getPaymentFormContext().getPaymentFormParameter()
                        .entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining("&"));

                transactionAdditionalData = transactionAdditionalData + "&" + paymentRequest.getPaymentFormContext().getSensitivePaymentFormParameter()
                        .entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining("&"));
            }
            if ("10300".equals(amount) && isPaymentFormUpdatedFilled) {
                // TODO : Ajouter les éléments du PaymentFormUpdated aux transactionAdditionalData si l'on est dans le cas 10300 et que le formulaire est déjà rempli
                transactionAdditionalData = transactionAdditionalData + getformatedStringFromCustomFormData();
                System.out.println(transactionAdditionalData);
            }


            builder.withTransactionAdditionalData(transactionAdditionalData);

            return builder.build();
        }

        /* PaymentResponseFailure */
        if ("10100".equals(amount)) {
            return PaymentResponseUtil.failureClassic();
        }
        if ("10101".equals(amount)) {
            return PaymentResponseUtil.failureMinimal();
        }
        if ("10102".equals(amount)) {
            return PaymentResponseUtil.failureLongErrorCode();
        }
        if ("10103".equals(amount)) {
            return PaymentResponseUtil.failureWithPartnerTransactionId();
        }

        /* PaymentResponseRedirect */
        PaymentResponseRedirect.RedirectionRequest redirectionRequest = null;
        try {
            redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder.aRedirectionRequest()
                    .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                    .withUrl(new URL("https", "www.google.com", "/fr"))
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if ("10200".equals(amount) || amount.startsWith("2")) {
            return PaymentResponseRedirect.PaymentResponseRedirectBuilder.aPaymentResponseRedirect()
                    .withRedirectionRequest(redirectionRequest)
                    .withPartnerTransactionId(PaymentResponseUtil.PARTNER_TRANSACTION_ID)
                    .build();
        }
        if ("10201".equals(amount)) {
            Map<String, String> context = new HashMap<>();
            context.put("key", "value");
            return PaymentResponseRedirect.PaymentResponseRedirectBuilder.aPaymentResponseRedirect()
                    .withRedirectionRequest(redirectionRequest)
                    .withPartnerTransactionId(PaymentResponseUtil.PARTNER_TRANSACTION_ID)
                    .withStatusCode("PARTNER_STATUS")
                    .withRequestContext(
                            RequestContext.RequestContextBuilder.aRequestContext()
                                    .withRequestData(context)
                                    .build()
                    )
                    .build();
        }

        /* PaymentResponseFormUpdated */
        // TODO : PAYLAPMEXT-207
        if ("10300".equals(amount)) {
            try {
                this.customForm = PaymentResponseUtil.aCustomForm();
                this.isPaymentFormUpdatedFilled = true;

                // Create the PaymentFormConfigurationResponse
                PaymentFormConfigurationResponse paymentFormConfigurationResponse = PaymentFormConfigurationResponseSpecific
                        .PaymentFormConfigurationResponseSpecificBuilder
                        .aPaymentFormConfigurationResponseSpecific()
                        .withPaymentForm(customForm)
                        .build();

                Map<String, String> context = new HashMap<>();
                context.put("key13", "value");

                // Return the PaymentResponseFormUpdated
                return PaymentResponseFormUpdated.PaymentResponseFormUpdatedBuilder
                        .aPaymentResponseFormUpdated()
                        .withPaymentFormConfigurationResponse(paymentFormConfigurationResponse)
                        .withRequestContext(RequestContext.RequestContextBuilder.aRequestContext()
                                .withRequestData(context)
                                .build())
                        .build();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            //

        }


        /* PaymentResponseDoPayment */
        if ("10400".equals(amount)) {
            return PaymentResponseUtil.doPaymentMinimal();
        }

        /* PaymentResponseActiveWaiting */
        if ("10500".equals(amount)) {
            return new PaymentResponseActiveWaiting();
        }

        /* Generic plugin behaviours */
        return super.generic(amount);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Amount, etc.)
     *
     * @param paymentRequest
     */
    private void verifyRequest(PaymentRequest paymentRequest) {
        if (paymentRequest.getContractConfiguration() == null
                || paymentRequest.getPartnerConfiguration() == null
                || paymentRequest.getAmount() == null) {
            throw new IllegalArgumentException("The PaymentRequest is missing required data");
        }
    }

    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * @param paymentRequest
     * @return
     */
    // TODO: remove (lors du traitement du ticket PAYLAPMEXT-207)
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
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Return a formated string with the customForm data
     * @return
     */
    private String getformatedStringFromCustomFormData() {

        String transactionAdditionalData = new String();

        for (PaymentFormField paymentFormField : this.customForm.getCustomFields()) {

            if (!transactionAdditionalData.isEmpty()) {
                transactionAdditionalData = transactionAdditionalData + "&";
            }

            // PaymentFormInputFieldAmount
            if (paymentFormField instanceof PaymentFormInputFieldAmount) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldAmount) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldAmount) paymentFormField).getValue();
            }

            // PaymentFormInputFieldBic
            if (paymentFormField instanceof PaymentFormInputFieldBic) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldBic) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldBic) paymentFormField).getLabel();
            }
            // PaymentFormInputFieldBirthdate
            if (paymentFormField instanceof PaymentFormInputFieldBirthdate) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldBirthdate) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldBirthdate) paymentFormField).getLabel();
            }
            // PaymentFormInputFieldCardNumber
            if (paymentFormField instanceof PaymentFormInputFieldCardNumber) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldCardNumber) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldCardNumber) paymentFormField).getLabel();
            }
            // PaymentFormInputFieldCardholder
            if (paymentFormField instanceof PaymentFormInputFieldCardholder) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldCardholder) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldCardholder) paymentFormField).getLabel();
            }
            // PaymentFormInputFieldCheckbox
            if (paymentFormField instanceof PaymentFormInputFieldCheckbox) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldCheckbox) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldCheckbox) paymentFormField).getLabel();
            }
            // PaymentFormInputFieldCvx
            if (paymentFormField instanceof PaymentFormInputFieldCvx) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldCvx) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldCvx) paymentFormField).getLabel();
            }
            // PaymentFormInputFieldExpirationDate
            if (paymentFormField instanceof PaymentFormInputFieldExpirationDate) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldExpirationDate) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldExpirationDate) paymentFormField).getLabel();
            }
            // PaymentFormInputFieldIban
            if (paymentFormField instanceof PaymentFormInputFieldIban) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldIban) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldIban) paymentFormField).getLabel();
            }
            // PaymentFormInputFieldSelect
            if (paymentFormField instanceof PaymentFormInputFieldSelect) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldSelect) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldSelect) paymentFormField).getLabel();
            }
            // PaymentFormInputFieldText
            if (paymentFormField instanceof PaymentFormInputFieldText) {
                transactionAdditionalData = transactionAdditionalData
                        + ((PaymentFormInputFieldText) paymentFormField).getKey()
                        + "="
                        + ((PaymentFormInputFieldText) paymentFormField).getValue();
            }

        }
        return transactionAdditionalData;
    }
    /**------------------------------------------------------------------------------------------------------------------*/

}
