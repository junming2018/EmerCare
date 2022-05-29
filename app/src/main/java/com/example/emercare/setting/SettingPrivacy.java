package com.example.emercare.setting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.emercare.R;
import com.example.emercare.sign.SignIn;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class SettingPrivacy extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_privacy);

        ImageView imgBack = findViewById(R.id.setting_privacy_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (isNetworkConnected()) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            Button btnGuideline = findViewById(R.id.setting_privacy_btn_guideline);
            btnGuideline.setOnClickListener(view -> {
                Dialog dialog = new Dialog(SettingPrivacy.this);
                dialog.setContentView(R.layout.dialog_guideline);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            });

            Button btnReset = findViewById(R.id.setting_privacy_btn_reset);
            btnReset.setOnClickListener(view -> {
                TextInputLayout tilOld = findViewById(R.id.setting_privacy_til_oPassword);
                TextInputLayout tilPassword = findViewById(R.id.setting_privacy_til_password);
                TextInputLayout tilConfirm = findViewById(R.id.setting_privacy_til_cPassword);

                TextInputEditText tietOld = findViewById(R.id.setting_privacy_tiet_oPassword);
                tietOld.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (tilOld.getEndIconMode() == TextInputLayout.END_ICON_NONE) {
                            tilOld.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                TextInputEditText tietPassword = findViewById(R.id.setting_privacy_tiet_password);
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

                TextInputEditText tietConfirm = findViewById(R.id.setting_privacy_tiet_cPassword);
                tietConfirm.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (tilConfirm.getEndIconMode() == TextInputLayout.END_ICON_NONE) {
                            tilConfirm.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                String oldPassword = Objects.requireNonNull(tietOld.getText()).toString().trim();
                String password = Objects.requireNonNull(tietPassword.getText()).toString().trim();
                String confirmPassword = Objects.requireNonNull(tietConfirm.getText()).toString().trim();

                String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{8,}$";

                if (TextUtils.isEmpty(oldPassword) && TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword)) {
                    tilOld.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietOld.setError("Password is required!");
                    tietOld.requestFocus();
                    tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietPassword.setError("Password is required!");
                    tilConfirm.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietConfirm.setError("Password is required!");
                    return;
                } else if (TextUtils.isEmpty(oldPassword) && TextUtils.isEmpty(password)) {
                    tilOld.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietOld.setError("Password is required!");
                    tietOld.requestFocus();
                    tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietPassword.setError("Password is required!");
                    return;
                } else if (TextUtils.isEmpty(oldPassword) && TextUtils.isEmpty(confirmPassword)) {
                    tilOld.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietOld.setError("Password is required!");
                    tietOld.requestFocus();
                    tilConfirm.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietConfirm.setError("Password is required!");
                    return;
                } else if (TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword)) {
                    tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietPassword.setError("Password is required!");
                    tietPassword.requestFocus();
                    tilConfirm.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietConfirm.setError("Password is required!");
                    return;
                } else if (TextUtils.isEmpty(oldPassword)) {
                    tilOld.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietOld.setError("Password is required!");
                    tietOld.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietPassword.setError("Password is required!");
                    tietPassword.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    tilConfirm.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietConfirm.setError("Password is required!");
                    tietConfirm.requestFocus();
                    return;
                } else if (!(password.equals(confirmPassword))) {
                    tilConfirm.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietConfirm.setError("Password does not match!");
                    tietConfirm.requestFocus();
                    return;
                } else if (!(password.matches(PASSWORD_PATTERN))) {
                    tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tietPassword.setError("Please follow the password guideline!");
                    tietPassword.requestFocus();
                    return;
                }
                AuthCredential authCredential = EmailAuthProvider
                        .getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser)
                                .getEmail()), oldPassword);
                firebaseUser.reauthenticate(authCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Objects.requireNonNull(firebaseUser).updatePassword(password).addOnSuccessListener(unused -> {
                            Toast.makeText(SettingPrivacy.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }).addOnFailureListener(e -> Toast.makeText(SettingPrivacy.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(SettingPrivacy.this, "The old password entered is invalid!", Toast.LENGTH_SHORT).show();
                        tietOld.requestFocus();
                    }
                });
            });

            Button btnDelete = findViewById(R.id.setting_privacy_btn_delete);
            btnDelete.setOnClickListener(view -> {
                Dialog dialog = new Dialog(SettingPrivacy.this);
                dialog.setContentView(R.layout.dialog_delete);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button btnNo = dialog.findViewById(R.id.dialog_delete_btn_no);
                btnNo.setOnClickListener(view1 -> dialog.cancel());

                Button btnYes = dialog.findViewById(R.id.dialog_delete_btn_yes);
                btnYes.setOnClickListener(view12 -> {
                    dialog.cancel();

                    Dialog dialogPassword = new Dialog(SettingPrivacy.this);
                    dialogPassword.setContentView(R.layout.dialog_password);
                    dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    Button btnDialogDelete = dialogPassword.findViewById(R.id.dialog_password_btn_delete);
                    btnDialogDelete.setOnClickListener(view13 -> {
                        TextInputLayout tilDialogPassword = dialogPassword.findViewById(R.id.dialog_password_til_password);

                        TextInputEditText tietDialogPassword = dialogPassword.findViewById(R.id.dialog_password_tiet_password);
                        tietDialogPassword.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if (tilDialogPassword.getEndIconMode() == TextInputLayout.END_ICON_NONE) {
                                    tilDialogPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                            }
                        });

                        String dialogPasswordString = Objects.requireNonNull(tietDialogPassword.getText()).toString().trim();

                        if (TextUtils.isEmpty(dialogPasswordString)) {
                            tilDialogPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                            tietDialogPassword.setError("Password is required!");
                            tietDialogPassword.requestFocus();
                            return;
                        }
                        AuthCredential authCredential = EmailAuthProvider
                                .getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser)
                                        .getEmail()), dialogPasswordString);
                        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Objects.requireNonNull(firebaseUser).delete()
                                        .addOnSuccessListener(unused -> {
                                            firebaseAuth.signOut();
                                            dialogPassword.cancel();
                                            Toast.makeText(SettingPrivacy.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SettingPrivacy.this, SignIn.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("Status", "Signed Out");
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(SettingPrivacy.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            dialogPassword.cancel();
                                        });
                            } else {
                                Toast.makeText(SettingPrivacy.this, "The password entered is invalid!", Toast.LENGTH_SHORT).show();
                                tietDialogPassword.requestFocus();
                            }
                        });
                    });
                    dialogPassword.show();
                });
                dialog.show();
            });
        } else {
            ScrollView scPrivacy = findViewById(R.id.setting_privacy_sv_privacy);
            scPrivacy.setVisibility(View.INVISIBLE);

            CardView cvNo = findViewById(R.id.setting_privacy_cv_no);
            cvNo.setVisibility(View.VISIBLE);

            TextView tvNo = findViewById(R.id.setting_privacy_tv_no);
            tvNo.setText(getResources().getText(R.string.no_internet));
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