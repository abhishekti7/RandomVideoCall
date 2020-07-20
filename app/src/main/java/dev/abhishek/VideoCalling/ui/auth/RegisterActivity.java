package dev.abhishek.VideoCalling.ui.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

import dev.abhishek.VideoCalling.R;
import dev.abhishek.VideoCalling.application.MyApplication;
import dev.abhishek.VideoCalling.managers.ActivitySwitchManager;
import dev.abhishek.VideoCalling.managers.SharedPrefManager;
import dev.abhishek.VideoCalling.models.User;
import dev.abhishek.VideoCalling.ui.MainActivity;
import dev.abhishek.VideoCalling.utils.Constants;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView btn_register;
    private TextView btn_login;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirm_password;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private SharedPrefManager sharedPrefManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initUi();
        initConfig();

    }

    private void initUi(){
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
    }

    private void initConfig() {
        firebaseAuth = MyApplication.getInstance().getFirebaseAuth();
        firestore = MyApplication.getInstance().getFirestore();
        sharedPrefManager = MyApplication.getInstance().getSharedPrefManager();
        progressDialog = new ProgressDialog(RegisterActivity.this);

        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    private boolean checkValidations() {
        if (username.getText().toString().equals("")) {
            username.setError("Enter username");
            return false;
        }
        if (email.getText().toString().equals("")) {
            email.setError("Enter your email address");
            return false;
        }
        if (password.getText().toString().equals("")) {
            password.setError("Enter a password");
            return false;
        }
        if (!password.getText().toString().equals(confirm_password.getText().toString())) {
            confirm_password.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void checkIfUserExists() {
        final String emailId = email.getText().toString();
        firestore.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("emailId", emailId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> users = task.getResult().getDocuments();
                            if (users.size() > 0) {
                                Toast.makeText(RegisterActivity.this, "Email Address is already registered", Toast.LENGTH_LONG).show();
                            } else {
                                signUpWithEmailPassword();
                            }
                        }
                    }
                });
    }

    private void signUpWithEmailPassword() {
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();

        String email_text = email.getText().toString();
        String password_text = password.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email_text, password_text)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            registerUser();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again later", Toast.LENGTH_LONG).show();
                            Log.w("Register", "signUpWithEmailPassword:failure", task.getException());
                        }
                    }
                });
    }

    private void registerUser() {
        final String username_text = username.getText().toString();
        final String email_text = email.getText().toString();
        progressDialog.setMessage("Creating new space for you...");
        progressDialog.show();

        final long time = System.currentTimeMillis();
        HashMap<String, Object> user = new HashMap<>();
        user.put("userName", username_text);
        user.put("emailId", email_text);
        user.put("created_on", time);

        firestore.collection(Constants.COLLECTION_USERS).document(firebaseAuth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        User user = new User(
                                firebaseAuth.getCurrentUser().getUid(),
                                username_text,
                                email_text,
                                "",
                                time
                        );
                        sharedPrefManager.loginUser(user);
                        Toast.makeText(RegisterActivity.this, "New user created", Toast.LENGTH_LONG).show();
                        new ActivitySwitchManager(RegisterActivity.this, MainActivity.class).openActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebaseAuth.signOut();
                        Toast.makeText(RegisterActivity.this, "Registration failed. Try again later", Toast.LENGTH_LONG).show();
                        Log.w("Register", "registerUser:failure" + e.getMessage());
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (checkValidations()) {
                    progressDialog.setMessage("Checking Databases...");
                    progressDialog.show();
                    checkIfUserExists();
                }
                break;

            case R.id.btn_login:
                finish();
                break;
        }
    }
}
