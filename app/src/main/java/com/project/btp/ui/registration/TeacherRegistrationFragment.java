package com.project.btp.ui.registration;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.project.btp.R;
import com.project.btp.data.model.LoggedInUser;

public class TeacherRegistrationFragment extends Fragment {
    private final Context mContext;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    View rootView;
    EditText username;
    EditText phone;
    EditText instiName;
    EditText depName;
    EditText email;
    EditText password;
    Button submitBtn;

    public TeacherRegistrationFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.teacher_registration, container, false);
        username = rootView.findViewById(R.id.teach_reg_name);
        phone = rootView.findViewById(R.id.teach_reg_phone);
        instiName = rootView.findViewById(R.id.teach_reg_insti_name);
        depName = rootView.findViewById(R.id.teach_reg_dep_name);
        email = rootView.findViewById(R.id.teach_reg_email);
        password = rootView.findViewById(R.id.teach_reg_pw);
        submitBtn = rootView.findViewById(R.id.teach_reg_sign_up);

        // TODO : Add form validations

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return rootView;
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(
                email.getText().toString(),
                password.getText().toString()
        )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final LoggedInUser user = new LoggedInUser(
                                    mAuth.getCurrentUser().getUid(),
                                    username.getText().toString(),
                                    phone.getText().toString(),
                                    instiName.getText().toString(),
                                    depName.getText().toString(),
                                    email.getText().toString());
                            user.setTeacher();
                            db.getReference("users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(user.getDisplayName())
                                                .build();
                                        mAuth.getCurrentUser().updateProfile(profileUpdate);
                                        Toast.makeText(mContext, getString(R.string.registration_successful), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(mContext,
                                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(mContext,
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
