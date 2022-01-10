package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.ui.NoteListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author zhongshan
 * 2020-04-19.
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;
    private static final String TAG = "MainAcitivity";

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, NoteActivity.class), REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        dbHelper = new TodoDbHelper(this);

        notesAdapter.refresh(loadNotesFromDatabase());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                return true;
            case R.id.action_database:
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans

        //获得写权限
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //查询语句
        String selection=null;
        //查询语句的值
        String[] selectionArgs=null;

        List<Note> notes = new ArrayList<>();

        //查询返回的结果
        String[] projection = {
                BaseColumns._ID,
                TodoContract.todoList.DATE,
                TodoContract.todoList.CONTENT,
                TodoContract.todoList.STATE,
                TodoContract.todoList.CONTENT,
                TodoContract.todoList.PRIORITY
        };

        // 以规定的顺序输出
        String sortOrder =
                TodoContract.todoList.PRIORITY + " DESC";

        Cursor cursor = db.query(
                TodoContract.todoList.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,          // The columns for the WHERE clause
                null,         // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder
        );
        //获取游标里存放的查询结果
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.todoList._ID));
            String content = cursor.getString(cursor.getColumnIndex(TodoContract.todoList.CONTENT));
            int state = cursor.getInt(cursor.getColumnIndex(TodoContract.todoList.STATE));
            int priority = cursor.getInt(cursor.getColumnIndex(TodoContract.todoList.PRIORITY));
            String date= cursor.getString(cursor.getColumnIndex(TodoContract.todoList.DATE));
            Note note = new Note(itemId);
            note.setDate(date);
            if(state==0)
                note.setState(State.TODO);
            else
                note.setState(State.DONE);
            note.setContent(content);
            note.setPriority(priority);
            notes.add(note);
            Log.i(TAG, "itemId:" + itemId + ", content:" + content + ", state:" + state
                    + ", priority:" +priority+ ", date:"+date);
        }
        cursor.close();
        return notes;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 相当于where id=note.id
        String selection=TodoContract.todoList._ID+" = ?";
        String temp=String.valueOf(note.getId());
        String[] selectionArgs= {temp};
        int deleteRows=db.delete(TodoContract.todoList.TABLE_NAME,selection,selectionArgs);
        Log.i(TAG, " "+deleteRows);

        // 刷新页面
        notesAdapter.refresh(loadNotesFromDatabase());
    }

    private void updateNode(Note note) {
        // 更新数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //新的数据
        ContentValues values=new ContentValues();
        String content = note.getContent();
        int state = note.getState().intValue;
        int priority = note.getPriority();
        String date= new Date().toString();

        values.put(TodoContract.todoList.CONTENT,content);
        values.put(TodoContract.todoList.DATE,date);
        values.put(TodoContract.todoList.STATE,state);
        values.put(TodoContract.todoList.PRIORITY,priority);

        // 相当于where id=note.id
        String selection=TodoContract.todoList._ID+" = ?";
        String temp=String.valueOf(note.getId());
        String[] selectionArgs= {temp};
        // 返回更新了几条数据的结果，以此判断是否更新成功
        int count=db.update(TodoContract.todoList.TABLE_NAME,values,selection,selectionArgs);

        Log.i(TAG, " "+count);


        //刷新页面
        notesAdapter.refresh(loadNotesFromDatabase());

    }

}
