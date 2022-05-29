package com.example.emercare.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.emercare.R;
import com.example.emercare.sign.SignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private String name, gender, unit;
    private long age, height;
    private boolean hasData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isNetworkConnected()) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null) {
                TextView tvEmail = requireView().findViewById(R.id.profile_tv_email);

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore
                        .collection(Objects.requireNonNull(firebaseUser.getEmail()))
                        .document("Account");
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvEmail.setText(documentSnapshot.getString("e-mail"));
                        if (!(documentSnapshot.getString("name") == null
                                || documentSnapshot.getString("gender") == null
                                || documentSnapshot.getLong("age") == null
                                || documentSnapshot.getLong("height") == null
                                || documentSnapshot.getString("unit") == null)) {
                            hasData = true;

                            TextView tvName = requireView().findViewById(R.id.profile_tv_name);
                            name = documentSnapshot.getString("name");
                            tvName.setText(name);

                            TextView tvGender = requireView().findViewById(R.id.profile_tv_gender);
                            gender = documentSnapshot.getString("gender");
                            tvGender.setText(gender);

                            TextView tvAge = requireView().findViewById(R.id.profile_tv_age);
                            age = Objects.requireNonNull(documentSnapshot.getLong("age"));
                            tvAge.setText(String.valueOf(age).concat(" Years Old"));

                            TextView tvHeight = requireView().findViewById(R.id.profile_tv_height);
                            height = Objects.requireNonNull(documentSnapshot.getLong("height"));
                            unit = documentSnapshot.getString("unit");
                            tvHeight.setText(String.valueOf(height).concat(" ").concat(unit));
                        } else {
                            hasData = false;
                        }
                    }
                }).addOnFailureListener(e -> {
                    tvEmail.setText(firebaseUser.getEmail());
                    hasData = false;
                });

                Button btnEdit = requireView().findViewById(R.id.profile_btn_edit);
                btnEdit.setOnClickListener(view1 -> {
                    Intent intent = new Intent(getActivity(), ProfileEdit.class);
                    intent.putExtra("hasData", hasData);
                    if (hasData) {
                        intent.putExtra("name", name);
                        intent.putExtra("gender", gender);
                        intent.putExtra("age", age);
                        intent.putExtra("height", height);
                        intent.putExtra("unit", unit);
                    }
                    startActivity(intent);
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
            } else {
                ConstraintLayout clProfile = requireView().findViewById(R.id.profile_cl_profile);
                clProfile.setVisibility(View.INVISIBLE);

                CardView cvSign = requireView().findViewById(R.id.profile_cv_sign);
                cvSign.setVisibility(View.VISIBLE);

                Button btnSign = requireView().findViewById(R.id.profile_btn_sign);
                btnSign.setOnClickListener(view12 -> {
                    Intent intent = new Intent(getActivity(), SignIn.class);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
            }
        } else {
            ConstraintLayout clProfile = requireView().findViewById(R.id.profile_cl_profile);
            clProfile.setVisibility(View.INVISIBLE);

            CardView cvNo = requireView().findViewById(R.id.profile_cv_no);
            cvNo.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}