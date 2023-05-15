package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ListView searchResultsListView;
    private DiariesDatabaseHelper dbHelper;
    private List<Diary> searchResults;
    private ArrayAdapter<Diary> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.edit_text_search);
        searchResultsListView = findViewById(R.id.recycler_view_search_results);
        dbHelper = new DiariesDatabaseHelper(this);
        searchResults = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults);
        searchResultsListView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                searchNotes(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Diary selectedDiary = adapter.getItem(position);
                    Intent intent = new Intent(SearchActivity.this, DiaryDetailsActivity.class);
                intent.putExtra("diaryId", selectedDiary.getId());
                startActivity(intent);
            }
        });
    }

    private void searchNotes(String query) {
        searchResults.clear();
        searchResults.addAll(dbHelper.searchNotes(query));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}