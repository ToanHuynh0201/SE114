package com.example.baitap3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2; // Tăng version để thêm column mới

    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String COLUMN_CREATED_TIME = "created_time";
    private static final String COLUMN_CREATOR = "creator";
    private static final String COLUMN_ASSIGNEE = "assignee";
    private static final String COLUMN_ASSIGNEES = "assignees"; // Column mới cho nhiều assignees
    private static final String COLUMN_IS_COMPLETED = "is_completed";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final Gson gson = new Gson();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DEADLINE + " TEXT,"
                + COLUMN_CREATED_TIME + " TEXT,"
                + COLUMN_CREATOR + " TEXT,"
                + COLUMN_ASSIGNEE + " TEXT,"
                + COLUMN_ASSIGNEES + " TEXT," // JSON string cho danh sách assignees
                + COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Thêm column assignees cho version 2
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN " + COLUMN_ASSIGNEES + " TEXT");
        }
    }

    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, task.getId());
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DEADLINE, task.getDeadline() != null ? dateFormat.format(task.getDeadline()) : null);
        values.put(COLUMN_CREATED_TIME, dateFormat.format(task.getCreatedTime()));
        values.put(COLUMN_CREATOR, task.getCreator());
        values.put(COLUMN_ASSIGNEE, task.getAssignee());
        values.put(COLUMN_ASSIGNEES, gson.toJson(task.getAssignees())); // Convert list to JSON
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result;
    }

    public boolean updateTaskStatus(String taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);

        int result = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{taskId});
        db.close();

        return result > 0;
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " ORDER BY " + COLUMN_CREATED_TIME + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));

                String deadlineStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE));
                if (deadlineStr != null) {
                    try {
                        task.setDeadline(dateFormat.parse(deadlineStr));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                String createdTimeStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_TIME));
                try {
                    task.setCreatedTime(dateFormat.parse(createdTimeStr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                task.setCreator(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR)));
                task.setAssignee(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASSIGNEE)));

                // Parse assignees JSON
                String assigneesJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASSIGNEES));
                if (assigneesJson != null && !assigneesJson.isEmpty()) {
                    try {
                        Type listType = new TypeToken<List<Contact>>(){}.getType();
                        List<Contact> assignees = gson.fromJson(assigneesJson, listType);
                        task.setAssignees(assignees);
                    } catch (Exception e) {
                        e.printStackTrace();
                        task.setAssignees(new ArrayList<>());
                    }
                } else {
                    task.setAssignees(new ArrayList<>());
                }

                task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1);

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DEADLINE, task.getDeadline() != null ? dateFormat.format(task.getDeadline()) : null);
        values.put(COLUMN_CREATOR, task.getCreator());
        values.put(COLUMN_ASSIGNEE, task.getAssignee());
        values.put(COLUMN_ASSIGNEES, gson.toJson(task.getAssignees())); // Update assignees JSON
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        int result = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{task.getId()});
        db.close();
        return result;
    }

    public void deleteTask(String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{taskId});
        db.close();
    }

    public boolean deleteTasks(List<String> taskIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = true;

        try {
            db.beginTransaction();
            for (String taskId : taskIds) {
                int result = db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{taskId});
                if (result == 0) {
                    success = false;
                    break;
                }
            }
            if (success) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return success;
    }
}