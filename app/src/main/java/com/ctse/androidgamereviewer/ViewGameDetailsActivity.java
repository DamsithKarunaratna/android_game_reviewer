package com.ctse.androidgamereviewer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctse.androidgamereviewer.data.entities.Game;
import com.ctse.androidgamereviewer.data.entities.Review;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ViewGameDetailsActivity extends AppCompatActivity {

    public static final int ADD_REVIEW_REQUEST = 3;

    private GameViewModel gameViewModel;
    private ReviewViewModel reviewViewModel;
    private Game game;

    // UI elements
    ImageView ivGameImage;


    // TODO: Implement RecyclerView for reviews

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_game_details);

        // Get reference for action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final TextView tvGameTitle = findViewById(R.id.text_view_game_title);
        final TextView tvGameGenre = findViewById(R.id.text_view_genre);
        final TextView tvGameReleaseDate = findViewById(R.id.text_view_release_date);

        final int position = getIntent().getIntExtra(GameViewAdapter.EXTRA_POSITION, -1);

        gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        gameViewModel.getAllGames().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                game = games.get(position);
                tvGameTitle.setText(game.getTitle());
                tvGameGenre.setText(game.getGenre());
                tvGameReleaseDate.setText(game.getRelease_date());
            }
        });

        reviewViewModel = ViewModelProviders.of(this).get(ReviewViewModel.class);

        Button addReviewButton = findViewById(R.id.button_add_review);
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewGameDetailsActivity.this, AddReviewActivity.class);
                startActivityForResult(intent, ADD_REVIEW_REQUEST);
            }
        });

    }

    // Action bar back button
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_REVIEW_REQUEST && resultCode == RESULT_OK) {
            int rating = data.getIntExtra(AddReviewActivity.EXTRA_REVIEW_RATING, 0);
            String reviewTitle = data.getStringExtra(AddReviewActivity.EXTRA_REVIEW_TITLE);
            String reviewBody = data.getStringExtra(AddReviewActivity.EXTRA_REVIEW_BODY);

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Review review = new Review();

            review.setRating(rating);
            review.setTitle(reviewTitle);
            review.setBody(reviewBody);
            review.setDate(dateFormat.format(date.getTime()));
            review.setGameId(game.get_id());

            reviewViewModel.insert(review);

            Toast.makeText(this, "Review Saved", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Review did not saved", Toast.LENGTH_SHORT).show();
        }
    }
}
