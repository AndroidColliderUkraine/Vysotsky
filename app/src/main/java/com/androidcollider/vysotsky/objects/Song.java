package com.androidcollider.vysotsky.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by pseverin on 24.12.14.
 */
public class Song implements Parcelable{

    public static long current_min_rating;
    public static long current_max_rating;

    private int id;
    private String name;
    private String text;
    private String chord;
    private int year;
    private String about;
    private String videoLink;
    private long rating;
    private ArrayList<Comment> comments;
    private boolean isFavorite;

    public Song(int id, String name, String text, String chord, int year,
                String about, String videoLink, long rating, ArrayList<Comment> comments,
                boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.chord = chord;
        this.year = year;
        this.about = about;
        this.videoLink = videoLink;
        this.rating = rating;
        this.comments= comments;
        this.isFavorite = isFavorite;

    }

    public Song(int id, String name, long rating, int year, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.year = year;
        this.isFavorite = isFavorite;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getChord() {
        return chord;
    }

    public int getYear() {
        return year;
    }

    public String getAbout() {
        return about;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public long getRating() {
        return rating;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }




    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setChord(String chord) {
        this.chord = chord;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Song(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.text = in.readString();
        this.chord = in.readString();
        this.year = in.readInt();
        this.about = in.readString();
        this.videoLink = in.readString();
        this.rating = in.readLong();
        if (this.comments!=null){
            Comment[] commentsArray = (Comment[])in.readParcelableArray(null);
            for (int i=0; i<commentsArray.length;i++){
                this.comments.add(commentsArray[i]);
            }
        }
        this.isFavorite = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.text);
        dest.writeString(this.chord);
        dest.writeInt(this.year);
        dest.writeString(this.about);
        dest.writeString(this.videoLink);
        dest.writeLong(this.rating);
        if (this.comments!=null){
            Comment[]commentsArray = this.comments.toArray(new Comment[comments.size()]);
            dest.writeParcelableArray(commentsArray,0);
        }
        dest.writeByte((byte) (this.isFavorite ? 1 : 0));
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public String toString() {
        String comm="";
        if (comments!=null){
            comm=comments.toString();
        }
        return " id="+id+"  "+
               " name="+name+"  "+
               " text="+text+"  "+
               " chord="+text+"  "+
               " year="+rating+"  "+
               " about="+about+"  "+
               " videoLink="+videoLink+"  "+
               " rating="+rating+"  "+
               "text="+text+"  "+
               "comments="+comm +
               "isFavorite=" +isFavorite ;
    }
}
