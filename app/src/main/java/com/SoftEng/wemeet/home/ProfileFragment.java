package com.SoftEng.wemeet.home;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.SoftEng.wemeet.DataModel.UserContainer;
import com.SoftEng.wemeet.LoginActivity;
import com.SoftEng.wemeet.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private Button button1;
    private TextView name, email, modifyAWebinar, deleteAccount, editAccount ;
    private ImageView profilePict ;
    private ProgressDialog progressDialog ;
    private AdView mAdView ;
    private Uri imageProfile;

    private FirebaseStorage imageStorage ;
    private FirebaseUser user ;

    private String username ;
    private String useremail ;
    private Boolean hasImage ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false) ;

        profilePict = (ImageView) view.findViewById(R.id.profileImageView) ;
        button1 = (Button) view.findViewById(R.id.btn_logOut) ;
        modifyAWebinar = (TextView) view.findViewById(R.id.webinarModify) ;
        name = (TextView) view.findViewById(R.id.displayName);
        email = (TextView) view.findViewById(R.id.displayEmail) ;
        deleteAccount = (TextView) view.findViewById(R.id.btntxt_deleteacc) ;
        editAccount = (TextView) view.findViewById(R.id.editProfile_txtbtn) ;

        MobileAds.initialize(view.getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.adViewProf);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        progressDialog = new ProgressDialog(getContext()) ;
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
            username = this.getArguments().getString("UserName") ;
            useremail = this.getArguments().getString("UserEmail") ;
            hasImage = this.getArguments().getBoolean("HasImage") ;

            name.setText(getString(R.string.greetings) + " " + username);
            email.setText(useremail);

            //set profile picture
            if(hasImage == true){
                user = FirebaseAuth.getInstance().getCurrentUser() ;
                imageStorage.getInstance().getReference()
                        .child("Users/"+ user.getUid() + "/profile.jpg")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageProfile = uri ;
                    }
                }) ;
                Glide.with(getContext()).load(imageProfile)
                        .placeholder(R.drawable.ic_baseline_account_circle_24).into(profilePict);
            }else{
                profilePict.setImageResource(R.drawable.ic_baseline_person_24);
            }

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

        editAccount.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);
        modifyAWebinar.setOnClickListener(this);
        button1.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent ;
        switch (view.getId()) {
            case R.id.btn_logOut:
                Toast.makeText(getContext(), "User has logged out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                getActivity().finish() ;
                break;
            case R.id.editProfile_txtbtn:
                //change username, email/ password (?)
                Fragment fragment = new EditProfile() ;
                if(imageProfile != null){
                    Bundle bundle = new Bundle() ;
                    bundle.putString("ImageUri", imageProfile.toString());
                    fragment.setArguments(bundle);
                }
                FragmentTransaction ft = getFragmentManager().beginTransaction() ;
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) ;
                ft.replace(R.id.profileFrag, fragment) ;
                ft.addToBackStack(null);
                ft.commit() ;
                break;
            case R.id.btntxt_deleteacc:
                AlertDialog.Builder deleteMsg = new AlertDialog.Builder(getContext()) ;
                deleteMsg.setTitle("Delete user") ;
                deleteMsg.setMessage("Are you sure?") ;
                deleteMsg.setCancelable(false) ;
                deleteMsg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser exitedUser = FirebaseAuth.getInstance().getCurrentUser();
                        exitedUser.delete();
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getContext(), "Account has been deleted", Toast.LENGTH_SHORT).show();
                        getActivity().finish() ;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                break ;
            case R.id.webinarModify:
                startActivity(new Intent(getActivity().getApplicationContext(), ModifyWebinar.class));
                //getActivity().finish() ;
                break;
            default:
                break;
        }
    }
}