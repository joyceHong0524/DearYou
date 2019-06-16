package com.junga.dearyou;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.junga.dearyou.lib.FabLib;
import com.junga.dearyou.lib.FontLib;

import java.util.ArrayList;
import java.util.List;

public class FriendMainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private final int GOT_FRIEND = 0;


    RecyclerView recyclerView;

    TextView friend_diaryName;
    TextView friend_nickname;

    Button friend_status;
    boolean isFriend = false; //defualt is false;

    UserItem friendUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MyDiaryAdapter adapter;

    FabLib fab;

    FontLib fontLib = new FontLib();

    @Override
    protected void onResume() {
        super.onResume();
        fab.closeFABMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_main);

        //set basic views

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        friend_diaryName = (TextView) findViewById(R.id.friend_diaryName);
        friend_nickname = (TextView) findViewById(R.id.friend_nickname);

        friend_status = (Button) findViewById(R.id.friend_status);


        friend_status.setOnClickListener(this);

        fontLib.setFont(this, "oleo_script_bold", friend_diaryName);
        fontLib.setFont(this, "oleo_script", friend_nickname);
        fontLib.setFont(this, "oleo_script", friend_status);


        // get friend userItem.


        String friendEmail = getIntent().getStringExtra("email");

        CollectionReference collection = db.collection("User");
        Query getFriendQuery = collection.whereEqualTo("email", friendEmail);

        getFriendQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> docs = querySnapshot.getDocuments();

                if (docs.size() == 1) {
                    Log.d(TAG, "Found your friend");
                    DocumentSnapshot doc = docs.get(0);
                    friendUser = doc.toObject(UserItem.class);
                    handler.sendEmptyMessage(GOT_FRIEND);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Something went wrong getting friend's information");
                //if you can't get a friend user's informatino just finish the activity.
                finish();
            }
        });

        fab = new FabLib(FriendMainActivity.this);
        fab.setFabMenu();


    }


    private void setFriendInfo() {

        //1. Check if it is my friend or not; -> set image view

        ArrayList<String> myFriends = MyApp.getApp().getUser().getFriends();
        isFriend = myFriends.contains(friendUser.getEmail());

        Log.d(TAG, "Is this my friend? " + isFriend);
        if (isFriend) {
            friend_status.setText(R.string.friend_delete);
//            Glide.with(this).load(R.drawable.friend_checked).into(friend_status);
        } else {
            friend_status.setText(R.string.friend_add);
//            Glide.with(this).load(R.drawable.friend_add).into(friend_status);
        }

        //2. Set Diaryname and set friend name;

        friend_diaryName.setText(friendUser.getDiaryName());
        friend_nickname.setText(friendUser.getNickname());

        setRecyclerView();

    }

    private void setRecyclerView() {

        // ** Most improtant thing is only showing unlocked diaries. **

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        ArrayList<DiaryItem> unLockedDiaries = new ArrayList<>();

        for (DiaryItem item : friendUser.getDiaries()) {
            if (!item.isLocked()) {
                unLockedDiaries.add(item);
            }
        }

        adapter = new MyDiaryAdapter(this, getApplicationContext(), unLockedDiaries, MyDiaryAdapter.FRIEND_MAIN);
        //When you go to FriendViewActivity , use Diary Id.
        recyclerView.setAdapter(adapter);

    }

    private void toggleFriendStatus() {
        //If they were friends -> unfollow,
        //If they were not friends -> follow each other.

        if (!isFriend) { //if they are not friend
            //update mine
            ArrayList<String> newFriends = MyApp.getApp().getUser().getFriends();
            newFriends.add(friendUser.getEmail());

            DocumentReference myRef = db.collection("User").document(MyApp.getApp().getUser().getUserId());

            myRef.update("friends", newFriends)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "successfully updated");
                            Toast.makeText(FriendMainActivity.this, R.string.add_friend_success, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "ooopsie something went wrong.");
                    Toast.makeText(FriendMainActivity.this, R.string.add_friend_failure, Toast.LENGTH_SHORT).show();
                }
            });

            //update friends
            ArrayList<String> friendsOfFriend = friendUser.getFriends();
            friendsOfFriend.add(MyApp.getApp().getUser().getEmail());

            DocumentReference friendRef = db.collection("User").document(friendUser.getUserId());

            friendRef.update("friends", friendsOfFriend)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "successfully update");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "ooooops something went wrong" + e.toString());
                    Toast.makeText(FriendMainActivity.this, R.string.add_friend_failure, Toast.LENGTH_SHORT).show();
                }
            });

            //update info ui

            isFriend = true;
            friend_status.setText(R.string.friend_delete);
        } else {

            ArrayList<String> newFriends = MyApp.getApp().getUser().getFriends();
            newFriends.remove(friendUser.getEmail());

            DocumentReference myRef = db.collection("User").document(MyApp.getApp().getUser().getUserId());

            myRef.update("friends", newFriends)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "successfully updated");
                            Toast.makeText(FriendMainActivity.this, R.string.add_friend_success, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "ooopsie something went wrong.");
                    Toast.makeText(FriendMainActivity.this, R.string.add_friend_failure, Toast.LENGTH_SHORT).show();

                }
            });

            //update friends
            ArrayList<String> friendsOfFriend = friendUser.getFriends();
            friendsOfFriend.remove(MyApp.getApp().getUser().getEmail());

            DocumentReference friendRef = db.collection("User").document(friendUser.getUserId());

            friendRef.update("friends", friendsOfFriend)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "successfully update");
                            Toast.makeText(FriendMainActivity.this, R.string.add_friend_success, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "ooooops! something went wrong" + e.toString());
                    Toast.makeText(FriendMainActivity.this, R.string.add_friend_failure, Toast.LENGTH_SHORT).show();
                }
            });

            isFriend = false;
            friend_status.setText(R.string.friend_add);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.friend_status) {
            toggleFriendStatus();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == GOT_FRIEND) {
                Log.d(TAG, "I will set Friend info");
                setFriendInfo();
            }
        }
    };


}
