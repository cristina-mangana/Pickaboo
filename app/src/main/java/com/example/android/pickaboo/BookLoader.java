package com.example.android.pickaboo;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Cristina on 03/07/2017.
 * This class loads a list of books by using an AsyncTask to perform the network request to
 * the given URL.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the HTTP request for book data and process the response.
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}
