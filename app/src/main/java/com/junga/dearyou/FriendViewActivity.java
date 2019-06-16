package com.junga.dearyou;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.junga.dearyou.lib.FontLib;

import java.util.List;

public class FriendViewActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    String diaryId;
    TextView title;
    TextView content;
    TextView back;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FontLib fontLib = new FontLib();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_view);

        //get Diary Id.

        diaryId = getIntent().getStringExtra("diaryId");

        title = (TextView) findViewById(R.id.textView_title);
        content = (TextView) findViewById(R.id.textView_content);
        back = (TextView) findViewById(R.id.textView_back);

        back.setOnClickListener(this);

        fontLib.setFont(this, "oleo_script_bold", title);
        fontLib.setFont(this, "inconsolata", content);
        fontLib.setFont(this, "inconsolata", back);

        setDiary();
    }


    private void setDiary() {

        CollectionReference col = db.collection("Diary");
        Query getDiary = col.whereEqualTo("diaryId", diaryId);

        getDiary.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot q = task.getResult();
                if (task.getResult() != null) {
                    List<DocumentSnapshot> docs = q.getDocuments();
                    DocumentSnapshot doc = docs.get(0);
                    DiaryItem item = doc.toObject(DiaryItem.class);

                    title.setText(item.getTitle());
                    content.setText(item.getContent());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Something went wrong");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView_back) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
