package com.example.emercare.resource;

public class CourseData {
    int courseIcon;
    String course, courseName;

    public CourseData(String course, int courseIcon, String courseName) {
        this.course = course;
        this.courseIcon = courseIcon;
        this.courseName = courseName;
    }

    public String getCourse() {
        return course;
    }

    public int getCourseIcon() {
        return courseIcon;
    }

    public String getCourseName() {
        return courseName;
    }
}