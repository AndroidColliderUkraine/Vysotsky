package com.androidcollider.vysotsky.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Пархоменко on 18.01.2015.
 */
public class Comment implements Parcelable{

    private int idComment;
    private int idSong;
    private String userName;
    private String text;
    private String datePosted;

    public Comment(int idComment, int idSong, String userName, String text, String datePosted) {
        this.idComment = idComment;
        this.idSong = idSong;
        this.userName = userName;
        this.text = text;
        this.datePosted = datePosted;
    }

    public int getIdComment() {
        return idComment;
    }

    public int getIdSong() {
        return idSong;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public String getDatePosted() {
        return datePosted;
    }


    public void setIdComment(int idComment) {
        this.idComment = idComment;
    }

    public void setIdSong(int idSong) {
        this.idSong = idSong;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }


    public Comment(Parcel in){
        this.idComment = in.readInt();
        this.idSong = in.readInt();
        this.userName = in.readString();
        this.text = in.readString();
        this.datePosted = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idComment);
        dest.writeInt(this.idSong);
        dest.writeString(this.userName);
        dest.writeString(this.text);
        dest.writeString(this.datePosted);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public String toString() {
        return  " idComment="+idComment+"  "+
                " idSong="+idSong+"  "+
                " username="+userName+"  "+
                " text="+text+"  "+
                " datePosted=" +datePosted ;
    }
}
