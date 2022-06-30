package com.SoftEng.wemeet.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.SoftEng.wemeet.R;
import com.SoftEng.wemeet.home.WebinarSettings.DisplayUsersWebinar;
import com.SoftEng.wemeet.home.WebinarSettings.WebinarPost;

public class ModifyWebinar extends AppCompatActivity implements View.OnClickListener{
    Button button ;
    TextView create, update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_webinar);
        button = (Button) findViewById(R.id.btn_backtoProfile) ;
        create = (TextView) findViewById(R.id.createWebinar) ;
        update = (TextView) findViewById(R.id.updateWebinar) ;

        button.setOnClickListener(this);
        create.setOnClickListener(this);
        update.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Fragment choosenFragment = null;
            switch (view.getId()) {
                case R.id.btn_backtoProfile:
                    finish();
                    break;
                case R.id.createWebinar:
                    choosenFragment = new WebinarPost();
                    break;
                case R.id.updateWebinar:
                    choosenFragment = new DisplayUsersWebinar();
                    break;
                default:
                    break;
            }
            if (choosenFragment != null) {
                fragmentChange(choosenFragment);
            }
    }
    private void fragmentChange(Fragment fragment){
        FragmentManager fragmentManage = getSupportFragmentManager() ;
        FragmentTransaction fragmentTransaction = fragmentManage.beginTransaction() ;
        fragmentTransaction.replace(R.id.modifyWebinarF,fragment, null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit() ;//isi replace itu, id dari container fragment (fragment layout tempat di mana fragment akan dipasang), kemudian objek fragmentnya.
    }
}