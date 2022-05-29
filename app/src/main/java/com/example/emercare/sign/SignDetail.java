package com.example.emercare.sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.emercare.Filter.IntegerInputFilter;
import com.example.emercare.MainActivity;
import com.example.emercare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignDetail extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_detail);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Objects.requireNonNull(firebaseUser).sendEmailVerification()
                .addOnSuccessListener(unused -> Toast.makeText(SignDetail.this, "An e-mail sent for verification",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SignDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        AutoCompleteTextView actvGender = findViewById(R.id.sign_detail_actv_gender);
        String[] genderList = {"Male", "Female"};
        ArrayAdapter<String> adGender = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, genderList);
        actvGender.setText(adGender.getItem(0), false);
        actvGender.setAdapter(adGender);

        TextInputEditText tietAge = findViewById(R.id.sign_detail_tiet_age);
        tietAge.setFilters(new InputFilter[]{new IntegerInputFilter("1", "125")});

        TextInputEditText tietHeight = findViewById(R.id.sign_detail_tiet_height);
        tietHeight.setFilters(new InputFilter[]{new IntegerInputFilter("1", "300")});

        AutoCompleteTextView actvUnit = findViewById(R.id.sign_detail_actv_unit);
        String[] unitList = {"cm", "inch"};
        ArrayAdapter<String> adUnit = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, unitList);
        actvUnit.setText(adUnit.getItem(0), false);
        actvUnit.setAdapter(adUnit);
        actvUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Objects.requireNonNull(actvUnit.getText()).toString().trim().equals("cm")) {
                    tietHeight.setFilters(new InputFilter[]{new IntegerInputFilter("1", "300")});
                    if (!(TextUtils.isEmpty(Objects.requireNonNull(tietHeight.getText()).toString().trim()))) {
                        if (tietHeight.getError() != null) {
                            tietHeight.setError(null);
                        }
                    }
                } else if (Objects.requireNonNull(actvUnit.getText()).toString().trim().equals("inch")) {
                    tietHeight.setFilters(new InputFilter[]{new IntegerInputFilter("1", "150")});
                    try {
                        if (Integer.parseInt(Objects.requireNonNull(tietHeight.getText()).toString().trim()) > 150) {
                            tietHeight.setError("Please enter a valid value!");
                            tietHeight.requestFocus();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Button btnVerify = findViewById(R.id.sign_detail_btn_verify);
        btnVerify.setOnClickListener(view -> {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Toast.makeText(SignDetail.this, "Please go to verify your e-mail address!", Toast.LENGTH_SHORT).show();
            }
            btnVerify.setBackgroundColor(getResources().getColor(R.color.green));
            btnVerify.setClickable(false);
        });

        Button btnUpdate = findViewById(R.id.sign_detail_btn_confirm);
        btnUpdate.setOnClickListener(view -> {
            TextInputEditText tietName = findViewById(R.id.sign_detail_tiet_name);

            String name = Objects.requireNonNull(tietName.getText()).toString().trim();
            String ageString = Objects.requireNonNull(tietAge.getText()).toString().trim();
            String heightString = Objects.requireNonNull(tietHeight.getText()).toString().trim();

            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(ageString) && TextUtils.isEmpty(heightString)) {
                tietName.setError("Name is required!");
                tietName.requestFocus();
                tietAge.setError("Age is required!");
                tietHeight.setError("Height is required!");
                return;
            } else if (TextUtils.isEmpty(name) && TextUtils.isEmpty(ageString)) {
                tietName.setError("Name is required!");
                tietName.requestFocus();
                tietAge.setError("Age is required!");
                return;
            } else if (TextUtils.isEmpty(name) && TextUtils.isEmpty(heightString)) {
                tietName.setError("Name is required!");
                tietName.requestFocus();
                tietHeight.setError("Height is required!");
                return;
            } else if (TextUtils.isEmpty(ageString) && TextUtils.isEmpty(heightString)) {
                tietAge.setError("Age is required!");
                tietAge.requestFocus();
                tietHeight.setError("Height is required!");
                return;
            } else if (TextUtils.isEmpty(name)) {
                tietName.setError("Name is required!");
                tietName.requestFocus();
                return;
            } else if (TextUtils.isEmpty(ageString)) {
                tietAge.setError("Age is required!");
                tietAge.requestFocus();
                return;
            } else if (TextUtils.isEmpty(heightString)) {
                tietHeight.setError("Height is required!");
                tietHeight.requestFocus();
                return;
            } else if (tietHeight.getError() != null) {
                tietHeight.requestFocus();
                return;
            } else if (btnVerify.isClickable()) {
                Toast.makeText(SignDetail.this, "Please go to verify your e-mail address!", Toast.LENGTH_SHORT).show();
                return;
            }
            String gender = actvGender.getText().toString().trim();
            int age = Integer.parseInt(ageString);
            int height = Integer.parseInt(heightString);
            String unit = actvUnit.getText().toString().trim();

            Map<String, Object> note = new HashMap<>();
            note.put("name", name);
            note.put("gender", gender);
            note.put("age", age);
            note.put("height", height);
            note.put("unit", unit);

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection(Objects.requireNonNull(firebaseUser.getEmail()))
                    .document("Account")
                    .update(note)
                    .addOnSuccessListener(unused -> {
                        firebaseAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), SignIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("Status", "Signed Out");
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(SignDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        firebaseAuth.signOut();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}