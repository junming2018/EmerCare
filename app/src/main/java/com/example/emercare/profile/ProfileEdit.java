package com.example.emercare.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.emercare.Filter.IntegerInputFilter;
import com.example.emercare.MainActivity;
import com.example.emercare.R;
import com.example.emercare.record.RecordWeight;
import com.example.emercare.sign.SignDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        ImageView imgBack = findViewById(R.id.profile_edit_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        TextInputEditText tietName = findViewById(R.id.profile_edit_tiet_name);

        AutoCompleteTextView actvGender = findViewById(R.id.profile_edit_actv_gender);
        String[] genderList = {"Male", "Female"};
        ArrayAdapter<String> adGender = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, genderList);

        TextInputEditText tietAge = findViewById(R.id.profile_edit_tiet_age);
        tietAge.setFilters(new InputFilter[]{new IntegerInputFilter("1", "125")});

        TextInputEditText tietHeight = findViewById(R.id.profile_edit_tiet_height);
        tietHeight.setFilters(new InputFilter[]{new IntegerInputFilter("1", "300")});

        AutoCompleteTextView actvUnit = findViewById(R.id.profile_edit_actv_unit);
        String[] unitList = {"cm", "inch"};
        ArrayAdapter<String> adUnit = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, unitList);

        Intent previousIntent = getIntent();
        if (previousIntent.getBooleanExtra("hasData", true)) {
            tietName.setText(previousIntent.getStringExtra("name"));
            actvGender.setText(previousIntent.getStringExtra("gender"));
            tietAge.setText(String.valueOf(previousIntent.getLongExtra("age", 1)));
            tietHeight.setText(String.valueOf(previousIntent.getLongExtra("height", 1)));
            actvUnit.setText(previousIntent.getStringExtra("unit"));
        } else {
            actvGender.setText(adGender.getItem(0), false);
            actvUnit.setText(adUnit.getItem(0), false);
        }

        actvGender.setAdapter(adGender);

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

        Button btnUpdate = findViewById(R.id.profile_edit_btn_update);
        btnUpdate.setOnClickListener(view -> {
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
            }
            String gender = actvGender.getText().toString().trim();
            int age = Integer.parseInt(ageString);
            int height = Integer.parseInt(heightString);
            String unit = actvUnit.getText().toString().trim();

            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("gender", gender);
            map.put("age", age);
            map.put("height", height);
            map.put("unit", unit);

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentReference documentReference = firebaseFirestore
                        .collection(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()))
                        .document("Account");
                transaction.update(documentReference, map);
                return null;
            }).addOnSuccessListener(unused -> {
                Intent intent = new Intent(ProfileEdit.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("Status", "Profile Edited");
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }).addOnFailureListener(e -> Toast.makeText(ProfileEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        Button btnCancel = findViewById(R.id.profile_edit_btn_cancel);
        btnCancel.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}