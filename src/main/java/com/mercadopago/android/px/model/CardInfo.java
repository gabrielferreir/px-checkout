package com.mercadopago.android.px.model;

import androidx.annotation.NonNull;
import java.io.Serializable;

/**
 * Visual representation DTO for cards.
 */
public class CardInfo implements Serializable {

    private Integer cardNumberLength;
    private Integer securityCodeLength;
    private String securityCodeLocation;
    private final String lastFourDigits;
    private final String firstSixDigits;

    private CardInfo(
        @NonNull final String firstSixDigits,
        @NonNull final String lastFourDigits,
        final int cardNumberLength,
        final int securityCodeLength) {
        this.cardNumberLength = cardNumberLength;
        this.lastFourDigits = lastFourDigits;
        this.firstSixDigits = firstSixDigits;
        this.securityCodeLength = securityCodeLength;
    }

    public static CardInfo create(final CardToken cardToken) {
        final int length = cardToken.getCardNumber().length();
        return new CardInfo(cardToken.getCardNumber().substring(0, 6),
            cardToken.getCardNumber().substring(length - 4, length),
            length,
            cardToken.getSecurityCode().length());
    }

    /**
     * @param token
     * @deprecated To be replaced with factory method.
     */
    @Deprecated
    public CardInfo(final Token token) {
        cardNumberLength = token.getCardNumberLength();
        lastFourDigits = token.getLastFourDigits();
        firstSixDigits = token.getFirstSixDigits();
    }

    /**
     * @param card
     * @deprecated To be replaced with factory method.
     */
    @Deprecated
    public CardInfo(final Card card) {
        lastFourDigits = card.getLastFourDigits();
        firstSixDigits = card.getFirstSixDigits();
        securityCodeLength = card.getSecurityCodeLength();
        securityCodeLocation = card.getSecurityCodeLocation();
    }

    @Deprecated
    public static boolean canCreateCardInfo(final Token token) {
        return token.getCardNumberLength() != null && token.getLastFourDigits() != null
            && token.getFirstSixDigits() != null;
    }

    public Integer getCardNumberLength() {
        return cardNumberLength;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public String getFirstSixDigits() {
        return firstSixDigits;
    }

    public Integer getSecurityCodeLength() {
        return securityCodeLength;
    }

    public String getSecurityCodeLocation() {
        return securityCodeLocation;
    }
}
