package com.payline.payment.sandbox.utils;

import com.payline.pmapi.bean.common.Amount;
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
import com.payline.pmapi.bean.paymentform.bean.field.*;
import com.payline.pmapi.bean.paymentform.bean.field.specific.*;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
import com.payline.pmapi.bean.paymentform.bean.form.PartnerWidgetForm;
import com.payline.pmapi.bean.paymentform.bean.form.partnerwidget.*;
import com.payline.pmapi.bean.paymentform.bean.scheme.CommonScheme;
import com.payline.pmapi.bean.paymentform.bean.scheme.Scheme;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.regex.Pattern;

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

    /**
     * @returnt a PaymentResponseFormUpdated initialised with all possible parameters
     */
    public static CustomForm aCustomForm() throws MalformedURLException {

        List<PaymentFormField> customFields = new ArrayList<>();

        // Add a PaymentFormDisplayFieldIFrame
        customFields.add(PaymentFormDisplayFieldIFrame.PaymentFormDisplayFieldIFrameBuilder.aPaymentFormDisplayFieldIFrame()
                .withHeight(100)
                .withSrc(new URL("https://www.google.com"))
                .withWidth(100)
                .build()
        );

        // Add PaymentFormDisplayFieldLink
        customFields.add(PaymentFormDisplayFieldLink.PaymentFormDisplayFieldLinkBuilder.aPaymentFormDisplayFieldLink()
                .withName("Nom")
                .withTitle("Titre")
                .withUrl(new URL("https://www.google.com"))
                .build());

        // Add PaymentFormDisplayFieldText
        customFields.add(PaymentFormDisplayFieldText.PaymentFormDisplayFieldTextBuilder.aPaymentFormDisplayFieldText()
                .withContent("Text")
                .build());

        // Add PaymentFormInputFieldAmount
        customFields.add(PaymentFormInputFieldAmount.PaymentFormInputFieldAmountBuilder.aPaymentFormInputFieldAmount()
                .withKey("Key1")
                .withLabel("Label")
                .withMaxAmount(new Amount(new BigInteger(  "10000"), Currency.getInstance("EUR")))
                .withMinAmount(new Amount(new BigInteger(  "10400"), Currency.getInstance("EUR")))
                .withRequired(true)
                .withValue("Value1")
                .withDisabled(false)
                .withRequiredErrorMessage("Required Error Message")
                .withSecured(true)
                .withValidationErrorMessage("Validation Error Message")
                .build());

        // Add PaymentFormInputFieldBic
        customFields.add(PaymentFormInputFieldBic.PaymentFormFieldBicBuilder.aPaymentFormFieldBic()
                .withDisabled(false)
                .withKey("Key2")
                .withLabel("Label2")
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .build());

        // Add PaymentFormInputFieldBirthdate
        customFields.add(PaymentFormInputFieldBirthdate.BirthDateFieldBuilder.aBirthDateField()
                .withDisabled(false)
                .withKey("Key3")
                .withLabel("Label3")
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .build());

        // Add PaymentFormInputFieldCardNumber
        List<Scheme> schemes = new ArrayList<>();
        Scheme scheme = new CommonScheme();
        schemes.add(scheme);

        customFields.add(PaymentFormInputFieldCardNumber.CardNumberFieldBuilder.aCardNumberField()
                .withDisabled(false)
                .withKey("Key4")
                .withLabel("Label4")
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .withSchemes(schemes)
                .build());

        // Add PaymentFormInputFieldCardholder
        customFields.add(PaymentFormInputFieldCardholder.CardHolderFieldBuilder.aCardHolderField()
                .withDisabled(false)
                .withKey("Key5")
                .withLabel("Label5")
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .build());

        // Add PaymentFormInputFieldCheckbox
        customFields.add(PaymentFormInputFieldCheckbox.PaymentFormFieldCheckboxBuilder.aPaymentFormFieldCheckbox()
                .withDisabled(false)
                .withKey("Key6")
                .withLabel("Label6")
                .withPrechecked(false)
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .withSecured(true)
                .build());

        // Add PaymentFormInputFieldCvx
        customFields.add(PaymentFormInputFieldCvx.CvxFieldBuilder.aCvxField()
                .withDisabled(false)
                .withKey("Key7")
                .withLabel("Label7")
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .build());

        // Add PaymentFormInputFieldExpirationDate
        customFields.add(PaymentFormInputFieldExpirationDate.ExpirationDateFieldBuilder.anExpirationDateField()
                .withDisabled(false)
                .withKey("Key8")
                .withLabel("Label8")
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .build());

        // Add PaymentFormInputFieldIban
        customFields.add(PaymentFormInputFieldIban.IbanFieldBuilder.anIbanField()
                .withDisabled(false)
                .withKey("Key9")
                .withLabel("Label9")
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .build());

        // Add PaymentFormInputFieldSelect
        List<SelectOption> selectOptions = new ArrayList<>();
        selectOptions.add(SelectOption.SelectOptionBuilder.aSelectOption()
                .withKey("Key10")
                .withValue("Value10")
                .build());

        customFields.add(PaymentFormInputFieldSelect.PaymentFormFieldSelectBuilder.aPaymentFormInputFieldSelect()
                .withDisabled(false)
                .withIsFilterable(false)
                .withKey("Key11")
                .withLabel("Label11")
                .withPlaceholder("PlaceHolder")
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .withSecured(true)
                .withSelectOptions(selectOptions)
                .withValidationErrorMessage("Validation Error Message")
                .build());

        // Add PaymentFormInputFieldText
        Pattern validation = Pattern.compile("Test");

        customFields.add(PaymentFormInputFieldText.PaymentFormFieldTextBuilder.aPaymentFormFieldText()
                .withDisabled(false)
                .withFieldIcon(FieldIcon.ENVELOPE)
                .withInputType(InputType.EMAIL)
                .withKey("Key12")
                .withLabel("Label12")
                .withPlaceholder("PlaceHolder")
                .withRequired(true)
                .withRequiredErrorMessage("Required Error Message")
                .withSecured(true)
                .withValidation(validation)
                .withValidationErrorMessage("Validation Error Message")
                .withValue("Value12")
                .build());

        // Create the custom form
        CustomForm customForm = CustomForm.builder()
                .withDescription("")
                .withCustomFields(customFields)
                .withButtonText("Button")
                .withDisplayButton(true)
                .build();
        return customForm;

    }

    /** @return a PartnerWidgetForm initialised with all possible parameters */
    public static PartnerWidgetForm aPartnerWidgetForm() throws MalformedURLException {
         String SCRIPT_BEFORE_IMPORT ="<script>" +
                 "console.log(\"Console log Before\");" +
                 "</script>";

         String SCRIPT_AFTER_IMPORT ="<script>" +
                 "console.log(\"Console log after\");" +
                 "</script>";


        // script to import
        PartnerWidgetScriptImport scriptImport = PartnerWidgetScriptImport.WidgetPartnerScriptImportBuilder
                .aWidgetPartnerScriptImport()
                .withUrl(new URL("https://www.payline.com"))
                .withCache(true)
                .withAsync(true)
                .build();

        // div that contains the script to load
        PartnerWidgetContainer container = PartnerWidgetContainerTargetDivId.WidgetPartnerContainerTargetDivIdBuilder
                .aWidgetPartnerContainerTargetDivId()
                .withId("SandBoxPaymentForm")
                .build();

        // method to call when payment is done (in the "onValidated" event)
        PartnerWidgetOnPay onPay = PartnerWidgetOnPayCallBack.WidgetContainerOnPayCallBackBuilder
                .aWidgetContainerOnPayCallBack()
                .withName("paylineProcessPaymentCallback")
                .build();

        PartnerWidgetForm widgetForm = PartnerWidgetForm.WidgetPartnerFormBuilder
                .aWidgetPartnerForm()
                .withDescription("Partner Widget Form Description")
                .withScriptImport(scriptImport)
                .withLoadingScriptBeforeImport(SCRIPT_BEFORE_IMPORT)
                .withLoadingScriptAfterImport(SCRIPT_AFTER_IMPORT)
                .withContainer(container)
                .withOnPay(onPay)
                .withPerformsAutomaticRedirection(true)
                .build();

         return widgetForm;
    }

}
