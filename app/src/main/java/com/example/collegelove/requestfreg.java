package com.example.collegelove;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class requestfreg extends Fragment {

    private View reqcr;
    private RecyclerView pubreqlist;


    private DatabaseReference crushreq,urref,reqlst;
    private FirebaseAuth mAth;
    private String usr;

    public requestfreg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        reqcr = inflater.inflate(R.layout.fragment_requestfreg, container, false);
        mAth=FirebaseAuth.getInstance();
        usr = mAth.getCurrentUser().getUid();
        urref = FirebaseDatabase.getInstance().getReference().child("users");
        crushreq = FirebaseDatabase.getInstance().getReference().child("friendrequest");
        reqlst = FirebaseDatabase.getInstance().getReference().child("friend");

        pubreqlist = (RecyclerView) reqcr.findViewById(R.id.reqlist);
        pubreqlist.setLayoutManager(new LinearLayoutManager(getContext()));
        return reqcr;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<users> options = new FirebaseRecyclerOptions.Builder<users>()
                .setQuery(crushreq.child(usr),users.class)
                .build();
        final FirebaseRecyclerAdapter<users,reqviewholder> adapter = new FirebaseRecyclerAdapter<users, reqviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final reqviewholder holder, int position, @NonNull users model) {
                final String listuserid = getRef(position).getKey();
                DatabaseReference gettyperef = getRef(position).child("request_type").getRef();
                gettyperef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            String type = dataSnapshot.getValue().toString();
                            if (type.equals("received"))
                            {
                                urref.child(listuserid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String proimg = dataSnapshot.child("image").getValue().toString();
                                        String pronam = dataSnapshot.child("name").getValue().toString();
                                        String prosta = dataSnapshot.child("status").getValue().toString();
                                         holder.usrname.setText(pronam);
                                         holder.usrstat.setText(prosta);
                                         Picasso.get().load(proimg).placeholder(R.drawable.addpic).into(holder.usrimg);
                                         holder.bt1.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 accept(listuserid);
                                             }
                                         });
                                         holder.bt2.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 cancel(listuserid);
                                             }
                                         });
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else if (type.equals("sent"))
                            {
                                Button reqsentnut = holder.itemView.findViewById(R.id.accbut);
                                reqsentnut.setText("request Cancel");


                                holder.itemView.findViewById(R.id.canbut).setVisibility(View.INVISIBLE);
                                urref.child(listuserid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String proimg = dataSnapshot.child("image").getValue().toString();
                                        String pronam = dataSnapshot.child("name").getValue().toString();
                                        String prosta = dataSnapshot.child("status").getValue().toString();
                                        holder.usrname.setText(pronam);
                                        holder.usrstat.setText(prosta);
                                        Picasso.get().load(proimg).placeholder(R.drawable.addpic).into(holder.usrimg);
                                        holder.bt1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                cancel(listuserid);
                                            }
                                        });

                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public reqviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reqlst,viewGroup,false);
                reqviewholder viewholder = new reqviewholder(view);
                return viewholder;
            }
        };
        pubreqlist.setAdapter(adapter);
        adapter.startListening();

    }
    public void accept(final String listusrid) {
        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        reqlst.child(usr).child(listusrid).child("date").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              if (task.isSuccessful())
              {
                  final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                  reqlst.child(listusrid).child(usr).child("date").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful())
                          {
                              crushreq.child(usr).child(listusrid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      if (task.isSuccessful())
                                      {
                                          crushreq.child(listusrid).child(usr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if (task.isSuccessful())
                                                  {
                                                      //kevebkvb

                                                  }
                                              }
                                          });

                                      }
                                  }
                              });

                          }
                      }
                  });

              }
            }
        });

    }
    public void cancel(final String listusrid) {
        crushreq.child(usr).child(listusrid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    crushreq.child(listusrid).child(usr).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                //kevebkvb

                            }
                        }
                    });

                }
            }
        });

    }
    public static class reqviewholder extends RecyclerView.ViewHolder
    {  TextView usrname,usrstat;
        CircleImageView usrimg;
        Button bt1,bt2;
        public reqviewholder(@NonNull View itemView) {
            super(itemView);
            usrname = itemView.findViewById(R.id.textView);
            usrstat = itemView.findViewById(R.id.textView4);
            usrimg = itemView.findViewById(R.id.pic);
            bt1 = itemView.findViewById(R.id.accbut);
            bt2 = itemView.findViewById(R.id.canbut);

        }
    }
}
