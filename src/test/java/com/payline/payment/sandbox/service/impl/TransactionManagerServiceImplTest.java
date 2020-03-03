package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.MockUtils;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.NameValuePair;

import java.math.BigInteger;
import java.util.Currency;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionManagerServiceImplTest {

    private PaymentServiceImpl service = new PaymentServiceImpl();
    private TransactionManagerServiceImpl TransactionManagerService = new TransactionManagerServiceImpl();

    @Test
    void readAdditionalData(){
        // given: the payment request containing the magic amount
        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withAmount(
                        new Amount( new BigInteger("10000"), Currency.getInstance("EUR") )
                ).withPaymentFormContext(MockUtils.aPaymentFormContextWithParameter())
                .build();

        // when: calling the method paymentRequest
        PaymentResponse response = service.paymentRequest( request );

        Map<String, String> readedAdditionalData = TransactionManagerService.readAdditionalData(((PaymentResponseSuccess) response).getTransactionAdditionalData(),"");

        // Check if the additionalData are eguals
        Boolean isEqualTest = true;

        for (Map.Entry<String, String> entry : readedAdditionalData.entrySet()){
            if(MockUtils.aPaymentFormContextWithParameter().getPaymentFormParameter().containsKey(entry.getKey())){
                if(!MockUtils.aPaymentFormContextWithParameter().getPaymentFormParameter().get((entry.getKey())).equals((entry.getValue()))){
                    isEqualTest = false;
                }
            }else{
                isEqualTest = false;
            }
        }

        // then: assert the Additional data are equals
        assertTrue(isEqualTest);
    }
}
