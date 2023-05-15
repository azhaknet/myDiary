package ir.derasat.mydiary;

import android.text.TextUtils;

import java.util.Date;

public class Diary {

    private int id;
    private String title;
    private Date creationDate;
    private String category;
    private String tags;
    private String contents;

    private String[] views;

    public Diary() {
        creationDate = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String[] getViews() {
        return views;
    }


    public void setViews(String[] views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return title;
    }

    private String arrayToString(String[] arr) {
        return TextUtils.join(",", arr);
    }
}
