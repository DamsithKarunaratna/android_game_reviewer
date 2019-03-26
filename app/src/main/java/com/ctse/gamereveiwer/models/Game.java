package com.ctse.gamereveiwer.models;

public class Game
{
    private String image;

    private Review[] reviews;

    private String release_date;

    private String genre;

    private String _id;

    private String title;

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public Review[] getReviews()
    {
        return reviews;
    }

    public void setReviews(Review[] reviews)
    {
        this.reviews = reviews;
    }

    public String getRelease_date ()
    {
        return release_date;
    }

    public void setRelease_date (String release_date)
    {
        this.release_date = release_date;
    }

    public String getGenre ()
    {
        return genre;
    }

    public void setGenre (String genre)
    {
        this.genre = genre;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
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
        return "Class [image = "+image+", reviews = "+ reviews +", release_date = "+release_date+", genre = "+genre+", _id = "+_id+", title = "+title+"]";
    }
}
