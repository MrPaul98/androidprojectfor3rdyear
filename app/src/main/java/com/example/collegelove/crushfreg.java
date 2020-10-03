package com.example.collegelove;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class crushfreg extends Fragment {

    private RecyclerView frilist;
    private DatabaseReference fridata,userdata;
    private FirebaseAuth mAuth;
    private String curuid;

    private View mview;
    public crushfreg() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_crushfreg,container,false);
        frilist = (RecyclerView) mview.findViewById(R.id.crushlist);
        frilist.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
      curuid = mAuth.getCurrentUser().getUid();
      fridata = FirebaseDatabase.getInstance().getReference().child("friend").child(curuid);
       userdata = FirebaseDatabase.getInstance().getReference().child("users");

        // Inflate the layout for this fragment
        return mview;
    }

    @Override
    public void onStart() {
        super.onStart();


            FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<users>()
                    .setQuery(fridata, users.class)
                    .build();
            FirebaseRecyclerAdapter<users, friviewholder> adapter = new FirebaseRecyclerAdapter<users, friviewholder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final friviewholder holder, int position, @NonNull users model) {

                    String userid = getRef(position).getKey();
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

                               String proimg = dataSnapshot.child("image").getValue().toString();
                               String pronam = dataSnapshot.child("name").getValue().toString();
                               String prosta = dataSnapshot.child("status").getValue().toString();

                               holder.usrname.setText(pronam);
                               holder.usrstat.setText(prosta);
                               Picasso.get().load(proimg).placeholder(R.drawable.addpic).into(holder.usrimg);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @NonNull
                @Override
                public friviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.userls, viewGroup, false);
                    friviewholder viewholder = new friviewholder(view);
                    return viewholder;


                }
            };
            frilist.setAdapter(adapter);
            adapter.startListening();
        }


    public static class friviewholder extends RecyclerView.ViewHolder
    {
     TextView usrname,usrstat,state;
      CircleImageView usrimg;

         public friviewholder(@NonNull View itemView) {
            super(itemView);
            usrname = itemView.findViewById(R.id.textView);
            usrstat = itemView.findViewById(R.id.textView4);
            usrimg = itemView.findViewById(R.id.pic);
            state = itemView.findViewById(R.id.textView5);

      }

   }
}
