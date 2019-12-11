package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.MagicAmountEnumValue;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.service.PaymentFormConfigurationService;

import java.math.BigInteger;
import java.util.Locale;

public class PaymentFormConfigurationServiceImpl extends AbstractService implements PaymentFormConfigurationService {

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        this.verifyRequest(paymentFormConfigurationRequest);
        return this.processRequest(paymentFormConfigurationRequest);
    }

    @Override
    public PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {
        this.verifyRequest(paymentFormLogoRequest);
        // FIXME : No amount in request
        return null;
    }

    @Override
    public PaymentFormLogo getLogo(String s, Locale locale) {
        return null;
    }

    @Override
    public PaymentFormLogo getSchemeLogo(String schemeName) {
        return null;
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Order, etc.)
     * @param paymentFormConfigurationRequest
     */
    private void verifyRequest(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required to process to a payment is filled in the request
     * (ex: PartnerConfiguration, Order, etc.)
     * @param paymentFormLogoRequest
     */
    private void verifyRequest(PaymentFormLogoRequest paymentFormLogoRequest) {
        // TODO ! (if any required attribute is missing, throw a RuntimeException)
    }

    /**
     *
     * @param paymentFormConfigurationRequest
     * @return
     */
    private PaymentFormConfigurationResponse processRequest(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {

        // TODO: given the paymentRequest.amount (magic amount), return the corresponding response

        PaymentFormConfigurationResponse paymentResponse = null;

        BigInteger amount = paymentFormConfigurationRequest.getAmount().getAmountInSmallestUnit();

        // PAYMENT FORM CONFIGURATION RESPONSE SPECIFIC
        if ("PAYMENT_FORM_CONFIGURATION_RESPONSE_SPECIFIC".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentFormConfigurationResponse(paymentFormConfigurationRequest);
        }

        // PAYMENT RESPONSE FAILURE
        if ("PAYMENT_RESPONSE_FAILURE".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = processRequestWithPaymentResponseFailure(paymentFormConfigurationRequest);
        }

        // GENERIC PLUGIN ERROR CASE
        if ("PAYMENT_RESPONSE_GENERIC_ERROR".equals(MagicAmountEnumValue.fromAmountValue(amount).getResponse())) {
            paymentResponse = (PaymentFormConfigurationResponse) processRequestWithResponseGenericError(paymentFormConfigurationRequest);
        }

        return paymentResponse;

    }

    /**
     *
     * @param paymentFormConfigurationRequest
     * @return
     */
    private PaymentFormConfigurationResponse processRequestWithPaymentFormConfigurationResponse(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {

        BigInteger amount = paymentFormConfigurationRequest.getAmount().getAmountInSmallestUnit();

        if (new BigInteger("30000").equals(amount)) {

            NoFieldForm noFieldForm = NoFieldForm
                    .NoFieldFormBuilder
                    .aNoFieldForm()
                    .withDisplayButton(true)
                    .withButtonText("Button text")
                    .withDescription("")
                    .build();

            return PaymentFormConfigurationResponseSpecific
                    .PaymentFormConfigurationResponseSpecificBuilder
                    .aPaymentFormConfigurationResponseSpecific()
                    .withPaymentForm(noFieldForm)
                    .build();

        }

        if (new BigInteger("30001").equals(amount)) {

            return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder.aPaymentFormConfigurationResponseSpecific().withPaymentForm(NoFieldForm.NoFieldFormBuilder.aNoFieldForm()
                    .withButtonText("Button text")
                    .withDescription("Description")
                    .withDisplayButton(true)
                    .build()).build();

        }

        return null;

    }

    /**
     *
     * @param paymentFormConfigurationRequest
     * @return
     */
    private PaymentFormConfigurationResponse processRequestWithPaymentResponseFailure(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {

        BigInteger amount = paymentFormConfigurationRequest.getAmount().getAmountInSmallestUnit();

        if (new BigInteger("30090").equals(amount)) {

            return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder
                    .aPaymentFormConfigurationResponseFailure()
                    .withPartnerTransactionId("NO TRANSACTION YET")
                    .withErrorCode("Unable to read js file")
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();

        }

        if (new BigInteger("30091").equals(amount)) {

            return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder
                    .aPaymentFormConfigurationResponseFailure()
                    .withErrorCode("Plugin error")
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();

        }

        return null;

    }

}
