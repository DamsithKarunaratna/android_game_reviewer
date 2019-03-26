package com.ctse.gamereveiwer.models;

public class Review
{
    private String date;

    private int rating;

    private String _id;

    private String body;

    private String title;

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public int getRating ()
    {
        return rating;
    }

    public void setRating (int rating)
    {
        this.rating = rating;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getBody ()
    {
        return body;
    }

    public void setBody (String body)
    {
        this.body = body;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    @Override
    public String toString()
    {
        return "Class [date = "+date+", rating = "+rating+", _id = "+_id+", body = "+body+", title = "+title+"]";
    }
}
