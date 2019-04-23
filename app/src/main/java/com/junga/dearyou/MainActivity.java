package com.junga.dearyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.junga.dearyou.lib.FabLib;

import org.w3c.dom.Text;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize all views.
        View info = (View) findViewById(R.id.info);
        info.setOnClickListener(this);

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

        diaryName = (TextView) findViewById(R.id.diaryName);
        nickname = (TextView) findViewById(R.id.nickname);


        String email = getIntent().getStringExtra("email");

        setInfoView();
        setRecyclerView();
        FabLib fab = new FabLib(MainActivity.this);
        fab.setFabMenu();
    }

    //When user comes back to main activity, reload recyclerView.
    @Override
    public void onResume() {
        super.onResume();
        //Writing Activity에서 다시 돌아왔을 때 RecyclerView를 다시 로딩한다.
        setRecyclerView();
        setInfoView();

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
}
