package com.junga.dearyou;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean flag = true;


    TextView email;
    TextView id;

    private final UserItem fuckingUser = new UserItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        id = (TextView) findViewById(R.id.id);
        email = (TextView) findViewById(R.id.email);

        login();

    }





    private void login() {
        auth.signInWithEmailAndPassword("joyce@naver.com","Rr115500..")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final FirebaseUser user = auth.getCurrentUser();

                        CollectionReference userCol = db.collection("User");
                        Query getUserQuery = userCol.whereEqualTo("email","joyce@naver.com");
                        Log.d("dddd","complete1");
                        getUserQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    List<DocumentSnapshot> docs = querySnapshot.getDocuments();

                                    DocumentSnapshot document = docs.get(0);
                                    UserItem userItem = document.toObject(UserItem.class);
                                    Log.d("dddd","complete2");
                                    Log.d("dddd",userItem.getEmail());
                                    Log.d("dddd",userItem.getDiaryName());
                                    dummy(userItem);

                                }
                            }
                        });

                    }
                });
    }

    private void dummy(UserItem itme) {


       Statics.setUser(itme);
        id.setText(Statics.getUser().getUserId());
        email.setText(Statics.getUser().getEmail());

    }
}
