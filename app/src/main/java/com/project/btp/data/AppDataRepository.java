package com.project.btp.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.project.btp.data.model.Attendance;
import com.project.btp.data.model.AttendanceDao;
import com.project.btp.data.model.AppDatabase;
import com.project.btp.data.model.Course;
import com.project.btp.data.model.CoursesDao;
import com.project.btp.data.model.User;
import com.project.btp.data.model.UserDao;

import java.util.List;

public class AppDataRepository {
    private AttendanceDao mAttendanceDao;
    private CoursesDao mCourseDao;
    private UserDao mUserDao;
    private LiveData<List<Course>> mCourses;

    public AppDataRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mAttendanceDao = db.attendanceDao();
        mCourseDao = db.coursesDao();
        mUserDao = db.userDao();
        mCourses = mCourseDao.getAllCourses();
    }

    public List<Attendance> getAttendance(String course, String date) {
        return mAttendanceDao.getAttendance(course, date);
    }

    public LiveData<List<Course>> getAllCourses() {
        return mCourses;
    }

    public List<User> getStudents(String courseId) {
        return mUserDao.getStudents(courseId);
    }

    public void insertStudent(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> mUserDao.insert(user));
    }

//    public List<String> getSavedCourses(String teacherId) {
//        return mCourseDao.getCourses(teacherId);
//    }

    public void insertAttendance(Attendance attendance) {
        AppDatabase.databaseWriteExecutor.execute(() -> mAttendanceDao.insert(attendance));
    }

    public void deleteAttendance(String courseId) {
        mAttendanceDao.deleteCourseAttendance(courseId);
    }

    public void insertCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> mCourseDao.insert(course));
    }

    public List<String> getAttendanceDates(String course) {
        return mAttendanceDao.getUniqueDates(course);
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private AttendanceDao attendanceDao;
        private UserDao userDao;
        private CoursesDao coursesDao;

        deleteAllAsyncTask(AttendanceDao attendanceDao, UserDao userDao, CoursesDao coursesDao) {
            this.attendanceDao = attendanceDao;
            this.userDao = userDao;
            this.coursesDao = coursesDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            attendanceDao.deleteAll();
            userDao.deleteAll();
            coursesDao.deleteAll();

            return null;
        }
    }

    public void deleteAll() {
        new deleteAllAsyncTask(mAttendanceDao, mUserDao, mCourseDao).execute();
    }
}
