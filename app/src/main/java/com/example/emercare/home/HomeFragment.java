package com.example.emercare.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emercare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("FieldMayBeFinal")
public class HomeFragment extends Fragment implements View.OnClickListener {
    private TextView tvSelectionGlucose, tvSelectionPressure, tvSelectionWeight, tvGlucose, tvPressure, tvWeight;
    private RecyclerView rvParent;
    private CardView cvWelcome;
    private FirebaseUser firebaseUser;
    private String recordString, realDate, dateString, newDateString;
    private List<String> dateList = new ArrayList<>(), newDateList;
    private ArrayList<RecordChildData> recordChildDataList = new ArrayList<>();
    private ArrayList<RecordParentData> recordParentDataList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSelectionGlucose = requireView().findViewById(R.id.home_selection_tv_glucose);
        tvSelectionPressure = requireView().findViewById(R.id.home_selection_tv_pressure);
        tvSelectionWeight = requireView().findViewById(R.id.home_selection_tv_weight);

        tvGlucose = requireView().findViewById(R.id.home_tv_glucose);
        tvGlucose.setOnClickListener(this);
        tvGlucose.setClickable(false);

        tvPressure = requireView().findViewById(R.id.home_tv_pressure);
        tvPressure.setOnClickListener(this);

        tvWeight = requireView().findViewById(R.id.home_tv_weight);
        tvWeight.setOnClickListener(this);

        rvParent = requireView().findViewById(R.id.home_rv_parent);

        cvWelcome = requireView().findViewById(R.id.home_cv_welcome);

