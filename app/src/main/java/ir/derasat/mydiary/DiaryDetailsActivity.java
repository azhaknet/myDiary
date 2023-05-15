package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.SimpleDateFormat;

public class DiaryDetailsActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView creationDateTextView;
    private TextView categoryTextView;
    private TextView tagsTextView;
    private LinearLayout contents;
    private Button editBtn;
    private Button deleteBtn;
    private int diaryId;
    private DiariesDatabaseHelper dbHelper;
    FrameLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_details);

        titleTextView = findViewById(R.id.text_view_title);
        creationDateTextView = findViewById(R.id.creation_date_text_view);
        categoryTextView = findViewById(R.id.category_text_view);
        tagsTextView = findViewById(R.id.tags_text_view);
        contents = findViewById(R.id.detailLay);
        editBtn=findViewById(R.id.button_edit);
        editBtn.setOnClickListener(this::onEditButtonClick);
        deleteBtn=findViewById(R.id.button_delete);
        deleteBtn.setOnClickListener(this::onDeleteButtonClick);
        dbHelper = new DiariesDatabaseHelper(this);

        diaryId = getIntent().getIntExtra("diaryId", -1);
        if (diaryId == -1) {
            finish();
        }

        Diary diary = dbHelper.getNoteById(diaryId);

        titleTextView.setText(diary.getTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        creationDateTextView.setText(dateFormat.format(diary.getCreationDate()));
        categoryTextView.setText(diary.getCategory());
        tagsTextView.setText(diary.getTags());
        String[] views =diary.getViews();
         layoutParams= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 620);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        contentLoader(views);

    }
    private void contentLoader(String[] views) {
        for (int i = 0; i < views.length; i++) {
            FrameLayout fm=new FrameLayout(DiaryDetailsActivity.this);
            String[] view = views[i].split("!@#");
            switch (view[0]) {
                case "IV":

                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(StringToBitMap(view[1]));
                    imageView.setBackgroundResource(R.drawable.player_bk);
                    imageView.setPadding(30,20,30,20);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(layoutParams);

                    fm.addView(imageView);
                    if (i!=views.length-1 && views[i+1].split("!@#")[0].equals("TV")) {
                        TextView cpC = new TextView(DiaryDetailsActivity.this);
                        cpC.setText( views[i+1].split("!@#")[1]);
                        cpC.setGravity(Gravity.CENTER);
                        cpC.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        cpC.setTextSize(12);
                        cpC.setBackgroundResource(R.drawable.cap_bk);
                        fm.addView(cpC, new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.BOTTOM | Gravity.CENTER));
                    }
                    contents.addView(fm);
                    break;
                case "AV":

                    AudioPlayerView faudioPlayer = new AudioPlayerView(DiaryDetailsActivity.this);
                    faudioPlayer.setAudioUri(Uri.parse(view[1]));
                    fm.addView(faudioPlayer);
                    if (i!=views.length-1 && views[i+1].split("!@#")[0].equals("TV")) {
                        TextView cpC = new TextView(DiaryDetailsActivity.this);
                        cpC.setText( views[i+1].split("!@#")[1]);
                        cpC.setGravity(Gravity.CENTER);
                        cpC.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        cpC.setTextSize(12);
                        cpC.setBackgroundResource(R.drawable.cap_bk);
                        fm.addView(cpC, new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.BOTTOM | Gravity.CENTER));
                    }
                    contents.addView(fm);
                    break;
                case "VV":
                    PlayerView fplayerView = new PlayerView(DiaryDetailsActivity.this);
                    fplayerView.setLayoutParams(layoutParams);
                    fplayerView.setUseController(true);
                    fplayerView.setControllerAutoShow(true);
                    fplayerView.setControllerHideOnTouch(true);
                    fplayerView.setBackgroundResource(R.drawable.vplayer_bk);
                    fplayerView.setPadding(30,20,30,20);

                    fplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                    ExoPlayer player = new ExoPlayer.Builder(DiaryDetailsActivity.this).build();
                    player.setMediaItem(MediaItem.fromUri(Uri.parse(view[1])));
                    player.prepare();
                    fplayerView.setPlayer(player);
                    fm.addView(fplayerView);
                    if (i!=views.length-1 && views[i+1].split("!@#")[0].equals("TV")) {
                        TextView cpC = new TextView(DiaryDetailsActivity.this);
                        cpC.setText( views[i+1].split("!@#")[1]);
                        cpC.setGravity(Gravity.CENTER);
                        cpC.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        cpC.setTextSize(12);
                        cpC.setBackgroundResource(R.drawable.cap_bk);
                        fm.addView(cpC, new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.BOTTOM | Gravity.CENTER));
                    }
                    contents.addView(fm);
                    break;
                case "ET":
                    TextView et = new TextView(this);
                    et.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                    et.setSingleLine(false);
                    et.setTextSize(18);

                    et.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.bottomMargin=20;
                    layoutParams.topMargin=20;
                    et.setLayoutParams(layoutParams);


                    if (view.length==2)
                        et.setText(view[1]);
                    contents.addView(et);
                    break;

            }
        }
    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void onEditButtonClick(View view) {
        Intent intent = new Intent(DiaryDetailsActivity.this, DiaryEditActivity.class);
        intent.putExtra("diaryId", diaryId);
        startActivity(intent);
    }

    public void onDeleteButtonClick(View view) {
        dbHelper.deleteNoteById(diaryId);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}