package com.project.btp.ui.courses;

public class Course {

    public String courseId;
    public String sem;
    public String slot;
    public String teacherId;

    public Course() {}

    public Course(String courseId, String sem, String slot, String teacherId) {
        this.courseId = courseId;
        this.sem = sem;
        this.slot = slot;
        this.teacherId = teacherId;
    }
}
