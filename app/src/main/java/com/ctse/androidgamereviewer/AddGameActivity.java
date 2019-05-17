package com.ctse.androidgamereviewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;


import java.io.ByteArrayOutputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * AddGameActivity is launched from the MainActivity when the floating action button is clicked.
 * It consists of a form to enter information about the game and pass it back as a result to the
 * Main activity where it can be persisted.
 * <p>
 * A custom DatePicker library was used since the DatePicker in newer versions of android
 * cannot use a Spinner style. See the <a href="https://github.com/drawers/SpinnerDatePicker">
 * Spinner date picker github page</a>
 */
public class AddGameActivity extends AppCompatActivity implements
        com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_TITLE = "com.ctse.androidgamereviewer.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.ctse.androidgamereviewer.EXTRA_DESCRIPTION";
    public static final String EXTRA_RELEASE_DATE = "com.ctse.androidgamereviewer.EXTRA_RELEASE_DATE";
    public static final String EXTRA_IMAGE = "com.ctse.androidgamereviewer.EXTRA_IMAGE";
    public static final int GALLERY_REQUEST_CODE = 12;

    private EditText etTitle;
    private EditText etGenre;
    private EditText etDate;
    private ImageView imageView;
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
        imageView = findViewById(R.id.image_view_game_image);

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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery();
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        etDate.setText(getFormattedDate(year, monthOfYear, dayOfMonth));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);

//                    System.out.println("Base 64 image : " +
//                            getBase64Image((BitmapDrawable) imageView.getDrawable()));
                    break;
            }
    }

    private String getBase64Image(BitmapDrawable drawable) {
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, bos);
        byte[] bb = bos.toByteArray();
//        return Base64.encodeToString(bb, 0);
        return Base64.encodeToString(bb, Base64.NO_WRAP);
    }


    /**
     * Method to pick an image from the gallery.
     * First Creates an implicit Intent with action as ACTION_PICK
     * Then sets the type as image/*. This ensures only components of type image are selected
     * Passes an extra array with the accepted mime types. This will ensure only components
     * with these MIME types as targeted.
     * <p>
     * See <a href="https://androidclarified.com/pick-image-gallery-camera-android/">
     * article
     * </a> for more information.
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /**
     * Creates an intent with data extracted from the form and passes it as a result back to
     * MainActivity. Simple form validation is carried out to ensure that missing values are not
     * persisted.
     */
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
        saveGameIntent.putExtra(EXTRA_IMAGE, getBase64Image(
                (BitmapDrawable) imageView.getDrawable()));

        setResult(RESULT_OK, saveGameIntent);
        finish();

    }

    /**
     * Helper method to create a Date in String format from integer values.
     */
    private String getFormattedDate(int selectedYear, int selectedMonth, int selectedDay) {

        String date = "";

        switch (selectedMonth) {
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


}
