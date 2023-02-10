package com.example.notesapp.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notesapp.R;
import com.example.notesapp.Room.Note;
import com.example.notesapp.Room.NotesDatabase;
import com.example.notesapp.UI.Fragments.ToBuyFragment;
import com.example.notesapp.UI.Fragments.ToDoFragment;
import com.example.notesapp.UI.Fragments.ToReadFragment;
import com.example.notesapp.UI.Fragments.ToSearchFragment;
import com.example.notesapp.UI.Fragments.ToWatchFragment;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewNoteEditor extends AppCompatActivity {

    String currentFragmentName="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_new_note);
        EditText newNoteTitle=findViewById(R.id.new_note_title);
        EditText newNoteContent=findViewById(R.id.new_note_content);
        Button newNoteConfirm=findViewById(R.id.new_note_confirm);

        /* to edit a note */
        Intent editNote=getIntent();
        newNoteTitle.setText(editNote.getStringExtra("NoteTitle"));
        newNoteContent.setText(editNote.getStringExtra("NoteContent"));
        currentFragmentName=editNote.getStringExtra("NoteFragment");

        /* Instance to deal with Room */
        NotesDatabase notesRoom = NotesDatabase.getInstance(getApplicationContext());

        /* Getting currentFragment Name */
        switch(MainActivity.CURRENT_FRAGMENT_ID){
            case R.id.toDo:     currentFragmentName="ToDo";     break;
            case R.id.toBuy:    currentFragmentName="ToBuy";    break;
            case R.id.toRead:   currentFragmentName="ToRead";   break;
            case R.id.toSearch: currentFragmentName="ToSearch"; break;
            case R.id.toWatch:  currentFragmentName="ToWatch";  break;
        }

        /* Adding new note or Editing old note */
        newNoteConfirm.setOnClickListener((View view)-> {

            /* Delete old note incase of editing old note */
            notesRoom.notesDao().deleteNote(editNote.getStringExtra("NoteTitle"))
                    .subscribeOn(Schedulers.computation())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onComplete() {}

                        @Override
                        public void onError(Throwable e) {}
                    });

            /* Save latest version of current note*/
            notesRoom.notesDao().insertNote(new Note(newNoteTitle.getText()+"",newNoteContent.getText()+"",currentFragmentName))
                    .subscribeOn(Schedulers.computation())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onComplete() {
                            Toast.makeText(getApplicationContext(), R.string.NoteSaved, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {}
                    });


           Intent returnToFragment=new Intent(this, MainActivity.class);
           returnToFragment.putExtra("fragment",MainActivity.CURRENT_FRAGMENT_ID);
           startActivity(returnToFragment);
        });

    }
}
