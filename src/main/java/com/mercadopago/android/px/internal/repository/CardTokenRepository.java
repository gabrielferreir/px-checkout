package com.mercadopago.android.px.internal.repository;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;

public interface CardTokenRepository {

    /**
     * After gathering user save card's information, create a Token to create Payment.
     *
     * @param savedCardToken: Save Card information to create Token.
     * @return Token associated to SavedCard.
     */
    MPCall<Token> createToken(final SavedCardToken savedCardToken);

    /**
     * An specialization of SavedCardToken. Create token for cards with ESC to create Payment.
     *
     * @param savedESCCardToken: saved ESC card token information to create TOken.
     * @return Token associated to SavedESCCard.
     */
    MPCall<Token> createToken(final SavedESCCardToken savedESCCardToken);

    /**
     * Clone Token.
     *
     * @param tokenId to clone.
     * @return Token cloned.
     */
    MPCall<Token> cloneToken(final String tokenId);

    /**
     * Update Token with securityCode.
     *
     * @param securityCode to update token.
     * @param tokenId to update.
     * @return Token updated with securityCode.
     */
    MPCall<Token> putSecurityCode(String securityCode, String tokenId);

    /**
     * Clear card cap and execute an action whatever it succeed or not
     *
     * @param cardId card id to clear cap
     * @param callback action to be executed
     */
    void clearCap(@NonNull final String cardId, @NonNull final ClearCapCallback callback);

    interface ClearCapCallback {
        void execute();
    }
}