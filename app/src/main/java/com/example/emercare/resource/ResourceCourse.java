package com.example.emercare.resource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.emercare.R;
import com.monstertechno.adblocker.AdBlockerWebView;
import com.monstertechno.adblocker.util.AdBlocker;

public class ResourceCourse extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_course);

        ImageView imgBack = findViewById(R.id.resource_course_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (isNetworkConnected()) {
            Intent intent = getIntent();

            TextView tvCourse = findViewById(R.id.resource_course_tv_course);
            tvCourse.setText(intent.getStringExtra("Course Name"));

            WebView webView = findViewById(R.id.resource_course_wv_course);
            new AdBlockerWebView.init(this).initializeWebView(webView);
            webView.setWebViewClient(new Browser());
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

            switch (intent.getStringExtra("Course Name")) {
                case "Allergies / Anaphylaxis":
                    if (intent.getStringExtra("Course").equals("First Aid Skill")) {
                        webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/allergic-reaction");
                    } else if (intent.getStringExtra("Course").equals("Disease")) {
                        webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/allergies/symptoms-causes/syc-20351497");
                    }
                    break;
                case "Asthma Attack":
                    if (intent.getStringExtra("Course").equals("First Aid Skill")) {
                        webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/asthma-attack");
                    } else if (intent.getStringExtra("Course").equals("Disease")) {
                        webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/asthma-attack/symptoms-causes/syc-20354268");
                    }
                    break;
                case "Bites / Stings":
                    webView.loadUrl("https://www.mayoclinic.org/first-aid/first-aid-insect-bites/basics/art-20056593");
                    break;
                case "Bleeding":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/bleeding-heavily");
                    break;
                case "Broken Bone":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/broken-bone");
                    break;
                case "Burns":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/burns");
                    break;
                case "Choking":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/choking");
                    break;
                case "Diabetic Emergency":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/diabetic-emergency");
                    break;
                case "Distress":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/distress");
                    break;
                case "Head Injury":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/head-injury");
                    break;
                case "Heart Attack":
                    if (intent.getStringExtra("Course").equals("First Aid Skill")) {
                        webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/heart-attack");
                    } else if (intent.getStringExtra("Course").equals("Disease")) {
                        webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/heart-attack/symptoms-causes/syc-20373106");
                    }
                    break;
                case "Heat Stroke":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/heat-exhaustion");
                    break;
                case "Hypothermia":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/hypothermia");
                    break;
                case "Meningitis":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/meningitis");
                    break;
                case "Poisoning / Harmful Substances":
                    webView.loadUrl("https://www.mayoclinic.org/first-aid/first-aid-poisoning/basics/art-20056657");
                    break;
                case "Psychological First Aid":
                    webView.loadUrl("https://www.nctsn.org/treatments-and-practices/psychological-first-aid-and-skills-for-psychological-recovery/about-pfa");
                    break;
                case "Seizure / Epilepsy":
                    if (intent.getStringExtra("Course").equals("First Aid Skill")) {
                        webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/seizures");
                    } else if (intent.getStringExtra("Course").equals("Disease")) {
                        webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/seizure/symptoms-causes/syc-20365711");
                    }
                    break;
                case "Sprains and Strains":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/strains-and-sprains");
                    break;
                case "Stroke":
                    if (intent.getStringExtra("Course").equals("First Aid Skill")) {
                        webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/stroke");
                    } else if (intent.getStringExtra("Course").equals("Disease")) {
                        webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/stroke/symptoms-causes/syc-20350113");
                    }
                    break;
                case "Unconscious":
                    webView.loadUrl("https://www.redcross.org.uk/first-aid/learn-first-aid/unresponsive-and-breathing");
                    break;
                case "Chemical Emergencies":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/chemical-emergency.html");
                    break;
                case "Drought":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/drought.html");
                    break;
                case "Earthquake":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/earthquake.html");
                    break;
                case "Fire Disaster":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/fire.html");
                    break;
                case "Flooding":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/flood.html");
                    break;
                case "Flu Pandemic":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/flu-safety.html");
                    break;
                case "Heatwave":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/heat-wave-safety.html");
                    break;
                case "Hurricane":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/hurricane.html");
                    break;
                case "Landslide":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/landslide.html");
                    break;
                case "Power Outage":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/power-outage.html");
                    break;
                case "Severe Winter Weather":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/winter-storm.html");
                    break;
                case "Tornado":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/tornado.html");
                    break;
                case "Tsunami":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/tsunami.html");
                    break;
                case "Volcano Eruption":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/volcano.html");
                    break;
                case "Wildfire":
                    webView.loadUrl("https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/wildfire.html");
                    break;
                case "Alzheimer’s Disease":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/alzheimers-disease/symptoms-causes/syc-20350447");
                    break;
                case "Anxiety":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/anxiety/symptoms-causes/syc-20350961");
                    break;
                case "Arthritis":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/arthritis/symptoms-causes/syc-20350772");
                    break;
                case "Cancer":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/cancer/symptoms-causes/syc-20370588");
                    break;
                case "Dementia":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/dementia/symptoms-causes/syc-20352013");
                    break;
                case "Depression":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/depression/symptoms-causes/syc-20356007");
                    break;
                case "Diabetes":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/diabetes/symptoms-causes/syc-20371444");
                    break;
                case "Dissociative Identity Disorder":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/dissociative-disorders/symptoms-causes/syc-20355215");
                    break;
                case "Parkinson’s Disease":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/parkinsons-disease/symptoms-causes/syc-20376055");
                    break;
                case "Schizophrenia":
                    webView.loadUrl("https://www.mayoclinic.org/diseases-conditions/schizophrenia/symptoms-causes/syc-20354443");
                    break;
                case "Burpee":
                    webView.loadUrl("https://www.verywellfit.com/how-to-do-burpees-5076186");
                    break;
                case "Dumbbell Overhead Press":
                    webView.loadUrl("https://www.verywellfit.com/how-to-do-the-dumbbell-overhead-press-3498298");
                    break;
                case "Dumbbell Row":
                    webView.loadUrl("https://www.verywellfit.com/how-to-properly-perform-the-dumbbell-bent-over-row-3498295");
                    break;
                case "Glute Bridge":
                    webView.loadUrl("https://www.verywellfit.com/how-to-do-the-bridge-exercise-3120738");
                    break;
                case "Lunge":
                    webView.loadUrl("https://www.verywellfit.com/how-to-lunge-variations-modifications-and-mistakes-1231320");
                    break;
                case "Plank":
                    webView.loadUrl("https://www.verywellfit.com/the-plank-exercise-3120068");
                    break;
                case "Push-up":
                    webView.loadUrl("https://www.verywellfit.com/the-push-up-exercise-3120574");
                    break;
                case "Side Plank":
                    webView.loadUrl("https://www.verywellfit.com/how-to-safely-progress-your-side-plank-exercise-4016853");
                    break;
                case "Single Leg Deadlift":
                    webView.loadUrl("https://www.verywellfit.com/how-to-do-a-single-leg-deadlift-2084681");
                    break;
                case "Squat":
                    webView.loadUrl("https://www.verywellfit.com/safe-squat-technique-3119136");
                    break;
            }
        } else {
            WebView webView = findViewById(R.id.resource_course_wv_course);
            webView.setVisibility(View.INVISIBLE);

            CardView cvNo = findViewById(R.id.resource_course_cv_no);
            cvNo.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class Browser extends WebViewClient {
        Browser() {
        }

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return AdBlockerWebView.blockAds(view, url) ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, url);
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}