package com.example.lukas.inclass10;

import android.graphics.Bitmap;

public class Message {
    String message;
    String sender;
    String timeStamp;
    String imageURL = "";
    //Bitmap image;
    String userID;
    String key;

    public Message(){

    }

    public Message(String message, String sender, String timeStamp, String imageURL, String userID, String key) {
        this.message = message;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.imageURL = imageURL;
        this.userID = userID;
        this.key = key;
    }

}
