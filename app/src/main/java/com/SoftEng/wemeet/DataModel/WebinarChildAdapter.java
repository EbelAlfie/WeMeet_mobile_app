package com.SoftEng.wemeet.DataModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SoftEng.wemeet.R;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class WebinarChildAdapter extends RecyclerView.Adapter<WebinarChildAdapter.WebinarViewHolder>{

    private Context context ;
    private ArrayList<WebinarFrontContainer> webinarLists ;
    private OnWebinarClickListener onWebinarClickListener ;
    private FirebaseStorage imageStorage ;

    public WebinarChildAdapter(Context context, ArrayList<WebinarFrontContainer> webinarLists, OnWebinarClickListener onWebinarClickListener) {
        this.context = context;
        this.webinarLists = webinarLists;
        this.onWebinarClickListener = onWebinarClickListener;
    }

    //Layout mana yang bakal jadi view Holder? tempat list ditampilkan
    @NonNull
    @Override
    public WebinarChildAdapter.WebinarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_webinar_record, parent, false);
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
    public static class WebinarViewHolder extends RecyclerView.ViewHolder{
        TextView title ;
        ImageView pamphlet, delete;
        OnWebinarClickListener onWebinarClickListener;

        public WebinarViewHolder(@NonNull View itemView, OnWebinarClickListener onWebinarClickListener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.webinarTitle);
            pamphlet = (ImageView) itemView.findViewById(R.id.webinarPamphletView) ;
            delete = (ImageView) itemView.findViewById(R.id.deleteBtn) ;

            this.onWebinarClickListener= onWebinarClickListener ;

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //delete webinar
                    onWebinarClickListener.onDeleteListener(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onWebinarClickListener.onWebinarClick(getAdapterPosition());
                }
            });
        }

    }
    //when you detect click into the recycler view, the best practice is to use an interface and use
    //that interface inside view Holder class
    public interface OnWebinarClickListener{
        void onWebinarClick(int position) ;
        void onDeleteListener(int position);
    }
    public void setOnWebinarCLickedListener(OnWebinarClickListener clickedListener){
        onWebinarClickListener = clickedListener ;
    }
    //kemungkinan onClickListener itu interface View
}