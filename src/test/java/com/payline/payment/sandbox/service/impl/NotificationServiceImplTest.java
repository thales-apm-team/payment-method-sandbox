package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.MockUtils;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotificationServiceImplTest {

    private NotificationServiceImpl service = new NotificationServiceImpl();

    /**
     * This test case ensures that, when a NotificationRequest is given with a delay in the contractConfiguration ,
     * the method returns a empty map after a delay of 2 seconds.
     */
    @ParameterizedTest(name = "[{index}] first digit: {0}")
    @ValueSource(strings = {"0"})
    void parse_CheckWithDelay(int lastDigit) {


        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put("delay", new ContractProperty("true"));

        // given: the payment request containing the magic amount and a contractConfiguration
        NotificationRequest request = MockUtils.aNotificationRequestBuilder()
                .withContractConfiguration(new ContractConfiguration("Sandbox APM", contractProperties))
                .build();

        long startTime = System.currentTimeMillis();

        // when: calling the method paymentRequest
        NotificationResponse response = service.parse(request);

        long endTime = System.currentTimeMillis();

        long duration = (endTime - startTime);

        // then: the response is a success
        assertEquals(IgnoreNotificationResponse.class, response.getClass());
        assertTrue(duration > 2000);
    }
}
