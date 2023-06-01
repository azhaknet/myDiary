package ir.derasat.mydiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DiaryListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final int IMPORT_REQUEST_CODE = 0;
    private static final int EXPORT_REQUEST_CODE = 1;
    private ListView DiaryListView;
    private ArrayAdapter<Diary> DiaryAdapter;
    private ArrayAdapter<String> adapterCat;
    private List<Diary> diaryList = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private DiariesDatabaseHelper dbHelper;
    private Spinner mCategorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        DiaryListView = findViewById(R.id.recycler_view);


        DiaryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, diaryList);
        DiaryListView.setAdapter(DiaryAdapter);
        DiaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DiaryListActivity.this, DiaryDetailsActivity.class);
                intent.putExtra("diaryId", diaryList.get(position).getId());
                startActivity(intent);
            }

        });
        DiaryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DiaryListActivity.this, DiaryEditActivity.class);
                intent.putExtra("diaryId", diaryList.get(position).getId());
                startActivity(intent);
                return true;
            }
        });

        dbHelper = new DiariesDatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        mCategorySpinner = findViewById(R.id.category_spinner);

        // Step 4: Populate the spinner with available categories from the database
        categories = new ArrayList<>(dbHelper.getAllCategories());
        categories.add(0, "All Categories");
        adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapterCat);
        mCategorySpinner.setPrompt("Select Category");

        // Step 5: Add an OnItemSelectedListener to filter Diarys by the selected category
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = (String) parent.getItemAtPosition(position);
                // Update your Diary list adapterCat to display Diarys with the selected category
                // You can do this by calling a method on your Diary list adapterCat and passing in the selected category
                diaryList.clear();
                if (category.equals("All Categories")) {
                    diaryList.addAll(dbHelper.getAllDiaries());
                }else
                    diaryList.addAll(dbHelper.getDiariesByCategory(category));
                DiaryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
                diaryList.clear();
                diaryList.addAll(dbHelper.getAllDiaries());
                DiaryAdapter.notifyDataSetChanged();
                if (diaryList.isEmpty()) {
                    Toast.makeText(DiaryListActivity.this, "No Diaries Found", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
            updateDiaryList();
            updateCategoryList();
    }

    private void updateDiaryList() {
        diaryList.clear();
        diaryList.addAll(dbHelper.getAllDiaries());
        DiaryAdapter.notifyDataSetChanged();
    }
    private void updateCategoryList() {
        categories.clear();
        categories.add(0, "All Categories");
        categories.addAll(dbHelper.getAllCategories());
        adapterCat.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diary_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                startActivity(new Intent(DiaryListActivity.this, SearchActivity.class));

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            if(item.getItemId()==R.id.action_add_diary) {
                Intent intent = new Intent(this, DiaryEditActivity.class);
                startActivity(intent);
                return true;
            }else if (item.getItemId() == R.id.action_export) {
                exportDiaries();
                return true;
            } else if (item.getItemId() == R.id.action_import) {
                importDiaries();
                return true;
            }
            return super.onOptionsItemSelected(item);

    }
    private static final int AES_KEY_SIZE = 256;


    private void exportDiaries() {

        BackupManager backupManager = new BackupManager(this);
        backupManager.dataChanged();
        // Show the file picker dialog
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "diaries_backup_" + System.currentTimeMillis() + ".bak");
        startActivityForResult(intent, EXPORT_REQUEST_CODE);
    }

    private void importDiaries() {
        // Show the file picker dialog
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, IMPORT_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == EXPORT_REQUEST_CODE) {
                // Get the URI of the selected file
                Uri selectedFileUri = data.getData();
                Gson gson = new Gson();

                // Get all Diariess from the database
                List<Diary> diariesList = dbHelper.getAllDiaries();

                // Convert the list to a JSON string
                String json = gson.toJson(diariesList);

                showPasswordInputDialog(json, selectedFileUri);

            } else if (requestCode == IMPORT_REQUEST_CODE) {
                // Get the URI of the selected file
                Uri selectedFileUri = data.getData();

                // Load the JSON string from the selected file
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder builder = new StringBuilder();
                    String line = reader.readLine();
                    while (line != null) {
                        builder.append(line);
                        line = reader.readLine();
                    }
                    reader.close();
                    getPasswordFromUser(builder.toString(), selectedFileUri);

                }catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DiaryListActivity.this, "Import failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void getPasswordFromUser(String reader,Uri fileUri) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter password");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String pass = input.getText().toString();

                    String json = AES.decrypt(reader, pass);

                    // Convert the JSON string back to a list of Diarys
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Diary>>() {
                    }.getType();
                    List<Diary> diariesList = gson.fromJson(json, type);

                    dbHelper.restoreData(diariesList);
                    updateDiaryList();
                    updateCategoryList();

                    Toast.makeText(DiaryListActivity.this, "Diaries imported successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DiaryListActivity.this, "Import failed", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void showPasswordInputDialog(String json,Uri selectedFileUri) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter password");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();

                String encryptedJson = null;
                try {
                    encryptedJson = AES.encrypt(json, password);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DiaryListActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Save the JSON string to the selected file
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(selectedFileUri);
                    outputStream.write(encryptedJson.getBytes());
                    outputStream.close();
                    Toast.makeText(DiaryListActivity.this, "Diaries exported successfully", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(DiaryListActivity.this);
                    builder.setTitle("Share Backup File");
                    builder.setMessage("save this file in social medias?");
                    builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showShareDialog(selectedFileUri);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DiaryListActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void showShareDialog(Uri backupFileUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, backupFileUri);
        startActivity(Intent.createChooser(shareIntent, "Share backup file"));
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        DiaryAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}