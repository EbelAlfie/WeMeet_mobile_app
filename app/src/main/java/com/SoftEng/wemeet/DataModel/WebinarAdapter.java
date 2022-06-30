package com.SoftEng.wemeet.DataModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SoftEng.wemeet.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WebinarAdapter extends RecyclerView.Adapter<WebinarAdapter.WebinarViewHolder>{

    private Context context ;
    private ArrayList<WebinarFrontContainer> webinarLists ;
    private OnWebinarClickListener onWebinarClickListener ;

    public void setFilteredList(ArrayList<WebinarFrontContainer> filteredList){
        webinarLists = filteredList ;
        notifyDataSetChanged();
    }

    public WebinarAdapter(Context context, ArrayList<WebinarFrontContainer> webinarLists, OnWebinarClickListener onWebinarClickListener) {
        this.context = context;
        this.webinarLists = webinarLists;
        this.onWebinarClickListener = onWebinarClickListener;
    }

    //Layout mana yang bakal jadi view Holder? tempat list ditampilkan
    @NonNull
    @Override
    public WebinarAdapter.WebinarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.webinar_records, parent, false);
        return new WebinarViewHolder(view, onWebinarClickListener);
    }

    //Bind value yang mau ditampilkan ke layoutnya (layoutnya ada di view WebinarViewHolder yang diinisialisasi sbg holder)
    @Override
    public void onBindViewHolder(@NonNull WebinarViewHolder holder, int position) { //kode 1 = home webinar 2 = display webinar
        WebinarFrontContainer webinarFront = webinarLists.get(position) ; //satu item aja

        //store Image
        //Toast.makeText(context, webinarFront.getHasPamphlet(), Toast.LENGTH_SHORT).show();
        if (webinarFront.getHasPamphlet().toLowerCase().trim().equals("true")) {
            Glide.with(context).load(webinarFront.getPamphlet())
                    .fitCenter().centerCrop().placeholder(R.drawable.placeholder)
                    .into(holder.pamphlet);
        }else{
            holder.pamphlet.setImageResource(R.drawable.placeholder);
        }
        holder.title.setText(webinarFront.getTitle()); //view title yang udh di inflate ke holder
    }


    //jumlah item di data set yang disimpan adapter
    @Override
    public int getItemCount() {
        return webinarLists.size();
    }

    //Inner class view holder
    public static class WebinarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title ;
        ImageView pamphlet;
        OnWebinarClickListener onWebinarClickListener;

        public WebinarViewHolder(@NonNull View itemView, OnWebinarClickListener onWebinarClickListener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.webinarTitle);
            pamphlet = (ImageView) itemView.findViewById(R.id.webinarPamphletView) ;

            this.onWebinarClickListener= onWebinarClickListener ;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onWebinarClickListener.onWebinarClick(getAdapterPosition());
        }
    }
    //when you detect click into the recycler view, the best practice is to use an interface and use
    //that interface inside view Holder class
     public interface OnWebinarClickListener{
        void onWebinarClick(int position) ;
    }
    //kemungkinan onClickListener itu interface View
}
