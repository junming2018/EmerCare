package com.example.emercare.record;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emercare.R;
import com.example.emercare.sign.SignIn;
import com.google.firebase.auth.FirebaseAuth;

public class RecordFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                CardView cvGlucose = requireView().findViewById(R.id.record_cv_glucose);
                cvGlucose.setOnClickListener(view1 -> {
                    Intent intent = new Intent(getActivity(), RecordGlucose.class);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });

                CardView cvPressure = requireView().findViewById(R.id.record_cv_pressure);
                cvPressure.setOnClickListener(view1 -> {
                    Intent intent = new Intent(getActivity(), RecordPressure.class);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });

                CardView cvWeight = requireView().findViewById(R.id.record_cv_weight);
                cvWeight.setOnClickListener(view1 -> {
                    Intent intent = new Intent(getActivity(), RecordWeight.class);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
            } else {
                ConstraintLayout clRecord = requireView().findViewById(R.id.record_cl_record);
                clRecord.setVisibility(View.INVISIBLE);

                CardView cvSign = requireView().findViewById(R.id.record_cv_sign);
                cvSign.setVisibility(View.VISIBLE);

                TextView tvSign = requireView().findViewById(R.id.record_tv_sign);
                tvSign.setText(getResources().getText(R.string.please_verify));
                tvSign.setTextSize(15);

                Button btnSign = requireView().findViewById(R.id.record_btn_sign);
                btnSign.setText(getResources().getText(R.string.send));
                btnSign.setBackgroundColor(getResources().getColor(R.color.red));
                btnSign.setOnClickListener(view13 -> firebaseAuth.getCurrentUser()
                        .sendEmailVerification()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(requireActivity(), "E-mail for verification sent", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            Intent intent = new Intent(requireActivity(), SignIn.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("Status", "Signed Out");
                            startActivity(intent);
                            requireActivity().finish();
                            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        })
                        .addOnFailureListener(e -> Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show()));
            }
        } else {
            ConstraintLayout clRecord = requireView().findViewById(R.id.record_cl_record);
            clRecord.setVisibility(View.INVISIBLE);

            CardView cvSign = requireView().findViewById(R.id.record_cv_sign);
            cvSign.setVisibility(View.VISIBLE);

            Button btnSign = requireView().findViewById(R.id.record_btn_sign);
            btnSign.setOnClickListener(view12 -> {
                Intent intent = new Intent(getActivity(), SignIn.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }
}