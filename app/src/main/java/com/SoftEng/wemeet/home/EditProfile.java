package com.SoftEng.wemeet.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.SoftEng.wemeet.DataModel.UserContainer;
import com.SoftEng.wemeet.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class EditProfile extends Fragment {
    private EditText newUsername, newPassword, confirm_password;
    private ImageView newProfPict;
    private Button confirmChanges, defaultImg;
    private String tempNewUsername;
    private String tempNewPassword, tempconfirmpass;
    private String tempEmail;
    private Boolean tempoldImage ;
    private Uri profileUri ;
    private DatabaseReference dbref ;
    private FirebaseStorage fbStorage ;
    private FirebaseUser user ;
    private AdView mAdView ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments() ;
        if(bundle != null){
            profileUri = Uri.parse(bundle.getString("ImageUri")) ;
        }else{
            profileUri = Uri.parse("android.resource://com.SoftEng.wemeet/" + R.drawable.ic_baseline_account_circle_24) ;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false) ;
        newProfPict = (ImageView) v.findViewById(R.id.profileChangeImageView) ;
        newUsername = (EditText) v.findViewById(R.id.changeUsername) ;
        newPassword = (EditText) v.findViewById(R.id.changePassword) ;
        confirm_password = (EditText) v.findViewById(R.id.confirmPassword) ;
        confirmChanges = (Button) v.findViewById(R.id.confirmChanges) ;
        defaultImg = (Button) v.findViewById(R.id.btnResetImage) ;

        Glide.with(getContext()).load(profileUri)
                .placeholder(R.drawable.ic_baseline_account_circle_24).into(newProfPict);

        newProfPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //redirect ke activity galery dengan intent
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
                startActivityForResult(intent, 100);
            }
        });

        defaultImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileUri = Uri.parse("android.resource://com.SoftEng.wemeet/" + R.drawable.ic_baseline_account_circle_24) ;
                Glide.with(getContext()).load(profileUri)
                        .into(newProfPict) ;
            }
        });

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = v.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        confirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Are you sure?
                AlertDialog.Builder confirm = new AlertDialog.Builder(getContext()) ;
                confirm.setTitle("Upload Webinar") ;
                confirm.setCancelable(false) ;
                confirm.setMessage("Are you sure this is the correct data?") ;
                confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateProfile() ;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }) ;
                AlertDialog alertDialog = confirm.create();
                alertDialog.show() ;
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                profileUri = data.getData() ;
                newProfPict.setImageURI(profileUri);
            }
        }
    }

    private void updateProfile() {
        user = FirebaseAuth.getInstance().getCurrentUser() ;
        tempNewUsername = newUsername.getText().toString().trim() ;
        tempNewPassword = newPassword.getText().toString().trim() ;
        tempconfirmpass = confirm_password.getText().toString().trim() ;
        tempEmail = user.getEmail();

        if(tempNewUsername.isEmpty()){
            newUsername.setError("User name must not be blank!");
            newUsername.requestFocus() ;
            return ;
        }
        if(tempNewPassword.length()< 8){
            newPassword.setError("Min password length must be 8!");
            newPassword.requestFocus() ;
            return ;
        }

        if(tempconfirmpass.isEmpty()){
            confirm_password.setError("Password must not be empty!");
            confirm_password.requestFocus();
            return;
        }
        if(tempconfirmpass.length()< 8){
            confirm_password.setError("Min password length must be 8!");
            confirm_password.requestFocus() ;
            return ;
        }
        if(!tempNewPassword.equals(tempconfirmpass)){
            confirm_password.setError("Password does not match!");
            confirm_password.requestFocus() ;
            return ;
        }


        if(profileUri.equals(Uri.parse("android.resource://com.SoftEng.wemeet/" + R.drawable.ic_baseline_person_24))){
            tempoldImage = false ;
        }else{
            tempoldImage = true ;
        }
        UserContainer updatedUser = new UserContainer(tempEmail, tempNewUsername, tempNewPassword, tempoldImage) ;
        // Prompt the user to re-provide their sign-in credentials
        user.updatePassword(tempNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Update database
                    dbref = FirebaseDatabase.getInstance().getReference("Users") ;
                    dbref.child(user.getUid()).setValue(updatedUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "User data updated!", Toast.LENGTH_SHORT).show();
                        }
                    }) ;
                } else {
                    Log.d("Failed! ", "Error password not updated") ;
                }
            }
        });
    }
}