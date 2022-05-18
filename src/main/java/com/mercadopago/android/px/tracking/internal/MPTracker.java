package com.mercadopago.android.px.tracking.internal;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.BehaviourProvider;
import com.mercadopago.android.px.addons.model.Track;
import com.mercadopago.android.px.addons.model.internal.Experiment;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.internal.tracking.TrackingRepository;
import com.mercadopago.android.px.internal.util.Logger;
import com.mercadopago.android.px.internal.util.PaymentConfigurationUtil;
import com.mercadopago.android.px.model.CheckoutType;
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mercadopago.android.px.internal.util.TextUtil.isEmpty;

public final class MPTracker {

    private static final String TAG = "PXTracker";
    private static final String ATTR_EXTRA_INFO = "extra_info";
    private static final String ATTR_FLOW_DETAIL = "flow_detail";
    private static final String ATTR_FLOW_NAME = "flow";
    private static final String ATTR_SESSION_ID = "session_id";
    private static final String ATTR_SESSION_TIME = "session_time";
    private static final String ATTR_CHECKOUT_TYPE = "checkout_type";
    private static final String ATTR_SECURITY_ENABLED = "security_enabled";
    private static final String ATTR_DEVICE_SECURED = "device_secured";
    private static final String ATTR_EXPERIMENTS = "experiments";

    private long initSessionTimestamp;

    @NonNull private List<Experiment> experiments = Collections.emptyList();

    @NonNull private final TrackingRepository trackingRepository;
    @NonNull private final PaymentConfiguration paymentConfiguration;

    public MPTracker(
        @NonNull final TrackingRepository trackingRepository,
        @NonNull final PaymentConfiguration paymentConfiguration
    ) {
        this.trackingRepository = trackingRepository;
        this.paymentConfiguration = paymentConfiguration;
    }

    /**
     * Set all A/B testing experiments that are active.
     *
     * @param experiments The active A/B testing experiments.
     */
    public void setExperiments(@NonNull final List<Experiment> experiments) {
        this.experiments = experiments;
    }

    public void track(@NonNull final TrackWrapper trackWrapper) {
        final Track track = trackWrapper.getTrack();
        // Event friction case needs to add flow detail in a different way. We ignore this case for now.
        if (!FrictionEventTracker.PATH.equals(track.getPath())) {
            addAdditionalFlowInfo(track.getData(), trackWrapper.getShouldTrackExperimentsLabel());
        } else {
            addAdditionalFlowIntoExtraInfo(track.getData(), trackWrapper.getShouldTrackExperimentsLabel());
        }
        BehaviourProvider.getTrackingBehaviour().track(track);
        Logger.debug(TAG, "Type: " + track.getType().name() + " - Path: " + track.getPath());
        Logger.debug(TAG, track.getData());
    }

    private void addAdditionalFlowIntoExtraInfo(@NonNull final Map<String, Object> data, final boolean shouldTrackExperimentsLabel) {
        if (data.containsKey(ATTR_EXTRA_INFO)) {
            try {
                final Map<String, Object> extraInfo = (Map<String, Object>) data.get(ATTR_EXTRA_INFO);
                addCommonFlowInfo(extraInfo, shouldTrackExperimentsLabel);
            } catch (final ClassCastException e) {
                // do nothing.
            }
        }
    }

    private void addAdditionalFlowInfo(@NonNull final Map<String, Object> data, final boolean shouldTrackExperimentsLabel) {
        data.put(ATTR_FLOW_DETAIL, trackingRepository.getFlowDetail());
        addCommonFlowInfo(data, shouldTrackExperimentsLabel);
    }

    private void addCommonFlowInfo(@NonNull final Map<String, Object> data, final boolean shouldTrackExperimentsLabel) {
        final boolean hasPaymentProcessor = PaymentConfigurationUtil.hasPaymentProcessor(paymentConfiguration);
        data.put(ATTR_FLOW_NAME, trackingRepository.getFlowId());
        data.put(ATTR_SESSION_ID, trackingRepository.getSessionId());
        data.put(ATTR_SESSION_TIME, getSecondsAfterInit());
        data.put(ATTR_CHECKOUT_TYPE, hasPaymentProcessor ? CheckoutType.ONE_TAP : CheckoutType.ONE_TAP_SELECTOR);
        data.put(ATTR_SECURITY_ENABLED, trackingRepository.getSecurityEnabled());
        data.put(ATTR_DEVICE_SECURED, trackingRepository.getDeviceSecured());
        if (shouldTrackExperimentsLabel) {
            data.put(ATTR_EXPERIMENTS, getExperimentsLabel());
        }
    }

    private String getExperimentsLabel() {
        final StringBuilder label = new StringBuilder();

        for (final Experiment experiment : experiments) {
            if (!isEmpty(label)) {
                label.append(",");
            }

            label.append(experiment.getName());
            label.append(" - ");
            label.append(experiment.getVariant().getName());
        }

        return label.toString();
    }

    private long getSecondsAfterInit() {
        if (initSessionTimestamp == 0) {
            initializeSessionTime();
        }
        final long milliseconds = Calendar.getInstance().getTime().getTime() - initSessionTimestamp;
        return TimeUnit.MILLISECONDS.toSeconds(milliseconds);
    }

    public void initializeSessionTime() {
        initSessionTimestamp = Calendar.getInstance().getTime().getTime();
    }
}
