package com.example.sqlite_listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
public class EntryAdapter extends ArrayAdapter<tbl_Entry> {
    Context context;

    public static final int WRAP_CONTENT_LENGTH = 50;
    public EntryAdapter(Context context, int resource, ArrayList<tbl_Entry> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.note_list_item, null);
        }

        tbl_Entry note = getItem(position);

        if(note != null) {
            TextView title = (TextView) convertView.findViewById( R.id.list_note_title);
            TextView date = (TextView) convertView.findViewById( R.id.list_note_date);
            TextView content = (TextView) convertView.findViewById( R.id.list_note_content_preview);

            title.setText(note.getEntryTitle()); date.setText(note.getDateTimeFormatted(context) + "");

//correctly show preview of the content
// (not more than 50 char or more than one line!)
            int toWrap = WRAP_CONTENT_LENGTH;
            int lineBreakIndex = note.getContent().indexOf('\n');
// used to wrap/cut the content
            if(note.getContent().length() > WRAP_CONTENT_LENGTH || lineBreakIndex < WRAP_CONTENT_LENGTH) {
                if(lineBreakIndex < WRAP_CONTENT_LENGTH) { toWrap = lineBreakIndex;
                }
                if(toWrap > 0) { content.setText(note.getContent().substring(
                        0, toWrap) + "...");
                } else {
                    content.setText(note.getContent());
                }
            } else { //if less than 50 chars...leave it as is :P
                content.setText(note.getContent());
            }
        }

        return convertView;
    }

}

