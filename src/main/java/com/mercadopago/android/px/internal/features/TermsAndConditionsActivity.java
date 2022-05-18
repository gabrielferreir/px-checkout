package com.mercadopago.android.px.internal.features;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.tracking.internal.views.TermsAndConditionsViewTracker;
import java.nio.charset.StandardCharsets;

public class TermsAndConditionsActivity extends PXActivity {

    public static final String EXTRA_DATA = "extra_data";

    public static void start(@NonNull final Context context, @Nullable final String data) {
        final Intent intent = new Intent(context, TermsAndConditionsActivity.class);
        intent.putExtra(EXTRA_DATA, data);
        context.startActivity(intent);
    }

    private View mMPTermsAndConditionsView;
    private WebView mTermsAndConditionsWebView;
    private ViewGroup mProgressLayout;
    private String data;

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        setContentView(R.layout.px_activity_terms_and_conditions);

        data = getIntent().getStringExtra(EXTRA_DATA);

        if (savedInstanceState == null) {
            Session.getInstance().getTracker().track(new TermsAndConditionsViewTracker(data));
        }

        mProgressLayout = findViewById(R.id.mpsdkProgressLayout);
        mMPTermsAndConditionsView = findViewById(R.id.mpsdkMPTermsAndConditions);
        mTermsAndConditionsWebView = findViewById(R.id.mpsdkTermsAndConditionsWebView);
        mTermsAndConditionsWebView.setVerticalScrollBarEnabled(true);
        mTermsAndConditionsWebView.setHorizontalScrollBarEnabled(true);
        initializeToolbar();
        showMPTermsAndConditions();
    }

    private void initializeToolbar() {
        final Toolbar mToolbar = findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void showMPTermsAndConditions() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mTermsAndConditionsWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, final String url) {
                mProgressLayout.setVisibility(View.GONE);
                mMPTermsAndConditionsView.setVisibility(View.VISIBLE);
            }
        });
        String version;
        try {
            final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (final PackageManager.NameNotFoundException e) {
            version = "1.0.0";
        }
        mTermsAndConditionsWebView.getSettings().setUserAgentString("MercadoLibre-Android/" + version);

        if (URLUtil.isValidUrl(data)) {
            mTermsAndConditionsWebView.loadUrl(data);
        } else {
            final String encodedUrl = Base64.encodeToString(data.getBytes(StandardCharsets.UTF_8),
                Base64.DEFAULT);
            mTermsAndConditionsWebView.loadData(encodedUrl, "text/html; charset=utf-8", "base64");
        }
    }
}
