package com.ctse.androidgamereviewer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctse.androidgamereviewer.data.entities.Game;

import java.util.List;

public class ViewGameDetailsActivity extends AppCompatActivity {

    private GameViewModel gameViewModel;
    private Game game;

    // UI elements
    ImageView ivGameImage;
    Button addReviewButton;

    // TODO: Implement RecyclerView

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

        final int position = getIntent().getIntExtra("position", -1);

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
}
