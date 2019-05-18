/*
 * CTSE Android Project - Game Reviewer
 * @author IT16037434 Karunaratne D. C.
 * @author IT15146366 Hettiarachchi H. A. I. S.
 *
 * File: AddReviewActivity.java
 */
package com.ctse.androidgamereviewer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ctse.androidgamereviewer.data.entities.Review;
import com.google.android.material.textfield.TextInputEditText;

/**
 * AddReviewActivity is launched from the ViewGameDetailsActivity when Add Review button is clicked
 * or from ViewReviewActivity when Edit button is clicked
 * It consists of a form to enter review details such as review title, review body and a star rating
 * and passed back to View game activity to add a new review or to the view review activity to
 * update the review.
 *
 */
public class AddReviewActivity extends AppCompatActivity {

    public static final String EXTRA_REVIEW_TITLE = "com.ctse.androidgamereviewer.EXTRA_REVIEW_TITLE";
    public static final String EXTRA_REVIEW_RATING = "com.ctse.androidgamereviewer.EXTRA_REVIEW_RATING";
    public static final String EXTRA_REVIEW_BODY = "com.ctse.androidgamereviewer.EXTRA_REVIEW_BODY";

    private RatingBar ratingBar;
    private EditText etReviewTitle;
    private TextInputEditText etReviewBody;

    private ReviewViewModel reviewViewModel;

    private String reviewId;

    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        requestCode = getIntent().getIntExtra(MainActivity.REVIEW_REQUEST_CODE, -999);

        reviewId = getIntent().getStringExtra(ReviewViewAdapter.EXTRA_REVIEW_ID);

        // Set action bar close icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set title
        if (requestCode == ViewReviewActivity.EDIT_REVIEW_REQUEST) {
            setTitle("Edit Review");
        } else {
            setTitle("Add Review");
        }

        ratingBar = findViewById(R.id.ratingBar);
        etReviewTitle = findViewById(R.id.edit_text_review_title);
        etReviewBody = findViewById(R.id.input_edit_text_review);


        /**
         * If request code is update review, form fields will be filled from current review values
         * Observe current review data as a live data
         * */
        if (requestCode == ViewReviewActivity.EDIT_REVIEW_REQUEST) {
            reviewViewModel = ViewModelProviders.of(this).get(ReviewViewModel.class);
            reviewViewModel.getReviewById(reviewId).observe(this, new Observer<Review>() {
                @Override
                public void onChanged(Review review) {
                    ratingBar.setRating(review.getRating());
                    etReviewBody.setText(review.getBody());
                    etReviewTitle.setText(review.getTitle());
                }
            });
        }

    }

    // Set menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_review_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_review_button:
                saveReview();
                return true;
            default:
                finish();
                return true;
        }
    }

    /**
     * Create an intent with data entered or modified by user and post it back as a result to
     * the activity where the intent was started (View game details activity or View review activity)
     * Rating, title and body of review will be validated before posting results.
     * */
    private void saveReview() {
        int rating = (int) ratingBar.getRating();
        String reviewTitle = etReviewTitle.getText().toString().trim();
        String reviewBody = etReviewBody.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please rate the application to continue", Toast.LENGTH_SHORT).show();
            return;
        } else if (reviewTitle.isEmpty() || reviewBody.isEmpty()) {
            Toast.makeText(this, "Please insert review title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent saveReviewIntent = new Intent();
        saveReviewIntent.putExtra(EXTRA_REVIEW_TITLE, reviewTitle);
        saveReviewIntent.putExtra(EXTRA_REVIEW_BODY, reviewBody);
        saveReviewIntent.putExtra(EXTRA_REVIEW_RATING, rating);

        setResult(RESULT_OK, saveReviewIntent);
        finish();
    }

}
