package com.SoftEng.wemeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.SoftEng.wemeet.DataModel.UserContainer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText email, password, repass, user ;
    private Button signup ;
    private TextView login ;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance() ;

        email = (EditText) findViewById(R.id.emailName) ;
        password = (EditText) findViewById(R.id.passwordText) ;
        repass = (EditText) findViewById(R.id.passwordretype) ;
        user = (EditText) findViewById(R.id.userName) ;
        signup = (Button) findViewById(R.id.signup) ;
        login = (TextView) findViewById(R.id.textLogin) ;
        progressBar = (ProgressBar) findViewById(R.id.progressbar2);

        login.setOnClickListener(this);
        signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.signup:
                registerprocess() ;
                break;
            case R.id.textLogin:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish() ;
                break;
            default:
                break ;
        }
    }

    private void registerprocess(){
        String useremail = email.getText().toString().trim() ;
        String username = user.getText().toString().trim() ;
        String pass = password.getText().toString().trim() ;
        String repassword = repass.getText().toString().trim() ;

        if(useremail.isEmpty()){
            email.setError("Email must not be empty!");
            email.requestFocus() ;
            return ;
        }

        if(pass.isEmpty()){
            password.setError("Password must not be empty!");
            password.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
            email.setError("Email pattern does not valid!");
            email.requestFocus() ;
            return ;
        }
        if(username.isEmpty()){
            user.setError("User name must not be blank!");
            user.requestFocus() ;
            return ;
        }
        if(pass.length()< 8){
            password.setError("Min password length must be 8!");
            password.requestFocus() ;
            return ;
        }

        if(repassword.isEmpty()){
            repass.setError("Password must not be empty!");
            repass.requestFocus();
            return;
        }
        if(repassword.length()< 8){
            repass.setError("Min password length must be 8!");
            repass.requestFocus() ;
            return ;
        }
        if(!pass.equals(repassword)){
            repass.setError("Password does not match!");
            repass.requestFocus() ;
            return ;
        }
        progressBar.setVisibility(View.VISIBLE);
        signup.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(useremail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            UserContainer userContainer = new UserContainer(useremail, username, pass, false) ;
                            try {
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userContainer)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //store image profile
                                                    progressBar.setVisibility(View.GONE);
                                                    signup.setEnabled(true);
                                                    Toast.makeText(RegisterActivity.this, "Register Success!", Toast.LENGTH_SHORT).show();
                                                    mAuth.getInstance().getCurrentUser().sendEmailVerification() ;

                                                    startActivity(new Intent(getApplicationContext(), EmailVerificationActivity.class));
                                                    finish() ;
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    signup.setEnabled(true);
                                                    Toast.makeText(RegisterActivity.this, "Register failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }catch(Exception e){
                                Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                signup.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this, "User might existed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish() ;
                        }
                    }
                });
    }
}