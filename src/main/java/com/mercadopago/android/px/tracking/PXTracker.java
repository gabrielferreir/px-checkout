package com.mercadopago.android.px.tracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.internal.TrackingDefaultBehaviour;
import com.mercadopago.android.px.addons.model.Track;
import com.mercadopago.android.px.addons.tracking.Tracker;
import com.mercadopago.android.px.addons.tracking.TrackerWrapper;
import com.mercadopago.android.px.internal.tracking.TrackingRepositoryHelper;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class PXTracker {

    private PXTracker() {
    }

    /**
     * Set your own tracker listener to be aware of PX - Checkout events.
     *
     * @param listener your listener.
     * @deprecated Deprecated due to new tracking implementation standards. Use {@link com.mercadopago.android.px.tracking.PXTracker#setListener(PXTrackingListener)}
     * instead.
     */
    @Deprecated
    public static void setListener(@Nullable final PXEventListener listener) {
        if (listener != null) {
            TrackingDefaultBehaviour.INSTANCE.setTrackWrapper(new TrackerWrapper() {
                @NonNull
                @Override
                public Tracker getTracker() {
                    return Tracker.CUSTOM;
                }

                @Override
                public void send(@NonNull final Track track) {
                    listener.onScreenLaunched(track.getPath(), new HashMap<>());
                }
            });
        }
    }

    /**
     * Set your own tracker listener to be aware of PX - Checkout events.
     *
     * @param listener your listener.
     */
    public static void setListener(@Nullable final PXTrackingListener listener) {
        setListener(listener, new HashMap<>(), null);
    }

    /**
     * Set your own tracker listener to be aware of PX - Checkout events.
     *
     * @param listener your listener.
     */
    public static void setListener(@Nullable final PXTrackingListener listener,
        @NonNull final Map<String, ?> flowDetail, @Nullable final String flowName) {
        TrackingRepositoryHelper.setLegacyFlowIdAndDetail(flowName, flowDetail);
        if (listener != null) {
            TrackingDefaultBehaviour.INSTANCE.setTrackWrapper(new TrackerWrapper() {
                @NonNull
                @Override
                public Tracker getTracker() {
                    return Tracker.CUSTOM;
                }

                @Override
                public void send(@NonNull final Track track) {
                    switch (track.getType()) {
                    case EVENT:
                        listener.onEvent(track.getPath(), track.getData());
                        break;
                    case VIEW:
                        listener.onView(track.getPath(), track.getData());
                        break;
                    }
                }
            });
        }
    }
}