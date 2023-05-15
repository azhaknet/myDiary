package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class DiaryListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ListView noteListView;
    private ArrayAdapter<Diary> noteAdapter;
    private List<Diary> diaryList = new ArrayList<>();
    private DiariesDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        noteListView = findViewById(R.id.recycler_view);
        noteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, diaryList);
        noteListView.setAdapter(noteAdapter);
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DiaryListActivity.this, DiaryDetailsActivity.class);
                intent.putExtra("diaryId", diaryList.get(position).getId());
                startActivity(intent);
            }

        });
        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNoteList();
    }

    private void updateNoteList() {
        diaryList.clear();
        diaryList.addAll(dbHelper.getAllNotes());
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_settings);
        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            if(item.getItemId()==R.id.action_add_note) {
                Intent intent = new Intent(this, DiaryEditActivity.class);
                startActivity(intent);
                return true;
            }
            else
                return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        noteAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}