package com.example.emercare.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emercare.R;

public class SettingMore extends AppCompatActivity implements View.OnClickListener {
    private TextView tvSelectionAbout, tvSelectionTerms, tvSelectionPrivacy, tvAbout, tvTerms, tvPrivacy;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_more);

        ImageView imgBack = findViewById(R.id.setting_more_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        tvSelectionAbout = findViewById(R.id.setting_more_selection_about);
        tvSelectionTerms = findViewById(R.id.setting_more_selection_terms);
        tvSelectionPrivacy = findViewById(R.id.setting_more_selection_privacy);

        tvAbout = findViewById(R.id.setting_more_tv_about);
        tvAbout.setOnClickListener(this);
        tvAbout.setClickable(false);

        tvTerms = findViewById(R.id.setting_more_tv_terms);
        tvTerms.setOnClickListener(this);

        tvPrivacy = findViewById(R.id.setting_more_tv_privacy);
        tvPrivacy.setOnClickListener(this);

        webView = findViewById(R.id.setting_more_wv_more);
        webView.loadUrl("file:///android_asset/setting/about.html");
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setting_more_tv_about) {
            tvAbout.setClickable(false);
            tvTerms.setClickable(true);
            tvPrivacy.setClickable(true);

            tvSelectionAbout.setBackground(getResources().getDrawable(R.drawable.selector_tab_select));
            tvSelectionTerms.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));
            tvSelectionPrivacy.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));

            tvAbout.setTextColor(getResources().getColor(R.color.white));
            tvTerms.setTextColor(getResources().getColor(R.color.word));
            tvPrivacy.setTextColor(getResources().getColor(R.color.word));

            webView.loadUrl("file:///android_asset/setting/about.html");

        } else if (view.getId() == R.id.setting_more_tv_terms) {
            tvTerms.setClickable(false);
            tvAbout.setClickable(true);
            tvPrivacy.setClickable(true);

            tvSelectionAbout.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));
            tvSelectionTerms.setBackground(getResources().getDrawable(R.drawable.selector_tab_select));
            tvSelectionPrivacy.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));

            tvAbout.setTextColor(getResources().getColor(R.color.word));
            tvTerms.setTextColor(getResources().getColor(R.color.white));
            tvPrivacy.setTextColor(getResources().getColor(R.color.word));

            webView.loadUrl("file:///android_asset/setting/terms.html");

        } else if (view.getId() == R.id.setting_more_tv_privacy) {
            tvPrivacy.setClickable(false);
            tvAbout.setClickable(true);
            tvTerms.setClickable(true);

            tvSelectionAbout.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));
            tvSelectionTerms.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));
            tvSelectionPrivacy.setBackground(getResources().getDrawable(R.drawable.selector_tab_select));

            tvAbout.setTextColor(getResources().getColor(R.color.word));
            tvTerms.setTextColor(getResources().getColor(R.color.word));
            tvPrivacy.setTextColor(getResources().getColor(R.color.white));

            webView.loadUrl("file:///android_asset/setting/privacy.html");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}