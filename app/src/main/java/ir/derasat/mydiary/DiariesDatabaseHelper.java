package ir.derasat.mydiary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiariesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "diaries.db";
    private static final int DATABASE_VERSION = 1;


    public DiariesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE diaries (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, creation_date INTEGER, category TEXT, tags TEXT,mood INTEGER, contents TEXT,views TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not used, as this is the first version of the database.
    }

    public void addDiary(Diary diary) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", diary.getTitle());
        values.put("creation_date", diary.getCreationDate().getTime());
        values.put("category", diary.getCategory());
        values.put("tags", diary.getTags());
        values.put("mood", diary.getMood());
        values.put("contents", diary.getContents());
        values.put("views", TextUtils.join(",", diary.getViews()));
        db.insert("diaries", null, values);
        db.close();
    }

    public void updateDiary(Diary diary) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", diary.getTitle());
        values.put("creation_date", diary.getCreationDate().getTime());
        values.put("category", diary.getCategory());
        values.put("tags", diary.getTags());
        values.put("mood", diary.getMood());
        values.put("contents", diary.getContents());
        values.put("views", TextUtils.join(",", diary.getViews()));
        db.update("diaries", values, "_id=?", new String[]{String.valueOf(diary.getId())});
        db.close();

    }

    public void deleteDiaryById(int diaryId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("diaries", "_id=?", new String[]{String.valueOf(diaryId)});
        db.close();
    }

    public List<Diary> getAllDiaries() {
        List<Diary> diaryList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM diaries ORDER BY creation_date DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Diary diary = new Diary();
                diary.setId(cursor.getInt(0));
                diary.setTitle(cursor.getString(1));
                diary.setCreationDate(new Date(cursor.getLong(2)));
                diary.setCategory(cursor.getString(3));
                diary.setTags(cursor.getString(4));
                diary.setMood(cursor.getInt(5));
                diary.setContents(cursor.getString(6));
                diary.setViews(cursor.getString(7).split(","));
                diaryList.add(diary);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return diaryList;
    }
    public void restoreData(List<Diary> diaries) {
        List<Diary> diaryList = getAllDiaries();
        for (Diary diary : diaries) {
            boolean isNew = true;
            for (Diary diaryP : diaryList) {
                if (diary.getIdentifier().equals(diaryP.getIdentifier())) {
                    diary.setId(diaryP.getId());
                    updateDiary(diary);
                    isNew=false;
                    break;
                }
            }
            if (isNew) {
                addDiary(diary);
            }
        }
    }
    @SuppressLint("Range")
    public List<Diary> searchDiaries(String query) {
        List<Diary> diariesList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM diaries WHERE title LIKE '%" + query + "%' OR contents LIKE '%" + query + "%'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Diary diary = new Diary();
                diary.setId(cursor.getInt(0));
                diary.setTitle(cursor.getString(1));
                diary.setContents(cursor.getString(5));
                diariesList.add(diary);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return diariesList;
    }
    public List<Diary> getDiariesByCategory(String category) {
        List<Diary> diariesList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM diaries WHERE category=?", new String[]{String.valueOf(category)});

        if (cursor.moveToFirst()) {
            do {
                Diary diary = new Diary();
                diary.setId(cursor.getInt(0));
                diary.setTitle(cursor.getString(1));
                diary.setCreationDate(new Date(cursor.getLong(2)));
                diary.setCategory(cursor.getString(3));
                diary.setTags(cursor.getString(4));
                diary.setMood(cursor.getInt(5));
                diary.setContents(cursor.getString(6));
                diary.setViews(cursor.getString(7).split(","));
                diariesList.add(diary);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return diariesList;
    }


    public Diary getDiaryById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM diaries WHERE _id=?", new String[]{String.valueOf(id)});
        Diary diary = null;
        if (cursor.moveToFirst()) {
            diary = new Diary();
            diary.setId(cursor.getInt(0));
            diary.setTitle(cursor.getString(1));
            diary.setCreationDate(new Date(cursor.getLong(2)));
            diary.setCategory(cursor.getString(3));
            diary.setTags(cursor.getString(4));
            diary.setMood(cursor.getInt(5));
            diary.setContents(cursor.getString(6));
            diary.setViews(cursor.getString(7).split(","));
        }
        cursor.close();
        db.close();
        return diary;
    }

    public Set<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM diaries", null);
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }
}