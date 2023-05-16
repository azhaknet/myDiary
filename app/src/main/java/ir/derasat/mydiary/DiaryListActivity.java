package ir.derasat.mydiary;

import androidx.annotation.NonNull;
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

    private ListView DiaryListView;
    private ArrayAdapter<Diary> DiaryAdapter;
    private List<Diary> diaryList = new ArrayList<>();
    private DiariesDatabaseHelper dbHelper;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
            updateDiaryList();
    }

    private void updateDiaryList() {
        diaryList.clear();
        diaryList.addAll(dbHelper.getAllDiaries());
        DiaryAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diary_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_settings);
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
        DiaryAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}