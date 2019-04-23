package com.ctse.androidgamereviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddGameActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "com.ctse.androidgamereviewer.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.ctse.androidgamereviewer.EXTRA_DESCRIPTION";

    private EditText etTitle;
    private EditText etGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        etTitle = findViewById(R.id.edit_text_game_title);
        etGenre = findViewById(R.id.edit_text_game_genre);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Game");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_game_button:
                saveGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveGame() {
        String title = etTitle.getText().toString().trim();
        String description = etGenre.getText().toString().trim();

        if(title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please insert Title and Description",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent saveGameIntent = new Intent();
        saveGameIntent.putExtra(EXTRA_TITLE, title);
        saveGameIntent.putExtra(EXTRA_DESCRIPTION, description);

        setResult(RESULT_OK, saveGameIntent);
        finish();

    }
}
