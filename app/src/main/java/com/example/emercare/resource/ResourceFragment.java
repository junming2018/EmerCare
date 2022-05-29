package com.example.emercare.resource;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.emercare.R;

public class ResourceFragment extends Fragment {
    private String course;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resource, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView cvSkill = requireView().findViewById(R.id.resource_cv_skill);
        cvSkill.setOnClickListener(view1 -> {
            course = "First Aid Skill";
            Intent intent = new Intent(getActivity(), ResourceList.class);
            intent.putExtra("Course", course);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        CardView cvDisaster = requireView().findViewById(R.id.resource_cv_disaster);
        cvDisaster.setOnClickListener(view1 -> {
            course = "Disaster Management";
            Intent intent = new Intent(getActivity(), ResourceList.class);
            intent.putExtra("Course", course);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        CardView cvDisease = requireView().findViewById(R.id.resource_cv_disease);
        cvDisease.setOnClickListener(view1 -> {
            course = "Disease";
            Intent intent = new Intent(getActivity(), ResourceList.class);
            intent.putExtra("Course", course);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        CardView cvSport = requireView().findViewById(R.id.resource_cv_exercise);
        cvSport.setOnClickListener(view1 -> {
            course = "Exercise";
            Intent intent = new Intent(getActivity(), ResourceList.class);
            intent.putExtra("Course", course);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}