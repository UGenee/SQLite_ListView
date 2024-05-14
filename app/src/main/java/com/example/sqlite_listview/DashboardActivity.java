package com.example.sqlite_listview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DashboardActivity extends AppCompatActivity {

    DatabaseHandler db;
    ListView listView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = findViewById(R.id.tvNone);
        listView = findViewById(R.id.list_item);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Access the database
        db = new DatabaseHandler(getApplicationContext());
        listView.setAdapter(null);
        ArrayList<tbl_Entry> entries = db.getAllEntry();

        // Sort the list according to date in reverse chronological order
        Collections.sort(entries, new Comparator<tbl_Entry>() {
            @Override
            public int compare(tbl_Entry lhs, tbl_Entry rhs) {
                Date left = new Date(lhs.getDate());
                Date right = new Date(rhs.getDate());
                if (left.before(right)) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        // The list is not empty, so load content
        if (entries != null && entries.size() > 0) {
            // Prepare adapter for customized ListView
            final EntryAdapter ea = new EntryAdapter(getApplicationContext(), R.layout.note_list_item, entries);
            listView.setAdapter(ea);

            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);

            // If an item of listView is clicked
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get its ID
                    int Id = ((tbl_Entry) listView.getItemAtPosition(position)).getEntryId();
                    // Open CreateActivity, passing the retrieved ID
                    Intent viewNoteIntent = new Intent(getApplicationContext(), CreateActivity.class);
                    viewNoteIntent.putExtra("Id", Id + "");
                    startActivity(viewNoteIntent);
                }
            });
        } else {
            // List is empty, display notice
            textView.setVisibility(View.VISIBLE);
        }
    }
}
