package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.exception.PluginException;
import com.payline.payment.sandbox.utils.Logger;
import com.payline.payment.sandbox.utils.PaymentResponseUtil;
import com.payline.payment.sandbox.utils.service.AbstractService;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.bean.field.SelectOption;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.bean.form.PartnerWidgetForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseProvided;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import com.payline.pmapi.service.PaymentFormConfigurationService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PaymentFormConfigurationServiceImpl extends AbstractService<PaymentFormConfigurationResponse> implements PaymentFormConfigurationService {

    private static final String GET_PAYMENT_FORM_CONFIGURATION = "getPaymentFormConfiguration";

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        this.verifyRequest(paymentFormConfigurationRequest);

        String amount = paymentFormConfigurationRequest.getAmount().getAmountInSmallestUnit().toString();

        /* Default case */
        NoFieldForm noFieldForm = NoFieldForm.NoFieldFormBuilder.aNoFieldForm()
                .withDisplayButton(true)
                .withButtonText("Button text")
                .withDescription("")
                .build();

        PaymentFormConfigurationResponseSpecific noFieldResponse = PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                .aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm( noFieldForm )
                .build();
        // The service tested by this request is not PaymentFormConfigurationService. So the plugin returns a NoField form.
        if( !amount.startsWith("3") ){
            return noFieldResponse;
        }

        switch( amount ){
            /* PaymentFormConfigurationResponseSpecific */
            case "30000":
                Logger.log(this.getClass().getSimpleName(),GET_PAYMENT_FORM_CONFIGURATION, amount, "PaymentFormConfigurationResponseSpecific NoField");
                return noFieldResponse;
            case "30001":
                Logger.log(this.getClass().getSimpleName(),GET_PAYMENT_FORM_CONFIGURATION, amount, "PaymentFormConfigurationResponseSpecific BankTransferForm");
                // retrieve the banks list from PluginConfiguration
                if( paymentFormConfigurationRequest.getPluginConfiguration() == null ){
                    throw new IllegalArgumentException("PaymentFormConfigurationRequest is missing a PluginConfiguration");
                }

                final List<SelectOption> banks = new ArrayList<>();



                for( String s : paymentFormConfigurationRequest.getPluginConfiguration().split("\\|") ){
                    String[] pieces = s.split(":");
                    if(pieces.length == 2){
                        banks.add(SelectOption.SelectOptionBuilder.aSelectOption()
                                .withKey(pieces[0])
                                .withValue(pieces[1])
                                .build());
                    }
                }

                // Build form
                CustomForm form = BankTransferForm.builder()
                        .withBanks( banks )
                        .withDescription( "Description" )
                        .withDisplayButton( true )
                        .withButtonText( "Payer" )
                        .withCustomFields( new ArrayList<>() )
                        .build();

                return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                        .aPaymentFormConfigurationResponseSpecific()
                        .withPaymentForm( form )
                        .build();

            case "30002":
                Logger.log(this.getClass().getSimpleName(), GET_PAYMENT_FORM_CONFIGURATION, amount, "PaymentFormConfigurationResponseSpecific avec un CustomForm complet");

                // Build form
                CustomForm customForm = PaymentResponseUtil.aCustomForm();

                return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                        .aPaymentFormConfigurationResponseSpecific()
                        .withPaymentForm(customForm)
                        .build();
            case "30003":
                Logger.log(this.getClass().getSimpleName(), GET_PAYMENT_FORM_CONFIGURATION, amount, "PaymentFormConfigurationResponseSpecific avec un PartnerWidgetForm complet");

                // Build form
                PartnerWidgetForm partnerWidgetForm = PaymentResponseUtil.aPartnerWidgetForm();

                return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                        .aPaymentFormConfigurationResponseSpecific()
                        .withPaymentForm(partnerWidgetForm)
                        .build();

            /* PaymentFormConfigurationResponseFailure */
            case "30100":
                Logger.log(this.getClass().getSimpleName(),GET_PAYMENT_FORM_CONFIGURATION, amount, "PaymentFormConfigurationResponseFailure avec failureCause (INVALID_DATA) &  errorCode (<= 50 caractères)");
                return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder.aPaymentFormConfigurationResponseFailure()
                        .withErrorCode("Error code less than 50 characters long")
                        .withFailureCause( FailureCause.INVALID_DATA )
                        .build();
            case "30101":
                Logger.log(this.getClass().getSimpleName(),GET_PAYMENT_FORM_CONFIGURATION, amount, "PaymentFormConfigurationResponseFailure avec failureCause (INVALID_DATA) &  errorCode (> 50 caractères)");
                return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder.aPaymentFormConfigurationResponseFailure()
                        .withErrorCode("This error code has not been truncated and is more than 50 characters long")
                        .withFailureCause( FailureCause.INVALID_DATA )
                        .build();
            case "30102":
                Logger.log(this.getClass().getSimpleName(),GET_PAYMENT_FORM_CONFIGURATION, amount, "PaymentFormConfigurationResponseFailure avec failureCause (INVALID_DATA) &  errorCode (<= 50 caractères) & partnerTransactionId");
                return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder.aPaymentFormConfigurationResponseFailure()
                        .withErrorCode("Error code less than 50 characters long")
                        .withFailureCause( FailureCause.INVALID_DATA )
                        .withPartnerTransactionId( PaymentResponseUtil.PARTNER_TRANSACTION_ID )
                        .build();

            /* PaymentFormConfigurationResponseProvided */
            case "30200":
                Logger.log(this.getClass().getSimpleName(),GET_PAYMENT_FORM_CONFIGURATION, amount, "PaymentFormConfigurationResponseProvided");
                return PaymentFormConfigurationResponseProvided.PaymentFormConfigurationResponseBuilder.aPaymentFormConfigurationResponse()
                        .withContextPaymentForm( new HashMap<>() )
                        .build();

            /* Generic plugin errors */
            default:
                return super.generic(this.getClass().getSimpleName(),GET_PAYMENT_FORM_CONFIGURATION, amount );
        }
    }

    @Override
    public PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {
        this.verifyRequest(paymentFormLogoRequest);

        return PaymentFormLogoResponseFile.PaymentFormLogoResponseFileBuilder.aPaymentFormLogoResponseFile()
                .withHeight( 58 )
                .withWidth( 156 )
                .withTitle("Sandbox APM")
                .withAlt("Sandbox APM logo")
                .build();
    }

    @Override
    public PaymentFormLogo getLogo(String s, Locale locale) {
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("payline_logo.png")) {
            if (input == null) {
                throw new PluginException("Plugin error: unable to load the logo file");
            }
                // Read logo file
                BufferedImage logo = ImageIO.read(input);

                // Recover byte array from image
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(logo, "png", baos);

                return PaymentFormLogo.PaymentFormLogoBuilder.aPaymentFormLogo()
                        .withFile(baos.toByteArray())
                        .withContentType("image/png")
                        .build();

        } catch (IOException e) {
            throw new PluginException("Plugin error: unable to load the logo file", e);
        }
    }

    /**
     * Performs standard verification of the request content.
     * Checks that every field required in the request is filled
     * @param paymentFormConfigurationRequest the request
     */
    private void verifyRequest(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        if( paymentFormConfigurationRequest.getAmount() == null ){
            throw new IllegalArgumentException( "The PaymentFormConfigurationRequest is missing an amount" );
        }
        if( paymentFormConfigurationRequest.getLocale() == null ){
            throw new IllegalArgumentException( "The PaymentFormConfigurationRequest is missing a locale" );
        }
    }

    /**
     * Performs standard verification of the request content.
     *      * Checks that every field required in the request is filled
     * @param paymentFormLogoRequest the request
     */
    private void verifyRequest(PaymentFormLogoRequest paymentFormLogoRequest) {
        if( paymentFormLogoRequest.getLocale() == null ){
            throw new IllegalArgumentException( "The PaymentFormLogoRequest is missing a locale" );
        }
    }

}
