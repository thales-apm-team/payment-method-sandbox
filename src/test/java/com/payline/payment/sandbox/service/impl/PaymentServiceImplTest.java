package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.MockUtils;
import com.payline.pmapi.bean.common.Amount;

import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFormUpdated;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;

import com.payline.pmapi.bean.paymentform.bean.field.*;
import com.payline.pmapi.bean.paymentform.bean.field.specific.*;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.bean.form.PartnerWidgetForm;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseProvided;

import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.Currency;
import java.util.HashMap;
;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentServiceImplTest {

    private PaymentServiceImpl service = new PaymentServiceImpl();

    /**
     * This test case ensures that, when the service tested is PaymentWithRedirectionService (amount starts by 2),
     * the method returns a PaymentResponseRedirect.
     */
    @Test
    void paymentRequest_redirection() {
        // given: the payment request containing the magic amount
        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withAmount(
                        new Amount(new BigInteger("20000"), Currency.getInstance("EUR"))
                )
                .build();

        // when: calling the method paymentRequest
        PaymentResponse response = service.paymentRequest(request);

        // then: the response is a success
        assertEquals(PaymentResponseRedirect.class, response.getClass());
    }

    /**
     * This test case ensures that, when an amount starting with any other value than 1 or 2 is given,
     * the method returns a PaymentResponseSuccess.
     */
    @ParameterizedTest(name = "[{index}] first digit: {0}")
    @ValueSource(strings = {"3", "4", "5", "6", "7", "8", "9"})
    void paymentRequest_otherServiceTested(int firstDigit) {
        // given: the payment request containing the magic amount
        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withAmount(
                        new Amount(new BigInteger(firstDigit + "0000"), Currency.getInstance("EUR"))
                )
                .build();

        // when: calling the method paymentRequest
        PaymentResponse response = service.paymentRequest(request);

        // then: the response is a success
        assertEquals(PaymentResponseSuccess.class, response.getClass());
    }

    /**
     * This test case ensures that, when an amount starting with any other value than 1 or 2 is given,
     * the method returns a PaymentResponseSuccess.
     */
    @Test
    void paymentRequest_PaymentResponseFormUpdated_CustomForm() {
        // Step
        Map<String, String> step = new HashMap<>();
        step.put("step", "1");

        // given: the payment request containing the magic amount
        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withAmount(new Amount(new BigInteger("10300"), Currency.getInstance("EUR")))
                .withRequestContext(RequestContext.RequestContextBuilder.aRequestContext()
                        .withRequestData(step)
                        .build())
                .build();

        // when: calling the method paymentRequest
        PaymentResponse response = service.paymentRequest(request);

        // Step Update
        step = ((PaymentResponseFormUpdated) response).getRequestContext().getRequestData();

        // Get form data from PaymentResponseFormUpdated
        CustomForm customForm = (CustomForm) ((PaymentFormConfigurationResponseSpecific) ((PaymentResponseFormUpdated) response).getPaymentFormConfigurationResponse()).getPaymentForm();
        List<PaymentFormField> customField = customForm.getCustomFields();

        // Form filling
        Map<String, String> map = new HashMap<>();
        Integer compteur = 0;
        for (PaymentFormField i : customField) {
            compteur = compteur ++;
            if (i instanceof AbstractPaymentFormInputField)
                map.put(((AbstractPaymentFormInputField) i).getKey(), "Value" + compteur);
        }

        // Set form data in a PaymentFormContext
        PaymentFormContext paymentFormContext = PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext().withPaymentFormParameter(map).build();

        // given: the payment request containing the magic amount
        request = MockUtils.aPaylinePaymentRequestBuilder()
                .withAmount(new Amount(new BigInteger("10300"), Currency.getInstance("EUR")))
                .withRequestContext(RequestContext.RequestContextBuilder.aRequestContext()
                        .withRequestData(step)
                        .build())
                .withPaymentFormContext(paymentFormContext)
                .build();

        // when: calling the method paymentRequest
        response = service.paymentRequest(request);


        // then: the response is a success
        assertEquals(PaymentResponseSuccess.class, response.getClass());
    }
    /**
     * This test case ensures that, when an amount starting with any other value than 1 or 2 is given,
     * the method returns a PaymentResponseSuccess.
     */
    @Test
    void paymentRequest_PaymentResponseFormUpdated_PartnerWidgetForm() {
        // Step
        Map<String, String> step = new HashMap<>();
        step.put("step", "1");

        // given: the payment request containing the magic amount
        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withAmount(new Amount(new BigInteger("10301"), Currency.getInstance("EUR")))
                .withRequestContext(RequestContext.RequestContextBuilder.aRequestContext()
                        .withRequestData(step)
                        .build())
                .build();

        // when: calling the method paymentRequest
        PaymentResponse response = service.paymentRequest(request);

        assertTrue(response instanceof PaymentResponseFormUpdated);

        // Step Update
        step = ((PaymentResponseFormUpdated) response).getRequestContext().getRequestData();

        // given: the payment request containing the magic amount
        request = MockUtils.aPaylinePaymentRequestBuilder()
                .withAmount(new Amount(new BigInteger("10301"), Currency.getInstance("EUR")))
                .withRequestContext(RequestContext.RequestContextBuilder.aRequestContext()
                        .withRequestData(step)
                        .build())
                .build();

        // when: calling the method paymentRequest
        response = service.paymentRequest(request);

        // then: the response is a success
        assertEquals(PaymentResponseSuccess.class, response.getClass());
    }
}
