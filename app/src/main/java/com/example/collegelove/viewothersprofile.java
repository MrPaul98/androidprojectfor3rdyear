package com.example.collegelove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class viewothersprofile extends AppCompatActivity {
    private TextView t1,t2,t3,t4;
    private CircleImageView cvm;
    private Toolbar tlb;

    private DatabaseReference mdb;
    //friend state
    private Button f1,c2;
    private String cfrndstate;
    private DatabaseReference frindrequestdatabase;
    private FirebaseUser curuser;
    private DatabaseReference mfrienddb,notificationref;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewothersprofile);
        tlb = (Toolbar) findViewById(R.id.prolb);
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("Make Friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        final String uid = getIntent().getStringExtra("userid");
        t1 = (TextView) findViewById(R.id.othprobio);
        t2 = (TextView) findViewById(R.id.othprost);
        t3 = (TextView) findViewById(R.id.othpronm);
        t4 = (TextView) findViewById(R.id.othprocld);
        cvm = (CircleImageView) findViewById(R.id.othprocrvm);
        //friendstate
        f1 = (Button) findViewById(R.id.fri);
        c2 = (Button)  findViewById(R.id.cru);
        cfrndstate = "not_friend";
        c2.setVisibility(View.INVISIBLE);
        c2.setEnabled(false);
        mdb = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        frindrequestdatabase = FirebaseDatabase.getInstance().getReference().child("friendrequest");
        notificationref = FirebaseDatabase.getInstance().getReference().child("notification");
        curuser = FirebaseAuth.getInstance().getCurrentUser();
        String c = curuser.getUid().toString();
        //friend database
        mfrienddb = FirebaseDatabase.getInstance().getReference().child("friend");


        mdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                 String img = dataSnapshot.child("image").getValue().toString();
                 String name = dataSnapshot.child("name").getValue().toString();
                 String ide = dataSnapshot.child("regstration").getValue().toString();
                 String status = dataSnapshot.child("status").getValue().toString();
                 String bio = dataSnapshot.child("Bio").getValue().toString();
                 t1.setText(bio);
                 t2.setText(status);
                 t3.setText(name);
                 t4.setText(ide);
                 Picasso.get().load(img).placeholder(R.drawable.addpic).into(cvm);


                //-----frindlist
                frindrequestdatabase.child(curuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(uid)){

                            String req_type = dataSnapshot.child(uid).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                cfrndstate = "req_received";
                                f1.setText("Accept Request");

                                c2.setVisibility(View.VISIBLE);
                                c2.setEnabled(true);


                            } else if(req_type.equals("sent")) {

                                cfrndstate = "req_sent";
                                f1.setText("Cancel Request");

                                c2.setVisibility(View.INVISIBLE);
                                c2.setEnabled(false);

                            }




                        } else {


                            mfrienddb.child(curuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(uid)){

                                        cfrndstate = "friends";
                                        f1.setText("don't like remove");

                                        c2.setVisibility(View.INVISIBLE);
                                        c2.setEnabled(false);

                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {


                                }
                            });

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-----frind state---
                f1.setEnabled(false);
               f1.setText("sending....");
                if (curuser.equals(uid))
                {
                    f1.setVisibility(View.INVISIBLE);
                    c2.setVisibility(View.INVISIBLE);
                }

                if(cfrndstate.equals("not_friend")){

                    Map requestMap = new HashMap();
                    requestMap.put("friendrequest/" + curuser.getUid() + "/" + uid + "/request_type", "sent");
                    requestMap.put("friendrequest/" + uid + "/" + curuser.getUid() + "/request_type", "received");


                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(viewothersprofile.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            }
                            else
                                {
                                    HashMap<String,String> friendnoti = new HashMap<>();
                                    friendnoti.put("from",curuser.getUid());
                                    friendnoti.put("type","request");
                                    notificationref.child(uid).push().setValue(friendnoti).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                          if (task.isSuccessful())
                                          {
                                              cfrndstate = "req_sent";
                                              f1.setText("Cancel Request");
                                          }
                                        }
                                    });



                            }

                            f1.setEnabled(true);


                        }
                    });

                }
                //-----cancel friend----
                if(cfrndstate.equals("req_sent"))
                {
                    frindrequestdatabase.child(curuser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            frindrequestdatabase.child(uid).child(curuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   f1.setEnabled(true);
                                   cfrndstate = "not_friend";
                                   f1.setText("crush Request");
                                    c2.setVisibility(View.INVISIBLE);
                                    c2.setEnabled(false);
                                }
                            });
                        }
                    });
                }
                //------Req recived ------

                if(cfrndstate.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());


                    Map friendsMap = new HashMap();
                    friendsMap.put("friend/" + curuser.getUid() + "/" + uid + "/date", currentDate);
                    friendsMap.put("friend/" + uid + "/"  + curuser.getUid() + "/date", currentDate);


                    friendsMap.put("friendrequest/" + curuser.getUid() + "/" + uid, null);
                    friendsMap.put("friendrequest/" + uid + "/" + curuser.getUid(), null);


                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                f1.setEnabled(true);
                                cfrndstate = "friends";
                                f1.setText("don't like remove");

                                c2.setVisibility(View.INVISIBLE);
                                c2.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(viewothersprofile.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                }
                ///---unf
                if(cfrndstate.equals("friends")){

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("friend/" + curuser.getUid() + "/" + uid, null);
                    unfriendMap.put("friend/" + uid + "/" + curuser.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                cfrndstate = "not_friend";
                                f1.setText("crush Request");

                                c2.setVisibility(View.INVISIBLE);
                                c2.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(viewothersprofile.this, error, Toast.LENGTH_SHORT).show();


                            }

                            f1.setEnabled(true);

                        }
                    });

                }



            }
        });


    }


}