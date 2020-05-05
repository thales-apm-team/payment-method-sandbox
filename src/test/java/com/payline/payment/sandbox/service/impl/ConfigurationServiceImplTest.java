package com.payline.payment.sandbox.service.impl;

import com.payline.payment.sandbox.MockUtils;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationServiceImplTest {

    private ConfigurationServiceImpl service = new ConfigurationServiceImpl();

    /**
     * This test case ensures that, when a ContractParametersCheckRequest is given with a delay in the contractConfiguration ,
     * the method returns a empty map after a delay of 2 seconds.
     */
    @ParameterizedTest(name = "[{index}] first digit: {0}")
    @ValueSource(strings = {"0"})
    void contractParametersCheckRequest_CheckWithDelay(int lastDigit) {

        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put("delay", new ContractProperty("true"));

        // given: the payment request containing the magic amount and a contractConfiguration
        ContractParametersCheckRequest request = MockUtils.aContractParametersCheckRequestBuilder()
                .withContractConfiguration(new ContractConfiguration("Sandbox APM", contractProperties))
                .build();

        long startTime = System.currentTimeMillis();

        // when: calling the method paymentRequest
        Map<String, String> response = service.check(request);

        long endTime = System.currentTimeMillis();

        long duration = (endTime - startTime);

        // then: the response is a success
        assertTrue(response.isEmpty());
        assertTrue(duration > 2000);
    }
}
