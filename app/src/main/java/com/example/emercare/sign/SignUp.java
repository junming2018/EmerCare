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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emercare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.transform.sax.SAXResult;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ImageView imgBack = findViewById(R.id.sign_up_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button btnGuideline = findViewById(R.id.sign_up_btn_guideline);
        btnGuideline.setOnClickListener(view -> {
            Dialog dialog = new Dialog(SignUp.this);
            dialog.setContentView(R.layout.dialog_guideline);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        Button btnUp = findViewById(R.id.sign_up_btn_sUp);
        btnUp.setOnClickListener(view -> {
            TextInputLayout tilEmail = findViewById(R.id.sign_up_til_email);
            TextInputLayout tilPassword = findViewById(R.id.sign_up_til_password);
            TextInputLayout tilConfirmPassword = findViewById(R.id.sign_up_til_cPassword);

            TextInputEditText tietEmail = findViewById(R.id.sign_up_tiet_email);
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

            TextInputEditText tietPassword = findViewById(R.id.sign_up_tiet_password);
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

            TextInputEditText tietConfirm = findViewById(R.id.sign_up_tiet_cPassword);
            tietConfirm.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (tilConfirmPassword.getEndIconMode() == TextInputLayout.END_ICON_NONE) {
                        tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            String email = Objects.requireNonNull(tietEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(tietPassword.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(tietConfirm.getText()).toString().trim();

            String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{8,}$";

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword)) {
                tietEmail.setError("E-mail is required!");
                tietEmail.requestFocus();
                tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietPassword.setError("Password is required!");
                tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietConfirm.setError("Password is required!");
                return;
            } else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                tietEmail.setError("E-mail is required!");
                tietEmail.requestFocus();
                tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietPassword.setError("Password is required!");
                return;
            } else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(confirmPassword)) {
                tietEmail.setError("E-mail is required!");
                tietEmail.requestFocus();
                tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietConfirm.setError("Password is required!");
                return;
            } else if (TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword)) {
                tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietPassword.setError("Password is required!");
                tietPassword.requestFocus();
                tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietConfirm.setError("Password is required!");
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
            } else if (TextUtils.isEmpty(confirmPassword)) {
                tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietConfirm.setError("Password is required!");
                tietConfirm.requestFocus();
                return;
            } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                tilEmail.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietEmail.setError("Please enter a valid e-mail address!");
                tietEmail.requestFocus();
                return;
            } else if (!(password.matches(PASSWORD_PATTERN))) {
                tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietPassword.setError("Please follow the password guideline!");
                tietPassword.requestFocus();
                return;
            } else if (!(password.equals(confirmPassword))) {
                tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                tietConfirm.setError("Password does not match!");
                tietConfirm.requestFocus();
                return;
            }
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        Map<String, Object> note = new HashMap<>();
                        note.put("e-mail", email);

                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        firebaseFirestore.collection(email)
                                .document("Account")
                                .set(note)
                                .addOnFailureListener(e -> Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                        firebaseFirestore.collection(email)
                                .document("Blood Glucose")
                                .set(note)
                                .addOnFailureListener(e -> Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                        firebaseFirestore.collection(email)
                                .document("Blood Pressure")
                                .set(note)
                                .addOnFailureListener(e -> Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                        firebaseFirestore.collection(email)
                                .document("Weight")
                                .set(note)
                                .addOnFailureListener(e -> Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show());

                        Intent intent = new Intent(getApplicationContext(), SignDetail.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }).addOnFailureListener(e -> Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}