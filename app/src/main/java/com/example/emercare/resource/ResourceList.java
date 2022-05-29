package com.example.emercare.resource;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emercare.R;

import java.util.ArrayList;
import java.util.List;

public class ResourceList extends AppCompatActivity {
    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_list);
        ImageView btnBack = findViewById(R.id.resource_list_img_back);
        btnBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        Intent intent = getIntent();
        course = intent.getStringExtra("Course");
        TextView tvCourse = findViewById(R.id.resource_list_tv_course);
        tvCourse.setText(course);
        RecyclerView recyclerView = findViewById(R.id.resource_list_rv_course);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ResourceList.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<CourseData> allCourseData = getAllCourseInfo();
        Activity thisActivity = this;
        CourseAdapter courseAdapter = new CourseAdapter(allCourseData, thisActivity);
        recyclerView.setAdapter(courseAdapter);
    }

    private List<CourseData> getAllCourseInfo() {
        List<CourseData> allCourse = new ArrayList<>();
        switch (course) {
            case "First Aid Skill":
                allCourse.add(new CourseData(course, R.drawable.allergies_anaphylaxis, "Allergies / Anaphylaxis"));
                allCourse.add(new CourseData(course, R.drawable.asthma_attack, "Asthma Attack"));
                allCourse.add(new CourseData(course, R.drawable.bites_stings, "Bites / Stings"));
                allCourse.add(new CourseData(course, R.drawable.bleeding, "Bleeding"));
                allCourse.add(new CourseData(course, R.drawable.broken_bone, "Broken Bone"));
                allCourse.add(new CourseData(course, R.drawable.burns, "Burns"));
                allCourse.add(new CourseData(course, R.drawable.choking, "Choking"));
                allCourse.add(new CourseData(course, R.drawable.diabetic_emergency, "Diabetic Emergency"));
                allCourse.add(new CourseData(course, R.drawable.distress, "Distress"));
                allCourse.add(new CourseData(course, R.drawable.head_injury, "Head Injury"));
                allCourse.add(new CourseData(course, R.drawable.heart_attack, "Heart Attack"));
                allCourse.add(new CourseData(course, R.drawable.heat_stroke, "Heat Stroke"));
                allCourse.add(new CourseData(course, R.drawable.hypothermia, "Hypothermia"));
                allCourse.add(new CourseData(course, R.drawable.meningitis, "Meningitis"));
                allCourse.add(new CourseData(course, R.drawable.poisoning_harmful_substances, "Poisoning / Harmful Substances"));
                allCourse.add(new CourseData(course, R.drawable.psychological_first_aid, "Psychological First Aid"));
                allCourse.add(new CourseData(course, R.drawable.seizure_epilepsy, "Seizure / Epilepsy"));
                allCourse.add(new CourseData(course, R.drawable.sprains_strains, "Sprains and Strains"));
                allCourse.add(new CourseData(course, R.drawable.stroke, "Stroke"));
                allCourse.add(new CourseData(course, R.drawable.unconscious, "Unconscious"));
                break;
            case "Disaster Management":
                allCourse.add(new CourseData(course, R.drawable.chemical_emergencies, "Chemical Emergencies"));
                allCourse.add(new CourseData(course, R.drawable.drought, "Drought"));
                allCourse.add(new CourseData(course, R.drawable.earthquake, "Earthquake"));
                allCourse.add(new CourseData(course, R.drawable.fire_disaster, "Fire Disaster"));
                allCourse.add(new CourseData(course, R.drawable.flooding, "Flooding"));
                allCourse.add(new CourseData(course, R.drawable.flu_pandemic, "Flu Pandemic"));
                allCourse.add(new CourseData(course, R.drawable.heatwave, "Heatwave"));
                allCourse.add(new CourseData(course, R.drawable.hurricane, "Hurricane"));
                allCourse.add(new CourseData(course, R.drawable.landslide, "Landslide"));
                allCourse.add(new CourseData(course, R.drawable.power_outage, "Power Outage"));
                allCourse.add(new CourseData(course, R.drawable.severe_winter_weather, "Severe Winter Weather"));
                allCourse.add(new CourseData(course, R.drawable.tornado, "Tornado"));
                allCourse.add(new CourseData(course, R.drawable.tsunami, "Tsunami"));
                allCourse.add(new CourseData(course, R.drawable.volcano_eruption, "Volcano Eruption"));
                allCourse.add(new CourseData(course, R.drawable.wildfire, "Wildfire"));
                break;
            case "Disease":
                allCourse.add(new CourseData(course, R.drawable.allergies_anaphylaxis, "Allergies / Anaphylaxis"));
                allCourse.add(new CourseData(course, R.drawable.alzheimers_disease, "Alzheimer’s Disease"));
                allCourse.add(new CourseData(course, R.drawable.anxiety, "Anxiety"));
                allCourse.add(new CourseData(course, R.drawable.arthritis, "Arthritis"));
                allCourse.add(new CourseData(course, R.drawable.asthma_attack, "Asthma Attack"));
                allCourse.add(new CourseData(course, R.drawable.cancer, "Cancer"));
                allCourse.add(new CourseData(course, R.drawable.dementia, "Dementia"));
                allCourse.add(new CourseData(course, R.drawable.depression, "Depression"));
                allCourse.add(new CourseData(course, R.drawable.diabetic_emergency, "Diabetes"));
                allCourse.add(new CourseData(course, R.drawable.dissociative_identity_disorder, "Dissociative Identity Disorder"));
                allCourse.add(new CourseData(course, R.drawable.heart_attack, "Heart Attack"));
                allCourse.add(new CourseData(course, R.drawable.parkinsons_disease, "Parkinson’s Disease"));
                allCourse.add(new CourseData(course, R.drawable.schizophrenia, "Schizophrenia"));
                allCourse.add(new CourseData(course, R.drawable.seizure_epilepsy, "Seizure / Epilepsy"));
                allCourse.add(new CourseData(course, R.drawable.stroke, "Stroke"));
                break;
            case "Exercise":
                allCourse.add(new CourseData(course, R.drawable.burpee, "Burpee"));
                allCourse.add(new CourseData(course, R.drawable.dumbbell_overhead_press, "Dumbbell Overhead Press"));
                allCourse.add(new CourseData(course, R.drawable.dumbbell_row, "Dumbbell Row"));
                allCourse.add(new CourseData(course, R.drawable.glute_bridge, "Glute Bridge"));
                allCourse.add(new CourseData(course, R.drawable.lunge, "Lunge"));
                allCourse.add(new CourseData(course, R.drawable.plank, "Plank"));
                allCourse.add(new CourseData(course, R.drawable.push_up, "Push-up"));
                allCourse.add(new CourseData(course, R.drawable.side_plank, "Side Plank"));
                allCourse.add(new CourseData(course, R.drawable.single_leg_deadlift, "Single Leg Deadlift"));
                allCourse.add(new CourseData(course, R.drawable.squat, "Squat"));
                break;
        }
        return allCourse;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}