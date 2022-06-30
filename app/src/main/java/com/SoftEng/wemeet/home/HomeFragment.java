

package com.SoftEng.wemeet.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.SoftEng.wemeet.DataModel.WebinarAdapter;
import com.SoftEng.wemeet.DataModel.WebinarFrontContainer;
import com.SoftEng.wemeet.R;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements WebinarAdapter.OnWebinarClickListener {
    private RecyclerView recyclerView1 ;
    private SearchView searchView ;
    private WebinarAdapter webinarAdapter ;
    private ArrayList<WebinarFrontContainer> webinarList ;
    private FirebaseFirestore db;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView1 = (RecyclerView) view.findViewById(R.id.webinarRecyclerViewHome);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(view.getContext()));
        searchView = view.findViewById(R.id.recyclerSearch);
        searchView.clearFocus();

        MobileAds.initialize(view.getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        loadAd();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        db = FirebaseFirestore.getInstance();

        webinarList = new ArrayList<WebinarFrontContainer>();

        webinarAdapter = new WebinarAdapter(getContext(), webinarList, this);

        recyclerView1.setAdapter(webinarAdapter);

        eventChangeListener();

        return view;
    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(), getString(R.string.interAdId), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(getActivity());
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                        Log.i("HomeFragment", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("HomeFragment", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(getActivity());
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }


    private void eventChangeListener() {
        //fetch from global webinar, masukin ke ArrayList
        db.collection("WebinarFront").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(getContext(), "Error fetching data!", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        webinarList.add(dc.getDocument().toObject(WebinarFrontContainer.class)) ;
                    }
                }
                webinarAdapter.notifyDataSetChanged();
            }
        });
    }

    private void filterList(String queryText) {
        ArrayList<WebinarFrontContainer> filteredList = new ArrayList<>() ;
        for(WebinarFrontContainer eachItem : webinarList){
            if(eachItem.getTitle().toLowerCase().contains(queryText.toLowerCase())){
                filteredList.add(eachItem) ;
            }
        }
        webinarAdapter.setFilteredList(filteredList);
    }

    @Override
    public void onWebinarClick(int position) {
        Fragment fragment = new WebinarDetail() ;
        Bundle bundle = new Bundle() ;
        bundle.putString("IDWebinar", webinarList.get(position).getID());
        //bundle.putSerializable("ObjFront", (Serializable) webinarList.get(position));
        bundle.putString("Pamphlet", webinarList.get(position).getPamphlet());
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction() ;
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null) ;
        ft.replace(R.id.FrameLayoutHome, fragment) ;
        ft.commit() ;
    }
}