package com.SoftEng.wemeet.home;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.SoftEng.wemeet.R;
import com.bumptech.glide.Glide;

public class FullScreenFragment extends DialogFragment {
    ImageView fullSizedImage ;
    String imageUrl ;

    public static FullScreenFragment newInstance(String imageUrl){
        FullScreenFragment frag = new FullScreenFragment() ;
        Bundle bundle = new Bundle() ;
        bundle.putString("imageUrl", imageUrl);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getArguments().getString("imageUrl") ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_full_screen, container, false) ;
        fullSizedImage = v.findViewById(R.id.fullImage) ;
        Glide.with(getContext()).load(imageUrl)
                .fitCenter().placeholder(R.drawable.placeholder)
                .into(fullSizedImage);
        return v;
    }
}