package com.junga.dearyou;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.junga.dearyou.lib.FabLib;

import org.w3c.dom.Text;

import java.util.List;

public class FriendActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    EditText editText_search;
    TextView textView_search;

    TextView friendNickname;
    TextView friendDiaryname;
    TextView friendVisit;
    View searchResult;

    RecyclerView recyclerView;

    UserItem friendUser;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

       editText_search = (EditText) findViewById(R.id.editText_search);
       textView_search = (TextView) findViewById(R.id.textView_search);
       searchResult = findViewById(R.id.search_result);

       friendNickname = (TextView) findViewById(R.id.friendNickname);
       friendDiaryname = (TextView) findViewById(R.id.friend_diaryName);
       friendVisit = (TextView) findViewById(R.id.visit);

       recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

       textView_search.setOnClickListener(this);
       friendVisit.setOnClickListener(this);

       setRecyclerView();
        FabLib fab = new FabLib(FriendActivity.this);
        fab.setFabMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerView();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.textView_search){
            friendSearch();
        } else if(v.getId() == R.id.visit){
            visitFriend();
        }
    }

    private void friendSearch(){
        String userEmail = editText_search.getText().toString();
        CollectionReference userCollection = db.collection("User");
        Query getUserQuery = userCollection.whereEqualTo("email",userEmail);


        getUserQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                    if(docs.size()==1) {
                        Log.d(TAG,"Friend Email founded");
                        DocumentSnapshot document = docs.get(0); //anyways there should be only one document snapshot.
                        friendUser = document.toObject(UserItem.class);
                        setFriendInfo(friendUser.getNickname(),friendUser.getDiaryName());
                    }else {
                        Toast.makeText(FriendActivity.this,"Can't find email Id. Please check it again",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Something went wrong"+e);
            }
        });

    }

    private void visitFriend(){
        // 1. first pass the email of friend.
        Intent intent = new Intent(FriendActivity.this,FriendMainActivity.class);
        intent.putExtra("email",friendUser.getEmail());
        startActivity(intent);
        searchResult.setVisibility(View.GONE);
    }

    private void setFriendInfo(String friendName, String diaryname){
        friendNickname.setText(friendName);
        friendDiaryname.setText(diaryname);
        searchResult.setVisibility(View.VISIBLE);

    }

    private void setRecyclerView(){
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        MyFriendAdapter adapter = new MyFriendAdapter(getApplicationContext(),this,MyApp.getApp().getUser().getFriends());
        recyclerView.setAdapter(adapter);
    }
}
