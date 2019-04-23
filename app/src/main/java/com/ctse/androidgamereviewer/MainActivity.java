package com.ctse.androidgamereviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ctse.androidgamereviewer.data.entities.Game;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_GAME_REQUEST = 2;

    private GameViewModel gameViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final GameViewAdapter adapter = new GameViewAdapter();
        recyclerView.setAdapter(adapter);

        gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        gameViewModel.getAllGames().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                adapter.setGames(games);
            }
        });

        FloatingActionButton addGameButton = findViewById(R.id.button_add_game);
        addGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddGameActivity.class);
                startActivityForResult(intent, ADD_GAME_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_GAME_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddGameActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddGameActivity.EXTRA_DESCRIPTION);

            Game game = new Game();
            game.setTitle(title);
            game.setGenre(description);

            gameViewModel.insert(game);

            Toast.makeText(this, "Game Saved", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this, "Game not saved", Toast.LENGTH_SHORT).show();

        }
    }
}
