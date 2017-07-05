package com.example.android.pickaboo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cristina on 03/07/2017.
 * This class represents a single book. Each object has information about the book, such as title,
 * subtitle, author, year of release, short description, rating and image link.
 */

public class Book implements Parcelable {

    private String mTitle, mAuthor, mYear, mDescription, mImageLink, mBookUrl;
    private double mRating;

    /**
     * Create a new {@link Book} object
     * @param title is the book title
     * @param author is the author name
     * @param year is the year of release
     * @param description is a short description of the book
     * @param imageLink is the url to download the book cover image
     * @param bookUrl is the website URL to find more details about the earthquake
     * @param rating is the average rating of the book (1.0 to 5.0)
     */
    public Book(String title, String author, String year, String description,
                String imageLink, String bookUrl, double rating) {
        mTitle = title;
        mAuthor = author;
        mYear = year;
        mDescription = description;
        mImageLink = imageLink;
        mBookUrl = bookUrl;
        mRating = rating;
    }

    /**
     * Get the book title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the author name
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Get the year of release
     */
    public String getYear() {
        return mYear;
    }

    /**
     * Get a short description of the book
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Get the url to download the book cover image
     */
    public String getImageLink() {
        return mImageLink;
    }

    /**
     * Get the book url to find more information
     */
    public String getBookUrl() {
        return mBookUrl;
    }

    /**
     * Get the average rating of the book (1.0 to 5.0)
     */
    public double getRating() {
        return mRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mAuthor);
        dest.writeString(mYear);
        dest.writeString(mDescription);
        dest.writeString(mImageLink);
        dest.writeString(mBookUrl);
        dest.writeDouble(mRating);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    // De-parcel object
    public Book(Parcel in) {
        mTitle = in.readString();
        mAuthor = in.readString();
        mYear = in.readString();
        mDescription = in.readString();
        mImageLink = in.readString();
        mBookUrl = in.readString();
        mRating = in.readDouble();
    }
}
