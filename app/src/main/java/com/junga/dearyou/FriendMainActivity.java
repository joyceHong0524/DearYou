package com.junga.dearyou;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FriendMainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private final int GOT_FRIEND = 0;


    RecyclerView recyclerView;

    TextView friend_diaryName;
    TextView friend_nickname;

    ImageView friend_status;
    boolean isFriend = false; //defualt is false;

    UserItem friendUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_main);

        String friendEmail = getIntent().getStringExtra("email");

        CollectionReference collection = db.collection("User");
        Query getFriendQuery = collection.whereEqualTo("email",friendEmail);

        getFriendQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> docs =  querySnapshot.getDocuments();

                if(docs.size() ==1 ){
                    Log.d(TAG,"Found your friend");
                    DocumentSnapshot doc = docs.get(0);
                    friendUser = doc.toObject(UserItem.class);
                    Log.d(TAG,"check frined name "+friendUser.getNickname());
                    Log.d(TAG,"check frined email "+friendUser.getEmail());

                    handler.sendEmptyMessage(GOT_FRIEND);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Something went wrong getting friend's information");
                //if you can't get a friend user's informatino just finish the activity.
                finish();
            }
        });




    }


    private void setFriendInfo(){

        //1. Check if it is my friend or not; -> set image view

        //2. Set Diaryname and set friend name;


    }

    private void setRecyclerView(){

        // ** Most improtant thing is only showing unlocked diaries. **

        //1. Get List only unlocked diaries.

        //2. set layout and set adapter to recyclerView



    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == GOT_FRIEND){
                setFriendInfo();
                setRecyclerView();
            }
        }
    };
}
