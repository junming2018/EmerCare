package com.example.emercare.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emercare.R;
import com.example.emercare.record.RecordGlucose;
import com.example.emercare.record.RecordPressure;
import com.example.emercare.record.RecordWeight;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class RecordChildAdapter extends RecyclerView.Adapter<RecordChildAdapter.ViewHolder> {
    Activity cActivity;
    RecordChildData recordChildData;
    ArrayList<RecordChildData> recordChildDataList;

    public RecordChildAdapter(RecordChildData recordChildData, Activity cActivity) {
        this.cActivity = cActivity;
        this.recordChildData = recordChildData;
    }

    public RecordChildAdapter(ArrayList<RecordChildData> recordChildDataList, Activity cActivity) {
        this.cActivity = cActivity;
        this.recordChildDataList = recordChildDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_record_child_item, parent, false);
        return new ViewHolder(view, cActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (recordChildDataList != null) {
            holder.bmiCheck = "No BMI";
            holder.dateString = recordChildDataList.get(position).getRecordDateString();
            holder.tvPeriod.setText(recordChildDataList.get(position).getRecordPeriod());
            holder.imgRecord.setImageResource(recordChildDataList.get(position).getRecordIcon());
            holder.tvRecord.setText(recordChildDataList.get(position).getChildRecord());
            holder.tvData.setText(recordChildDataList.get(position).getRecordData());

            double glucoseRecord = Double.parseDouble(recordChildDataList.get(position).getSystolicString());
            String unit = recordChildDataList.get(position).getDiastolicString();

            if (unit.equals("mmol/L")) {
                if (glucoseRecord >= 11.1 || glucoseRecord <= 2.9) {
                    holder.tvData.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
                } else if (glucoseRecord >= 7.8 || glucoseRecord <= 3.8) {
                    holder.tvData.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.yellow));
                }
            } else if (unit.equals("mg/dL")) {
                if (glucoseRecord >= 200.0 || glucoseRecord <= 53.0) {
                    holder.tvData.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
                } else if (glucoseRecord >= 140.0 || glucoseRecord <= 69.0) {
                    holder.tvData.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.yellow));
                }
            }

            if (recordChildDataList.get(position).getChildRecord().equals("Blood Pressure")) {
                holder.systolicString = recordChildDataList.get(position).getSystolicString();
                holder.diastolicString = recordChildDataList.get(position).getDiastolicString();
                holder.pulseString = recordChildDataList.get(position).getPulseString();

                int systolicRecord = Integer.parseInt(recordChildDataList.get(position).getSystolicString());
                int diastolicRecord = Integer.parseInt(recordChildDataList.get(position).getDiastolicString());

                if (systolicRecord >= 140 || diastolicRecord >= 90 || systolicRecord <= 90 || diastolicRecord <= 60) {
                    holder.tvData.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
                }
            }
        }
        if (recordChildData != null) {
            holder.dateString = recordChildData.getRecordDateString();
            holder.imgRecord.setImageResource(recordChildData.getRecordIcon());

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore
                    .collection(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                    .document("Account");
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (!(documentSnapshot.getLong("height") == null || documentSnapshot.getString("unit") == null)) {
                        double weight = Double.parseDouble(recordChildData.getSystolicString());
                        double height = (double) Objects.requireNonNull(documentSnapshot.getLong("height"));

                        if (Objects.requireNonNull(documentSnapshot.getString("unit")).equals("cm")) {
                            height /= 100;
                        } else if (Objects.requireNonNull(documentSnapshot.getString("unit")).equals("inch")) {
                            height *= 0.0254;
                        }
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        double bmi = weight / (height * height);
                        String bmiString = decimalFormat.format(bmi);

                        holder.bmiCheck = bmiString;
                        holder.tvPeriod.setText(recordChildData.getChildRecord());
                        holder.tvPeriod.setTypeface(Typeface.SANS_SERIF);
                        holder.tvRecord.setText(recordChildData.getRecordData());
                        holder.tvRecord.setTypeface(Typeface.DEFAULT_BOLD);
                        holder.tvData.setText(holder.itemView.getContext().getString(R.string.bmi)
                                .concat(bmiString).concat(holder.itemView.getContext().getString(R.string.bmi_unit)));

                        if (bmi >= 30.00 || bmi <= 17.0) {
                            holder.tvData.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
                        } else if ((bmi >= 25.0 && bmi <= 29.9) || (bmi >= 17.1 && bmi <= 18.4)) {
                            holder.tvData.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.yellow));
                        }
                    } else {
                        holder.bmiCheck = "No BMI";
                        holder.tvPeriod.setText(holder.itemView.getResources().getText(R.string.all));
                        holder.tvRecord.setText(recordChildData.getChildRecord());
                        holder.tvData.setText(recordChildData.getRecordData());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (recordChildDataList != null) {
            return recordChildDataList.size();
        }
        return 1;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Activity cActivity;
        String dateString, systolicString, diastolicString, pulseString, bmiCheck;
        TextView tvPeriod, tvRecord, tvData;
        ImageView imgRecord;

        public ViewHolder(@NonNull View itemView, Activity cActivity) {
            super(itemView);
            this.cActivity = cActivity;
            tvPeriod = itemView.findViewById(R.id.home_list_tv_period);
            imgRecord = itemView.findViewById(R.id.home_list_img_record);
            tvRecord = itemView.findViewById(R.id.home_list_tv_record);
            tvData = itemView.findViewById(R.id.home_list_tv_data);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = null;

            String newData = "", unit = "";

            if (bmiCheck.equals("No BMI")) {

                switch (tvRecord.getText().toString()) {

                    case "Blood Glucose":
                        intent = new Intent(view.getContext(), RecordGlucose.class);
                        intent.putExtra("Status", "Update");
                        intent.putExtra("Date", dateString);
                        intent.putExtra("Period", tvPeriod.getText().toString());

                        String glucoseData = tvData.getText().toString();

                        if (glucoseData.contains("mmol/L")) {
                            newData = glucoseData.replaceAll("mmol/L", "").trim();
                            unit = "mmol/L";
                        } else if (glucoseData.contains("mg/dL")) {
                            newData = glucoseData.replaceAll("mg/dL", "").trim();
                            unit = "mg/dL";
                        }

                        intent.putExtra("Unit", unit);
                        intent.putExtra("Data", newData);
                        break;

                    case "Blood Pressure":
                        intent = new Intent(view.getContext(), RecordPressure.class);
                        intent.putExtra("Status", "Update");
                        intent.putExtra("Date", dateString);
                        intent.putExtra("Period", tvPeriod.getText().toString());
                        intent.putExtra("Systolic", systolicString);
                        intent.putExtra("Diastolic", diastolicString);
                        intent.putExtra("Pulse", pulseString);
                        break;

                    case "Weight":
                        intent = new Intent(view.getContext(), RecordWeight.class);
                        intent.putExtra("Status", "Update");
                        intent.putExtra("Date", dateString);

                        String weightData;

                        if (bmiCheck.equals("No BMI")) {
                            weightData = tvData.getText().toString();
                        } else {
                            weightData = tvRecord.getText().toString();
                        }

                        if (weightData.contains("kg")) {
                            newData = weightData.replaceAll("kg", "").trim();
                            unit = "kg";
                        } else if (weightData.contains("lbs")) {
                            newData = weightData.replaceAll("lbs", "").trim();
                            unit = "lbs";
                        }

                        intent.putExtra("Unit", unit);
                        intent.putExtra("Data", newData);
                        break;
                }
            } else {
                intent = new Intent(view.getContext(), RecordWeight.class);
                intent.putExtra("Status", "Update");
                intent.putExtra("Date", dateString);

                String weightData;

                if (bmiCheck.equals("No BMI")) {
                    weightData = tvData.getText().toString();
                } else {
                    weightData = tvRecord.getText().toString();
                }

                if (weightData.contains("kg")) {
                    newData = weightData.replaceAll("kg", "").trim();
                    unit = "kg";
                } else if (weightData.contains("lbs")) {
                    newData = weightData.replaceAll("lbs", "").trim();
                    unit = "lbs";
                }

                intent.putExtra("Unit", unit);
                intent.putExtra("Data", newData);
            }
            view.getContext().startActivity(intent);
            cActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}