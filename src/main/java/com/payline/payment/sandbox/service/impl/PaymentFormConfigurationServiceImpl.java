package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.utils.PaymentResponseUtil;
import com.payline.payment.sandbox.utils.service.AbstractService;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.bean.field.SelectOption;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
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
                return noFieldResponse;
            case "30001":
                // retrieve the banks list from PluginConfiguration
                if( paymentFormConfigurationRequest.getPluginConfiguration() == null ){
                    throw new IllegalArgumentException("PaymentFormConfigurationRequest is missing a PluginConfiguration");
                }
                final List<SelectOption> banks = new ArrayList<>();
                for( String s : paymentFormConfigurationRequest.getPluginConfiguration().split("\\|") ){
                    String[] pieces = s.split(":");
                    banks.add( SelectOption.SelectOptionBuilder.aSelectOption()
                            .withKey( pieces[0] )
                            .withValue( pieces[1] )
                            .build() );
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
                // TODO: exhaustive CustomForm which includes all the possible fields ! (PAYLAPMEXT-209)
                return noFieldResponse;

            /* PaymentFormConfigurationResponseFailure */
            case "30100":
                return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder.aPaymentFormConfigurationResponseFailure()
                        .withErrorCode("Error code less than 50 characters long")
                        .withFailureCause( FailureCause.INVALID_DATA )
                        .build();
            case "30101":
                return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder.aPaymentFormConfigurationResponseFailure()
                        .withErrorCode("This error code has not been truncated and is more than 50 characters long")
                        .withFailureCause( FailureCause.INVALID_DATA )
                        .build();
            case "30102":
                return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder.aPaymentFormConfigurationResponseFailure()
                        .withErrorCode("Error code less than 50 characters long")
                        .withFailureCause( FailureCause.INVALID_DATA )
                        .withPartnerTransactionId( PaymentResponseUtil.PARTNER_TRANSACTION_ID )
                        .build();

            /* PaymentFormConfigurationResponseProvided */
            case "30200":
                return PaymentFormConfigurationResponseProvided.PaymentFormConfigurationResponseBuilder.aPaymentFormConfigurationResponse()
                        .withContextPaymentForm( new HashMap<>() )
                        .build();

            /* Generic plugin errors */
            default:
                return super.generic( amount );
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
        InputStream input = this.getClass().getClassLoader().getResourceAsStream( "payline_logo.png" );
        if (input == null) {
            throw new RuntimeException("Plugin error: unable to load the logo file");
        }
        try {
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
            throw new RuntimeException("Plugin error: unable to read the logo", e);
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
