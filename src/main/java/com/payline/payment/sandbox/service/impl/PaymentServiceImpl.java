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
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
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

public class PaymentServiceImpl extends AbstractService<PaymentResponse> implements PaymentService {

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        this.verifyRequest(paymentRequest);

        String amount = paymentRequest.getAmount().getAmountInSmallestUnit().toString();
        String partnerTransactionId = "PARTNER_ID.0123456789";

        /* PaymentResponseSuccess */
        if( "10000".equals( amount )
                || amount.matches("^[3-9][0-9]*$") ){
            // TODO: manage the case where the amount is "103XX" and the form has been updated
            PaymentResponseSuccess.PaymentResponseSuccessBuilder builder = PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                    .withPartnerTransactionId( partnerTransactionId )
                    .withTransactionDetails( new EmptyTransactionDetails() );

            // TODO: if paymentRequest.getPaymentFormContext().getPaymentFormParameter() contains entries,
            //  put them into TransactionAdditionalData

            return builder.build();
        }

        /* PaymentResponseFailure */
        if( "10100".equals( amount ) ){
            return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withFailureCause( FailureCause.INVALID_DATA )
                    .withErrorCode("Error code less than 50 characters long")
                    .build();
        }
        if( "10101".equals( amount ) ){
            return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withFailureCause( FailureCause.INVALID_DATA )
                    .build();
        }
        if( "10102".equals( amount ) ){
            return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withFailureCause( FailureCause.INVALID_DATA )
                    .withErrorCode("This error code has not been truncated and is more than 50 characters long")
                    .build();
        }
        if( "10103".equals( amount ) ){
            return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withFailureCause( FailureCause.INVALID_DATA )
                    .withErrorCode("Error code less than 50 characters long")
                    .withPartnerTransactionId( partnerTransactionId )
                    .build();
        }

        /* PaymentResponseRedirect */
        PaymentResponseRedirect.RedirectionRequest redirectionRequest = null;
        try {
            redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder.aRedirectionRequest()
                    .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                    .withUrl( new URL("https", "www.google.com", "/fr") )
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if( "10200".equals( amount ) || amount.startsWith("2") ){
            return PaymentResponseRedirect.PaymentResponseRedirectBuilder.aPaymentResponseRedirect()
                    .withRedirectionRequest( redirectionRequest )
                    .withPartnerTransactionId( partnerTransactionId )
                    .build();
        }
        if( "10201".equals( amount ) ){
            Map<String, String> context = new HashMap<>();
            context.put("key", "value");
            return PaymentResponseRedirect.PaymentResponseRedirectBuilder.aPaymentResponseRedirect()
                    .withRedirectionRequest( redirectionRequest )
                    .withPartnerTransactionId( partnerTransactionId )
                    .withStatusCode("PARTNER_STATUS")
                    .withRequestContext(
                            RequestContext.RequestContextBuilder.aRequestContext()
                            .withRequestData(context)
                            .build()
                    )
                    .build();
        }

        /* PaymentResponseFormUpdated */
        // TODO !

        /* PaymentResponseDoPayment */
        if( "10400".equals( amount ) ){
            Card card = Card.CardBuilder.aCard()
                    .withBrand("brand")
                    .withHolder("holder")
                    .withExpirationDate(YearMonth.of(21, 01))
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

        /* PaymentResponseActiveWaiting */
        if( "10500".equals( amount ) ){
            return new PaymentResponseActiveWaiting();
        }

        /* Generic plugin behaviours */
        return super.generic( amount );
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Amount, etc.)
     * @param paymentRequest
     */
    private void verifyRequest(PaymentRequest paymentRequest) {
        if( paymentRequest.getContractConfiguration() == null
                || paymentRequest.getPartnerConfiguration() == null
                || paymentRequest.getAmount() == null ){
            throw new IllegalArgumentException( "The PaymentRequest is missing required data" );
        }
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

}
