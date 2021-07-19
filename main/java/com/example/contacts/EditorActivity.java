package com.example.contacts;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import data.ContactContract;
import data.ContactContract.ContactEntry;


/**
 * Allows user to create a new contact or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the contact data loader */
    private static final int EXISTING_CONTACT_LOADER = 0;

    /** Content URI for the existing contact (null if it's a new contact) */
    private Uri mCurrentPetUri;

    /** EditText field to enter the contact's name */
    private EditText mNameEditText;

    /** EditText field to enter the contact's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the contact's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the contact's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the contact. The possible valid values are in the ContactContract.java file:
     * {@link ContactEntry#GENDER_UNKNOWN}, {@link ContactEntry#GENDER_MALE}, or
     * {@link ContactEntry#GENDER_FEMALE}.
     */
    private int mGender = ContactEntry.GENDER_UNKNOWN;

    /** Boolean flag that keeps track of whether the contact has been edited (true) or not (false) */
    private boolean mPetHasChanged = false;


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new contact or editing an existing one.
        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();

        // If the intent DOES NOT contain a contact content URI, then we know that we are
        // creating a new contact.
        if (mCurrentPetUri == null) {
            // This is a new contact, so change the app bar to say "Add a Contact"
            setTitle(getString(R.string.editor_activity_title_new_contact));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a contact that hasn't been created yet.)
            invalidateOptionsMenu();



            
        } else {
            // Otherwise this is an existing contact, so change app bar to say "Edit Contact"
            setTitle(getString(R.string.editor_activity_title_edit_contact));

            // Initialize a loader to read the contact data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_CONTACT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_contact_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_contact_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_contact_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the contact.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = ContactEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = ContactEntry.GENDER_FEMALE;
                    } else {
                        mGender = ContactEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = ContactEntry.GENDER_UNKNOWN;
            }
        });
        // Setup FAB to open EditorActivity


        FloatingActionButton calling = (FloatingActionButton) findViewById(R.id.call);
        calling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText weight = (EditText) findViewById(R.id.edit_contact_weight);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + weight.getText().toString() ));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Get user input from editor and save contact into database.
     */
    private void saveContact() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();

        // Check if this is supposed to be a new contact
        // and check if all the fields in the editor are blank
        if (mCurrentPetUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) &&
                TextUtils.isEmpty(weightString) && mGender == ContactEntry.GENDER_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new contact.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and contact attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ContactEntry.COLUMN_CONTACT_NAME, nameString);
        values.put(ContactEntry.COLUMN_CONTACT_BREED, breedString);
        values.put(ContactEntry.COLUMN_CONTACT_WEIGHT,weightString);
        values.put(ContactEntry.COLUMN_CONTACT_GENDER, mGender);


        // Determine if this is a new or existing contact by checking if mCurrentPetUri is null or not
        if (mCurrentPetUri == null) {
            // This is a NEW contact, so insert a new contact into the provider,
            // returning the content URI for the new contact.
            Uri newUri = getContentResolver().insert(ContactEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_contact_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_contact_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING contact, so update the contact with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_contact_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_contact_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void deletePet() {
        // Only perform the delete if this is an existing contact.
        if (mCurrentPetUri != null) {
            // Call the ContentResolver to delete the contact at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the contact that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_contact_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_contact_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new contact, hide the "Delete" menu item.
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save contact to database
                saveContact();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                deletePet();

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the contact hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the contact hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all contact attributes, define a projection that contains
        // all columns from the contact table
        String[] projection = {
                ContactEntry._ID,
                ContactEntry.COLUMN_CONTACT_NAME,
                ContactEntry.COLUMN_CONTACT_BREED,
                ContactEntry.COLUMN_CONTACT_GENDER,
                ContactEntry.COLUMN_CONTACT_WEIGHT };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentPetUri,         // Query the content URI for the current contact
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of contact attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_NAME);
            int breedColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_BREED);
            int genderColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CONTACT_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            String weight = cursor.getString(weightColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(weight);

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (gender) {
                case ContactEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case ContactEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0); // Select "Unknown" gender
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the contact.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}


