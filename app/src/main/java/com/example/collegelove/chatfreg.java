package com.example.collegelove;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.CharSequenceTransformation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class chatfreg extends Fragment {
    private FirebaseAuth mAuth;
    private String cur;
    private DatabaseReference rootref,chatref,userdata;
    private View privatechatview;
    private RecyclerView chatlist;
    public chatfreg() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
      //  cur = mAuth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
       cur = mAuth.getCurrentUser().getUid();
        privatechatview = inflater.inflate(R.layout.fragment_chatfreg, container, false);
        chatref = FirebaseDatabase.getInstance().getReference().child("friend").child(cur);
        chatlist = (RecyclerView) privatechatview.findViewById(R.id.chatlist);
        chatlist.setLayoutManager(new LinearLayoutManager(getContext()));
        userdata = FirebaseDatabase.getInstance().getReference().child("users");

        return privatechatview;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser cur = mAuth.getCurrentUser();
        if (cur != null){
            updateuserstate("online");
            FirebaseRecyclerOptions<users> options = new FirebaseRecyclerOptions.Builder<users>()
                    .setQuery(chatref,users.class)
                    .build();
            FirebaseRecyclerAdapter<users, crushfreg.friviewholder> adapter = new FirebaseRecyclerAdapter<users, crushfreg.friviewholder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final crushfreg.friviewholder holder, int position, @NonNull users model) {

                  final String userid = getRef(position).getKey();
                    final String[] proimg = {"default"};
                    userdata.child(userid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("userstate").hasChild("state")) {

                                String state = dataSnapshot.child("userstate").child("state").getValue().toString();
                                String time = dataSnapshot.child("userstate").child("time").getValue().toString();
                                if (state.equals("online"))
                                {
                                    holder.state.setText("online");
                                }
                                else if (state.equals("offline"))
                                {
                                    holder.state.setText(time);
                                }
                            }
                            else
                            {
                                holder.state.setText("offline");
                            }

                             proimg[0] = dataSnapshot.child("image").getValue().toString();
                            final String pronam = dataSnapshot.child("name").getValue().toString();
                            String prosta = dataSnapshot.child("status").getValue().toString();

                            holder.usrname.setText(pronam);
                            holder.usrstat.setText(prosta);
                            Picasso.get().load(proimg[0]).placeholder(R.drawable.addpic).into(holder.usrimg);
                             holder.itemView.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     Intent chatintent = new Intent(getContext(),chat.class);
                                     chatintent.putExtra("userid", userid);
                                     chatintent.putExtra("username", pronam);
                                     chatintent.putExtra("userimg", proimg[0]);
                                     startActivity(chatintent);
                                 }
                             });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @NonNull
                @Override
                public crushfreg.friviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.userls, viewGroup, false);
                    crushfreg.friviewholder viewholder = new crushfreg.friviewholder(view);
                    return viewholder;


                }
            };
            chatlist.setAdapter(adapter);
            adapter.startListening();

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser cur = mAuth.getCurrentUser();
        if (cur != null){
            updateuserstate("online");}
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseUser cur = mAuth.getCurrentUser();
        if (cur != null){
            updateuserstate("offline");}

    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseUser cur = mAuth.getCurrentUser();
        if (cur != null){
            updateuserstate("offline");}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseUser cur = mAuth.getCurrentUser();
        if (cur != null){
            updateuserstate("offline");}
    }
    void updateuserstate(String state)
    {
        String sct,scd,cure;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat cd = new SimpleDateFormat("MM dd, yyyy");
        scd = cd.format(calendar.getTime());
        SimpleDateFormat ct = new SimpleDateFormat("hh:mm a");
        sct = ct.format(calendar.getTime());
        HashMap<String, Object> usrstate = new HashMap<>();
        usrstate.put("time",sct);
        usrstate.put("date",scd);
        usrstate.put("state",state);
        cure = mAuth.getCurrentUser().getUid();
        rootref.child("users").child(cure).child("userstate").updateChildren(usrstate);

    }
    public static class chatview extends RecyclerView.ViewHolder
    {
        TextView usrname,usrstat,state;
        CircleImageView usrimg;
        View view;
        public chatview(@NonNull View itemView) {
            super(itemView);
            usrname = itemView.findViewById(R.id.textView);
            usrstat = itemView.findViewById(R.id.textView4);
            usrimg = itemView.findViewById(R.id.pic);
            state = itemView.findViewById(R.id.textView5);
            view = itemView;
        }
    }

}
