package com.junga.dearyou.lib;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import com.junga.dearyou.FriendActivity;
import com.junga.dearyou.MainActivity;
import com.junga.dearyou.ProfileActivity;
import com.junga.dearyou.R;
import com.junga.dearyou.WritingActivity;

public class FabLib {

    private FabLib instance;

    FloatingActionButton fab_menu;
    FloatingActionButton fab_add;
    FloatingActionButton fab_surfing;
    FloatingActionButton fab_friends;
    FloatingActionButton fab_setting;

    Activity mActivity;

    boolean isMenuOpen = false;


    public FabLib(Activity mActivity){
        this.mActivity = mActivity;
    }

    public boolean isMenuOpen() {
        return isMenuOpen;
    }

    public void setMenuOpen(boolean menuOpen) {
        isMenuOpen = menuOpen;
        setFabMenu();
    }

    public void setFabMenu() {

        fab_menu = (FloatingActionButton) mActivity.findViewById(R.id.fab_menu);
        fab_add = (FloatingActionButton) mActivity.findViewById(R.id.fab_add);
        fab_surfing = (FloatingActionButton) mActivity.findViewById(R.id.fab_surfing);
        fab_friends = (FloatingActionButton) mActivity.findViewById(R.id.fab_friends);
        fab_setting = (FloatingActionButton) mActivity.findViewById(R.id.fab_setting);


        fab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMenuOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, WritingActivity.class);
                intent.putExtra("mode", 0); // value 1 : update , value 0 : initial save.
                mActivity.startActivity(intent);
            }
        });


        fab_surfing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity,"Coming Soon!",Toast.LENGTH_SHORT).show();
            }
        });

        fab_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, FriendActivity.class);
                mActivity.startActivity(intent);
            }
        });

        fab_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ProfileActivity.class);
                mActivity.startActivity(intent);
            }
        });

    }


    public void showFABMenu() {
        isMenuOpen = true;
        fab_setting.animate().translationY(-mActivity.getResources().getDimension(R.dimen.standard_65));
        fab_friends.animate().translationY(-mActivity.getResources().getDimension(R.dimen.standard_135));
        fab_surfing.animate().translationY(-mActivity.getResources().getDimension(R.dimen.standard_205));
        fab_add.animate().translationY(-mActivity.getResources().getDimension(R.dimen.standard_275));

        fab_setting.setElevation(mActivity.getResources().getDimension(R.dimen.fab_elevation));
        fab_friends.setElevation(mActivity.getResources().getDimension(R.dimen.fab_elevation));
        fab_surfing.setElevation(mActivity.getResources().getDimension(R.dimen.fab_elevation));
        fab_add.setElevation(mActivity.getResources().getDimension(R.dimen.fab_elevation));

    }

    public void closeFABMenu() {
        isMenuOpen = false;
        fab_add.animate().translationY(0);
        fab_surfing.animate().translationY(0);
        fab_friends.animate().translationY(0);
        fab_setting.animate().translationY(0);

        fab_setting.setElevation(0);
        fab_friends.setElevation(0);
        fab_surfing.setElevation(0);
        fab_add.setElevation(0);
    }

}

