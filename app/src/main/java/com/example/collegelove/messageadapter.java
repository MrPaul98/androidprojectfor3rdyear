package com.example.collegelove;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;



import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageadapter extends RecyclerView.Adapter<messageadapter.Messageviewholder> {
    private List<Messages> usermessagelist ;
    private FirebaseAuth mAth;
    private DatabaseReference userref;
    public messageadapter(List<Messages> usermessagelist)
    {
         this.usermessagelist = usermessagelist;
    }

    public class Messageviewholder extends RecyclerView.ViewHolder
    {
        public TextView sendermessagetext,recivermessagetext;
        public CircleImageView recivermessageimage;
        public ImageView messagesenderpicture,messagereciverpicture;

        public Messageviewholder(@NonNull View itemView) {
            super(itemView);
            sendermessagetext = (TextView) itemView.findViewById(R.id.senderid);
            recivermessagetext = (TextView) itemView.findViewById(R.id.reciverid);
            recivermessageimage = (CircleImageView) itemView.findViewById(R.id.proimgmseeage);
            messagesenderpicture = (ImageView) itemView.findViewById(R.id.senderimg);
            messagereciverpicture = (ImageView) itemView.findViewById(R.id.reciverimg);
        }
    }


    @NonNull
    @Override
    public Messageviewholder onCreateViewHolder(@NonNull ViewGroup viewgroup, int i) {
        View view = LayoutInflater.from(viewgroup.getContext())
                .inflate(R.layout.customlayoutformessage,viewgroup,false);
       mAth = FirebaseAuth.getInstance();
        return new Messageviewholder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final Messageviewholder messageviewholder, int i)
    {
        String messagesenderid = mAth.getCurrentUser().getUid();
        Messages messages = usermessagelist.get(i);
        String fromUserid = messages.getFrom();
        String frommessagetype = messages.getType();
        userref = FirebaseDatabase.getInstance().getReference().child("users").child(fromUserid);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image"))
                {
                    String revimg= dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(revimg).placeholder(R.drawable.addpic).into(messageviewholder.recivermessageimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messageviewholder.recivermessagetext.setVisibility(View.GONE);
        messageviewholder.recivermessageimage.setVisibility(View.GONE);
        messageviewholder.sendermessagetext.setVisibility(View.GONE);
        messageviewholder.messagesenderpicture.setVisibility(View.GONE);
        messageviewholder.messagereciverpicture.setVisibility(View.GONE);


        if (frommessagetype.equals("text"))
        {

            if (fromUserid.equals(messagesenderid))
            {
                messageviewholder.sendermessagetext.setVisibility(View.VISIBLE);
                messageviewholder.sendermessagetext.setBackgroundResource(R.drawable.sendermessagelauyout);
                messageviewholder.sendermessagetext.setText(messages.getMessage() + "\n \n" + messages.getTime() + "-" + messages.getDate());
            }
            else
            {


                messageviewholder.recivermessageimage.setVisibility(View.VISIBLE);
                messageviewholder.recivermessagetext.setVisibility(View.VISIBLE);

                messageviewholder.recivermessagetext.setBackgroundResource(R.drawable.recivermessagelayout);
                messageviewholder.recivermessagetext.setText(messages.getMessage() + "\n \n" + messages.getTime() + "-" + messages.getDate());
            }
        }
        else if (frommessagetype.equals("image"))
        {
            if (fromUserid.equals(messagesenderid))
            {
                messageviewholder.messagesenderpicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageviewholder.messagesenderpicture);
            }
            else
            {
                messageviewholder.messagereciverpicture.setVisibility(View.VISIBLE);
                messageviewholder.recivermessageimage.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageviewholder.messagereciverpicture);

            }
        }

    }



    @Override
    public int getItemCount() {
        return usermessagelist.size();
    }



}
