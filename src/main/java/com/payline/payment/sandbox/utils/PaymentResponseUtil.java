package com.payline.payment.sandbox.utils;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.response.PaymentData3DS;
import com.payline.pmapi.bean.payment.response.PaymentModeCard;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.BankAccount;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.Card;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.BankTransfer;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.Email;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseDoPayment;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;

import java.time.YearMonth;

public class PaymentResponseUtil {

    public final static String PARTNER_TRANSACTION_ID = "PARTNER_ID.0123456789";

    public static PaymentResponseDoPayment doPaymentMinimal(){
        Card card = Card.CardBuilder.aCard()
                .withBrand("brand")
                .withHolder("holder")
                .withExpirationDate(YearMonth.of(21, 01))
                .withPan("pan")
                .withPanType(Card.PanType.CARD_PAN)
                .build();

        PaymentData3DS paymentData3DS = PaymentData3DS.Data3DSBuilder.aData3DS()
                .withCavv("cavv")
                .withEci("eci")
                .build();

        PaymentModeCard paymentModeCard = PaymentModeCard.PaymentModeCardBuilder.aPaymentModeCard()
                .withPaymentDatas3DS(paymentData3DS)
                .withCard(card)
                .build();

        return PaymentResponseDoPayment.PaymentResponseDoPaymentBuilder.aPaymentResponseDoPayment()
                .withPartnerTransactionId( PARTNER_TRANSACTION_ID )
                .withPaymentMode( paymentModeCard )
                .build();
    }

    /**
     * @return The most returned failure response : a failureCause and an error code less than 50 characters long.
     */
    public static PaymentResponseFailure failureClassic(){
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause( FailureCause.INVALID_DATA )
                .withErrorCode("Error code less than 50 characters long")
                .build();
    }

    /**
     * @return A minimal response of type {@link PaymentResponseFailure} (only the attributes required by the builder)
     */
    public static PaymentResponseFailure failureMinimal(){
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause( FailureCause.INVALID_DATA )
                .build();
    }

    /**
     * @return A failure response which error code is more than 50 characters long, as the PM-API allows it.
     */
    public static PaymentResponseFailure failureLongErrorCode(){
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause( FailureCause.INVALID_DATA )
                .withErrorCode("This error code has not been truncated and is more than 50 characters long")
                .build();
    }

    /**
     * @return A classic failure response, with a partner transaction ID.
     */
    public static PaymentResponseFailure failureWithPartnerTransactionId(){
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause( FailureCause.INVALID_DATA )
                .withErrorCode("Error code less than 50 characters long")
                .withPartnerTransactionId( PARTNER_TRANSACTION_ID )
                .build();
    }

    /**
     * @return A minimal response of type {@link PaymentResponseOnHold}, with SCORING_ASYNC as the {@link OnHoldCause}.
     */
    public static PaymentResponseOnHold onHoldMinimalScoringAsync(){
        return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                .withPartnerTransactionId( PARTNER_TRANSACTION_ID )
                .withOnHoldCause( OnHoldCause.SCORING_ASYNC )
                .build();
    }

    /**
     * @return A minimal response of type {@link PaymentResponseOnHold}, with ASYNC_RETRY as the {@link OnHoldCause}.
     */
    public static PaymentResponseOnHold onHoldMinimalAsyncRetry(){
        return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                .withPartnerTransactionId( PARTNER_TRANSACTION_ID )
                .withOnHoldCause( OnHoldCause.ASYNC_RETRY )
                .build();
    }

    /**
     * @return A success response containing transaction additional data.
     */
    public static PaymentResponseSuccess successAdditionalData(){
        return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                .withPartnerTransactionId( PARTNER_TRANSACTION_ID )
                .withTransactionDetails( new EmptyTransactionDetails() )
                .withTransactionAdditionalData( "" ) // TODO !
                .build();
    }

    /**
     * @return A success response which <code>transactionDetails</code> field is of type {@link BankTransfer}
     */
    public static PaymentResponseSuccess successBankTransferDetails(){
        BankAccount owner = BankAccount.BankAccountBuilder.aBankAccount()
                .withHolder("M. Owner")
                .withAccountNumber("12345678901")
                .withIban("FR7610107001011234567890129")
                .withBic("CCBPFRPPNAN")
                .withCountryCode("FR")
                .withBankName("Banque Populaire")
                .withBankCode("CCBP")
                .build();
        BankAccount receiver = BankAccount.BankAccountBuilder.aBankAccount()
                .withHolder("Mme Receiver")
                .withAccountNumber("12345678901")
                .withIban("FR7630006000011234567890189")
                .withBic("AGRIFRPPCHY")
                .withCountryCode("FR")
                .withBankName("Crédit Agricole")
                .withBankCode("AGRI")
                .build();
        return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                .withPartnerTransactionId( PARTNER_TRANSACTION_ID )
                .withTransactionDetails( new BankTransfer(owner, receiver) )
                .build();
    }

    /**
     * @return A success response which <code>transactionDetails</code> field is of type {@link Email}
     */
    public static PaymentResponseSuccess successEmailDetails(){
        return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                .withPartnerTransactionId( PARTNER_TRANSACTION_ID )
                .withTransactionDetails( Email.EmailBuilder.anEmail().withEmail("toto.tutu@fai.fr").build() )
                .build();
    }

    /**
     * @return A minimal response of type {@link PaymentResponseSuccess}
     */
    public static PaymentResponseSuccess successMinimal(){
        return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                .withPartnerTransactionId( PARTNER_TRANSACTION_ID )
                .withTransactionDetails( new EmptyTransactionDetails() )
                .build();
    }

}
