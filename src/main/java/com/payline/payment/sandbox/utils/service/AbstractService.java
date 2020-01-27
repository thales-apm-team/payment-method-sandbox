package com.payline.payment.sandbox.utils.service;

public class AbstractService<T> {

    protected T generic( String magicAmount ){
        // Remove the first number of the magic amount, representing the service
        // Remove the second and third number, representing the method called or a global use case
        String genericAmount = magicAmount.substring(3);

        if( "98".equals( genericAmount ) ){
            return null;
        }
        if( "99".equals( genericAmount ) ){
            throw new NullPointerException("Simulate a NullPointerException thrown by the plugin");
        }

        throw new IllegalArgumentException("Illegal (invalid or unknown) magic amount : " + magicAmount);
    }

}