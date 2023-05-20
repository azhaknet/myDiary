package ir.derasat.mydiary;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

public class MyBackupAgent extends BackupAgentHelper {

    private static final String DATABASE_NAME = "diaries.db";


    @Override
    public void onCreate() {
        super.onCreate();
        FileBackupHelper helper = new FileBackupHelper(this, "../databases/" + DATABASE_NAME);
        addHelper(DATABASE_NAME, helper);
    }
}