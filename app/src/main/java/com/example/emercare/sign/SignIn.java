package com.example.emercare.sign;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emercare.MainActivity;
import com.example.emercare.R;
import com.example.emercare.setting.SettingPrivacy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Button btnIn = findViewById(R.id.sign_in_btn_sIn);
        btnIn.setOnClickListener(view -> {
            TextInputLayout tilEmail = findViewById(R.id.sign_in_til_email);
            TextInputLayout tilPassword = findViewById(R.id.sign_in_til_password);

            TextInputEditText tietEmail = findViewById(R.id.sign_in_tiet_email);
            tietEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (tilEmail.getEndIconMode() == TextInputLayout.END_ICON_NONE) {
                        tilEmail.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            TextInputEditText tietPassword = findViewById(R.id.sign_in_tiet_password);
            tietPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (tilPassword.getEndIconMode() == TextInputLayout.END_ICON_NONE) {
                        tilPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            String email = Objects.requireNonNull(tietEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(tietPassword.getText()).toString().trim();

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                tietEmail.setError("E-mail is required!");
                tietEmail.requestFocus();
                tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietPassword.setError("Password is required!");
                return;
            } else if (TextUtils.isEmpty(email)) {
                tietEmail.setError("E-mail is required!");
                tietEmail.requestFocus();
                return;
            } else if (TextUtils.isEmpty(password)) {
                tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietPassword.setError("Password is required!");
                tietPassword.requestFocus();
                return;
            } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                tilEmail.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietEmail.setError("Please enter a valid e-mail address!");
                tietEmail.requestFocus();
                return;
            }
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }).addOnFailureListener(e -> Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        TextView tvForgot = findViewById(R.id.sign_in_tv_forgot);
        tvForgot.setOnClickListener(view -> {
            Dialog dialog = new Dialog(SignIn.this);
            dialog.setContentView(R.layout.dialog_forgot);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button btnConfirm = dialog.findViewById(R.id.dialog_forgot_btn_confirm);
            btnConfirm.setOnClickListener(view1 -> {
                TextInputLayout tilForgot = dialog.findViewById(R.id.dialog_forgot_til_email);

                TextInputEditText tietForgot = dialog.findViewById(R.id.dialog_forgot_tiet_email);
                tietForgot.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (tilForgot.getEndIconMode() == TextInputLayout.END_ICON_NONE) {
                            tilForgot.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                String email = Objects.requireNonNull(tietForgot.getText()).toString().trim();

                if (TextUtils.isEmpty(email)) {
                    tietForgot.setError("E-mail is required!");
                    tietForgot.requestFocus();
                    return;
                } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                    tilForgot.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietForgot.setError("Please enter a valid e-mail address!");
                    tietForgot.requestFocus();
                    return;
                }
                firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
                            Toast.makeText(SignIn.this, "Reset password e-mail sent!", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }).addOnFailureListener(e -> Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            });
            dialog.show();
        });

        Button btnNow = findViewById(R.id.sign_in_btn_now);
        btnNow.setOnClickListener(view -> {
            startActivity(new Intent(SignIn.this, SignUp.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("Status") != null) {
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}