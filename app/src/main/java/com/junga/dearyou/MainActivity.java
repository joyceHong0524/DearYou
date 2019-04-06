package com.junga.dearyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    FloatingActionButton fab_menu;
    FloatingActionButton fab_add;
    FloatingActionButton fab_surfing;
    FloatingActionButton fab_friends;
    FloatingActionButton fab_setting;

    boolean isMenuOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize all views.

        fab_menu = (FloatingActionButton) findViewById(R.id.fab_menu);
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_surfing = (FloatingActionButton) findViewById(R.id.fab_surfing);
        fab_friends = (FloatingActionButton) findViewById(R.id.fab_friends);
        fab_setting = (FloatingActionButton) findViewById(R.id.fab_setting);

        fab_add.setOnClickListener(this);
        fab_menu.setOnClickListener(this);
        fab_surfing.setOnClickListener(this);
        fab_friends.setOnClickListener(this);
        fab_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.fab_menu){
            if(!isMenuOpen){
                showFABMenu();
            } else{
                closeFABMenu();
            }
        } else if(v.getId()==R.id.fab_add){
            Intent intent = new Intent(MainActivity.this, WritingActivity.class);
            startActivity(intent);
        } else if(v.getId()==R.id.fab_surfing){

        } else if(v.getId()==R.id.fab_friends){

        }
         else if(v.getId()==R.id.fab_setting){

        }


    }

    private void showFABMenu(){
        isMenuOpen=true;
        fab_setting.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
        fab_friends.animate().translationY(-getResources().getDimension(R.dimen.standard_135));
        fab_surfing.animate().translationY(-getResources().getDimension(R.dimen.standard_205));
        fab_add.animate().translationY(-getResources().getDimension(R.dimen.standard_275));
    }

    private void closeFABMenu(){
        isMenuOpen=false;
        fab_add.animate().translationY(0);
        fab_surfing.animate().translationY(0);
        fab_friends.animate().translationY(0);
        fab_setting.animate().translationY(0);
    }
}
