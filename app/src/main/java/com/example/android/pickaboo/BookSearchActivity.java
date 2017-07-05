package com.example.android.pickaboo;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookSearchActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    /** URL for last releases data from the Google Books API */
    private static final String BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes";

    /** User's query */
    String query;

    /** Adapter for the list of books */
    private BookAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        handleIntent(getIntent());

        // Toolbar settings
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // No title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Title text settings
        // Find the TextView
        TextView textViewResults = (TextView) findViewById(R.id.results_text);
        // Find and build the string
        String stringResults = getString(R.string.results, query);
        // Find the position of the " character
        int queryFirstPosition = stringResults.indexOf("\"");
        int queryLastPosition = stringResults.lastIndexOf("\"");
        // Different text style for query
        SpannableString spannableResultsTitle = new SpannableString(stringResults);
        spannableResultsTitle
                .setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                        queryFirstPosition, queryLastPosition + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Set custom text
        textViewResults.setText(spannableResultsTitle);

        // Progress bar color
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        if (progressBar.getIndeterminateDrawable() != null) {
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(this, R.color.colorAccent),
                            android.graphics.PorterDuff.Mode.SRC_IN);
        }

        // Find a reference to the {@link ListView} in the layout
        ListView booksListView = (ListView) findViewById(R.id.list);

        // Find a reference to the TextView in the layout
        mEmptyTextView = (TextView) findViewById(R.id.empty_text);
        // Set an empty view
        booksListView.setEmptyView(mEmptyTextView);

        // Create a new {@link BookAdapter} that takes an empty list of books as input
        final List<Book> mBooksList = new ArrayList<>();
        mAdapter = new BookAdapter(this, mBooksList);

        // Set the adapter on the {@link ListView} so the list can be populated in the UI
        booksListView.setAdapter(mAdapter);

        // Set a click listener to open activity detail when the list item is clicked on
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openActivityDetail = new Intent(getApplicationContext(),
                        BookDetailActivity.class);
                openActivityDetail.putExtra("book", mBooksList.get(position));
                startActivity(openActivityDetail);
            }
        });

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

    // Get the intent from the search action in the toolbar
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    // Helper method to handle the search intent
    // https://developer.android.com/guide/topics/search/search-dialog.html#SearchableConfiguration
    private void handleIntent(Intent intent) {
        // If query comes from the toolbar search widget, refresh activity
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            finish();
            overridePendingTransition(0, 0);
            Intent openActivitySearch = new Intent(this, BookSearchActivity.class);
            openActivitySearch.putExtra("query", query);
            openActivitySearch.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(openActivitySearch);
        } else {
            // If query comes from MainActivity
            query = getIntent().getExtras().getString("query");
        }
    }

    // Handle back button on toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_toolbar_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    // Handle clicks on toolbar menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        String queryForUrl = query;
        // Replace spaces with + character in query, to be used as URL to fetch data
        if (query.contains(" ")) {
            queryForUrl = query.replace(" ", "+");
        }

        // Create a new loader for the given URL appending the user query
        Uri baseUri = Uri.parse(BOOKS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", queryForUrl);

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Hide the loading indicator
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);

        // Set empty state text to display "No results found."
        mEmptyTextView.setText(getString(R.string.no_results));

        // Clear the adapter of previous data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
            mEmptyTextView.setText("");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
