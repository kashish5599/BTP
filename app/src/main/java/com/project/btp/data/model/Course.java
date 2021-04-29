package com.project.btp.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses_table", indices = {@Index(value = {"course_id"}, unique = true)})
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    @ColumnInfo(name = "course_id")
    private String courseId;
    @NonNull
    private String teacherId;
    private String sem = "";
    private String slot = "";
    private long nstudents;

    @Ignore
    public Course() {

    }

    public Course(@NonNull String courseId, @NonNull String teacherId, String sem, String slot) {
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.sem = sem;
        this.slot = slot;
        this.nstudents = 0;
    }

    @NonNull
    public String getCourseId() {
        return courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTeacherId() {
        return teacherId;
    }

    public String getSem() {
        return sem;
    }

    public String getSlot() {
        return slot;
    }

    public long getNstudents() {
        return nstudents;
    }

    public void setCourseId(@NonNull String courseId) {
        this.courseId = courseId;
    }

    public void setTeacherId(@NonNull String teacherId) {
        this.teacherId = teacherId;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public void setNstudents(long nstudents) {
        this.nstudents = nstudents;
    }
}
