package com.androidcollider.vysotsky.objects;

/**
 * Created by Пархоменко on 12.01.2015.
 */
public class SongForUpdateRating {

    private int id;
    private long localRating;

    public SongForUpdateRating(int id, long localRating) {
        this.id = id;
        this.localRating = localRating;
    }

    public int getId() {
        return id;
    }

    public long getLocalRating() {
        return localRating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLocalRating(long localRating) {
        this.localRating = localRating;
    }
}
