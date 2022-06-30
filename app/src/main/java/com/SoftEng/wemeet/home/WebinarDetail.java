package com.SoftEng.wemeet.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.SoftEng.wemeet.DataModel.WebinarContainer;
import com.SoftEng.wemeet.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class WebinarDetail extends Fragment{
    private ImageView pamphletDetail;
    private TextView titleDetail, speakerDetail, dateDetail, timeDetail, feeDetail, linkDetail, certifDetail ;

    private ProgressDialog progressDialog ;
    private FirebaseFirestore db ;
    private String webinarID, webinarPamphlet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            try {
                webinarID = bundle.getString("IDWebinar");
                webinarPamphlet = bundle.getString("Pamphlet");
            }catch(Exception e){
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webinar_detail, container, false) ;

        pamphletDetail = (ImageView) view.findViewById(R.id.pamphletDetail) ;
        titleDetail= (TextView) view.findViewById(R.id.detailTitle);

        speakerDetail =(TextView) view.findViewById(R.id.detailSpeaker);
        dateDetail = (TextView) view.findViewById(R.id.detailDate);
        timeDetail = (TextView) view.findViewById(R.id.detailTime);
        feeDetail = (TextView) view.findViewById(R.id.detailFee);
        linkDetail = (TextView) view.findViewById(R.id.detailLink);
        linkDetail.setMovementMethod(LinkMovementMethod.getInstance());
        certifDetail = (TextView) view.findViewById(R.id.detailCert);

        db = FirebaseFirestore.getInstance() ;
        //query data webinar di global collection
        try{
            progressDialog = new ProgressDialog(getContext()) ;
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            db.collection("Webinars").document(webinarID)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        WebinarContainer webinarDetail = documentSnapshot.toObject(WebinarContainer.class);
                        if(webinarDetail.getPamphlet().trim().toLowerCase().equals("true")){
                            Glide.with(getContext()).load(webinarPamphlet)
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
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }

        pamphletDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenFragment frag = new FullScreenFragment() ;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Create and show the dialog.
                DialogFragment newFragment = FullScreenFragment.newInstance(webinarPamphlet);
                newFragment.show(ft, "dialog");
            }
        });

        return view;
    }

}
