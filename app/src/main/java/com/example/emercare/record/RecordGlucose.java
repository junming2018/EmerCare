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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.example.emercare.Filter.DoubleInputFilter;
import com.example.emercare.MainActivity;
import com.example.emercare.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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

public class RecordGlucose extends AppCompatActivity {
    private TextInputEditText tietDate, tietGlucose;
    private AutoCompleteTextView actvPeriod, actvUnit;
    private Calendar calendar;
    private SimpleDateFormat dateFormat, dayFormat;
    protected String glucoseDay;
    private String previousDateStringWithTag;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private Intent previousIntent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_glucose);

        previousIntent = getIntent();

        ImageView imgBack = findViewById(R.id.record_glucose_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (isNetworkConnected()) {
            actvPeriod = findViewById(R.id.record_glucose_actv_period);
            String[] periodList = {"Before Breakfast", "After Breakfast", "Before Lunch", "After Lunch", "Before Dinner", "After Dinner", "At Bedtime"};
            ArrayAdapter<String> ad_period = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, periodList);
            if (previousIntent.getStringExtra("Status") != null) {
                actvPeriod.setText(previousIntent.getStringExtra("Period"));
            } else {
                actvPeriod.setText(ad_period.getItem(0), false);
            }
            actvPeriod.setAdapter(ad_period);

            actvUnit = findViewById(R.id.record_glucose_actv_unit);
            String[] unitList = {"mmol/L", "mg/dL"};
            ArrayAdapter<String> ad_unit = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, unitList);
            if (previousIntent.getStringExtra("Status") != null) {
                actvUnit.setText(previousIntent.getStringExtra("Unit"));
            } else {
                actvUnit.setText(ad_unit.getItem(0), false);
            }
            actvUnit.setAdapter(ad_unit);

            tietGlucose = findViewById(R.id.record_glucose_tiet_glucose);
            tietGlucose.setFilters(new InputFilter[]{new DoubleInputFilter("1", "50")});
            if (previousIntent.getStringExtra("Status") != null) {
                tietGlucose.setText(previousIntent.getStringExtra("Data"));
            }

            tietDate = findViewById(R.id.record_glucose_tiet_date);
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
                } else if (actvUnit.hasFocus()) {
                    actvUnit.clearFocus();
                } else if (tietGlucose.hasFocus()) {
                    tietGlucose.clearFocus();
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

            actvUnit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (Objects.requireNonNull(actvUnit.getText()).toString().trim().equals("mmol/L")) {
                        tietGlucose.setFilters(new InputFilter[]{new DoubleInputFilter("1", "50")});
                        try {
                            if (Double.parseDouble(Objects.requireNonNull(tietGlucose.getText()).toString().trim()) > 50) {
                                tietGlucose.setError("Please enter a valid value!");
                                tietGlucose.requestFocus();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (Objects.requireNonNull(actvUnit.getText()).toString().trim().equals("mg/dL")) {
                        tietGlucose.setFilters(new InputFilter[]{new DoubleInputFilter("1", "900")});
                        if (!(TextUtils.isEmpty(Objects.requireNonNull(tietGlucose.getText()).toString().trim()))) {
                            if (tietGlucose.getError() != null) {
                                tietGlucose.setError(null);
                            }
                        }
                    }
                }
            });

            tietGlucose.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String text = charSequence.toString();
                    if (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 2) {
                        tietGlucose.setText(text.substring(0, text.length() - 1));
                        tietGlucose.setSelection(Objects.requireNonNull(tietGlucose.getText()).length());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (Objects.requireNonNull(actvUnit.getText()).toString().trim().equals("mmol/L")) {
                        try {
                            if (Double.parseDouble(Objects.requireNonNull(tietGlucose.getText()).toString().trim()) > 50) {
                                tietGlucose.setError("Please enter a valid value!");
                                tietGlucose.requestFocus();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Button btnAdd = findViewById(R.id.record_glucose_btn_add);
            if (previousIntent.getStringExtra("Status") != null) {
                btnAdd.setText(getResources().getText(R.string.update));
            }

            btnAdd.setOnClickListener(view -> {
                if (previousIntent.getStringExtra("Status") != null) {
                    Dialog dialog = new Dialog(RecordGlucose.this);
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

            Button btnCancel = findViewById(R.id.record_glucose_btn_cancel);
            if (previousIntent.getStringExtra("Status") != null) {
                btnCancel.setText(getResources().getText(R.string.delete));
            }

            btnCancel.setOnClickListener(view -> {
                if (previousIntent.getStringExtra("Status") != null) {
                    Dialog dialog = new Dialog(RecordGlucose.this);
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
                                    .document("Blood Glucose");
                            transaction.update(documentReference, mapDate);
                            return null;
                        }).addOnSuccessListener(unused -> {
                            Intent intent = new Intent(RecordGlucose.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }).addOnFailureListener(e -> Toast.makeText(RecordGlucose.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    });
                    dialog.show();
                } else {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        } else {
            ConstraintLayout clGlucose = findViewById(R.id.record_glucose_cl_glucose);
            clGlucose.setVisibility(View.INVISIBLE);

            CardView cvNo = findViewById(R.id.record_glucose_cv_no);
            cvNo.setVisibility(View.VISIBLE);

            TextView tvNo = findViewById(R.id.record_glucose_tv_no);
            tvNo.setText(getResources().getText(R.string.no_internet));
        }
    }

    public void updateDate() {
        glucoseDay = dayFormat.format(calendar.getTime());
        String date = dateFormat.format(calendar.getTime()).concat(" - ").concat(glucoseDay);
        tietDate.setText(date);
    }

    public void updateDatabase() {
        String glucoseString = Objects.requireNonNull(tietGlucose.getText()).toString();
        try {
            if (Double.parseDouble(Objects.requireNonNull(tietGlucose.getText()).toString()) == 0) {
                tietGlucose.setError("Please enter a valid value!");
                tietGlucose.requestFocus();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(glucoseString)) {
            tietGlucose.setError("Blood glucose data is required!");
            tietGlucose.requestFocus();
            return;
        } else if (glucoseString.equals(".")) {
            tietGlucose.setError("Please enter a valid value!");
            tietGlucose.requestFocus();
            return;
        } else if (tietGlucose.getError() != null) {
            tietGlucose.requestFocus();
            return;
        } else if (!(glucoseString.contains("."))) {
            tietGlucose.append(".00");
        }
        String period = actvPeriod.getText().toString().trim();
        double glucose = Double.parseDouble(glucoseString);
        String unit = actvUnit.getText().toString().trim();

        Map<String, Object> mapRecord = new HashMap<>();
        mapRecord.put("record", glucose);
        mapRecord.put("unit", unit);

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
                    .document("Blood Glucose");
            if (previousIntent.getStringExtra("Status") != null) {
                Map<String, Object> mapPreviousDate = new HashMap<>();
                mapPreviousDate.put(previousDateStringWithTag, FieldValue.delete());

                transaction.update(documentReference, mapPreviousDate);
            }
            transaction.update(documentReference, mapDate);
            return null;
        }).addOnSuccessListener(unused -> {
            Intent intent = new Intent(RecordGlucose.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }).addOnFailureListener(e -> Toast.makeText(RecordGlucose.this, e.getMessage(), Toast.LENGTH_SHORT).show());
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