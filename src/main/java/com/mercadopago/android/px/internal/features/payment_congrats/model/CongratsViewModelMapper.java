package com.mercadopago.android.px.internal.features.payment_congrats.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadolibre.android.mlbusinesscomponents.common.MLBusinessSingleItem;
import com.mercadolibre.android.mlbusinesscomponents.components.actioncard.MLBusinessActionCardViewData;
import com.mercadolibre.android.mlbusinesscomponents.components.common.downloadapp.MLBusinessDownloadAppData;
import com.mercadolibre.android.mlbusinesscomponents.components.common.downloadapp.MLBusinessDownloadAppView;
import com.mercadolibre.android.mlbusinesscomponents.components.crossselling.MLBusinessCrossSellingBoxData;
import com.mercadolibre.android.mlbusinesscomponents.components.discount.MLBusinessDiscountBoxData;
import com.mercadolibre.android.mlbusinesscomponents.components.discount.MLBusinessDiscountTracker;
import com.mercadolibre.android.mlbusinesscomponents.components.loyalty.MLBusinessLoyaltyRingData;
import com.mercadolibre.android.mlbusinesscomponents.components.touchpoint.domain.model.AdditionalEdgeInsets;
import com.mercadolibre.android.mlbusinesscomponents.components.touchpoint.domain.response.MLBusinessTouchpointResponse;
import com.mercadolibre.android.mlbusinesscomponents.components.touchpoint.tracking.MLBusinessTouchpointTracker;
import com.mercadopago.android.px.internal.features.business_result.BusinessPaymentResultTracker;
import com.mercadopago.android.px.internal.features.business_result.CongratsViewModel;
import com.mercadopago.android.px.internal.features.business_result.MLBusinessMapper;
import com.mercadopago.android.px.internal.features.business_result.PXDiscountBoxData;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.mappers.Mapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CongratsViewModelMapper extends Mapper<PaymentCongratsResponse, CongratsViewModel> {

    /* default */ final BusinessPaymentResultTracker discountTracker;

    /**
     * Constructor
     *
     * @param discountTracker A {@link BusinessPaymentResultTracker}
     */
    public CongratsViewModelMapper(final BusinessPaymentResultTracker discountTracker) {
        this.discountTracker = discountTracker;
    }

    @Override
    public CongratsViewModel map(@NonNull final PaymentCongratsResponse paymentCongratsResponse) {
        return getCongratsViewModel(paymentCongratsResponse);
    }

    private CongratsViewModel getCongratsViewModel(@NonNull final PaymentCongratsResponse paymentCongratsResponse) {
        final PaymentCongratsResponse.Discount discount = paymentCongratsResponse.getDiscount();
        return new CongratsViewModel(getLoyaltyData(paymentCongratsResponse.getLoyalty()),
            getDiscountBoxData(discount), getShowAllDiscount(discount),
            getDownloadAppData(discount),
            getExpenseSplitData(paymentCongratsResponse.getExpenseSplit()),
            getCrossSellingBoxData(paymentCongratsResponse.getCrossSellings()),
            paymentCongratsResponse.getViewReceipt(), paymentCongratsResponse.hasCustomOrder(),
            paymentCongratsResponse.getOperationInfo());
    }

    @Nullable
    private MLBusinessLoyaltyRingData getLoyaltyData(@Nullable final PaymentCongratsResponse.Loyalty loyalty) {

        if (loyalty == null) {
            return null;
        }

        final PaymentCongratsResponse.Loyalty.Progress progress = loyalty.getProgress();
        final PaymentCongratsResponse.Action action = loyalty.getAction();

        return new MLBusinessLoyaltyRingData() {
            @Override
            public String getRingHexaColor() {
                return progress.getColor();
            }

            @Override
            public int getRingNumber() {
                return progress.getLevel();
            }

            @Override
            public float getRingPercentage() {
                return progress.getPercentage();
            }

            @Override
            public String getTitle() {
                return loyalty.getTitle();
            }

            @Override
            public String getButtonTitle() {
                return action.getLabel();
            }

            @Override
            public String getButtonDeepLink() {
                return action.getTarget();
            }
        };
    }

    @Nullable
    private PXDiscountBoxData getDiscountBoxData(@Nullable final PaymentCongratsResponse.Discount discount) {

        if (discount == null) {
            return null;
        }

        return new PXDiscountBoxData() {
            @Nullable
            @Override
            public String getTitle() {
                return discount.getTitle();
            }

            @Nullable
            @Override
            public String getSubtitle() {
                return discount.getSubtitle();
            }

            @Nullable
            @Override
            public MLBusinessTouchpointResponse getTouchpoint() {
                return mapTouchpoint(discount.getTouchpoint());
            }

            @Nullable
            @Override
            public MLBusinessTouchpointTracker getTracker() {
                return discountTracker;
            }

            @Override
            public MLBusinessDiscountBoxData getDiscountBoxData() {
                return new MLBusinessDiscountBoxData() {
                    @Nullable
                    @Override
                    public String getTitle() {
                        return discount.getTitle();
                    }

                    @Nullable
                    @Override
                    public String getSubtitle() {
                        return discount.getSubtitle();
                    }

                    @NonNull
                    @Override
                    public List<MLBusinessSingleItem> getItems() {
                        return getDisCountItems(discount.getItems());
                    }

                    @Nullable
                    @Override
                    public MLBusinessDiscountTracker getTracker() {
                        return discountTracker;
                    }
                };
            }
        };
    }

    @NonNull
    private List<MLBusinessSingleItem> getDisCountItems(@NonNull List<PaymentCongratsResponse.Discount.Item> items) {

        final List<MLBusinessSingleItem> singleItems = new LinkedList<>();

        for (final PaymentCongratsResponse.Discount.Item item : items) {
            singleItems.add(new MLBusinessSingleItem() {
                @Override
                public String getImageUrl() {
                    return item.getIcon();
                }

                @Override
                public String getTitleLabel() {
                    return item.getTitle();
                }

                @Override
                public String getSubtitleLabel() {
                    return item.getSubtitle();
                }

                @Nullable
                @Override
                public String getDeepLinkItem() {
                    return item.getTarget();
                }

                @Nullable
                @Override
                public String getTrackId() {
                    return item.getCampaignId();
                }

                @Nullable
                @Override
                public Map<String, Object> getEventData() {
                    if (item.getCampaignId() != null && !item.getCampaignId().isEmpty()) {
                        return new HashMap<>(Collections.singletonMap("tracking_id", item.getCampaignId()));
                    }
                    return null;
                }
            });
        }
        return singleItems;
    }

    @Nullable
        /* default */ MLBusinessTouchpointResponse mapTouchpoint(
        @Nullable final PaymentCongratsResponse.PXBusinessTouchpoint touchpoint) {
        if (touchpoint == null) {
            return null;
        }
        final MLBusinessTouchpointResponse touchpointResponse = new MLBusinessTouchpointResponse();
        touchpointResponse.id = touchpoint.getId();
        touchpointResponse.type = touchpoint.getType();
        touchpointResponse.content = JsonUtil.getGson().toJsonTree(touchpoint.getContent());
        if (touchpoint.getAdditionalEdgeInsets() != null) {
            final PaymentCongratsResponse.AdditionalEdgeInsets insets = touchpoint.getAdditionalEdgeInsets();
            touchpointResponse.additionalEdgeInsets = new AdditionalEdgeInsets(
                insets.getTop(), insets.getLeft(), insets.getBottom(), insets.getRight());
        }
        try {
            touchpointResponse.tracking = touchpoint.getTracking();
        } catch (final ClassCastException e) {
            //no-op
        }
        return touchpointResponse;
    }

    @Nullable
    private PaymentCongratsResponse.Action getShowAllDiscount(@Nullable final PaymentCongratsResponse.Discount discount) {
        final PaymentCongratsResponse.Action showAllDiscount;
        if (discount == null || (showAllDiscount = discount.getAction()) == null) {
            return null;
        }

        return showAllDiscount;
    }

    @Nullable
    private MLBusinessDownloadAppData getDownloadAppData(@Nullable final PaymentCongratsResponse.Discount discount) {
        final PaymentCongratsResponse.Discount.DownloadApp downloadApp;
        if (discount == null || (downloadApp = discount.getActionDownload()) == null) {
            return null;
        }

        return new MLBusinessDownloadAppData() {
            @NonNull
            @Override
            public MLBusinessDownloadAppView.AppSite getAppSite() {

                //TODO: Logica para saber en que app estoy.
                return MLBusinessDownloadAppView.AppSite.MP;
            }

            @NonNull
            @Override
            public String getTitle() {
                return downloadApp.getTitle();
            }

            @NonNull
            @Override
            public String getButtonTitle() {
                return downloadApp.getAction().getLabel();
            }

            @NonNull
            @Override
            public String getButtonDeepLink() {
                return downloadApp.getAction().getTarget();
            }
        };
    }

    @NonNull
    private List<MLBusinessCrossSellingBoxData> getCrossSellingBoxData(
        final List<PaymentCongratsResponse.CrossSelling> crossSellingList) {

        final List<MLBusinessCrossSellingBoxData> crossSellingBoxData = new LinkedList<>();

        for (final PaymentCongratsResponse.CrossSelling crossSellingItem : crossSellingList) {

            final PaymentCongratsResponse.Action action = crossSellingItem.getAction();
            crossSellingBoxData.add(new MLBusinessCrossSellingBoxData() {
                @NonNull
                @Override
                public String getIconUrl() {
                    return crossSellingItem.getIcon();
                }

                @NonNull
                @Override
                public String getText() {
                    return crossSellingItem.getTitle();
                }

                @NonNull
                @Override
                public String getButtonTitle() {
                    return action.getLabel();
                }

                @NonNull
                @Override
                public String getButtonDeepLink() {
                    return action.getTarget();
                }
            });
        }

        return crossSellingBoxData;
    }

    @Nullable
    private MLBusinessActionCardViewData getExpenseSplitData(@Nullable final PaymentCongratsResponse.ExpenseSplit moneySplit) {
        return MLBusinessMapper.map(moneySplit);
    }
}