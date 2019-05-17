package com.ctse.androidgamereviewer.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room performs Object Relational Mapping and the @Entity annotation ensures that
 * the Review entity class is mapped to the relevant SQLite table in the database.
 *
 * @see Entity
 * <a href="https://developer.android.com/topic/libraries/architecture/room">
 * Official Room Documentation</a>
 */
@Entity(indices = {@Index(value = {"remote_id"}, unique = true)})
public class Review {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "remote_id")
    private String _id;
    private String gameId;
    private String title;
    private String body;
    private String date;
    private int rating;

    public Review() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
