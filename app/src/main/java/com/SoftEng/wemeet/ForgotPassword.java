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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener{
    Button resetPass;
    EditText emailOfUser ;
    TextView login ;
    FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mAuth = FirebaseAuth.getInstance() ;
        resetPass = (Button) findViewById(R.id.buttonResetPass) ;
        emailOfUser = (EditText) findViewById(R.id.emailUser) ;
        login = (TextView) findViewById(R.id.forgotpasstext2) ;

        resetPass.setOnClickListener(this) ;
        login.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish() ;
        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonResetPass:
                requestUserEmail() ;
                break ;
            case R.id.forgotpasstext2:
                finish() ;
                break ;
            default:
                break ;
        }
    }
    private void requestUserEmail(){
        String userEmail = emailOfUser.getText().toString() ;
        if(userEmail.isEmpty()){
            emailOfUser.setError("Email must not be empty!");
            emailOfUser.requestFocus();
            return ;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            emailOfUser.setError("Email is not valid!");
            emailOfUser.requestFocus();
            return ;
        }
        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Success! Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ForgotPassword.this, "Failed, email not found", Toast.LENGTH_SHORT).show();
                }
            }
        }) ;
    }
}