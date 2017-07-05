package com.example.android.pickaboo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        final Book book = getIntent().getExtras().getParcelable("book");

        // Toolbar settings
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // No title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set cover image
        // Find the ImageView
        ImageView bookCoverImage = (ImageView) findViewById(R.id.book_cover_image);
        // Get the imageLink to download the book cover image
        String imageLink = book.getImageLink();
        // Download the image and attach it to the ImageView. If imageLink is null, no cover art is
        // assign to that book or is not recognized as such. Then, a default cover is assigned.
        if (imageLink != null && imageLink.length() > 0) {
            Picasso.with(this).load(imageLink).into(bookCoverImage);
        } else {
            Picasso.with(this).load(R.drawable.default_cover).into(bookCoverImage);
        }

        // Set book title
        // Find the TextView
        TextView titleTextView = (TextView) findViewById(R.id.title);
        // Set text
        titleTextView.setText(book.getTitle());

        // Set author
        // Find the TextView
        TextView authorTextView = (TextView) findViewById(R.id.author);
        // Set text if author exists, else set visibility as gone
        if (!TextUtils.isEmpty(book.getAuthor())) {
            authorTextView.setText(getString(R.string.by_author, book.getAuthor()));
        } else {
            authorTextView.setVisibility(View.GONE);
        }

        // Set year of release
        // Find the TextView
        TextView yearTextView = (TextView) findViewById(R.id.year);
        // Set text if year exists, else set visibility as gone
        if (!TextUtils.isEmpty(book.getYear())) {
            yearTextView.setText(book.getYear());
        } else {
            yearTextView.setVisibility(View.GONE);
        }

        // If year and author exist add a separator
        TextView separatorTextView = (TextView) findViewById(R.id.separator);
        if (!TextUtils.isEmpty(book.getYear()) && !TextUtils.isEmpty(book.getAuthor())) {
            separatorTextView.setVisibility(View.VISIBLE);
        }

        // Set rating
        // Find the RatingBar
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        // Set rating if it is not 0, else set visibility as gone
        if (book.getRating() != 0.0) {
            ratingBar.setRating((float) book.getRating());
        } else {
            ratingBar.setVisibility(View.GONE);
        }

        // Set description
        // Find the TextView
        TextView descriptionTextView = (TextView) findViewById(R.id.description);
        // Set text if year exists, else set no_info string
        if (!TextUtils.isEmpty(book.getDescription())) {
            descriptionTextView.setText(Html.fromHtml(book.getDescription()));
        } else {
            descriptionTextView.setText(getString(R.string.no_info));
        }

        // Set click listener on "view more" button
        // Find the Button
        Button viewMoreButton = (Button) findViewById(R.id.view_more_button);
        // Set OnClickListener
        viewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If bookUrl exists, open it in the browser, else open the default google books
                // webPage
                Uri webPage;
                if (!TextUtils.isEmpty(book.getBookUrl())) {
                    webPage = Uri.parse(book.getBookUrl());
                } else {
                    webPage = Uri.parse("http://books.google.es/");
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    // Handle back button on toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
