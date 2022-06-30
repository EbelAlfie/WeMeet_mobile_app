package com.SoftEng.wemeet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class EmailVerificationActivity extends AppCompatActivity implements View.OnClickListener{
    TextView resendEmail, result ;
    Button backToLogin ;
    FirebaseAuth mAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        mAuth = FirebaseAuth.getInstance() ;

        resendEmail = (TextView) findViewById(R.id.Text_didntReceiveEmail);
        backToLogin = (Button) findViewById(R.id.btnBackToLogin) ;
        result = (TextView) findViewById(R.id.template);
        result.setText("Verification link has been sent to your email or spam! If you press the login button or exit the app before the email validation, you will have to register your account again!");
        resendEmail.setText("Resend email verification link");

        resendEmail.setOnClickListener(this);
        backToLogin.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish() ;
        return true ;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.Text_didntReceiveEmail:
                mAuth.getCurrentUser().sendEmailVerification() ;
                Toast.makeText(getApplicationContext(), "Verification link has been resent to your email!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnBackToLogin:
                //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish() ;
                return ;
            default:
                break;
        }
    }
}