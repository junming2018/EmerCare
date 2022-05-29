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

public class RecordWeight extends AppCompatActivity {
    private TextInputEditText tietDate, tietWeight;
    private AutoCompleteTextView actvUnit;
    private Calendar calendar;
    private SimpleDateFormat dateFormat, dayFormat;
    protected String weightDay;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private String previousDateString;
    private Intent previousIntent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_weight);

        previousIntent = getIntent();

        ImageView imgBack = findViewById(R.id.record_weight_img_back);
        imgBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (isNetworkConnected()) {
            actvUnit = findViewById(R.id.record_weight_actv_unit);
            String[] unitList = {"kg", "lbs"};
            ArrayAdapter<String> adUnit = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, unitList);
            if (previousIntent.getStringExtra("Status") != null) {
                actvUnit.setText(previousIntent.getStringExtra("Unit"));
            } else {
                actvUnit.setText(adUnit.getItem(0), false);
            }
            actvUnit.setAdapter(adUnit);

            tietWeight = findViewById(R.id.record_weight_tiet_weight);
            tietWeight.setFilters(new InputFilter[]{new DoubleInputFilter("1", "500")});
            if (previousIntent.getStringExtra("Status") != null) {
                tietWeight.setText(previousIntent.getStringExtra("Data"));
            }

            tietDate = findViewById(R.id.record_weight_tiet_date);
            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            if (previousIntent.getStringExtra("Status") != null) {
                tietDate.setText(previousIntent.getStringExtra("Date"));
            } else {
                updateDate();
            }
            previousDateString = Objects.requireNonNull(tietDate.getText()).toString().trim();

            tietDate.setOnClickListener(view -> {
                if (actvUnit.hasFocus()) {
                    actvUnit.clearFocus();
                } else if (tietWeight.hasFocus()) {
                    tietWeight.clearFocus();
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
                    if (Objects.requireNonNull(actvUnit.getText()).toString().trim().equals("kg")) {
                        tietWeight.setFilters(new InputFilter[]{new DoubleInputFilter("1", "500")});
                        try {
                            if (Double.parseDouble(Objects.requireNonNull(tietWeight.getText()).toString().trim()) > 500) {
                                tietWeight.setError("Please enter a valid value!");
                                tietWeight.requestFocus();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (Objects.requireNonNull(actvUnit.getText()).toString().trim().equals("lbs")) {
                        tietWeight.setFilters(new InputFilter[]{new DoubleInputFilter("1", "1000")});
                        if (!(TextUtils.isEmpty(Objects.requireNonNull(tietWeight.getText()).toString().trim()))) {
                            if (tietWeight.getError() != null) {
                                tietWeight.setError(null);
                            }
                        }
                    }
                }
            });

            tietWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String text = charSequence.toString();
                    if (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 2) {
                        tietWeight.setText(text.substring(0, text.length() - 1));
                        tietWeight.setSelection(Objects.requireNonNull(tietWeight.getText()).length());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (Objects.requireNonNull(actvUnit.getText()).toString().trim().equals("kg")) {
                        try {
                            if (Double.parseDouble(Objects.requireNonNull(tietWeight.getText()).toString().trim()) > 500) {
                                tietWeight.setError("Please enter a valid value!");
                                tietWeight.requestFocus();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Button btnAdd = findViewById(R.id.record_weight_btn_add);
            if (previousIntent.getStringExtra("Status") != null) {
                btnAdd.setText(getResources().getText(R.string.update));
            }

            btnAdd.setOnClickListener(view -> {
                if (previousIntent.getStringExtra("Status") != null) {
                    Dialog dialog = new Dialog(RecordWeight.this);
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

            Button btnCancel = findViewById(R.id.record_weight_btn_cancel);
            if (previousIntent.getStringExtra("Status") != null) {
                btnCancel.setText(getResources().getText(R.string.delete));
            }

            btnCancel.setOnClickListener(view -> {
                if (previousIntent.getStringExtra("Status") != null) {
                    Dialog dialog = new Dialog(RecordWeight.this);
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
                        mapDate.put(previousDateString, FieldValue.delete());

                        firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
                            DocumentReference documentReference = firebaseFirestore
                                    .collection(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()))
                                    .document("Weight");
                            transaction.update(documentReference, mapDate);
                            return null;
                        }).addOnSuccessListener(unused -> {
                            Intent intent = new Intent(RecordWeight.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }).addOnFailureListener(e -> Toast.makeText(RecordWeight.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    });
                    dialog.show();
                } else {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        } else {
            ConstraintLayout clWeight = findViewById(R.id.record_weight_cl_weight);
            clWeight.setVisibility(View.INVISIBLE);

            CardView cvNo = findViewById(R.id.record_weight_cv_no);
            cvNo.setVisibility(View.VISIBLE);

            TextView tvNo = findViewById(R.id.record_weight_tv_no);
            tvNo.setText(getResources().getText(R.string.no_internet));
        }
    }

    public void updateDate() {
        weightDay = dayFormat.format(calendar.getTime());
        String date = dateFormat.format(calendar.getTime()).concat(" - ").concat(weightDay);
        tietDate.setText(date);
    }

    public void updateDatabase() {
        String weightString = Objects.requireNonNull(tietWeight.getText()).toString();
        try {
            if (Double.parseDouble(Objects.requireNonNull(tietWeight.getText()).toString()) == 0) {
                tietWeight.setError("Please enter a valid value!");
                tietWeight.requestFocus();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(weightString)) {
            tietWeight.setError("Weight data is required!");
            tietWeight.requestFocus();
            return;
        } else if (weightString.equals(".")) {
            tietWeight.setError("Please enter a valid value!");
            tietWeight.requestFocus();
            return;
        } else if (tietWeight.getError() != null) {
            tietWeight.requestFocus();
            return;
        } else if (!(weightString.contains("."))) {
            tietWeight.append(".00");
            weightString = weightString.concat(".00");
        }
        double weight = Double.parseDouble(weightString);
        String unit = actvUnit.getText().toString().trim();

        Map<String, Object> mapRecord = new HashMap<>();
        mapRecord.put("record", weight);
        mapRecord.put("unit", unit);

        Map<String, Object> mapDate = new HashMap<>();

        String newDateString = Objects.requireNonNull(tietDate.getText()).toString().trim();

        mapDate.put(newDateString, mapRecord);

        firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference documentReference = firebaseFirestore
                    .collection(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()))
                    .document("Weight");
            if (previousIntent.getStringExtra("Status") != null) {
                Map<String, Object> mapPreviousDate = new HashMap<>();
                mapPreviousDate.put(previousDateString, FieldValue.delete());

                transaction.update(documentReference, mapPreviousDate);
            }
            transaction.update(documentReference, mapDate);
            return null;
        }).addOnSuccessListener(unused -> {
            Intent intent = new Intent(RecordWeight.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }).addOnFailureListener(e -> Toast.makeText(RecordWeight.this, e.getMessage(), Toast.LENGTH_SHORT).show());
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