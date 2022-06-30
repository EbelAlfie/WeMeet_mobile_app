package com.SoftEng.wemeet.home.WebinarSettings;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.SoftEng.wemeet.DataModel.WebinarChildAdapter;
import com.SoftEng.wemeet.DataModel.WebinarFrontContainer;
import com.SoftEng.wemeet.R;
import com.SoftEng.wemeet.home.EditUserWebinars;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DisplayUsersWebinar extends Fragment implements WebinarChildAdapter.OnWebinarClickListener {
    private RecyclerView recyclerView ;
    private ArrayList<WebinarFrontContainer> webinarList;
    private WebinarChildAdapter webinarAdapter ;
    private FirebaseFirestore db;
    private StorageReference imageRef ;
    private ProgressDialog progressDialog ;

    private FirebaseUser user ;
    private Button button ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        webinarAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_users_webinar, container, false) ;
        button = (Button) view.findViewById(R.id.btn_backtoProfile1) ;
        recyclerView = (RecyclerView) view.findViewById(R.id.webinar_recyclerView) ;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext())); //?

        //database
        db = FirebaseFirestore.getInstance() ;
        user = FirebaseAuth.getInstance().getCurrentUser() ;

        webinarList = new ArrayList<WebinarFrontContainer>() ;
        webinarAdapter= new WebinarChildAdapter(getActivity().getApplicationContext(), webinarList,  this) ;

        recyclerView.setAdapter(webinarAdapter);

        //perlu adapter

        webinarAdapter.setOnWebinarCLickedListener(this);

        eventChangeListener() ;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish() ;
            }
        });

        return view;
    }

    private void eventChangeListener() {
        //query webinar yang dibuat user sendiri ke recyclerView editWebinar
        db.collection("WebinarUploadersFront").document(user.getUid()).collection("Webinars")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Database Error", error.getMessage()) ;
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED) {
                        //Add data display recycler view
                        webinarList.add(dc.getDocument().toObject(WebinarFrontContainer.class));
                        //add detailed data
                    }
                }
                //query juga full datanya
                webinarAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onWebinarClick(int position) {
        //buka detilnya
        Intent intent = new Intent(getContext(), EditUserWebinars.class) ;
        intent.putExtra("IDWebinar", webinarList.get(position).getID());
        intent.putExtra("Pamphlet", webinarList.get(position).getPamphlet());
        startActivity(intent);
    }

        @Override
        public void onDeleteListener(int position) {
            AlertDialog.Builder deleteMsg = new AlertDialog.Builder(getContext()) ;
            deleteMsg.setTitle("Delete Webinar") ;
            deleteMsg.setMessage("Are you sure?") ;
            deleteMsg.setCancelable(false) ;
            deleteMsg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progressDialog = new ProgressDialog(getContext()) ;
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Deleting...");
                    progressDialog.show();
                    String webinarID = webinarList.get(position).getID();
                    db.collection("Webinars").document(webinarID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection("WebinarFront").document(webinarID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db.collection("WebinarUploaders")
                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .collection("Webinars").document(webinarID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("WebinarUploadersFront")
                                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .collection("Webinars").document(webinarID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    if(!webinarList.get(position).getPamphlet().equals("android.resource://com.SoftEng.wemeet/"+ R.drawable.placeholder)){
                                                        imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(webinarList.get(position).getPamphlet()) ;
                                                        imageRef.delete() ;
                                                    }
                                                    //remove dari list sekaligus adapternya
                                                    webinarList.remove(position) ;
                                                    webinarAdapter.notifyItemRemoved(position);
                                                    if(progressDialog.isShowing()){
                                                        progressDialog.dismiss();
                                                    }
                                                    Toast.makeText(getContext(), "Webinar deleted successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            }) ;
                                        }
                                    }) ;
                                }
                            }) ;
                        }
                    }) ;
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel() ;
                }
            });
            AlertDialog alertDialog = deleteMsg.create();
            alertDialog.show() ;
        }

}