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

    public LiveData<List<Attendance>> getAttendance(String course) {
        return mAttendanceDao.getAttendance(course);
    }

    public LiveData<List<Course>> getAllCourses() {
        return mCourses;
    }

    public LiveData<List<User>> getStudents(String courseId) {
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
        new deleteAttendanceAsyncTask(mAttendanceDao).execute(courseId);
    }

    private static class deleteAttendanceAsyncTask extends AsyncTask<String, Void, Void> {
        private AttendanceDao attendanceDao;

        deleteAttendanceAsyncTask(AttendanceDao attendanceDao) {
            this.attendanceDao = attendanceDao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            attendanceDao.deleteCourseAttendance(params[0]);

            return null;
        }
    }

    public void insertCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> mCourseDao.insert(course));
    }

    public LiveData<List<String>> getAttendanceDates(String course) {
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
