package com.SoftEng.wemeet.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.SoftEng.wemeet.R;
import com.SoftEng.wemeet.DataModel.UserContainer;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity{
    private FirebaseUser usernow ;
    private DatabaseReference dbref ;
    private StorageReference imageRef ;
    private String userID ;
    private UserContainer userProfile;
    private String username ;
    private String useremail ;
    private Boolean hasImage = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        usernow = FirebaseAuth.getInstance().getCurrentUser() ;
        dbref = FirebaseDatabase.getInstance().getReference("Users") ;
        userID = usernow.getUid() ;
        dbref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile = snapshot.getValue(UserContainer.class) ;
                if(userProfile != null){
                    username = userProfile.getUsename() ;
                    useremail = userProfile.getUserEmail() ;
                    hasImage = userProfile.getHasImage() ;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });

        fragmentChange(new HomeFragment()); //set default fragment as home fragment

        //Toast.makeText(this, username + useremail, Toast.LENGTH_SHORT).show();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbarbottom) ;
        bottomNavigationView.setOnItemSelectedListener(navselected);
    }

    private NavigationBarView.OnItemSelectedListener navselected =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null ;
                    switch(item.getItemId()){
                        case R.id.home_nav:
                            selectedFragment = new HomeFragment() ;
                            break;
                        case R.id.account_nav:
                            try {
                                Bundle bundle = new Bundle();
                                bundle.putString("UserName", username);
                                bundle.putString("UserEmail", useremail);
                                bundle.putBoolean("HasImage", hasImage);
                                selectedFragment = new ProfileFragment();
                                selectedFragment.setArguments(bundle);
                            }catch(Exception e){
                                Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default :
                            break ;
                    }
                    fragmentChange(selectedFragment);
                    return true;
                }
            } ;
    private void fragmentChange(Fragment fragment){
        FragmentManager fragmentManage = getSupportFragmentManager() ;
        FragmentTransaction fragmentTransaction = fragmentManage.beginTransaction() ;
        fragmentTransaction.replace(R.id.isiFragment,fragment, null)
                .commit() ; //isi replace itu, id dari container fragmen (fragment layout tempat di mana fragment akan dipasang), kemudian objek fragmentnya.
    }
}


/*
dbref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserContainer userProfile = snapshot.getValue(UserContainer.class) ;
                                                String username = userProfile.getUsename() ;
                                                String useremail = userProfile.getUserEmail() ;
                                                //Toast.makeText(LoginActivity.this, "yey" + username + useremail, Toast.LENGTH_SHORT).show();
                                                intent.putExtra("USERNAMEtag", username) ;
                                                intent.putExtra("USEREMAILtag", useremail) ;
                                                //Toast.makeText(LoginActivity.this, "yey" + username + useremail, Toast.LENGTH_SHORT).show();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getApplicationContext(), "Something wrong happened!", Toast.LENGTH_LONG).show();
                                        }
                                    }) ;



try {
            usernow = FirebaseAuth.getInstance().getCurrentUser() ;
            dbref = FirebaseDatabase.getInstance().getReference("Users") ;
            userID = usernow.getUid() ;
            dbref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserContainer userProfile = snapshot.getValue(UserContainer.class) ;
                    if(userProfile != null){
                        username = userProfile.getUsename() ;
                        useremail = userProfile.getUserEmail() ;
                        //KIRIM KE FRAGMENT
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Something wrong happened!", Toast.LENGTH_LONG).show();
                }
            });
        }catch(Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

*/