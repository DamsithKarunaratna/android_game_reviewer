package com.ctse.androidgamereviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import androidx.appcompat.app.AppCompatActivity;

public class AddGameActivity extends AppCompatActivity implements
        com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_TITLE = "com.ctse.androidgamereviewer.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.ctse.androidgamereviewer.EXTRA_DESCRIPTION";
    public static final String EXTRA_RELEASE_DATE = "com.ctse.androidgamereviewer.EXTRA_RELEASE_DATE";

    private EditText etTitle;
    private EditText etGenre;
    private EditText etDate;
    private ImageButton imageButton;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        etTitle = findViewById(R.id.edit_text_game_title);
        etGenre = findViewById(R.id.edit_text_game_genre);
        etDate = findViewById(R.id.edit_text_date);
        imageButton = findViewById(R.id.btn_open_date_picker);

        datePickerDialog = new SpinnerDatePickerDialogBuilder()
                .context(AddGameActivity.this)
                .callback(AddGameActivity.this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(2017, 0, 1)
                .maxDate(2030, 0, 1)
                .minDate(1990, 0, 1)
                .build();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.show();

            }
        });

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
        String releaseDate = etDate.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please insert Title and Description",
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (releaseDate.isEmpty()) {
            Toast.makeText(this, "Please specify the release date",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent saveGameIntent = new Intent();
        saveGameIntent.putExtra(EXTRA_TITLE, title);
        saveGameIntent.putExtra(EXTRA_DESCRIPTION, description);
        saveGameIntent.putExtra(EXTRA_RELEASE_DATE, releaseDate);

        setResult(RESULT_OK, saveGameIntent);
        finish();

    }

    private String getFormattedDate(int selectedYear, int selectedMonth, int selectedDay) {

        String date = "";

        switch(selectedMonth) {
            case 0:
                date += "January";
                break;
            case 1:
                date += "February";
                break;
            case 2:
                date += "March";
                break;
            case 3:
                date += "April";
                break;
            case 4:
                date += "May";
                break;
            case 5:
                date += "June";
                break;
            case 6:
                date += "July";
                break;
            case 7:
                date += "August";
                break;
            case 8:
                date += "September";
                break;
            case 9:
                date += "October";
                break;
            case 10:
                date += "November";
                break;
            case 11:
                date += "December";
                break;
        }

        date += " " + selectedDay;
        date += " " + selectedYear;

        return date;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        etDate.setText(getFormattedDate(year, monthOfYear, dayOfMonth));
    }
}
