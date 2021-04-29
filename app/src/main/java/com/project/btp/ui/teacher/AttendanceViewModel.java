package com.project.btp.ui.teacher;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.project.btp.data.AppDataRepository;
import com.project.btp.data.model.Attendance;
import com.project.btp.data.model.User;

import java.util.List;

public class AttendanceViewModel extends AndroidViewModel {

    private AppDataRepository mRepository;

    public AttendanceViewModel (Application application) {
        super(application);
        mRepository = new AppDataRepository(application);
    }

//    public LiveData<List<Attendance>> getAttendance(String courseId) {
//        return mRepository.getAttendance(courseId);
//    }

    public void insert(List<Attendance> attendanceList) {
        for (Attendance attendance : attendanceList) mRepository.insertAttendance(attendance);
    }

    public List<User> getStudents(String courseId) {
        return mRepository.getStudents(courseId);
    }
}
