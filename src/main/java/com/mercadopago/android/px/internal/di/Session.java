package com.mercadopago.android.px.internal.di;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.BehaviourProvider;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.core.internal.TrackingRepositoryModelMapper;
import com.mercadopago.android.px.internal.audio.AudioPlayer;
import com.mercadopago.android.px.internal.core.ApplicationModule;
import com.mercadopago.android.px.internal.datasource.AmountConfigurationRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.AmountService;
import com.mercadopago.android.px.internal.datasource.CardHolderAuthenticatorRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.CardTokenService;
import com.mercadopago.android.px.internal.datasource.CheckoutRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.ConfigurationSolver;
import com.mercadopago.android.px.internal.datasource.ConfigurationSolverImpl;
import com.mercadopago.android.px.internal.datasource.CongratsRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolver;
import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolverImpl;
import com.mercadopago.android.px.internal.datasource.DiscountServiceImpl;
import com.mercadopago.android.px.internal.datasource.EscPaymentManagerImp;
import com.mercadopago.android.px.internal.datasource.ExperimentsRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.ModalRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.OneTapItemRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.PayerPaymentMethodRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.PaymentMethodRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.PaymentService;
import com.mercadopago.android.px.internal.datasource.PrefetchInitService;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.features.payment_congrats.CongratsResultFactory;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PXPaymentCongratsTracking;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.CardHolderAuthenticatorRepository;
import com.mercadopago.android.px.internal.repository.CardTokenRepository;
import com.mercadopago.android.px.internal.repository.CheckoutRepository;
import com.mercadopago.android.px.internal.repository.CongratsRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.EscPaymentManager;
import com.mercadopago.android.px.internal.repository.ExperimentsRepository;
import com.mercadopago.android.px.internal.repository.ModalRepository;
import com.mercadopago.android.px.internal.repository.OneTapItemRepository;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.PaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.services.CardHolderAuthenticatorService;
import com.mercadopago.android.px.internal.services.CongratsService;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.tracking.TrackingRepository;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Device;
import com.mercadopago.android.px.services.MercadoPagoServices;
import com.mercadopago.android.px.tracking.internal.MPTracker;

import static com.mercadopago.android.px.internal.util.MercadoPagoUtil.getPlatform;

public final class Session extends ApplicationModule {

    /**
     * This singleton instance is safe because session will work with application applicationContext. Application
     * applicationContext it's never leaking.
     */
    @SuppressLint("StaticFieldLeak")
    private static Session instance;

    // mem cache - lazy init.
    private final CheckoutConfigurationModule configurationModule;
    private DiscountRepository discountRepository;
    private AmountRepository amountRepository;
    private CheckoutRepository checkoutRepository;
    private PaymentRepository paymentRepository;
    private AmountConfigurationRepository amountConfigurationRepository;
    private CardTokenRepository cardTokenRepository;
    private CongratsRepository congratsRepository;
    private ExperimentsRepository experimentsRepository;
    private EscPaymentManagerImp escPaymentManager;
    private MPTracker tracker;
    private PaymentResultViewModelFactory paymentResultViewModelFactory;
    private CongratsResultFactory congratsResultFactory;
    private ViewModelModule viewModelModule;
    private PayerPaymentMethodRepository payerPaymentMethodRepository;
    private OneTapItemRepository oneTapItemRepository;
    private PaymentMethodRepository paymentMethodRepository;
    private ModalRepository modalRepository;
    private ConfigurationSolver configurationSolver;
    private CardHolderAuthenticatorRepositoryImpl cardHolderAuthenticatorRepository;
    private UseCaseModule useCaseModule;
    private FactoryModule factoryModule;
    private CustomOptionIdSolver customOptionIdSolver;
    private AudioPlayer audioPlayer;
    private final NetworkModule networkModule;
    private HelperModule helperModule;

    private Session(@NonNull final Context context) {
        super(context);
        configurationModule = new CheckoutConfigurationModule(context);
        networkModule = new NetworkModule(context);
    }

