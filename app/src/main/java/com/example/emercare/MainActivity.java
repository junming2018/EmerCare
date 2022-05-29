package com.example.emercare;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.emercare.find.FindFragment;
import com.example.emercare.home.HomeFragment;
import com.example.emercare.profile.ProfileFragment;
import com.example.emercare.record.RecordFragment;
import com.example.emercare.resource.ResourceFragment;
import com.example.emercare.setting.SettingMore;
import com.example.emercare.setting.SettingPrivacy;
import com.example.emercare.sign.SignIn;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tbTop = findViewById(R.id.main_activity_top_tb);
        setSupportActionBar(tbTop);

        bottomNavigationView = findViewById(R.id.main_activity_bottom_navbar);

        if (getIntent().getStringExtra("Status") != null) {
            replaceFragment(new ProfileFragment());
            bottomNavigationView.setSelectedItemId(R.id.profile);
        } else {
            replaceFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.home);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = new HomeFragment();
                    bottomNavigationView.getMenu().findItem(R.id.home).setEnabled(false);
                    bottomNavigationView.getMenu().findItem(R.id.find).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.record).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.resource).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.profile).setEnabled(true);
                    break;
                case R.id.find:
                    fragment = new FindFragment();
                    bottomNavigationView.getMenu().findItem(R.id.find).setEnabled(false);
                    bottomNavigationView.getMenu().findItem(R.id.home).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.record).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.resource).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.profile).setEnabled(true);
                    break;
                case R.id.record:
                    fragment = new RecordFragment();
                    bottomNavigationView.getMenu().findItem(R.id.record).setEnabled(false);
                    bottomNavigationView.getMenu().findItem(R.id.home).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.find).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.resource).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.profile).setEnabled(true);
                    break;
                case R.id.resource:
                    fragment = new ResourceFragment();
                    bottomNavigationView.getMenu().findItem(R.id.resource).setEnabled(false);
                    bottomNavigationView.getMenu().findItem(R.id.home).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.find).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.record).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.profile).setEnabled(true);
                    break;
                case R.id.profile:
                    fragment = new ProfileFragment();
                    bottomNavigationView.getMenu().findItem(R.id.profile).setEnabled(false);
                    bottomNavigationView.getMenu().findItem(R.id.home).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.find).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.record).setEnabled(true);
                    bottomNavigationView.getMenu().findItem(R.id.resource).setEnabled(true);
                    break;
            }
            replaceFragment(fragment);
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_activity_fl_main, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_setting);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button btnMore = dialog.findViewById(R.id.dialog_setting_btn_more);
            btnMore.setOnClickListener(view -> {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, SettingMore.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });

            Button btnPrivacy = dialog.findViewById(R.id.dialog_setting_btn_privacy);
            Button btnOut = dialog.findViewById(R.id.dialog_setting_btn_sOut);

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() != null) {
                btnPrivacy.setOnClickListener(view -> {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, SettingPrivacy.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
                btnOut.setOnClickListener(view -> {
                    dialog.dismiss();
                    firebaseAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("Status", "Signed Out");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
            } else {
                btnPrivacy.setOnClickListener(view -> {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
                btnOut.setBackgroundColor(getResources().getColor(R.color.blue));
                btnOut.setText(getResources().getText(R.string.sign_in));
                btnOut.setOnClickListener(view -> {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
            }
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.home) {
            super.onBackPressed();
            finish();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
}