package com.project.btp.data.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Attendance attendance);

    @Query("DELETE FROM attendance_table")
    void deleteAll();

    @Query("DELETE FROM attendance_table WHERE course_id = :courseId")
    void deleteCourseAttendance(String courseId);

    @Query("SELECT * FROM attendance_table WHERE course_id = :courseId AND date = :date")
    List<Attendance> getAttendance(String courseId, String date);

    @Query("SELECT DISTINCT date FROM attendance_table WHERE course_id = :courseId")
    List<String> getUniqueDates(String courseId);
}
