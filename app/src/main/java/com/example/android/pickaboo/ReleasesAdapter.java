package com.example.android.pickaboo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Cristina on 04/07/2017.
 * This adapter provides access to the "last releases" items in the data set, creates views for
 * items, and replaces the content of some of the views with new data items when the original item
 * is no longer visible.
 */

public class ReleasesAdapter extends RecyclerView.Adapter<ReleasesAdapter.ViewHolder> {

    private Context mContext;
    private List<Book> mBooks;
    private View.OnClickListener mClickListener;
    public static MyAdapterListener mOnClickListener;

    // Handle button click
    public interface MyAdapterListener {
        void OnClick(View v, int position);
    }

    // Provide a reference to the views for each data item. The ViewHolder is ba static
    // class instance which is associated with a view when it's created, caching the child views.
    // If the view already exists, retrieve the holder instance and use its fields instead of
    // calling again findViewById.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView bookCover;
        public TextView title, author;

        public ViewHolder(View view) {
            super(view);
            bookCover = (ImageView) view.findViewById(R.id.release_image);
            title = (TextView) view.findViewById(R.id.release_title);
            author = (TextView) view.findViewById(R.id.release_author);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.OnClick(v, getAdapterPosition());
                }
            });
        }
    }

    /**
     * Custom constructor
     * @param books is the list of {@link Book} objects to be populated in the RecyclerView
     */
    public ReleasesAdapter(Context context, List<Book> books, MyAdapterListener listener) {
        mContext = context;
        mBooks = books;
        mOnClickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReleasesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.releases_list_item, parent, false);
        // Set the view's parameters
        // Find the Layout
        LinearLayout releasesLayout = (LinearLayout) itemView.findViewById(R.id.release_layout);
        TextView title = (TextView) itemView.findViewById(R.id.release_title);
        TextView author = (TextView) itemView.findViewById(R.id.release_author);
        // Get the device screen dimensions
        int screenWidth = parent.getContext().getResources().getDisplayMetrics().widthPixels;
        // Set the dimensions of the layout based on the screen dimensions
        releasesLayout.setMinimumWidth((int) (0.35 * screenWidth));
        title.setWidth((int) (0.4 * screenWidth));
        author.setWidth((int) (0.4 * screenWidth));
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ReleasesAdapter.ViewHolder holder, int position) {
        Book currentBook = mBooks.get(position);

        // Get the imageLink to download the book cover image
        String imageLink = currentBook.getImageLink();
        // Download the image and attach it to the ImageView. If imageLink is null, no cover art is
        // assign to that book or is not recognized as such. Then, a default cover is assigned.
        if (imageLink != null && imageLink.length() > 0) {
            Picasso.with(mContext).load(imageLink).into(holder.bookCover);
        } else {
            Picasso.with(mContext).load(R.drawable.default_cover).into(holder.bookCover);
        }

        // Get the book title and set as text for the Textview
        holder.title.setText(currentBook.getTitle());

        // Get the author and set as text for the Textview
        if (!TextUtils.isEmpty(currentBook.getAuthor())) {
            holder.author.setText(mContext.getString(R.string.by_author, currentBook.getAuthor()));
        } else {
            holder.author.setText("");
        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    // Clear the adapter data
    // https://stackoverflow.com/questions/29978695/remove-all-items-from-recyclerview
    public void clear() {
        int size = mBooks.size();
        mBooks.clear();
        notifyItemRangeRemoved(0, size);
    }

    // Add new data to the adapter's data set
    // https://stackoverflow.com/questions/30053610/best-way-to-update-data-with-a-recyclerview-adapter
    public void addAll(List<Book> newList) {
        mBooks.addAll(newList);
        notifyDataSetChanged();
    }
}
