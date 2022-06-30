package com.SoftEng.wemeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.SoftEng.wemeet.home.HomeActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText email, password ;
    private Button loginbtn ;
    private TextView signup, forgor ;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar ;
    private AdView mAdView, adViewbot ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adViewUp);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        adViewbot = findViewById(R.id.adViewBottom);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        adViewbot.loadAd(adRequest2);


        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.emailUser) ;
        password = (EditText) findViewById(R.id.text_password);
        loginbtn = (Button) findViewById(R.id.buttonResetPass) ;
        signup = (TextView) findViewById(R.id.createaccText) ;
        forgor = (TextView) findViewById(R.id.forgotpasstext2) ;
        progressBar = (ProgressBar) findViewById(R.id.progressbar1) ;

        loginbtn.setEnabled(true) ;
        loginbtn.setOnClickListener(this);
        signup.setOnClickListener(this);
        forgor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.createaccText:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                //finish() ;
                break;
            case R.id.forgotpasstext2:
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                //finish() ;
                break;
            case R.id.buttonResetPass:
                loginProcess();
                break;
            default:
                break;
        }
    }

    private void loginProcess(){
        try{
            String useremail = email.getText().toString().trim() ;
            String pass = password.getText().toString().trim() ;

            if(useremail.isEmpty()){
                email.setError("Email must not be empty!");
                email.requestFocus() ;
                return ;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
                email.setError("Email pattern does not valid!");
                email.requestFocus() ;
                return ;
            }

            if(pass.isEmpty()){
                password.setError("Password must not be empty!");
                password.requestFocus();
                return;
            }
            if(pass.length() < 8){
                password.setError("Min password length must be 8 or more!");
                password.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE) ;
            loginbtn.setEnabled(false);
            mAuth.signInWithEmailAndPassword(useremail, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //verifikasi email
                                FirebaseUser userBaruLogin = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users") ;
                                String userID = userBaruLogin.getUid() ;

                                if(userBaruLogin.isEmailVerified()){
                                    progressBar.setVisibility(View.GONE) ;
                                    loginbtn.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                }else{
                                    progressBar.setVisibility(View.GONE) ;
                                    loginbtn.setEnabled(true);
                                    userBaruLogin.delete() ;
                                    //DatabaseReference unveriviedRef = unveriviedUser.getReference() ;
                                    dbref.child(userID).removeValue() ;
                                    Toast.makeText(LoginActivity.this, "Login failed! You might have to sign in again if your email isn't verified", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(LoginActivity.this, "Login Failed! Sign up if you don't have an account", Toast.LENGTH_SHORT).show();
                                loginbtn.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy(){
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
        FirebaseUser exitedUser = FirebaseAuth.getInstance().getCurrentUser();
        if(!exitedUser.isEmailVerified()){
            FirebaseAuth.getInstance().getCurrentUser().delete() ;
            FirebaseDatabase.getInstance().getReference() ;
        }
    }
}