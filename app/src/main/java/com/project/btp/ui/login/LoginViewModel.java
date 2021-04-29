package com.project.btp.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.btp.data.AppDataRepository;
import com.project.btp.data.LoginRepository;
import com.project.btp.data.Result;
import com.project.btp.data.model.LoggedInUser;
import com.project.btp.R;

public class LoginViewModel extends AndroidViewModel {
    private AppDataRepository mRepository;
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public LoginViewModel(Application application) {
        super(application);
        mRepository = new AppDataRepository(application);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult(Context ctx) {
        LoggedInUser savedUser = getSavedUserState(ctx);
        if (!savedUser.getUserId().equals("")) {
            loginResult.setValue(new LoginResult(new LoggedInUserView(savedUser)));
        }
        return loginResult;
    }

    static final String UserID = "attend_userID";
    static final String UserName = "attend_userName";
    static final String UserType = "attend_userType";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void saveUserState(Context ctx, LoggedInUser user)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(UserID, user.getUserId());
        editor.putString(UserName, user.getDisplayName());
        editor.putBoolean(UserType, user.getIsTeacher());
        editor.apply();
    }

    public static LoggedInUser getSavedUserState(Context ctx) {
        String username = getSharedPreferences(ctx).getString(UserName, "");
        String userId = getSharedPreferences(ctx).getString(UserID, "");
        Boolean isTeacher = getSharedPreferences(ctx).getBoolean(UserType, false);
        return new LoggedInUser(userId, username, isTeacher);
    }

    public void login(final Context ctx, String username, String password) {
        // can be launched in a separate asynchronous job
        Log.d("userLInfo", username + " " + password);
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            db.getReference("users")
                                    .child(user.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    LoggedInUser user = snapshot.getValue(LoggedInUser.class);
                                    assert user != null;
                                    saveUserState(ctx, user);
//                                    System.out.println(user);
                                    loginResult.setValue(new LoginResult(new LoggedInUserView(user)));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.w("userLoginFail", error.toException());
                                }
                            });
                        } else {
                            loginResult.setValue(new LoginResult(R.string.login_failed));
                        }
                    }
                });
    }

    public void logout(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.apply();
        mRepository.deleteAll();

        Intent intent = new Intent(ctx, LoginActivity.class);
        // Closing all the Activities
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
        // Add new Flag to start new Activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Starting Login Activity
        ctx.startActivity(intent);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}