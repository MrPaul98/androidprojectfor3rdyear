package com.example.collegelove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class alluser extends AppCompatActivity {
    private Toolbar mtoolbar;
    private RecyclerView muserlist;

    private DatabaseReference mdatabase;
    private FirebaseAuth cur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alluser);

        mtoolbar = (Toolbar) findViewById(R.id.usrlist);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Find Crush");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cur = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("users");
        muserlist = (RecyclerView) findViewById(R.id.urlst);
        muserlist.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<users> options = new FirebaseRecyclerOptions.Builder<users>()
                .setQuery(mdatabase,users.class)
                .build();
        FirebaseRecyclerAdapter<users,usrholder> adapter =
                new FirebaseRecyclerAdapter<users, usrholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull usrholder holder, int position, @NonNull users model)
                    {
                        final String userid = getRef(position).getKey();
                        String cc = cur.getUid().toString();
                        holder.usrname.setText(model.getName());
                        holder.usrstat.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.addpic).into(holder.usrimg);
                        if (userid.equals(cc)){
                            //donothing
                            holder.viw.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(alluser.this,"You cannot send request to yourself",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        else {

                             holder.viw.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {

                                 Intent prointent = new Intent(alluser.this, viewothersprofile.class);
                                 prointent.putExtra("userid", userid);
                                 startActivity(prointent);


                             }
                         });
                     }
                     }





                    @NonNull
                    @Override
                    public usrholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.userls,viewGroup,false);
                        usrholder viewholder = new usrholder(view);
                        return viewholder;
                    }
                };
         muserlist.setAdapter(adapter);
         adapter.startListening();
    }
    public static class usrholder extends RecyclerView.ViewHolder
    {
        TextView usrname,usrstat;
        CircleImageView usrimg;
        View viw;
        public usrholder(@NonNull View itemView) {
            super(itemView);
            usrname = itemView.findViewById(R.id.textView);
            usrstat = itemView.findViewById(R.id.textView4);
            usrimg = itemView.findViewById(R.id.pic);
            viw = itemView;
        }
    }
}
