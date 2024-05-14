package com.example.sqlite_listview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean mIsViewingOrUpdating;
    private String EntryName;
    private tbl_Entry EntryNote;
    private long DateTime;

    Button save, cancel, delete;
    EditText title, content;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        delete = findViewById(R.id.delete);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        delete.setOnClickListener(this);

        EntryName = getIntent().getStringExtra("Id");
        if (EntryName != null && !EntryName.isEmpty()) {
            int id = Integer.parseInt(EntryName);
            db = new DatabaseHandler(getApplicationContext());
            EntryNote = db.getEntry(id);
            Log.d("STATUS", (EntryNote == null) + "");
            if (EntryNote != null) {
                // update the widgets from the loaded note
                delete.setVisibility(View.VISIBLE);
                title.setText(EntryNote.getEntryTitle());
                content.setText(EntryNote.getContent());
                DateTime = EntryNote.getDate();
                mIsViewingOrUpdating = true;
            }
        } else {
            // user wants to create a new note
            delete.setVisibility(View.GONE);
            DateTime = System.currentTimeMillis();
            mIsViewingOrUpdating = false;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Diary");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.save) {
            validateAndSaveNote();
        } else if (id == R.id.cancel) {
            actionCancel();
        } else if (id == R.id.delete) {
            actionDelete();
        }
    }

    private void actionCancel() {
        if (!checkNoteAltered()) {
            // if note is not altered by user
            // (user only viewed the note/or did not write anything)
            finish(); // just exit the activity and go back to DashboardActivity
        } else {
            // we want to remind user to decide about
            // saving the changes or not, by showing a dialog
            AlertDialog.Builder dialogCancel = new AlertDialog.Builder(this)
                    .setTitle("Discard changes...")
                    .setMessage("Are you sure you do not want to save " +
                            "changes to this note?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish(); // just go back to main activity
                        }
                    })
                    .setNegativeButton("NO", null); // null = stay in the activity!
            dialogCancel.show();
        }
    }

    private void actionDelete() {
        // ask user if he really wants to delete the note!
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this)
                .setTitle("Delete note")
                .setMessage("Really delete the note?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db = new DatabaseHandler(getApplicationContext());
                        db.deleteEntry(EntryNote);
                        finish();
                    }
                })
                .setNegativeButton("NO", null); // do nothing on clicking NO button :P

        dialogDelete.show();
    }

    /**
     * Handle cancel action
     */
    private boolean checkNoteAltered() {
        if (mIsViewingOrUpdating) { // if in view/update mode
            return EntryNote != null
                    && (!title.getText().toString().equalsIgnoreCase(
                    EntryNote.getEntryTitle()) ||
                    !content.getText().toString().equalsIgnoreCase(
                            EntryNote.getContent()));
        } else { // if in new note mode
            return !title.getText().toString().isEmpty()
                    || !content.getText().toString().isEmpty();
        }
    }

    /**
     * Validate the title and content and save the note
     * and finally exit the activity and go back to DashboardActivity
     */
    private void validateAndSaveNote() {
        String title = this.title.getText().toString();
        String content = this.content.getText().toString();

        // see if user has entered anything :D lol
        if (title.isEmpty()) { // title
            Toast.makeText(CreateActivity.this, "Please enter a title."
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) { // content
            Toast.makeText(CreateActivity.this, "Please enter a content. "
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        // set the creation time, if new note, now,
        // otherwise the loaded note's creation time
        if (EntryNote != null) {
            DateTime = EntryNote.getDate();
        } else {
            DateTime = System.currentTimeMillis();
        }

        // finally save the note!
        db = new DatabaseHandler(getApplicationContext());

        if (EntryNote != null) {
            EntryNote.setEntryTitle(this.title.getText().toString());
            EntryNote.setContent(this.content.getText().toString());
            EntryNote.setDate(DateTime);
            db.updateEntry(EntryNote);
            mIsViewingOrUpdating = true;
        } else {
            EntryNote = new tbl_Entry();
            EntryNote.setEntryTitle(this.title.getText().toString());
            EntryNote.setContent(this.content.getText().toString());
            EntryNote.setDate(DateTime);
            db.addEntry(EntryNote);
        }

        finish(); // exit the activity, should return us to DashboardActivity
    }
}
