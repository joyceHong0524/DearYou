package com.junga.dearyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.junga.dearyou.lib.FabLib;
import com.junga.dearyou.lib.FontLib;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    FloatingActionButton fab_menu;
    FloatingActionButton fab_add;
    FloatingActionButton fab_surfing;
    FloatingActionButton fab_friends;
    FloatingActionButton fab_setting;

    RecyclerView recyclerView;
    MyDiaryAdapter adapter;

    TextView diaryName;
    TextView nickname;
    boolean isMenuOpen = false;
    FabLib fab;
    FontLib fontLib = new FontLib();
    private int backbuttonPressed = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        //Initialize all views.
        View info = (View) findViewById(R.id.info);
        info.setOnClickListener(this);

        diaryName = (TextView) findViewById(R.id.diaryName);
        nickname = (TextView) findViewById(R.id.nickname);


        String email = getIntent().getStringExtra("email");
        setFont();
        setInfoView();
        setRecyclerView();
//        fab = new FabLib(MainActivity.this);
//        fab.setFabMenu();
    }

    //When user comes back to main activity, reload recyclerView.
    @Override
    public void onResume() {
        super.onResume();
        //Writing Activity에서 다시 돌아왔을 때 RecyclerView를 다시 로딩한다.
        setRecyclerView();
        setInfoView();
//        fab.closeFABMenu();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.info) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    private void setRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        adapter = new MyDiaryAdapter(this, getApplicationContext(), MyApp.getApp().getUser().getDiaries(), MyDiaryAdapter.MY_MAIN);
        recyclerView.setAdapter(adapter);

    }

    private void setInfoView() {
        diaryName.setText(MyApp.getApp().getUser().getDiaryName());
        String setTextNickname = "By " + MyApp.getApp().getUser().getNickname();
        nickname.setText(setTextNickname);

    }

    private void setFont() {
        fontLib.setFont(this, "roboto_thin", diaryName);
        fontLib.setFont(this, "roboto_regular", nickname);
    }

    @Override
    public void onBackPressed() {
        if (backbuttonPressed >= 1) {
            moveTaskToBack(true);
            finish();
        } else {
            Toast.makeText(this, R.string.kill_app, Toast.LENGTH_SHORT).show();
            backbuttonPressed++;
        }
    }

}