    @NonNull
    public static Session getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                "Session is not initialized. Make sure to call Session.initialize(Context) first.");
        }
        return instance;
    }

    @NonNull
    public static Session initialize(@NonNull final Context context) {
        instance = new Session(context);
        ConfigurationModule.initialize(instance.configurationModule);
        return instance;
    }

    /**
     * Initialize Session with MercadoPagoCheckout information.
     *
     * @param mercadoPagoCheckout non mutable checkout intent.
     */
    public void init(@NonNull final MercadoPagoCheckout mercadoPagoCheckout) {
        clear();
        configIds(mercadoPagoCheckout);

        // Store persistent paymentSetting
        final CheckoutConfigurationModule configurationModule = getConfigurationModule();

        final PaymentConfiguration paymentConfiguration = mercadoPagoCheckout.getPaymentConfiguration();
        final PaymentSettingRepository paymentSetting = configurationModule.getPaymentSettings();
        paymentSetting.configure(mercadoPagoCheckout.getPublicKey());
        paymentSetting.configure(mercadoPagoCheckout.getAdvancedConfiguration());
        paymentSetting.configure(paymentConfiguration);
        resolvePreference(mercadoPagoCheckout, paymentSetting);
        // end Store persistent paymentSetting
    }

    public void init(@NonNull final PaymentCongratsModel paymentCongratsModel) {
        clear();
        final PXPaymentCongratsTracking trackingData = paymentCongratsModel.getPxPaymentCongratsTracking();
        configurationModule.getTrackingRepository().configure(
            new TrackingRepository.Model(trackingData.getSessionId(), trackingData.getFlow(),
                trackingData.getFlowExtraInfo()));
    }

    @NonNull
    public State getSessionState() {
        try {
            if (configurationModule.getPaymentSettings().getPaymentConfiguration() != null) {
                return Session.State.VALID;
            } else {
                return Session.State.UNKNOWN;
            }
        } catch (final Exception e) {
            return Session.State.INVALID;
        }
    }

    private void resolvePreference(@NonNull final MercadoPagoCheckout mercadoPagoCheckout,
        final PaymentSettingRepository paymentSetting) {
        final String preferenceId = mercadoPagoCheckout.getPreferenceId();

        if (TextUtil.isEmpty(preferenceId)) {
            paymentSetting.configure(mercadoPagoCheckout.getCheckoutPreference());
        } else {
            //Pref cerrada.
            paymentSetting.configurePreferenceId(preferenceId);
        }
    }

    private void clear() {
        if (paymentRepository != null) {
            paymentRepository.reset();
            paymentRepository = null;
        }
        if (experimentsRepository != null) {
            experimentsRepository.reset();
            experimentsRepository = null;
        }
        if (payerPaymentMethodRepository != null) {
            payerPaymentMethodRepository.reset();
            payerPaymentMethodRepository = null;
        }
        if (paymentMethodRepository != null) {
            paymentMethodRepository.reset();
            paymentMethodRepository = null;
        }
        if (modalRepository != null) {
            modalRepository.reset();
            modalRepository = null;
        }
        if (amountConfigurationRepository != null) {
            amountConfigurationRepository.reset();
            amountConfigurationRepository = null;
        }
        if (discountRepository != null) {
            discountRepository.reset();
            discountRepository = null;
        }
        if (configurationModule != null) {
            configurationModule.reset();
        }
        if (oneTapItemRepository != null) {
            oneTapItemRepository.reset();
            oneTapItemRepository = null;
        }

        useCaseModule = null;
        factoryModule = null;
        helperModule = null;
        amountRepository = null;
        checkoutRepository = null;
        cardTokenRepository = null;
        congratsRepository = null;
        escPaymentManager = null;
        viewModelModule = null;
        configurationSolver = null;
        cardHolderAuthenticatorRepository = null;
        customOptionIdSolver = null;
        audioPlayer = null;
        congratsResultFactory = null;
    }

    @NonNull
    public NetworkModule getNetworkModule() {
        return networkModule;
    }

    @NonNull
    public CheckoutRepository getCheckoutRepository() {
        if (checkoutRepository == null) {
            final PaymentSettingRepository paymentSettings = getConfigurationModule().getPaymentSettings();
            checkoutRepository = new CheckoutRepositoryImpl(paymentSettings, getExperimentsRepository(),
                configurationModule.getDisabledPaymentMethodRepository(),
                networkModule.getNetworkApi(), getTracker(),
                getPayerPaymentMethodRepository(), getOneTapItemRepository(),
                getPaymentMethodRepository(),
                getModalRepository(), getConfigurationModule().getPayerComplianceRepository(),
                getAmountConfigurationRepository(), getDiscountRepository(), configurationModule.getChargeRepository(),
                MapperProvider.INSTANCE.getCustomChargeToPaymentTypeChargeMapper(),
                MapperProvider.INSTANCE.getInitRequestBodyMapper(),
                MapperProvider.INSTANCE.getOneTapItemToDisabledPaymentMethodMapper()) {
            };
        }
        return checkoutRepository;
    }

    @NonNull
    public ExperimentsRepository getExperimentsRepository() {
        if (experimentsRepository == null) {
            experimentsRepository = new ExperimentsRepositoryImpl(getSharedPreferences());
        }

        return experimentsRepository;
    }

    @NonNull
    public ESCManagerBehaviour getMercadoPagoESC() {
        final TrackingRepository trackingRepository = configurationModule.getTrackingRepository();
        return BehaviourProvider
            .getEscManagerBehaviour(trackingRepository.getSessionId(), trackingRepository.getFlowId());
    }

    @NonNull
    private Device getDevice() {
        return new Device(getApplicationContext(), getMercadoPagoESC());
    }

    @NonNull
    public MercadoPagoServices getMercadoPagoServices() {
        final PaymentSettingRepository paymentSettings = getConfigurationModule().getPaymentSettings();
        return new MercadoPagoServices(getApplicationContext(), paymentSettings.getPublicKey(),
            configurationModule.getAuthorizationProvider().getPrivateKey());
    }

    @NonNull
    public AmountRepository getAmountRepository() {
        if (amountRepository == null) {
            amountRepository = new AmountService(configurationModule.getPaymentSettings(),
                configurationModule.getChargeRepository(), getDiscountRepository(), getAmountConfigurationRepository());
        }
        return amountRepository;
    }

    @NonNull
    public DiscountRepository getDiscountRepository() {
        if (discountRepository == null) {
            discountRepository =
                new DiscountServiceImpl(getFileManager(), getConfigurationModule().getUserSelectionRepository(),
                    getAmountConfigurationRepository(), getConfigurationSolver());
        }
        return discountRepository;
    }

    @NonNull
    public AmountConfigurationRepository getAmountConfigurationRepository() {
        if (amountConfigurationRepository == null) {
            amountConfigurationRepository = new AmountConfigurationRepositoryImpl(getFileManager(),
                getConfigurationModule().getUserSelectionRepository(), getConfigurationSolver());
        }
        return amountConfigurationRepository;
    }

    @NonNull
    public CheckoutConfigurationModule getConfigurationModule() {
        return configurationModule;
    }

    @NonNull
    public PaymentRepository getPaymentRepository() {
        if (paymentRepository == null) {
            paymentRepository = new PaymentService(configurationModule.getUserSelectionRepository(),
                configurationModule.getPaymentSettings(),
                configurationModule.getDisabledPaymentMethodRepository(),
                getApplicationContext(),
                getEscPaymentManager(),
                getAmountConfigurationRepository(),
                getCongratsRepository(),
                getFileManager(),
                getUseCaseModule().getValidationProgramUseCase(),
                getFactoryModule().getPaymentResultFactory(),
                getFactoryModule().getPaymentDataFactory());
        }

        return paymentRepository;
    }

    @NonNull
    public EscPaymentManager getEscPaymentManager() {
        if (escPaymentManager == null) {
            escPaymentManager = new EscPaymentManagerImp(getMercadoPagoESC(), configurationModule.getPaymentSettings());
        }
        return escPaymentManager;
    }

    @NonNull
    public CardTokenRepository getCardTokenRepository() {
        if (cardTokenRepository == null) {
            final GatewayService gatewayService =
                networkModule.getRetrofitClient().create(GatewayService.class);
            cardTokenRepository = new CardTokenService(gatewayService, configurationModule.getPaymentSettings(),
                getDevice(), getMercadoPagoESC(), configurationModule.getAuthorizationProvider());
        }
        return cardTokenRepository;
    }

    @NonNull
    public CongratsRepository getCongratsRepository() {
        if (congratsRepository == null) {
            final CongratsService congratsService = networkModule.getRetrofitClient().create(CongratsService.class);
            congratsRepository = new CongratsRepositoryImpl(congratsService,
                getPlatform(getApplicationContext()), configurationModule.getTrackingRepository(),
                configurationModule.getUserSelectionRepository(), getAmountRepository(),
                configurationModule.getDisabledPaymentMethodRepository(),
                getMercadoPagoESC(), getOneTapItemRepository(),
                configurationModule.getPaymentSettings(), getPayerPaymentMethodRepository(),
                MapperProvider.INSTANCE.getAlternativePayerPaymentMethodsMapper(),
                configurationModule.getAuthorizationProvider()
            );
        }
        return congratsRepository;
    }

    @NonNull
    public ViewModelModule getViewModelModule() {
        if (viewModelModule == null) {
            viewModelModule = new ViewModelModule();
        }
        return viewModelModule;
    }

    public UseCaseModule getUseCaseModule() {
        if (useCaseModule == null) {
            useCaseModule = new UseCaseModule(configurationModule, MapperProvider.INSTANCE);
        }
        return useCaseModule;
    }

    public PayerPaymentMethodRepository getPayerPaymentMethodRepository() {
        if (payerPaymentMethodRepository == null) {
            payerPaymentMethodRepository = new PayerPaymentMethodRepositoryImpl(
                getFileManager(),
                getConfigurationModule().getApplicationSelectionRepository());
        }
        return payerPaymentMethodRepository;
    }

    public OneTapItemRepository getOneTapItemRepository() {
        if (oneTapItemRepository == null) {
            oneTapItemRepository = new OneTapItemRepositoryImpl(getFileManager(),
                getConfigurationModule().getDisabledPaymentMethodRepository());
        }
        return oneTapItemRepository;
    }

    public PaymentMethodRepository getPaymentMethodRepository() {
        if (paymentMethodRepository == null) {
            paymentMethodRepository = new PaymentMethodRepositoryImpl(getFileManager());
        }
        return paymentMethodRepository;
    }

    public ModalRepository getModalRepository() {
        if (modalRepository == null) {
            modalRepository = new ModalRepositoryImpl(getFileManager());
        }
        return modalRepository;
    }

    public ConfigurationSolver getConfigurationSolver() {
        if (configurationSolver == null) {
            configurationSolver = new ConfigurationSolverImpl(getPayerPaymentMethodRepository());
        }
        return configurationSolver;
    }

    public CustomOptionIdSolver getCustomOptionIdSolver() {
        if (customOptionIdSolver == null) {
            customOptionIdSolver =
                new CustomOptionIdSolverImpl(getConfigurationModule().getApplicationSelectionRepository());
        }
        return customOptionIdSolver;
    }

    @NonNull
    public PrefetchInitService getPrefetchInitService(@NonNull final MercadoPagoCheckout checkout) {
        configIds(checkout);
        return new PrefetchInitService(checkout, networkModule.getNetworkApi(),
            MapperProvider.INSTANCE.getInitRequestBodyMapper(checkout));
    }

    @NonNull
    public MPTracker getTracker() {
        if (tracker == null) {
            tracker = new MPTracker(
                configurationModule.getTrackingRepository(),
                configurationModule.getPaymentSettings().getPaymentConfiguration()
            );
        }
        return tracker;
    }

    @NonNull
    public AudioPlayer getAudioPlayer() {
        if (audioPlayer == null) {
            audioPlayer = new AudioPlayer(getApplicationContext());
        }
        return audioPlayer;
    }

    @NonNull
    public PaymentResultViewModelFactory getPaymentResultViewModelFactory() {
        if (paymentResultViewModelFactory == null) {
            paymentResultViewModelFactory = new PaymentResultViewModelFactory(getTracker());
        }
        return paymentResultViewModelFactory;
    }

    @NonNull
    public CongratsResultFactory getCongratsResultFactory() {
        if (congratsResultFactory == null) {
            congratsResultFactory = new CongratsResultFactory(MapperProvider.INSTANCE.getPaymentCongratsMapper());
        }
        return congratsResultFactory;
    }

    @NonNull
    public CardHolderAuthenticatorRepository getCardHolderAuthenticationRepository() {
        if (cardHolderAuthenticatorRepository == null) {
            final CardHolderAuthenticatorService service =
                networkModule.getRetrofitClient().create(CardHolderAuthenticatorService.class);
            cardHolderAuthenticatorRepository =
                new CardHolderAuthenticatorRepositoryImpl(service, configurationModule.getPaymentSettings());
        }
        return cardHolderAuthenticatorRepository;
    }

    private void configIds(@NonNull final MercadoPagoCheckout checkout) {
        //Favoring product id in discount params because that one is surely custom if exists
        final String deprecatedProductId =
            checkout.getAdvancedConfiguration().getDiscountParamsConfiguration().getProductId();
        final String productId = TextUtil.isNotEmpty(deprecatedProductId) ? deprecatedProductId
            : checkout.getAdvancedConfiguration().getProductId();
        final String accessToken = checkout.getPrivateKey();

        configurationModule.getTrackingRepository().configure(
            TrackingRepositoryModelMapper.INSTANCE.map(checkout.getTrackingConfiguration()));
        configurationModule.getProductIdProvider().configure(productId);
        if (TextUtil.isNotEmpty(accessToken)) {
            configurationModule.getAuthorizationProvider().configure(accessToken);
        }
    }

    public FactoryModule getFactoryModule() {
        if (factoryModule == null) {
            factoryModule = new FactoryModule();
        }
        return factoryModule;
    }

    @NonNull
    public HelperModule getHelperModule() {
        if (helperModule == null) {
            helperModule = new HelperModule();
        }
        return helperModule;
    }

    public enum State {
        VALID,
        INVALID,
        UNKNOWN
    }
}
