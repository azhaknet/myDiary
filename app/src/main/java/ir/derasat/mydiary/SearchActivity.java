package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ListView searchResultsListView;
    private DiariesDatabaseHelper dbHelper;
    private List<Diary> searchResults;
    private TextView noResultsTextView;
    private ArrayAdapter<Diary> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.edit_text_search);
        noResultsTextView = findViewById(R.id.text_view_no_results);
        searchResultsListView = findViewById(R.id.recycler_view_search_results);
        dbHelper = new DiariesDatabaseHelper(this);
        searchResults = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults);
        searchResultsListView.setAdapter(adapter);
        searchResultsListView.setVisibility(View.GONE);
        noResultsTextView.setVisibility(View.GONE);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                searchDiary(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchResults.size() == 0) {
                    noResultsTextView.setVisibility(View.VISIBLE);
                    searchResultsListView.setVisibility(View.GONE);
                }else {
                    noResultsTextView.setVisibility(View.GONE);
                    searchResultsListView.setVisibility(View.VISIBLE);
                }
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

    private void searchDiary(String query) {
        searchResults.clear();
        searchResults.addAll(dbHelper.searchDiaries(query));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}