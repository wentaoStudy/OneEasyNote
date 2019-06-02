package com.wentaostudy.notefundition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wentaostudy.notefundition.ui.noteedit.NoteEditFragment;

public class NoteEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, NoteEditFragment.newInstance())
                    .commitNow();
        }
    }
}
