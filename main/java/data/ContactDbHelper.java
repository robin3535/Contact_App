package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Database helper for Contacts app. Manages database creation and version management.
 */
public class ContactDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ContactDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "shelter.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ContactDbHelper}.
     *
     * @param context of the app
     */
    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the contacts table
        String SQL_CREATE_CONTACTS_TABLE =  "CREATE TABLE " + ContactContract.ContactEntry.TABLE_NAME + " ("
                + ContactContract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ContactContract.ContactEntry.COLUMN_CONTACT_NAME + " TEXT NOT NULL, "
                + ContactContract.ContactEntry.COLUMN_CONTACT_BREED + " TEXT, "
                + ContactContract.ContactEntry.COLUMN_CONTACT_GENDER + " INTEGER NOT NULL, "
                + ContactContract.ContactEntry.COLUMN_CONTACT_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}

