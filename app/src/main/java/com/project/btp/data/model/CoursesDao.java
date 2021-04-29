package com.project.btp.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CoursesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Course Course);

    @Query("DELETE FROM courses_table")
    void deleteAll();

    @Query("SELECT * FROM courses_table")
    LiveData<List<Course>> getAllCourses();

//    @Query("SELECT course_id FROM courses_table WHERE teacherId = :teacherId")
//    List<String> getCourses(String teacherId);
}
