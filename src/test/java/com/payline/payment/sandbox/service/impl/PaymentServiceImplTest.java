package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.MockUtils;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentServiceImplTest {

    private PaymentServiceImpl service = new PaymentServiceImpl();

    /**
     * This test case ensures that, when the service tested is PaymentWithRedirectionService (amount starts by 2),
     * the method returns a PaymentResponseRedirect.
     */
    @Test
    void paymentRequest_redirection(){
        // given: the payment request containing the magic amount
        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withAmount(
                        new Amount( new BigInteger("20000"), Currency.getInstance("EUR") )
                )
                .build();

        // when: calling the method paymentRequest
        PaymentResponse response = service.paymentRequest( request );

        // then: the response is a success
        assertEquals( PaymentResponseRedirect.class, response.getClass() );
    }

    /**
     * This test case ensures that, when an amount starting with any other value than 1 or 2 is given,
     * the method returns a PaymentResponseSuccess.
     */
    @ParameterizedTest(name = "[{index}] first digit: {0}")
    @ValueSource(strings = {"3", "4", "5", "6", "7", "8" , "9"})
    void paymentRequest_otherServiceTested( int firstDigit ){
        // given: the payment request containing the magic amount
        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withAmount(
                        new Amount( new BigInteger( firstDigit + "0000"), Currency.getInstance("EUR") )
                )
                .build();

        // when: calling the method paymentRequest
        PaymentResponse response = service.paymentRequest( request );

        // then: the response is a success
        assertEquals( PaymentResponseSuccess.class, response.getClass() );
    }

}
