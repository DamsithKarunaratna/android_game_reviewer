package com.ctse.androidgamereviewer;

import androidx.annotation.Nullable;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ratingBar = findViewById(R.id.ratingBar);
        etReviewTitle = findViewById(R.id.edit_text_review_title);
        etReviewBody = findViewById(R.id.input_edit_text_review);

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
