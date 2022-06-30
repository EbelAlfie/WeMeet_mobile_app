package com.SoftEng.wemeet.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.SoftEng.wemeet.DataModel.WebinarContainer;
import com.SoftEng.wemeet.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class EditUserWebinars extends AppCompatActivity implements View.OnClickListener{
    private ImageView pamphletDetail;
    private TextView titleDetail, speakerDetail, dateDetail, timeDetail, feeDetail, linkDetail, certifDetail ;
    private Button btnEdit ;

    private ProgressDialog progressDialog ;
    private FirebaseFirestore db ;
    private StorageReference imageref;
    private String webinarID, webinarPamphlet;
    private WebinarContainer webinarDetail ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_webinars);
        Intent intent = getIntent() ;
        if (intent != null) {
            try {
                webinarID = intent.getStringExtra("IDWebinar");
                webinarPamphlet = intent.getStringExtra("Pamphlet");
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

            pamphletDetail = (ImageView) findViewById(R.id.pamphletDetail) ;
            titleDetail= (TextView) findViewById(R.id.detailTitle);

            speakerDetail =(TextView) findViewById(R.id.detailSpeaker);
            dateDetail = (TextView) findViewById(R.id.detailDate);
            timeDetail = (TextView) findViewById(R.id.detailTime);
            feeDetail = (TextView) findViewById(R.id.detailFee);
            linkDetail = (TextView) findViewById(R.id.detailLink);
            certifDetail = (TextView) findViewById(R.id.detailCert);
            btnEdit = (Button) findViewById(R.id.btn_edit) ;

            pamphletDetail.setOnClickListener(this);
            btnEdit.setOnClickListener(this);

            db = FirebaseFirestore.getInstance() ;

            //query data webinar di global collection
            try{
                progressDialog = new ProgressDialog(EditUserWebinars.this) ;
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                db.collection("Webinars").document(webinarID)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            webinarDetail = documentSnapshot.toObject(WebinarContainer.class);
                            if(webinarDetail.getPamphlet().trim().toLowerCase().equals("true")){
                                Glide.with(getApplicationContext()).load(webinarPamphlet)
                                        .fitCenter().centerCrop().placeholder(R.drawable.placeholder)
                                        .into(pamphletDetail);
                            }else{
                                pamphletDetail.setImageResource(R.drawable.placeholder);
                            }

                            titleDetail.setText(webinarDetail.getWebinarTitle());
                            speakerDetail.setText(webinarDetail.getWebinarspeaker()) ;
                            dateDetail.setText(webinarDetail.getWebinarDate()) ;
                            timeDetail.setText(webinarDetail.getWebinarTime()) ;
                            feeDetail.setText(webinarDetail.getWebinarFee()) ;
                            linkDetail.setText(webinarDetail.getWebinarLink()) ;
                            certifDetail.setText(webinarDetail.getWebinarCertStatus()) ;
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onClick(View view) {
        Fragment choosenFragment;
        FragmentTransaction ft ;
        Bundle bundle ;
        switch(view.getId()){
            case R.id.pamphletDetail:
                FullScreenFragment frag = new FullScreenFragment() ;
                ft = getSupportFragmentManager().beginTransaction();
                // Create and show the dialog.
                DialogFragment newFragment = FullScreenFragment.newInstance(webinarPamphlet);
                newFragment.show(ft, "dialog");
                break;
            case R.id.btn_edit:
                bundle = new Bundle() ;
                bundle.putString("IDWebinar", webinarID);
                bundle.putString("Pamphlet", webinarPamphlet);
                bundle.putSerializable("WebinarData", webinarDetail);
                choosenFragment = new UpdateWebinar();
                choosenFragment.setArguments(bundle);
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.webinarLinearLayoutUser, choosenFragment) ;
                ft.addToBackStack(null) ;
                ft.commit() ;
                break;
            default:
                break;
        }
    }
}