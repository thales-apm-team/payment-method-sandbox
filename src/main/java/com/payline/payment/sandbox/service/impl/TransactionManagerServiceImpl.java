package com.payline.payment.sandbox.service.impl;

import com.payline.pmapi.service.TransactionManagerService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class TransactionManagerServiceImpl implements TransactionManagerService {

    @Override
    public Map<String, String> readAdditionalData(String s, String s1) {
        Map<String, String> query_pairs = new HashMap<String, String>();

        String[] pairs = s.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return query_pairs;
    }

}
