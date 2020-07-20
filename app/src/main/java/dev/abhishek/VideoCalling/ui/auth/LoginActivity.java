package dev.abhishek.VideoCalling.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
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

import dev.abhishek.VideoCalling.R;
import dev.abhishek.VideoCalling.application.MyApplication;
import dev.abhishek.VideoCalling.managers.ActivitySwitchManager;
import dev.abhishek.VideoCalling.managers.SharedPrefManager;
import dev.abhishek.VideoCalling.models.User;
import dev.abhishek.VideoCalling.ui.MainActivity;
import dev.abhishek.VideoCalling.utils.Constants;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView btn_login;
    private TextView btn_register;
    private EditText email;
    private EditText password;


    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private SharedPrefManager sharedPrefManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUi();
        initConfig();
    }

    private void initUi(){
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
    }

    private void initConfig(){
        firebaseAuth = MyApplication.getInstance().getFirebaseAuth();
        firestore = MyApplication.getInstance().getFirestore();
        sharedPrefManager = MyApplication.getInstance().getSharedPrefManager();
        progressDialog = new ProgressDialog(LoginActivity.this);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

    }

    private boolean checkValidations(){
        if(email.getText().toString().length()==0){
            email.setError("Enter a valid email address");
            return false;
        }
        if(password.getText().toString().length()==0){
            password.setError("Enter your password");
            return false;
        }
        return true;
    }

    private void fetchUserDetails(){
        progressDialog.setMessage("Checking Databases...");
        progressDialog.show();

        firestore.collection(Constants.COLLECTION_USERS).document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        progressDialog.dismiss();
                        User user = new User(
                                firebaseAuth.getCurrentUser().getUid(),
                                snapshot.getString("userName"),
                                snapshot.getString("emailId"),
                                "",
                                snapshot.getLong("created_on")
                        );
                        sharedPrefManager.loginUser(user);
                        Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_LONG).show();
                        new ActivitySwitchManager(LoginActivity.this, MainActivity.class).openActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.e("User Login: ", "fetchUserDetails: failed"+e.getMessage());
                    }
                });
    }


    private void loginUser(){
        progressDialog.setMessage("Signing you in...");
        progressDialog.show();

        String email_text = email.getText().toString();
        String password_text = password.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email_text,password_text)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            fetchUserDetails();
                        }else{
                            Toast.makeText(LoginActivity.this, "Login Failed. Check your credentials or try again later", Toast.LENGTH_LONG).show();
                            Log.w("User Login: ", "loginUser: failed", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                if(checkValidations()){
                    loginUser();
                }
                break;

            case R.id.btn_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
