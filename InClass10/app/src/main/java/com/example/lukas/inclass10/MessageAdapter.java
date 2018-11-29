package com.example.lukas.inclass10;

import android.content.Context;
import android.icu.util.TimeZone;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ocpsoft.prettytime.PrettyTime;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    Context context;
    ViewHolder viewHolder = null;
    int resource = 0;
    private FirebaseDatabase database;

    public MessageAdapter(Context context, int resource, List<Message> objects){
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        final Message message = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(this.resource, parent, false);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.textMessage = convertView.findViewById(R.id.textViewMessageMessage);
            viewHolder.textName = convertView.findViewById(R.id.textViewMessageName);
            viewHolder.textTimeStamp = convertView.findViewById(R.id.textViewMessageTimeStamp);
            viewHolder.imageButtonDelete = convertView.findViewById(R.id.imageButtonMessageDelete);
            viewHolder.imageViewImage = convertView.findViewById(R.id.imageViewMessageImage);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textMessage.setText(message.message);
        viewHolder.textName.setText(message.sender);
        String url = message.imageURL;

        Log.d("demo", "MessageAdapter " + url);

        if(url.length() > 0) {
            Picasso.get().load(url).into(viewHolder.imageViewImage);
        } else {
            viewHolder.imageViewImage.setVisibility(View.INVISIBLE);
        }

        String time = "";
        Date date = null;
        PrettyTime p = new PrettyTime();
        android.icu.text.SimpleDateFormat formatter = new android.icu.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("EST"));
        time = message.timeStamp;

        try {
            date = formatter.parse(time);
            Log.d("demo", date.toString());
        } catch (ParseException e) {
            Log.d("demo", "Pretty Time Conversion Error" + e.toString());
        }
        time = p.format(date).toString();

        if (time.equals("moments from now")) {
            time = "moments ago";
        }
        Log.d("demo", "Time String: " + time);
        viewHolder.textTimeStamp.setText(time);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (message.userID.equals(user.getUid())) {
            viewHolder.imageButtonDelete.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageButtonDelete.setVisibility(View.INVISIBLE);
        }

        viewHolder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference deleteRef = database.getReference("Messages");
                deleteRef.child(message.key).removeValue();
                //Toast.makeText(context, "Pressed", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    private class ViewHolder{
        TextView textName;
        TextView textMessage;
        TextView textTimeStamp;
        ImageView imageViewImage;
        ImageButton imageButtonDelete;
    }
}
