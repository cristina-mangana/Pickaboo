package com.example.android.pickaboo;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    /** URL for last releases data from the Google Books API */
    private static final String LAST_RELEASES_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=subject:fiction&maxResults=10&orderBy=newest";

    /** Adapter for the list of books */
    private ReleasesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Progress bar color
        // https://stackoverflow.com/questions/26962136/indeterminate-circle-progress-bar-on-android-is-white-despite-coloraccent-color
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        if (progressBar.getIndeterminateDrawable() != null) {
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        // RecyclerView settings
        // Find a reference to the {@link RecyclerView} in the layout
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.releases_recycler_view);
        // Use a horizontal linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Create a new {@link ReleasesAdapter} that takes an empty list of books as input
        final List<Book> mBooksList = new ArrayList<>();
        // Specify an adapter
        mAdapter = new ReleasesAdapter(this, mBooksList, new ReleasesAdapter.MyAdapterListener() {
            @Override
            public void OnClick(View v, int position) {
                Intent openActivityDetail = new Intent(getApplicationContext(),
                        BookDetailActivity.class);
                openActivityDetail.putExtra("book", mBooksList.get(position));
                startActivity(openActivityDetail);
            }
        });
        // Set the adapter on the {@link RecyclerView} so the list can be populated in the UI
        mRecyclerView.setAdapter(mAdapter);

        // Check for network connectivity
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(0, null, this);
        } else {
            // Hide the loading indicator
            progressBar.setVisibility(View.GONE);
            // Show the Error layout
            finish();
            Intent openActivityNoConnection = new Intent(getApplicationContext(),
                    NoConnectionActivity.class);
            startActivity(openActivityNoConnection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL of last releases
        Uri baseUri = Uri.parse(LAST_RELEASES_REQUEST_URL);
        return new BookLoader(this, baseUri.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Hide the loading indicator
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);
        // Hide empty state text
        TextView emptyTextView = (TextView) findViewById(R.id.empty_text);
        emptyTextView.setVisibility(View.GONE);
        // Clear the adapter of previous data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the RecyclerView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        } else {
            // Set empty state text
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_results));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    //This method is called when the search button is clicked.
    public void search(View view) {
        EditText editText = (EditText) findViewById(R.id.search_query);
        String query = editText.getText().toString();
        if (!TextUtils.isEmpty(query)) {
            Intent openActivitySearch = new Intent(this, BookSearchActivity.class);
            openActivitySearch.putExtra("query", query);
            startActivity(openActivitySearch);
        } else {
            // Show an error message as a toast
            Toast.makeText(this, getString(R.string.query_error), Toast.LENGTH_SHORT).show();
        }
    }
}
