package com.junga.dearyou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MyFriendAdapter extends RecyclerView.Adapter<MyFriendAdapter.MyViewHolder> {

    private final String TAG = getClass().getSimpleName();

    ArrayList<String> friendList;
    Activity mActivity;
    MyFriendAdapter mAdapter;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public MyFriendAdapter(Context context, Activity mActivity, ArrayList<String> friendList) {
        this.friendList = friendList;
        this.context = context;
        this.mActivity = mActivity;
        mAdapter = this;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_friend,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        String friendEmail = friendList.get(position);
        //각각의 정보를 가져와야한다. user로

        Query query = db.collection("User").whereEqualTo("email",friendEmail);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                DocumentSnapshot doc = docs.get(0);
                final UserItem friendItem = doc.toObject(UserItem.class);

                holder.friend_nickname.setText(friendItem.getNickname());
                holder.friend_diaryName.setText(friendItem.getDiaryName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        visitFriend(friendItem.getEmail());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to retrieve info of friend");
            }
        });



    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView friend_nickname;
        TextView friend_diaryName;
        TextView last_update;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            friend_nickname = (TextView) itemView.findViewById(R.id.friend_nickname);
            friend_diaryName= (TextView) itemView.findViewById(R.id.friend_diaryName);
            last_update =  (TextView) itemView.findViewById(R.id.last_update);
        }
    }

    private void visitFriend(String friendEmail){
        Intent intent = new Intent(context,FriendMainActivity.class);
        intent.putExtra("email",friendEmail);
        mActivity.startActivity(intent);
    }
}
