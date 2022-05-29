package com.example.emercare.record;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.emercare.Filter.IntegerInputFilter;
import com.example.emercare.MainActivity;
import com.example.emercare.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RecordPressure extends AppCompatActivity {
    private TextInputEditText tietDate, tietSystolic, tietDiastolic, tietPulse;
    private AutoCompleteTextView actvPeriod;
    private Calendar calendar;
    private SimpleDateFormat dateFormat, dayFormat;
    protected String pressureDay;
    private String previousDateStringWithTag;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private Intent previousIntent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_pressure);

        previousIntent = getIntent();

        ImageView imgBack = findViewById(R.id.record_pressure_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (isNetworkConnected()) {
            actvPeriod = findViewById(R.id.record_pressure_actv_period);
            String[] periodList = {"Before Breakfast", "After Breakfast", "Before Lunch", "After Lunch", "Before Dinner", "After Dinner", "At Bedtime"};
            ArrayAdapter<String> adPeriod = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, periodList);
            if (previousIntent.getStringExtra("Status") != null) {
                actvPeriod.setText(previousIntent.getStringExtra("Period"));
            } else {
                actvPeriod.setText(adPeriod.getItem(0), false);
            }
            actvPeriod.setAdapter(adPeriod);

            tietSystolic = findViewById(R.id.record_pressure_tiet_systolic);
            tietSystolic.setFilters(new InputFilter[]{new IntegerInputFilter("1", "500")});

            tietDiastolic = findViewById(R.id.record_pressure_tiet_diastolic);
            tietDiastolic.setFilters(new InputFilter[]{new IntegerInputFilter("1", "500")});

            tietPulse = findViewById(R.id.record_pressure_tiet_pulse);
            tietPulse.setFilters(new InputFilter[]{new IntegerInputFilter("1", "500")});

            if (previousIntent.getStringExtra("Status") != null) {
                tietSystolic.setText(previousIntent.getStringExtra("Systolic"));
                tietDiastolic.setText(previousIntent.getStringExtra("Diastolic"));
                tietPulse.setText(previousIntent.getStringExtra("Pulse"));
            }

            tietDate = findViewById(R.id.record_pressure_tiet_date);
            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            if (previousIntent.getStringExtra("Status") != null) {
                tietDate.setText(previousIntent.getStringExtra("Date"));
            } else {
                updateDate();
            }

            switch (actvPeriod.getText().toString().trim()) {
                case "Before Breakfast":
                    previousDateStringWithTag = Objects.requireNonNull(tietDate.getText()).toString().trim().concat(" -1");
                    break;
                case "After Breakfast":
                    previousDateStringWithTag = Objects.requireNonNull(tietDate.getText()).toString().trim().concat(" -2");
                    break;
                case "Before Lunch":
                    previousDateStringWithTag = Objects.requireNonNull(tietDate.getText()).toString().trim().concat(" -3");
                    break;
                case "After Lunch":
                    previousDateStringWithTag = Objects.requireNonNull(tietDate.getText()).toString().trim().concat(" -4");
                    break;
                case "Before Dinner":
                    previousDateStringWithTag = Objects.requireNonNull(tietDate.getText()).toString().trim().concat(" -5");
                    break;
                case "After Dinner":
                    previousDateStringWithTag = Objects.requireNonNull(tietDate.getText()).toString().trim().concat(" -6");
                    break;
                case "At Bedtime":
                    previousDateStringWithTag = Objects.requireNonNull(tietDate.getText()).toString().trim().concat(" -7");
                    break;
            }

            tietDate.setOnClickListener(view -> {
                if (actvPeriod.hasFocus()) {
                    actvPeriod.clearFocus();
                } else if (tietSystolic.hasFocus()) {
                    tietSystolic.clearFocus();
                } else if (tietDiastolic.hasFocus()) {
                    tietDiastolic.clearFocus();
                } else if (tietPulse.hasFocus()) {
                    tietPulse.clearFocus();
                }
                CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
                constraintBuilder.setValidator(DateValidatorPointBackward.now());
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select a Date")
                        .setCalendarConstraints(constraintBuilder.build()).build();
                datePicker.show(getSupportFragmentManager(), "Date_Picker");
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    calendar.setTimeInMillis(selection);
                    updateDate();
                });
            });

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            firebaseFirestore = FirebaseFirestore.getInstance();

            Button btnAdd = findViewById(R.id.record_pressure_btn_add);
            if (previousIntent.getStringExtra("Status") != null) {
                btnAdd.setText(getResources().getText(R.string.update));
            }

            btnAdd.setOnClickListener(view -> {
                if (previousIntent.getStringExtra("Status") != null) {
                    Dialog dialog = new Dialog(RecordPressure.this);
                    dialog.setContentView(R.layout.dialog_delete);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView tvTitle = dialog.findViewById(R.id.dialog_delete_tv_title);
                    tvTitle.setText(getResources().getText(R.string.update_record));

                    TextView tvDescription = dialog.findViewById(R.id.dialog_delete_tv_description);
                    tvDescription.setText(getResources().getText(R.string.are_you_sure_you));

                    Button btnNo = dialog.findViewById(R.id.dialog_delete_btn_no);
                    btnNo.setBackgroundColor(getResources().getColor(R.color.red));
                    btnNo.setOnClickListener(view1 -> dialog.cancel());

                    Button btnYes = dialog.findViewById(R.id.dialog_delete_btn_yes);
                    btnYes.setBackgroundColor(getResources().getColor(R.color.green));
                    btnYes.setOnClickListener(view12 -> updateDatabase());
                    dialog.show();
                } else {
                    updateDatabase();
                }
            });

            Button btnCancel = findViewById(R.id.record_pressure_btn_cancel);
            if (previousIntent.getStringExtra("Status") != null) {
                btnCancel.setText(getResources().getText(R.string.delete));
            }

            btnCancel.setOnClickListener(view -> {
                if (previousIntent.getStringExtra("Status") != null) {
                    Dialog dialog = new Dialog(RecordPressure.this);
                    dialog.setContentView(R.layout.dialog_delete);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView tvTitle = dialog.findViewById(R.id.dialog_delete_tv_title);
                    tvTitle.setText(getResources().getText(R.string.delete_record));

                    TextView tvDescription = dialog.findViewById(R.id.dialog_delete_tv_description);
                    tvDescription.setText(getResources().getText(R.string.are_you_sure));

                    Button btnNo = dialog.findViewById(R.id.dialog_delete_btn_no);
                    btnNo.setOnClickListener(view1 -> dialog.cancel());

                    Button btnYes = dialog.findViewById(R.id.dialog_delete_btn_yes);
                    btnYes.setOnClickListener(view12 -> {

                        Map<String, Object> mapDate = new HashMap<>();
                        mapDate.put(previousDateStringWithTag, FieldValue.delete());

                        firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
                            DocumentReference documentReference = firebaseFirestore
                                    .collection(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()))
                                    .document("Blood Pressure");
                            transaction.update(documentReference, mapDate);
                            return null;
                        }).addOnSuccessListener(unused -> {
                            Intent intent = new Intent(RecordPressure.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }).addOnFailureListener(e -> Toast.makeText(RecordPressure.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    });
                    dialog.show();
                } else {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        } else {
            ConstraintLayout clPressure = findViewById(R.id.record_pressure_cl_pressure);
            clPressure.setVisibility(View.INVISIBLE);

            CardView cvNo = findViewById(R.id.record_pressure_cv_no);
            cvNo.setVisibility(View.VISIBLE);

            TextView tvNo = findViewById(R.id.record_pressure_tv_no);
            tvNo.setText(getResources().getText(R.string.no_internet));
        }
    }

    public void updateDate() {
        pressureDay = dayFormat.format(calendar.getTime());
        String date = dateFormat.format(calendar.getTime()).concat(" - ").concat(pressureDay);
        tietDate.setText(date);
    }

    public void updateDatabase() {
        String systolicString = Objects.requireNonNull(tietSystolic.getText()).toString();
        String diastolicString = Objects.requireNonNull(tietDiastolic.getText()).toString();
        String pulseString = Objects.requireNonNull(tietPulse.getText()).toString();

        if (TextUtils.isEmpty(systolicString) && TextUtils.isEmpty(diastolicString) && TextUtils.isEmpty(pulseString)) {
            tietSystolic.setError("Systolic data is required!");
            tietSystolic.requestFocus();
            tietDiastolic.setError("Diastolic data is required!");
            tietPulse.setError("Pulse data is required!");
            return;
        } else if (TextUtils.isEmpty(systolicString) && TextUtils.isEmpty(diastolicString)) {
            tietSystolic.setError("Systolic data is required!");
            tietSystolic.requestFocus();
            tietDiastolic.setError("Diastolic data is required!");
            return;
        } else if (TextUtils.isEmpty(systolicString) && TextUtils.isEmpty(pulseString)) {
            tietSystolic.setError("Systolic data is required!");
            tietSystolic.requestFocus();
            tietPulse.setError("Pulse data is required!");
            return;
        } else if (TextUtils.isEmpty(diastolicString) && TextUtils.isEmpty(pulseString)) {
            tietDiastolic.setError("Diastolic data is required!");
            tietDiastolic.requestFocus();
            tietPulse.setError("Pulse data is required!");
            return;
        } else if (TextUtils.isEmpty(systolicString)) {
            tietSystolic.setError("Systolic data is required!");
            tietSystolic.requestFocus();
            return;
        } else if (TextUtils.isEmpty(diastolicString)) {
            tietDiastolic.setError("Diastolic data is required!");
            tietDiastolic.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pulseString)) {
            tietPulse.setError("Pulse data is required!");
            tietPulse.requestFocus();
            return;
        }
        String period = actvPeriod.getText().toString().trim();
        int systolic = Integer.parseInt(systolicString);
        int diastolic = Integer.parseInt(diastolicString);
        int pulse = Integer.parseInt(pulseString);

        Map<String, Object> mapRecord = new HashMap<>();
        mapRecord.put("systolic", systolic);
        mapRecord.put("diastolic", diastolic);
        mapRecord.put("pulse", pulse);

        Map<String, Object> mapPeriod = new HashMap<>();
        mapPeriod.put(period, mapRecord);

        Map<String, Object> mapDate = new HashMap<>();

        String newDateString = Objects.requireNonNull(tietDate.getText()).toString().trim();

        switch (period) {
            case "Before Breakfast":
                mapDate.put(newDateString + " -1", mapPeriod);
                break;
            case "After Breakfast":
                mapDate.put(newDateString + " -2", mapPeriod);
                break;
            case "Before Lunch":
                mapDate.put(newDateString + " -3", mapPeriod);
                break;
            case "After Lunch":
                mapDate.put(newDateString + " -4", mapPeriod);
                break;
            case "Before Dinner":
                mapDate.put(newDateString + " -5", mapPeriod);
                break;
            case "After Dinner":
                mapDate.put(newDateString + " -6", mapPeriod);
                break;
            case "At Bedtime":
                mapDate.put(newDateString + " -7", mapPeriod);
                break;
        }
        firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference documentReference = firebaseFirestore
                    .collection(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()))
                    .document("Blood Pressure");
            if (previousIntent.getStringExtra("Status") != null) {
                Map<String, Object> mapPreviousDate = new HashMap<>();
                mapPreviousDate.put(previousDateStringWithTag, FieldValue.delete());

                transaction.update(documentReference, mapPreviousDate);
            }
            transaction.update(documentReference, mapDate);
            return null;
        }).addOnSuccessListener(unused -> {
            Intent intent = new Intent(RecordPressure.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }).addOnFailureListener(e -> Toast.makeText(RecordPressure.this, e.getMessage(), Toast.LENGTH_SHORT).show());
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