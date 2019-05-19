/*
 * CTSE Android Project - Game Reviewer
 * @author IT16037434 Karunaratne D. C.
 * @author IT15146366 Hettiarachchi H. A. I. S.
 *
 * File: AddGameActivity.java
 */
package com.ctse.androidgamereviewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.ctse.androidgamereviewer.helpers.FileUtil;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import id.zelory.compressor.Compressor;

/**
 * AddGameActivity is launched from the MainActivity when the floating action button is clicked.
 * It consists of a form to enter information about the game and pass it back as a result to the
 * Main activity where it can be persisted.
 * <p>
 * A custom DatePicker library was used since the DatePicker in newer versions of android
 * cannot use a Spinner style. See the <a href="https://github.com/drawers/SpinnerDatePicker">
 * Spinner date picker github page</a>
 * <p>
 * Image compression is handled by the <a href="https://github.com/zetbaitsu/Compressor">
 * compressor </a> library
 */
public class AddGameActivity extends AppCompatActivity implements
        com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    // String constants accessible statically from other classes used to tag Intent extras
    public static final String EXTRA_TITLE = "com.ctse.androidgamereviewer.EXTRA_TITLE";
    public static final String EXTRA_GENRE = "com.ctse.androidgamereviewer.EXTRA_GENRE";
    public static final String EXTRA_RELEASE_DATE = "com.ctse.androidgamereviewer.EXTRA_RELEASE_DATE";
    public static final String EXTRA_IMAGE = "com.ctse.androidgamereviewer.EXTRA_IMAGE";

    // Arbitrary code for creating implicit intent
    public static final int GALLERY_REQUEST_CODE = 12;

    private EditText etTitle;
    private EditText etGenre;
    private EditText etDate;
    private ImageView imageView;
    private ImageButton showDatePickerButton;

    /**
     * From the Android Open Source SpinnerDatePicker project
     * See the <a href="https://github.com/drawers/SpinnerDatePicker">
     * Spinner date picker github page</a>
     */
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        int requestCode = getIntent().getIntExtra(MainActivity.EXTRA_REQUEST_CODE,
                -999);

        // Initialize views
        etTitle = findViewById(R.id.edit_text_game_title);
        etGenre = findViewById(R.id.edit_text_game_genre);
        etDate = findViewById(R.id.edit_text_date);
        showDatePickerButton = findViewById(R.id.btn_open_date_picker);
        imageView = findViewById(R.id.image_view_game_image);

        /*
         * From the Android Open Source SpinnerDatePicker project
         * at https://github.com/drawers/SpinnerDatePicker"
         *
         * Initialize a DatePickerDialog
         */
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

        showDatePickerButton.setOnClickListener(new View.OnClickListener() {
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

        switch (requestCode) {
            case MainActivity.ADD_GAME_REQUEST:
                setTitle("Add Game");
                break;
            case ViewGameDetailsActivity.EDIT_GAME_REQUEST:
                setTitle("Edit Game");
                etTitle.setText(getIntent().getStringExtra(EXTRA_TITLE));
                etGenre.setText(getIntent().getStringExtra(EXTRA_GENRE));
                etDate.setText(getIntent().getStringExtra(EXTRA_RELEASE_DATE));
                Bitmap bmp = ViewGameDetailsActivity.decodeBase64(getIntent()
                        .getStringExtra(EXTRA_IMAGE));
                if (bmp != null) {
                    imageView.setImageBitmap(bmp);
                }
                break;
        }
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
                    try {
                        /*
                         * "id.zelory:compressor:2.1.0" library used for compressing bitmap images
                         * This saves bandwidth when saving the image as well as allows safe
                         * transfer of data through Intent
                         *
                         * See github project at https://github.com/zetbaitsu/Compressor
                         */
                        File actualImage = FileUtil.from(this, data.getData());
                        Bitmap bitmap = new Compressor(this)
                                .setMaxWidth(400)
                                .setMaxHeight(600)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .compressToBitmap(actualImage);

                        imageView.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to read picture data!",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    break;
            }
    }

    /**
     * Helper method to encode Bitmap image as a Base64 String.
     *
     * @param drawable Image to be encoded.
     * @return Base64 encoded image.
     * @see <a href="https://stackoverflow.com/questions/36492084/how-to-convert-an-image-to-base64-string-in-java">
     * This stackoverflow question</a>
     */
    private String getBase64Image(BitmapDrawable drawable) {
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 90, bos);
        byte[] bb = bos.toByteArray();
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
        String genre = etGenre.getText().toString().trim();
        String releaseDate = etDate.getText().toString().trim();

        if (title.isEmpty() || genre.isEmpty()) {
            Toast.makeText(this, "Please insert Title and Description",
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (releaseDate.isEmpty()) {
            Toast.makeText(this, "Please specify the release date",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int requestCode = getIntent().getIntExtra(MainActivity.EXTRA_REQUEST_CODE,
                -999);

        Intent saveGameIntent = new Intent();
        saveGameIntent.putExtra(EXTRA_TITLE, title);
        saveGameIntent.putExtra(EXTRA_GENRE, genre);
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
