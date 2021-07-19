package com.example.contacts;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import data.ContactContract;

/**
 * {@link ContactCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of contact data as its data source. This adapter knows
 * how to create list items for each row of contact data in the {@link Cursor}.
 */
public class ContactCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ContactCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ContactCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the contact data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current contact can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        TextView contactLetter = (TextView) view.findViewById(R.id.contact_letter);


        // Find the columns of contact attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_CONTACT_NAME);
        int breedColumnIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_CONTACT_BREED);

        // Read the contact attributes from the Cursor for the current contact
        String contactName = cursor.getString(nameColumnIndex);
        String contactBreed = cursor.getString(breedColumnIndex);

        // Update the TextViews with the attributes for the current contact
        nameTextView.setText(contactName);
        summaryTextView.setText(contactBreed);
        contactLetter.setText(contactName.toCharArray(),0,1);

    }

}

