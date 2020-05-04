package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.MockUtils;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.paymentform.bean.form.BankTransferForm;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.bean.form.PartnerWidgetForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseProvided;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;



class  PaymentFormConfigurationServiceImplTest {

    private PaymentFormConfigurationServiceImpl service = new PaymentFormConfigurationServiceImpl();

    @Mock
    private PaymentFormConfigurationRequest mockRequest;
    @Mock
    private PaymentFormLogoRequest mockLogoRequest;

    @Mock
    private PaymentFormConfigurationServiceImpl spiedClient;

    @BeforeEach
    void setup() {
        mockRequest = MockUtils.aPaymentFormConfigurationRequestBuilder()
                .withAmount(
                        new Amount(new BigInteger("30000"), Currency.getInstance("EUR"))
                )
                .build();

        mockLogoRequest = MockUtils.aPaymentFormLogoRequest();

        spiedClient = Mockito.spy( service );

        MockitoAnnotations.initMocks(this);
    }

    /**
     * This test case ensures that, when the tested service is not PaymentFormConfigurationService,
     * the methods returns a NoFieldForm
     */
    @ParameterizedTest(name = "[{index}] first digit: {0}")
    @ValueSource(strings = {"1", "2", "4", "5", "6", "7", "8" , "9"})
    void getPaymentFormConfiguration_otherServiceTested( int firstDigit ){
        // given: the request containing the magic amount
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequestBuilder()
                .withAmount(
                        new Amount( new BigInteger( firstDigit + "0000"), Currency.getInstance("EUR") )
                )
                .build();

        // when: calling the method paymentRequest
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration( request );

        // then: the response is a success
        assertEquals( PaymentFormConfigurationResponseSpecific.class, response.getClass() );
        assertEquals( NoFieldForm.class, ((PaymentFormConfigurationResponseSpecific)response).getPaymentForm().getClass() );
    }

    /**
     * Tests the parsing of the <code>PluginConfiguration</code> string, which contains a list of key/value pairs, representing the
     * banks to display in the <code>BankTransferForm</code>
     */
    @Test
    void getPaymentFormConfiguration_pluginConfigurationParsing(){
        // given: the request containing the magic amount 30001
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequestBuilder()
                .withAmount(
                        new Amount( new BigInteger( "30001"), Currency.getInstance("EUR") )
                )
                .build();

        // when: calling the method paymentRequest
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration( request );

        // then: the banks list contains 2 elements
        assertEquals( PaymentFormConfigurationResponseSpecific.class, response.getClass() );
        assertEquals( BankTransferForm.class, ((PaymentFormConfigurationResponseSpecific)response).getPaymentForm().getClass() );
        BankTransferForm form = (BankTransferForm)((PaymentFormConfigurationResponseSpecific)response).getPaymentForm();
        assertEquals( 2, form.getBanks().size() );
        assertEquals( "bankId1", form.getBanks().get(0).getKey() );
        assertEquals( "bank name 1", form.getBanks().get(0).getValue() );
        assertEquals( "bankId2", form.getBanks().get(1).getKey() );
        assertEquals( "bank name 2", form.getBanks().get(1).getValue() );
    }

    /**
     *
     */
    @Test
    void getPaymentFormConfiguration_CustomForm() {
        // given: the request containing the magic amount
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequestBuilder()
                .withAmount(
                        new Amount(new BigInteger("30002"), Currency.getInstance("EUR"))
                )
                .build();

        // when: calling the method paymentRequest
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration(request);

        // then: the response is a success
        assertEquals(PaymentFormConfigurationResponseSpecific.class, response.getClass());
        assertEquals(CustomForm.class, ((PaymentFormConfigurationResponseSpecific) response).getPaymentForm().getClass());
    }

    /**
     *
     */
    @Test
    void getPaymentFormConfiguration_PartnerWidgetForm() {
        // given: the request containing the magic amount
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequestBuilder()
                .withAmount(
                        new Amount(new BigInteger("30003"), Currency.getInstance("EUR"))
                )
                .build();

        // when: calling the method paymentRequest
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration(request);

        // then: the response is a success
        assertEquals(PaymentFormConfigurationResponseSpecific.class, response.getClass());
        assertEquals(PartnerWidgetForm.class, ((PaymentFormConfigurationResponseSpecific) response).getPaymentForm().getClass());
    }

    /**
     *
     */
    @Test
    void getPaymentFormConfiguration_NoFieldResponse() {
        // given: the request containing the magic amount
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequestBuilder()
                .withAmount(
                        new Amount(new BigInteger("30000"), Currency.getInstance("EUR"))
                )
                .build();

        // when: calling the method paymentRequest
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration(request);

        // then: the response is a success
        assertEquals(PaymentFormConfigurationResponseSpecific.class, response.getClass());
    }

    /**
     *
     */
    @Test
    void getPaymentFormConfiguration_NoPluginConfiguration() {
        doReturn(null).when(mockRequest).getPluginConfiguration();
        assertThrows(IllegalArgumentException.class, () -> service.getPaymentFormConfiguration(mockRequest));
    }

    /**
     * This test case ensures that, when the tested service is PaymentFormConfigurationResponseProvided,
     * the methods returns a PaymentFormConfigurationResponseProvided
     */
    @ParameterizedTest(name = "[{index}] first digit: {0}")
    @ValueSource(strings = {"0"})
    void getPaymentFormConfiguration_PaymentFormConfigurationResponseFailure( int lastDigit ){
        // given: the request containing the magic amount
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequestBuilder()
                .withAmount(
                        new Amount( new BigInteger( "3020" + lastDigit), Currency.getInstance("EUR") )
                )
                .build();

        // when: calling the method paymentRequest
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration( request );

        // then: the response is a success
        assertEquals( PaymentFormConfigurationResponseProvided.class, response.getClass() );
    }

    /**
     * This test case ensures that, when an null amount is given,
     * the method returns throw an IllegalArgumentException.
     */
    @Test
    void paymentRequest_VerifyRequest_Amount() {
        doReturn(null).when(mockRequest).getAmount();
        assertThrows(IllegalArgumentException.class, () -> service.getPaymentFormConfiguration(mockRequest));
    }

    /**
     * This test case ensures that, when an null amount is given,
     * the method returns throw an IllegalArgumentException.
     */
    @Test
    void paymentRequest_VerifyRequest_Local() {

        doReturn(new Amount( new BigInteger( "30000" ), Currency.getInstance("EUR") )).when(mockRequest).getAmount();
        doReturn(null).when(mockRequest).getLocale();
        assertThrows(IllegalArgumentException.class, () -> service.getPaymentFormConfiguration(mockRequest));
    }
    /**
     * This test case ensures that, when an null amount is given,
     * the method returns throw an IllegalArgumentException.
     */
    @Test
    void paymentRequest_PaymentFormLogoRequest() {
        doReturn(null).when(mockRequest).getLocale();
        assertThrows(IllegalArgumentException.class, () -> service.getPaymentFormLogo(mockLogoRequest));
    }

    /**
     * This test case ensures that, when an null amount is given,
     * the method returns throw an IllegalArgumentException.
     */
    @Test
    void paymentRequest_getLogo() {
        //TODO

    }
}
