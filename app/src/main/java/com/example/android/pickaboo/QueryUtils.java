package com.example.android.pickaboo;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.pickaboo.MainActivity.LOG_TAG;

/**
 * Created by Cristina on 03/07/2017.
 * Helper methods related to requesting and receiving books data from Google Bocks API.
 */

public final class QueryUtils {

    private static final String LOCATION_SEPARATOR = "-";
    private static final String EMPTY_STRING = "";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google Books dataset and return a List of {@link Book}.
     */
    public static List<Book> fetchBookData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        // Extract relevant fields from the JSON response and create an List<Book> object
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the List
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from parsing a JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            JSONArray items = baseJsonResponse.getJSONArray("items");

            // Loop through each feature in the array
            for (int i = 0; i < items.length(); i++) {
                // Get book JSONObject at position i
                JSONObject book = items.getJSONObject(i);
                // Get "volumeInfo" JSONObject
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                // Extract "title"
                StringBuilder title = new StringBuilder(volumeInfo.getString("title"));
                // Create title from title + subtitle
                if (!volumeInfo.isNull("subtitle")) {
                    // Extract "subtitle"
                    String subtitle = volumeInfo.getString("subtitle");
                    title.append(", ");
                    title.append(subtitle);
                }
                // Create author from array
                StringBuilder author = new StringBuilder();
                if (!volumeInfo.isNull("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    if (authors.length() > 0) {
                        author = new StringBuilder(authors.getString(0));
                        if (authors.length() > 1) {
                            for (int j = 1; j < authors.length(); j++) {
                                author.append(" - ");
                                author.append(authors.getString(j));
                            }
                        }
                    }
                }
                // Create year from date (published date is returned in format yyyy-mm-dd)
                String year = EMPTY_STRING;
                if (!volumeInfo.isNull("publishedDate")) {
                    String publishedDate = volumeInfo.getString("publishedDate");
                    if (publishedDate.contains(LOCATION_SEPARATOR)) {
                        String[] parts = publishedDate.split(LOCATION_SEPARATOR);
                        year = parts[0];
                    } else {
                        year = publishedDate;
                    }
                }
                // Extract "thumbnail" for imageLink
                String imageLink = EMPTY_STRING;
                if (!volumeInfo.isNull("imageLinks")) {
                    if (!volumeInfo.getJSONObject("imageLinks").isNull("thumbnail")) {
                        imageLink = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                    }
                }
                // Extract "infoLink" for bookUrl
                String bookUrl = EMPTY_STRING;
                if (!volumeInfo.isNull("infoLink")) {
                    bookUrl = volumeInfo.getString("infoLink");
                }
                // Extract "averageRating" for ratting
                Double ratting = 0.0;
                if (!volumeInfo.isNull("averageRating")) {
                    ratting = volumeInfo.getDouble("averageRating");
                }
                // Extract "textSnippet" for description
                String description = EMPTY_STRING;
                if (!book.isNull("searchInfo")) {
                    if (!book.getJSONObject("searchInfo").isNull("textSnippet")) {
                        description = book.getJSONObject("searchInfo").getString("textSnippet");
                    }
                }
                // Create Earthquake java object from magnitude, location, and time
                Book parsedBook = new Book(title.toString(), author.toString(), year, description,
                        imageLink, bookUrl, ratting);
                // Add earthquake to list of earthquakes
                books.add(parsedBook);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        }
        // Return the list of earthquakes
        return books;
    }
}