        if (isNetworkConnected()) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            performGlucoseClick();
        } else {
            rvParent.setVisibility(View.INVISIBLE);
            cvWelcome.setVisibility(View.VISIBLE);
        }

    }

    public void performGlucoseClick() {
        tvGlucose.performClick();
    }

    public void resetRecyclerView() {
        if (rvParent.getVisibility() == View.INVISIBLE) {
            rvParent.setVisibility(View.VISIBLE);
            cvWelcome.setVisibility(View.INVISIBLE);
        }
        dateList.clear();
        recordParentDataList.clear();
        recordChildDataList.clear();
    }

    public void getDateString(String dateString) {
        String[] date = dateString.split(" ");
        String newDate = "";

        for (String word : date) {
            if (!(word.equals("-1") || word.equals("-2") || word.equals("-3") || word.equals("-4")
                    || word.equals("-5") || word.equals("-6") || word.equals("-7"))) {
                newDate = newDate.concat(word).concat(" ");
            }
        }
        newDateString = newDate.trim();
    }

    public void removeDateString(String recordString) {
        if (recordString.equals("Blood Glucose") || recordString.equals("Blood Pressure")) {
            String[] date = dateString.split(" ");
            String newDate = "";

            for (String word : date) {
                if (!(word.equals("-1") || word.equals("-2") || word.equals("-3") || word.equals("-4")
                        || word.equals("-5") || word.equals("-6") || word.equals("-7") || word.equals("Monday")
                        || word.equals("Tuesday") || word.equals("Wednesday") || word.equals("Thursday")
                        || word.equals("Friday") || word.equals("Saturday") || word.equals("Sunday")
                        || word.equals("-"))) {
                    newDate = newDate.concat(word).concat(" ");
                }
            }
            realDate = newDate.trim();
            dateList.add(realDate);

        } else if (recordString.equals("Weight")) {
            String[] date = dateString.split(" ");
            String newDate = "";

            for (String word : date) {
                if (!(word.equals("Monday") || word.equals("Tuesday") || word.equals("Wednesday")
                        || word.equals("Thursday") || word.equals("Friday") || word.equals("Saturday")
                        || word.equals("Sunday") || word.equals("-"))) {
                    newDate = newDate.concat(word).concat(" ");
                }
            }
            realDate = newDate.trim();
            dateList.add(newDate.trim());
        }
    }

    public void getRecordData(Map.Entry<String, Object> entry2, String recordString) {
        switch (entry2.getKey()) {
            case "Before Breakfast":
                if (recordString.equals("Blood Glucose")) {
                    getGlucoseRecord(entry2, "Before Breakfast");
                } else if (recordString.equals("Blood Pressure")) {
                    getPressureRecord(entry2, "Before Breakfast");
                }
                break;
            case "After Breakfast":
                if (recordString.equals("Blood Glucose")) {
                    getGlucoseRecord(entry2, "After Breakfast");
                } else if (recordString.equals("Blood Pressure")) {
                    getPressureRecord(entry2, "After Breakfast");
                }
                break;
            case "Before Lunch":
                if (recordString.equals("Blood Glucose")) {
                    getGlucoseRecord(entry2, "Before Lunch");
                } else if (recordString.equals("Blood Pressure")) {
                    getPressureRecord(entry2, "Before Lunch");
                }
                break;
            case "After Lunch":
                if (recordString.equals("Blood Glucose")) {
                    getGlucoseRecord(entry2, "After Lunch");
                } else if (recordString.equals("Blood Pressure")) {
                    getPressureRecord(entry2, "After Lunch");
                }
                break;
            case "Before Dinner":
                if (recordString.equals("Blood Glucose")) {
                    getGlucoseRecord(entry2, "Before Dinner");
                } else if (recordString.equals("Blood Pressure")) {
                    getPressureRecord(entry2, "Before Dinner");
                }
                break;
            case "After Dinner":
                if (recordString.equals("Blood Glucose")) {
                    getGlucoseRecord(entry2, "After Dinner");
                } else if (recordString.equals("Blood Pressure")) {
                    getPressureRecord(entry2, "After Dinner");
                }
                break;
            case "At Bedtime":
                if (recordString.equals("Blood Glucose")) {
                    getGlucoseRecord(entry2, "At Bedtime");
                } else if (recordString.equals("Blood Pressure")) {
                    getPressureRecord(entry2, "At Bedtime");
                }
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void getGlucoseRecord(Map.Entry<String, Object> entry2, String period) {
        Map<String, Object> periodMap = (Map<String, Object>) entry2.getValue();

        String record = "", unit = "";

        for (Map.Entry<String, Object> entry3 : periodMap.entrySet()) {
            if (entry3.getKey().equals("record")) {
                record = entry3.getValue().toString();
            }
            if (entry3.getKey().equals("unit")) {
                unit = entry3.getValue().toString();
            }
        }

        RecordChildData recordChildData = new RecordChildData(realDate, newDateString, period,
                R.drawable.blood_glucose, "Blood Glucose", record + " " + unit, record, unit, null);
        recordChildDataList.add(recordChildData);
    }

    @SuppressWarnings("unchecked")
    public void getPressureRecord(Map.Entry<String, Object> entry2, String period) {
        Map<String, Object> periodMap = (Map<String, Object>) entry2.getValue();

        String systolic = "", diastolic = "", pulse = "";

        for (Map.Entry<String, Object> entry3 : periodMap.entrySet()) {
            if (entry3.getKey().equals("systolic")) {
                systolic = entry3.getValue().toString();
            }
            if (entry3.getKey().equals("diastolic")) {
                diastolic = entry3.getValue().toString();
            }
            if (entry3.getKey().equals("pulse")) {
                pulse = entry3.getValue().toString();
            }
        }

        RecordChildData recordChildData = new RecordChildData(realDate, newDateString, period,
                R.drawable.blood_pressure, "Blood Pressure", systolic + "/" + diastolic + " mmHg\n" + pulse + " bpm",
                systolic, diastolic, pulse);
        recordChildDataList.add(recordChildData);
    }

    public void removeDuplicateDate() {
        Set<String> set = new LinkedHashSet<>(dateList);
        newDateList = new ArrayList<>(set);
    }

    public void sortList(List<String> newDateList) {
        Collections.sort(newDateList, (s, t1) -> {
            try {
                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                return Objects.requireNonNull(dateFormat.parse(s)).compareTo(dateFormat.parse(t1));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        });
        Collections.reverse(newDateList);

        for (int i = 0; i < newDateList.size(); i++) {
            RecordParentData recordParentData = new RecordParentData(newDateList.get(i));
            recordParentDataList.add(recordParentData);
        }
    }

    public void createRecyclerView() {
        RecordParentAdapter recordParentAdapter = new RecordParentAdapter(requireActivity(), recordParentDataList, recordChildDataList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());

        rvParent.setLayoutManager(linearLayoutManager);
        rvParent.setAdapter(recordParentAdapter);

        if (recordParentAdapter.getItemCount() == 0) {
            rvParent.setVisibility(View.INVISIBLE);
            cvWelcome.setVisibility(View.VISIBLE);
        } else {
            rvParent.setVisibility(View.VISIBLE);
            cvWelcome.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        FirebaseFirestore firebaseFirestore;
        String email;

        if (view.getId() == R.id.home_tv_glucose) {
            tvGlucose.setClickable(false);
            tvPressure.setClickable(true);
            tvWeight.setClickable(true);

            tvSelectionPressure.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));
            tvSelectionGlucose.setBackground(getResources().getDrawable(R.drawable.selector_tab_select));
            tvSelectionWeight.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));

            tvGlucose.setTextColor(getResources().getColor(R.color.white));
            tvPressure.setTextColor(getResources().getColor(R.color.word));
            tvWeight.setTextColor(getResources().getColor(R.color.word));

            if (firebaseUser != null && isNetworkConnected()) {
                resetRecyclerView();

                firebaseFirestore = FirebaseFirestore.getInstance();

                email = firebaseUser.getEmail();

                recordString = "Blood Glucose";

                DocumentReference glucoseDocument = firebaseFirestore
                        .collection(Objects.requireNonNull(email))
                        .document(recordString);
                glucoseDocument
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task1.getResult();

                                if (documentSnapshot.exists()) {
                                    Map<String, Object> glucoseMap = documentSnapshot.getData();
                                    Objects.requireNonNull(glucoseMap).remove("e-mail");

                                    for (Map.Entry<String, Object> entry : Objects.requireNonNull(glucoseMap).entrySet()) {
                                        dateString = entry.getKey();

                                        getDateString(dateString);
                                        removeDateString(recordString);

                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> dateMap = (Map<String, Object>) entry.getValue();

                                        for (Map.Entry<String, Object> entry2 : dateMap.entrySet()) {
                                            getRecordData(entry2, recordString);
                                        }
                                    }
                                }
                            }
                            removeDuplicateDate();
                            sortList(newDateList);
                            createRecyclerView();
                        });
            } else {
                rvParent.setVisibility(View.INVISIBLE);
                cvWelcome.setVisibility(View.VISIBLE);
            }

        } else if (view.getId() == R.id.home_tv_pressure) {
            tvPressure.setClickable(false);
            tvGlucose.setClickable(true);
            tvWeight.setClickable(true);

            tvSelectionPressure.setBackground(getResources().getDrawable(R.drawable.selector_tab_select));
            tvSelectionGlucose.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));
            tvSelectionWeight.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));

            tvGlucose.setTextColor(getResources().getColor(R.color.word));
            tvPressure.setTextColor(getResources().getColor(R.color.white));
            tvWeight.setTextColor(getResources().getColor(R.color.word));

            if (firebaseUser != null && isNetworkConnected()) {
                resetRecyclerView();

                firebaseFirestore = FirebaseFirestore.getInstance();

                email = firebaseUser.getEmail();

                recordString = "Blood Pressure";

                DocumentReference pressureDocument = firebaseFirestore
                        .collection(Objects.requireNonNull(email))
                        .document(recordString);
                pressureDocument
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task1.getResult();

                                if (documentSnapshot.exists()) {
                                    Map<String, Object> pressureMap = documentSnapshot.getData();
                                    Objects.requireNonNull(pressureMap).remove("e-mail");

                                    for (Map.Entry<String, Object> entry : Objects.requireNonNull(pressureMap).entrySet()) {
                                        dateString = entry.getKey();

                                        getDateString(dateString);
                                        removeDateString(recordString);

                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> dateMap = (Map<String, Object>) entry.getValue();

                                        for (Map.Entry<String, Object> entry2 : dateMap.entrySet()) {
                                            getRecordData(entry2, recordString);
                                        }
                                    }
                                }
                            }
                            removeDuplicateDate();
                            sortList(newDateList);
                            createRecyclerView();
                        });
            } else {
                rvParent.setVisibility(View.INVISIBLE);
                cvWelcome.setVisibility(View.VISIBLE);
            }

        } else if (view.getId() == R.id.home_tv_weight) {
            tvWeight.setClickable(false);
            tvGlucose.setClickable(true);
            tvPressure.setClickable(true);

            tvSelectionPressure.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));
            tvSelectionGlucose.setBackground(getResources().getDrawable(R.drawable.background_dialog_tab));
            tvSelectionWeight.setBackground(getResources().getDrawable(R.drawable.selector_tab_select));

            tvGlucose.setTextColor(getResources().getColor(R.color.word));
            tvPressure.setTextColor(getResources().getColor(R.color.word));
            tvWeight.setTextColor(getResources().getColor(R.color.white));

            if (firebaseUser != null && isNetworkConnected()) {
                resetRecyclerView();

                firebaseFirestore = FirebaseFirestore.getInstance();

                email = firebaseUser.getEmail();

                recordString = "Weight";

                DocumentReference weightDocument = firebaseFirestore
                        .collection(Objects.requireNonNull(email))
                        .document(recordString);
                weightDocument
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();

                                if (documentSnapshot.exists()) {
                                    Map<String, Object> weightMap = documentSnapshot.getData();
                                    Objects.requireNonNull(weightMap).remove("e-mail");

                                    for (Map.Entry<String, Object> entry : Objects.requireNonNull(weightMap).entrySet()) {
                                        dateString = entry.getKey();

                                        getDateString(dateString);
                                        removeDateString(recordString);

                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> dateMap = (Map<String, Object>) entry.getValue();
                                        String record = "", unit = "";

                                        for (Map.Entry<String, Object> entry2 : dateMap.entrySet()) {
                                            if (entry2.getKey().equals("record")) {
                                                record = entry2.getValue().toString();
                                            }
                                            if (entry2.getKey().equals("unit")) {
                                                unit = entry2.getValue().toString();
                                            }
                                        }
                                        RecordChildData recordChildData = new RecordChildData(realDate, newDateString,
                                                "All Day", R.drawable.weight, "Weight", record + " " + unit, record, unit, null);
                                        recordChildDataList.add(recordChildData);
                                    }
                                }
                            }
                            sortList(dateList);
                            createRecyclerView();
                        });
            } else {
                rvParent.setVisibility(View.INVISIBLE);
                cvWelcome.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}