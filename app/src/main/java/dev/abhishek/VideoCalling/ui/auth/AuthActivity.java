package dev.abhishek.VideoCalling.ui.auth;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import dev.abhishek.VideoCalling.R;
import dev.abhishek.VideoCalling.application.MyApplication;
import dev.abhishek.VideoCalling.managers.ActivitySwitchManager;
import dev.abhishek.VideoCalling.managers.SharedPrefManager;


public class AuthActivity extends AppCompatActivity {

    private ImageView img_1;
    private ImageView img_2;
    private MaterialButton signUp;
    private MaterialButton signIn;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private SharedPrefManager sharedPrefManager;
    private ProgressDialog progressDialog;
    private ValueAnimator valueAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        initUi();
        initConfig();
        initBackground();
    }

    private void initUi(){
        img_1 = findViewById(R.id.imageBg1);
        img_2 = findViewById(R.id.imageBg2);
        signUp = findViewById(R.id.signup_btn);
        signIn = findViewById(R.id.signin_btn);
    }

    private void initBackground() {
        valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(40000L);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float progress = (float) valueAnimator.getAnimatedValue();
                float width = img_1.getWidth();
                final float translationX = width * progress;
                img_1.setTranslationX(translationX);
                img_2.setTranslationX(translationX - width);
            }
        });
        valueAnimator.start();
    }

    private void initConfig(){
        firebaseAuth = MyApplication.getInstance().getFirebaseAuth();
        firestore = MyApplication.getInstance().getFirestore();
        sharedPrefManager = MyApplication.getInstance().getSharedPrefManager();
        progressDialog = new ProgressDialog(AuthActivity.this);


       signUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               new ActivitySwitchManager(AuthActivity.this, RegisterActivity.class).openActivityWthoutFinish();
           }
       });

       signIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               new ActivitySwitchManager(AuthActivity.this, LoginActivity.class).openActivityWthoutFinish();
           }
       });
    }
}
