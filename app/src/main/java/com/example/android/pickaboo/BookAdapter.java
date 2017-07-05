package com.example.android.pickaboo;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Cristina on 04/07/2017.
 * {@link BookAdapter} is an {@link ArrayAdapter} that can provide the layout for each list
 * based on a data source, which is a list of {@link Book} objects.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Custom constructor.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param books   A List of Book objects to display in a list
     */
    public BookAdapter(Activity context, List<Book> books) {
        super(context, 0, books);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        BookViewHolder holder; // to reference the child views for later actions
        // Check if the existing view is being reused, otherwise inflate the view
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item,
                    parent, false);

            // cache view fields into the holder
            holder = new BookViewHolder(listItemView);
            // associate the holder with the view for later lookup
            listItemView.setTag(holder);
        } else {
            // view already exists, get the holder instance from the view
            holder = (BookViewHolder) listItemView.getTag();
        }

        // Get the {@link Book} object located at this position in the list
        Book currentBook = getItem(position);

        // Get the imageLink to download the book cover image
        String imageLink = currentBook.getImageLink();
        // Download the image and attach it to the ImageView. If imageLink is null, no cover art is
        // assign to that book or is not recognized as such. Then, a default cover is assigned.
        if (imageLink != null && imageLink.length() > 0) {
            Picasso.with(getContext()).load(imageLink).into(holder.imageViewBook);
        } else {
            Picasso.with(getContext()).load(R.drawable.default_cover).into(holder.imageViewBook);
        }

        // Get the book title and set as text for the Textview
        holder.textViewTitle.setText(currentBook.getTitle());

        // Get the author and set as text for the Textview
        if (!TextUtils.isEmpty(currentBook.getAuthor())) {
            holder.textViewAuthor.setText(getContext().getString(R.string.by_author, currentBook.getAuthor()));
        } else {
            holder.textViewAuthor.setText("");
        }

        // Return the whole list item layout so that it can be shown in the ListView
        return listItemView;
    }

    /**
     * Provide a reference to the views for each data item
     */
    public static class BookViewHolder {

        ImageView imageViewBook;
        TextView textViewTitle;
        TextView textViewAuthor;

        public BookViewHolder(View itemView) {

            imageViewBook = (ImageView) itemView.findViewById(R.id.book_image);
            textViewTitle = (TextView) itemView.findViewById(R.id.book_title);
            textViewAuthor = (TextView) itemView.findViewById(R.id.book_author);
        }
    }
}
