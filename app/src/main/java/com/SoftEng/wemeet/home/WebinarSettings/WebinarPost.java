package com.SoftEng.wemeet.home.WebinarSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.SoftEng.wemeet.DataModel.WebinarContainer;
import com.SoftEng.wemeet.DataModel.WebinarFrontContainer;
import com.SoftEng.wemeet.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WebinarPost extends Fragment implements View.OnClickListener{
    private static final int PICK_FROM_GALLERY = 1;
    private EditText title, time, speaker, date, fee, link;
    private ImageView pamphlet;
    private Button post, defaultImage;
    private DatePickerDialog datePickerDialog ;
    private RadioGroup radioGroup ;
    private ProgressDialog progressDialog ;

    private Calendar calendar ;
    private Uri imageuri ;
    private int hour, minute ;

    private FirebaseFirestore db ;
    private StorageReference storageReference;
    private FirebaseUser userNow ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.activity_webinar_post, container, false);

        pamphlet = (ImageView) v.findViewById(R.id.pamphletImageView) ;
        defaultImage = (Button) v.findViewById(R.id.defaultimage) ;
        title = (EditText) v.findViewById(R.id.editTextTitle) ;

        speaker = (EditText) v.findViewById(R.id.editTextPembicara);
        date = (EditText) v.findViewById(R.id.editText_date) ;
        time = (EditText) v.findViewById(R.id.editText_time) ;
        fee = (EditText) v.findViewById(R.id.editText_fee);
        link = (EditText) v.findViewById(R.id.editTextLink) ;
        post = (Button) v.findViewById(R.id.btn_updateWebinar) ;
        radioGroup = (RadioGroup) v.findViewById(R.id.radioCertStatus) ;

        calendar = Calendar.getInstance() ;
        storageReference = FirebaseStorage.getInstance().getReference() ;
        imageuri = Uri.parse("android.resource://com.SoftEng.wemeet/" + R.drawable.placeholder) ;
        userNow = FirebaseAuth.getInstance().getCurrentUser() ;

        pamphlet.setOnClickListener(this);
        date.setOnClickListener(this);
        time.setOnClickListener(this);
        post.setOnClickListener(this);
        defaultImage.setOnClickListener(this);
        return v ;
    }

    @Override
    public void onClick(View view) {
        final int YEAR = calendar.get(Calendar.YEAR) ;
        final int MONTH = calendar.get(Calendar.MONTH) ;
        final int DAY = calendar.get(Calendar.DAY_OF_MONTH) ;

        switch(view.getId()){
            case R.id.editText_date:
                try {
                    datePickerDialog = new DatePickerDialog(getContext(), //kalau pake getActivity().getAppcontext(), muncul error is your activity running?
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int day) {
                                    DateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy") ;
                                    month = month + 1 ;
                                    String currdate = day + "/" + month + "/" + year;
                                    Date dateToPrint = null;
                                    try {
                                        dateToPrint = dateFormater.parse(currdate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    date.setText(dateFormater.format(dateToPrint));
                                }
                            }, YEAR, MONTH, DAY);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                }catch(Exception e){
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.editText_time:
                //for time
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour = selectedHour ;
                        minute = selectedMinute ;
                        time.setText(String.format(Locale.getDefault(), "%02d:%02d" ,selectedHour, selectedMinute));
                    }
                } ;
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), AlertDialog.THEME_HOLO_DARK, onTimeSetListener, hour, minute, true) ;
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
                break;
            case R.id.pamphletImageView:
                //Buka galery
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent galeryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galeryIntent, 100);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btn_updateWebinar:
                AlertDialog.Builder confirm = new AlertDialog.Builder(getContext()) ;
                confirm.setTitle("Upload Webinar") ;
                confirm.setCancelable(false) ;
                confirm.setMessage("Are you sure this is the correct data?") ;
                confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        webinarUpload() ;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }) ;
                AlertDialog alertDialog = confirm.create();
                alertDialog.show() ;
                break ;
            case R.id.defaultimage:
                imageuri = Uri.parse("android.resource://com.SoftEng.wemeet/" + R.drawable.placeholder) ;
                Glide.with(getContext())
                        .load(imageuri)
                        .fitCenter().centerCrop().into(pamphlet) ;
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                imageuri = data.getData() ;
                pamphlet.setImageURI(imageuri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void webinarUpload(){
        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy") ;
        calendar = Calendar.getInstance() ;
        Integer webinarHour = hour ;
        Boolean hasImage = false ;

        String tempTitle = title.getText().toString().trim() ;
        String tempSpeaker = speaker.getText().toString().trim() ;
        String tempFee = fee.getText().toString().trim() ;
        String tempLink = link.getText().toString().trim() ;
        String tempDate = date.getText().toString();
        String tempTime = time.getText().toString() ;
        String certificate ;
        Uri tempPamphlet = imageuri ;

        if(!tempPamphlet.equals(Uri.parse("android.resource://com.SoftEng.wemeet/" + R.drawable.placeholder))){
            hasImage = true ;
        }

        //inisialisasi Radio button
        int selectedId = radioGroup.getCheckedRadioButtonId() ;
        certificate = selectedId == R.id.availableRadiobtn ? "Available" : "Unavailable" ;

        //cek isi
        if(tempTitle.isEmpty()){
            title.setError("Title must not be empty!");
            title.requestFocus() ;
            return ;
        }
        if(tempSpeaker.isEmpty()){
            speaker.setError("Email must not be empty!");
            speaker.requestFocus() ;
            return ;
        }

        Date webinarDate = null;
        try {
            webinarDate = parser.parse(tempDate);
        } catch (ParseException e) { e.printStackTrace(); }

        if(tempDate.isEmpty()){
            date.setError("Date must not be empty");
            date.requestFocus() ;
            return ;
        }
        if(tempTime.isEmpty()){
            time.setError("Time must not be empty");
            time.requestFocus() ;
            return;
        }
        //cek waktu
        if(webinarDate.compareTo(calendar.getTime()) == 0 && webinarHour <= calendar.get(Calendar.HOUR_OF_DAY)){
            time.setError("Invalid time");
            time.requestFocus() ;
            return;
        }

        if(tempFee.isEmpty()){
            tempFee = "0" ;
        }
        if(tempLink.isEmpty()){
            link.setError("Link must not be empty!");
            link.requestFocus() ;
            return ;
        }

        progressDialog = new ProgressDialog(getContext()) ;
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading webinar...");
        progressDialog.show();

        WebinarContainer map = new WebinarContainer(tempTitle, tempTime, tempSpeaker, hasImage.toString(), tempLink, tempFee, tempDate, certificate) ;

        db = FirebaseFirestore.getInstance() ;
        //Add a new document with a generated ID
        //Add webinar to database as global webinars
        Boolean finalHasImage = hasImage;
        db.collection("Webinars")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String userID = userNow.getUid() ;
                        if(!userID.isEmpty()){
                            //add the pamphlet of the webinar
                            String imageUrl = "android.resource://com.SoftEng.wemeet/" + R.drawable.placeholder ;
                            if(finalHasImage){
                                storageReference = storageReference.child("Webinars/"+ documentReference.getId() + ".jpg") ; //global
                                storageReference.putFile(tempPamphlet).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uriTask = taskSnapshot.getMetadata().getReference().getDownloadUrl() ;
                                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadedUrl = uri.toString() ;
                                                addToUserExclusive(map, userID, documentReference.getId(), downloadedUrl, tempTitle, finalHasImage) ;
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if(progressDialog.isShowing()){
                                                    progressDialog.dismiss();
                                                }
                                                Toast.makeText(getContext(), "Failed to add data!", Toast.LENGTH_SHORT).show();
                                            }
                                        }) ;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if(progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }
                                        Toast.makeText(getContext(), "Failed to add data!", Toast.LENGTH_SHORT).show();
                                    }
                                }) ;;
                            }else{
                                addToUserExclusive(map, userID, documentReference.getId(), imageUrl, tempTitle, finalHasImage) ;
                            }
                        }else{
                            Toast.makeText(getContext(), "You must be logged in!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(), "Failed to add data!", Toast.LENGTH_SHORT).show();
            }
        }) ;
    }

    public void addToUserExclusive(WebinarContainer map, String userId, String docId, String uri, String tempTitle, Boolean finalHasImage){
        //Denormalisation. Add docID and name only.
        WebinarFrontContainer forDisplay = new WebinarFrontContainer(docId, tempTitle, uri, finalHasImage.toString());
        //add webinar utk ditampilkan di recycler view global
        db.collection("WebinarFront").document(forDisplay.getID())
                .set(forDisplay).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //add webinar to Uploaders database exclusive to user (di user display)
                db.collection("WebinarUploaders")
                        .document(userId).collection("Webinars").document(forDisplay.getID()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("WebinarUploadersFront")
                                .document(userId).collection("Webinars").document(forDisplay.getID()).set(forDisplay).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(getActivity().getApplicationContext(), "Webinar Added!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(getContext(), "Failed to add data!", Toast.LENGTH_SHORT).show();
                            }
                        }) ;;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getContext(), "Failed to add data!", Toast.LENGTH_SHORT).show();
                    }
                }) ;;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(), "Failed to add data!", Toast.LENGTH_SHORT).show();
            }
        }) ;
    }
}




/*date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(WebinarPost.this,
                        R.style.Theme_AppCompat_Light_Dialog_MinWidth, setListener, YEAR, MONTH, DAY) ;
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });*/

//masukin datanya ke database
        /*Map<String, Object> map = new HashMap<>();
        map.put("WebinarTitle", tempTitle);
        map.put("WebinarSpeaker", tempSpeaker);
        map.put("WebinarDate", tempDate) ;
        map.put("WebinarTime",tempTime) ;
        map.put("WebinarFee", tempFee);
        map.put("WebinarLink", tempLink);
        map.put("WebinarCertificateStatus", certificate);
        map.put("WebinarPamphlet", hasImage.toString()) ;*/