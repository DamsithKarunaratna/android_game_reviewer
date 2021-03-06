/*
 * CTSE Android Project - Game Reviewer
 * @author IT16037434 Karunaratne D. C.
 * @author IT15146366 Hettiarachchi H. A. I. S.
 *
 * File: ViewGameDetailsActivity.java
 */
package com.ctse.androidgamereviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctse.androidgamereviewer.data.entities.Game;
import com.ctse.androidgamereviewer.data.entities.Review;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.bson.types.ObjectId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.ctse.androidgamereviewer.MainActivity.EXTRA_REQUEST_CODE;

/**
 * ViewGameDetailsActivity is launched from GameViewAdapter through MainActivity when user click on
 * a game in the current game list. It contains information about the selected game including a
 * picture if available. All the reviews added to the game also shown in a list with ratings.
 */

public class ViewGameDetailsActivity extends AppCompatActivity {

  // Request ID for adding a review
  public static final int ADD_REVIEW_REQUEST = 3;
  public static final int EDIT_GAME_REQUEST = 414;
  FirebaseUser user;
  private GameViewModel gameViewModel;
  private ReviewViewModel reviewViewModel;
  private Game game;

  /**
   * Method to create image from base64.
   *
   * @param base64 base64 string to be converted
   *               <p>
   *               See <a href="https://stackoverflow.com/questions/15683032/android-convert-base64-encoded-string-into-image-view">
   *               Stackoverflow question</a>
   */
  public static Bitmap decodeBase64(String base64) {

    if (base64 != null) {
      byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
      return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    } else return null;

  }

  @Override
  public void startActivityForResult(Intent intent, int requestCode) {
    intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
    super.startActivityForResult(intent, requestCode);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_game_details);

    user = FirebaseAuth.getInstance().getCurrentUser();

    // hide the action bar programmatically
    ActionBar actionBar = getSupportActionBar();
    actionBar.hide();

    // initialize views
    final TextView tvGameTitle = findViewById(R.id.text_view_game_title);
    final TextView tvGameGenre = findViewById(R.id.text_view_genre);
    final TextView tvGameReleaseDate = findViewById(R.id.text_view_release_date);
    final ImageView ivGameImage = findViewById(R.id.image_view_game_image);

    final int position = getIntent().getIntExtra(GameViewAdapter.EXTRA_POSITION, -1);
    final String gameId = getIntent().getStringExtra(GameViewAdapter.EXTRA_GAME_ID);

    final FloatingActionButton fabEdit = findViewById(R.id.button_edit_game);
    fabEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (game != null) {
          Intent intent = new Intent(ViewGameDetailsActivity.this,
              AddGameActivity.class);
          intent.putExtra(AddGameActivity.EXTRA_TITLE, game.getTitle());
          intent.putExtra(AddGameActivity.EXTRA_GENRE, game.getGenre());
          intent.putExtra(AddGameActivity.EXTRA_RELEASE_DATE, game.getRelease_date());
          intent.putExtra(AddGameActivity.EXTRA_IMAGE, game.getImage());
          startActivityForResult(intent, EDIT_GAME_REQUEST);
        }
      }
    });

    RecyclerView recyclerView = findViewById(R.id.recycler_view_reviews);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    final ReviewViewAdapter adapter = new ReviewViewAdapter(this);
    recyclerView.setAdapter(adapter);

    /*
     * Get the current game and set information to the UI
     * @see GameViewModel
     * */
    gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
    gameViewModel.getAllGames().observe(this, new Observer<List<Game>>() {
      @Override
      public void onChanged(List<Game> games) {
        game = games.get(position);
        tvGameTitle.setText(game.getTitle());
        tvGameGenre.setText(game.getGenre());
        tvGameReleaseDate.setText(game.getRelease_date());

        Bitmap bmp = decodeBase64(game.getImage());
        if (bmp != null) {
          ivGameImage.setImageBitmap(bmp);
        }

        // Hide edit button if the owner is not logged in.
        if (user == null) {
          fabEdit.setVisibility(View.INVISIBLE);
        } else if (!Objects.equals(user.getEmail(), game.getOwner_email())) {
          fabEdit.setVisibility(View.INVISIBLE);
        }

      }
    });

    /**
     * Get all the reviews for current game and display in Review adapter
     * @see ReviewViewAdapter
     * @see ReviewViewModel
     * */
    reviewViewModel = ViewModelProviders.of(this).get(ReviewViewModel.class);
    reviewViewModel.getReviewForGame(gameId).observe(this, new Observer<List<Review>>() {
      @Override
      public void onChanged(List<Review> reviews) {
        Log.d("GAME ID", "onChanged: " + gameId);
        adapter.setReviews(reviews);
      }
    });

    /**
     * Start AddReviewActivity for result
     * Review request code is passed as extras
     * */
    Button addReviewButton = findViewById(R.id.button_add_review);
    addReviewButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (null != user) {
          Intent intent = new Intent(ViewGameDetailsActivity.this,
              AddReviewActivity.class);
          intent.putExtra(MainActivity.REVIEW_REQUEST_CODE, ADD_REVIEW_REQUEST);
          startActivityForResult(intent, ADD_REVIEW_REQUEST);
        } else {
          Toast.makeText(ViewGameDetailsActivity.this,
              "You need to be logged in to add review", Toast.LENGTH_SHORT).show();
        }
      }
    });

  }

  // Action bar options
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


  /**
   * Add a new review to database from the intent extras passed from AddReviewActivity
   *
   * @see ReviewViewModel
   *
   * <p>MongoDB Driver is used to generate a object id for the review
   * See
   * <a href="https://docs.mongodb.com/manual/reference/method/ObjectId/">MongoDB Documentation</a>
   * for more information
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // check the request code and result code
    if (requestCode == ADD_REVIEW_REQUEST) {
      if (resultCode == RESULT_OK) {
        int rating = data.getIntExtra(AddReviewActivity.EXTRA_REVIEW_RATING, 0);
        String reviewTitle = data.getStringExtra(AddReviewActivity.EXTRA_REVIEW_TITLE);
        String reviewBody = data.getStringExtra(AddReviewActivity.EXTRA_REVIEW_BODY);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Review review = new Review();
        ObjectId objectId = new ObjectId();

        review.setRating(rating);
        review.setTitle(reviewTitle);
        review.setBody(reviewBody);
        review.setDate(dateFormat.format(date.getTime()));
        review.setGameId(game.get_id());
        review.set_id(objectId.toString());
        review.setUserEmail(user.getEmail());

        reviewViewModel.insert(review);

        Toast.makeText(this, "Review Saved", Toast.LENGTH_SHORT).show();

      } else {
        Toast.makeText(this, "Review did not saved", Toast.LENGTH_SHORT).show();
      }
    } else if (requestCode == EDIT_GAME_REQUEST) {
      if (resultCode == RESULT_OK) {

        game.setTitle(data.getStringExtra(AddGameActivity.EXTRA_TITLE));
        game.setGenre(data.getStringExtra(AddGameActivity.EXTRA_GENRE));
        game.setRelease_date(data.getStringExtra(AddGameActivity.EXTRA_RELEASE_DATE));
        game.setImage(data.getStringExtra(AddGameActivity.EXTRA_IMAGE));

        gameViewModel.update(game);
        Toast.makeText(this, "Game edited", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, "editing canceled", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
