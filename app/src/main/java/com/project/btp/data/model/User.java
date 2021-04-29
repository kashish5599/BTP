package com.project.btp.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table", indices = {@Index(value = {"userId"}, unique = true)})
public class User {
    @PrimaryKey
    @NonNull
    private String userId;
    private String username;
    private Boolean isTeacher;
    @ColumnInfo(name = "course_id")
    private String courseId;

    @Ignore
    public User() {

    }

    public User(@NonNull String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.isTeacher = false;
    }

    public Boolean getTeacher() {
        return isTeacher;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setTeacher(Boolean teacher) {
        isTeacher = teacher;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

