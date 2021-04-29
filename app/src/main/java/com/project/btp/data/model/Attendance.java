package com.project.btp.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "attendance_table"
//        @ForeignKey(
//            entity = Course.class,
//            parentColumns = "course_id",
//            childColumns = "course_id",
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE
//        ),
)
public class Attendance {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "course_id")
    private String courseId;

    @NonNull
    @ColumnInfo(name = "student_id")
    private String studentId;

    private Boolean isPresent;
    private String date;

    public Attendance(@NonNull String courseId, @NonNull String studentId, Boolean isPresent, String date) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.date = date;
        this.isPresent = isPresent;
    }

    public String getDate() {
        return date;
    }

    @NonNull
    public String getCourse() {
        return courseId;
    }

    @NonNull
    public String getStudentId() {
        return studentId;
    }

    public Boolean getPresent() {
        return isPresent;
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
}
