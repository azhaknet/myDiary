package ir.derasat.mydiary;

import static ir.derasat.mydiary.LockActivity.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.SupportedLanguagesListener;
import net.gotev.speech.UnsupportedReason;
import net.gotev.speech.ui.SpeechProgressView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import ir.hamsaa.persiandatepicker.util.PersianCalendarUtils;

public class DiaryEditActivity extends AppCompatActivity implements SpeechDelegate {

    private EditText titleEditText;
    private EditText categoryEditText;
    private EditText tagsEditText;
    private Spinner moodSpinner;
    private LinearLayout contentsEditView;

    private Button saveBtn;
    private int diaryId;
    private DiariesDatabaseHelper dbHelper;
    private EditText editText;
    LinearLayout caption;
    int reqCode;
    private final int PERMISSION_RECORD_AUDIO_REQUEST = 8;

    FrameLayout.LayoutParams layoutParams;
    private ImageButton button,lang;
    private EditText text;
    private SpeechProgressView progress;
    private LinearLayout linearLayout;
    private PersianDatePickerDialog picker;

    private Date date;

    private static final String TAG = "TextClassification";
    private TextClassificationHelper client;
    private TextView resultTextView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_edit);

        titleEditText = findViewById(R.id.edit_text_title);
        categoryEditText = findViewById(R.id.category_edit_text);
        tagsEditText = findViewById(R.id.tags_edit_text);
        moodSpinner = findViewById(R.id.mood_spinner);
        List<String> moods = new ArrayList<>();
        moods.add(0, "Natural");
        moods.add(1, "Positive");
        moods.add(2, "Negative");
        ArrayAdapter<String> adapterMood = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moods);
        adapterMood.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapterMood);
        contentsEditView = findViewById(R.id.cLayout);
        saveBtn= findViewById(R.id.button_save);
        saveBtn.setOnClickListener(this::onSaveButtonClick);
        dbHelper = new DiariesDatabaseHelper(this);



        Log.v(TAG, "onCreate");

        client = new TextClassificationHelper(getApplicationContext());
        handler = new Handler();



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 620);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        diaryId = getIntent().getIntExtra("diaryId", -1);

        if (diaryId != -1) {
            Diary diary = dbHelper.getDiaryById(diaryId);
            titleEditText.setText(diary.getTitle());
            categoryEditText.setText(diary.getCategory());
            tagsEditText.setText(diary.getTags());
            moodSpinner.setSelection(diary.getMood());
            //contentsEditView.setText(diary.getContents());
            contentLoader();
        }
        else {addEditText();}

        caption = findViewById(R.id.captionLay);
        caption.setVisibility(View.GONE);
        contentsEditView.setPadding(20,20,20,20);
        editText=findViewById(R.id.editTextText);
        ImageButton dataPicker=findViewById(R.id.date_piker);

        ImageButton signatureButton = findViewById(R.id.signPick);
        signatureButton.setOnClickListener(v -> {
            Intent intent = new Intent(DiaryEditActivity.this, SignActivity.class);
            startActivityForResult(intent, SIGNATURE_REQUEST_CODE);
        });

        dataPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker = new PersianDatePickerDialog(DiaryEditActivity.this)
                        .setPositiveButtonString("باشه")
                        .setNegativeButton("بیخیال")
                        .setTodayButton("امروز")
                        .setTodayButtonVisible(true)
                        .setMinYear(1300)
                        .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                        .setInitDate(PersianDatePickerDialog.THIS_YEAR, PersianDatePickerDialog.THIS_MONTH, PersianDatePickerDialog.THIS_DAY)
                        .setActionTextColor(Color.GRAY)
                        .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                        .setShowInBottomSheet(true)
                        .setListener(new PersianPickerListener() {
                            @Override
                            public void onDateSelected(@NotNull PersianPickerDate persianPickerDate) {
                                Log.d(TAG, "onDateSelected: " + persianPickerDate.getTimestamp());//675930448000
                                Log.d(TAG, "onDateSelected: " + persianPickerDate.getGregorianDate());//Mon Jun 03 10:57:28 GMT+04:30 1991
                                Log.d(TAG, "onDateSelected: " + persianPickerDate.getPersianLongDate());// دوشنبه  13  خرداد  1370
                                Log.d(TAG, "onDateSelected: " + persianPickerDate.getPersianMonthName());//خرداد
                                Log.d(TAG, "onDateSelected: " + PersianCalendarUtils.isPersianLeapYear(persianPickerDate.getPersianYear()));//true
                                Toast.makeText(DiaryEditActivity.this, persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay(), Toast.LENGTH_SHORT).show();
                                date=persianPickerDate.getGregorianDate();
                            }

                            @Override
                            public void onDismissed() {

                            }
                        });

                picker.show();
            }
        });


        ImageButton btnv= findViewById(R.id.videoPick);

        btnv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchVideoPicker();
                //addMediaView(PICK_VIDEO_REQUEST_CODE,selectedVideoUri,editText.getText().toString());

            }
        });

        ImageButton btna= findViewById(R.id.audioPick);

        btna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAudioPicker();
                //addMediaView(PICK_AUDIO_REQUEST_CODE,selectedAudioUri,editText.getText().toString());

            }
        });
        ImageButton btnr= findViewById(R.id.recordVoice);

        btnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DiaryEditActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission at runtime
                    ActivityCompat.requestPermissions(DiaryEditActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
                }
                else {
                    Intent intent = new Intent(DiaryEditActivity.this, RecordActivity.class);
                    startActivityForResult(intent, RECORD_AUDIO_REQUEST_CODE);
                }

            }
        });
        ImageButton btni=findViewById(R.id.imagePick);

        btni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchImagePicker();

            }
        });
        ImageButton btnT=findViewById(R.id.addText);

        btnT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEditText();

            }
        });
        ImageButton btnc=findViewById(R.id.takeCamera);

        btnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });
        button=findViewById(R.id.sttEngine);
        lang=findViewById(R.id.langButton);






        Button ok= findViewById(R.id.button6);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addMediaView(PICK_IMAGE_REQUEST_CODE,selectedImageUri,editText.getText().toString());
                /*LinearLayout.LayoutParams flp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                flp.gravity=Gravity.CENTER;*/
                FrameLayout frameLayout = new FrameLayout(DiaryEditActivity.this);
                //frameLayout.setLayoutParams(flp);
                ImageButton del=new ImageButton(DiaryEditActivity.this);
                del.setImageResource(R.drawable.delete_btn);
                del.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                del.setVisibility(View.GONE);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contentsEditView.removeView((ViewGroup)v.getParent());
                    }
                });

                switch (reqCode) {
                    case IMAGE_CAPTURE_REQUEST_CODE:
                        // Get the selected image URI
                        ImageView cv = new ImageView(DiaryEditActivity.this);
                        cv.setImageBitmap(camBitmap);
                        cv.setTag(saveBitmapToCache(camBitmap,"image.jpg"));

                        cv.setBackgroundResource(R.drawable.player_bk);
                        cv.setPadding(20,20,20,20);
                        cv.setAdjustViewBounds(true);


                        cv.setLayoutParams(layoutParams);
                        cv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                del.setVisibility(View.VISIBLE);
                                return false;
                            }
                        });

                        TextView cpC=new TextView(DiaryEditActivity.this);
                        cpC.setText(editText.getText().toString());
                        cpC.setGravity(Gravity.CENTER);
                        cpC.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        cpC.setTextSize(12);
                        cpC.setBackgroundResource(R.drawable.cap_bk);
                        frameLayout.addView(cv);
                        if (!cpC.getText().toString().equals(""))
                            frameLayout.addView(cpC, new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.BOTTOM | Gravity.CENTER));
                        contentsEditView.addView(frameLayout);
                        addEditText();

                        // Do something with the selected image
                        break;
                    case PICK_IMAGE_REQUEST_CODE:
                        // Get the selected image URI
                        ImageView iv = new ImageView(DiaryEditActivity.this);
                        iv.setImageURI(selectedImageUri);
                        iv.setTag(saveBitmapToCache(((BitmapDrawable)iv.getDrawable()).getBitmap(),"image.jpg"));

                        iv.setBackgroundResource(R.drawable.player_bk);
                        iv.setPadding(20,20,20,20);
                        iv.setAdjustViewBounds(true);


                        iv.setLayoutParams(layoutParams);
                        iv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                del.setVisibility(View.VISIBLE);
                                return false;
                            }
                        });

                        TextView cpI=new TextView(DiaryEditActivity.this);
                        cpI.setText(editText.getText().toString());
                        cpI.setGravity(Gravity.CENTER);
                        cpI.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        cpI.setTextSize(12);

                        frameLayout.addView(iv);
                        if (!cpI.getText().toString().equals("")) {
                            cpI.setBackgroundResource(R.drawable.cap_bk);
                            frameLayout.addView(cpI, new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.BOTTOM | Gravity.CENTER));
                        }
                        contentsEditView.addView(frameLayout);
                        addEditText();

                        // Do something with the selected image
                        break;

                    case PICK_VIDEO_REQUEST_CODE:
                        // Get the selected video URI

                        PlayerView playerView = new PlayerView(DiaryEditActivity.this);
                        playerView.setLayoutParams(layoutParams);
                        playerView.setUseController(true);
                        playerView.setControllerAutoShow(true);
                        playerView.setControllerHideOnTouch(true);
                        playerView.setBackgroundResource(R.drawable.vplayer_bk);
                        playerView.setPadding(20,20,20,20);

                        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                        ExoPlayer player = new ExoPlayer.Builder(DiaryEditActivity.this).build();
                        player.setMediaItem(MediaItem.fromUri(selectedVideoUri));
                        playerView.setTag(getRealPathVFromURI(DiaryEditActivity.this,selectedVideoUri));
                        player.prepare();
                        playerView.setPlayer(player);
                        playerView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                del.setVisibility(View.VISIBLE);
                                return false;
                            }
                        });
                        TextView cpV=new TextView(DiaryEditActivity.this);
                        cpV.setText(editText.getText().toString());
                        cpV.setGravity(Gravity.CENTER);
                        cpV.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        cpV.setTextSize(12);
                        cpV.setBackgroundResource(R.drawable.cap_bk);

                        frameLayout.addView(playerView);
                        if (!cpV.getText().toString().equals(""))
                            frameLayout.addView(cpV, new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.BOTTOM | Gravity.CENTER));
                        contentsEditView.addView(frameLayout);

                        addEditText();

                        // Do something with the selected video
                        break;
                    case PICK_AUDIO_REQUEST_CODE:
                        // Get the selected audio URI
                        AudioPlayerView audioPlayer = new AudioPlayerView(DiaryEditActivity.this);
                        audioPlayer.setAudioUri(selectedAudioUri);
                        audioPlayer.setTag(selectedAudioUri);
                        audioPlayer.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                del.setVisibility(View.VISIBLE);
                                return false;
                            }
                        });

                        TextView cpA=new TextView(DiaryEditActivity.this);
                        cpA.setText(editText.getText().toString());
                        cpA.setGravity(Gravity.CENTER);
                        cpA.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        cpA.setTextSize(12);
                        cpA.setBackgroundResource(R.drawable.cap_bk);
                        frameLayout.addView(audioPlayer);
                        if (!cpA.getText().toString().equals(""))
                            frameLayout.addView(cpA, new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.BOTTOM | Gravity.CENTER));
                        contentsEditView.addView(frameLayout);
                        addEditText();

                        // Do something with the selected audio
                        break;
                    case RECORD_AUDIO_REQUEST_CODE:
                        // Get the selected audio URI
                        AudioPlayerView rAudioPlayer = new AudioPlayerView(DiaryEditActivity.this);
                        rAudioPlayer.setAudioUri(Uri.parse(recordFile));
                        rAudioPlayer.setTag(recordFile);
                        rAudioPlayer.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                del.setVisibility(View.VISIBLE);
                                return false;
                            }
                        });

                        TextView cpR=new TextView(DiaryEditActivity.this);
                        cpR.setText(editText.getText().toString());
                        cpR.setGravity(Gravity.CENTER);
                        cpR.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        cpR.setTextSize(12);
                        cpR.setBackgroundResource(R.drawable.cap_bk);
                        frameLayout.addView(rAudioPlayer);
                        if (!cpR.getText().toString().equals(""))
                            frameLayout.addView(cpR, new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.BOTTOM | Gravity.CENTER));
                        contentsEditView.addView(frameLayout);
                        addEditText();

                        // Do something with the selected audio
                        break;
                    case SIGNATURE_REQUEST_CODE:
                        // Get the selected audio URI
                        ImageView si = new ImageView(DiaryEditActivity.this);
                        si.setImageBitmap(signatureBitmap);

                        si.setTag(saveBitmapToCache(signatureBitmap,"signature.png"));
                        si.setBackgroundResource(R.drawable.player_bk);
                        si.setPadding(40,40,40,40);
                        si.setAdjustViewBounds(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            si.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F9F9F9")));
                        }

                        si.setLayoutParams(layoutParams);
                        si.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                del.setVisibility(View.VISIBLE);



                                return false;
                            }
                        });

                        TextView cps=new TextView(DiaryEditActivity.this);
                        cps.setText(editText.getText().toString());
                        cps.setGravity(Gravity.CENTER);
                        cps.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        cps.setTextSize(12);
                        cps.setBackgroundResource(R.drawable.cap_bk);
                        frameLayout.addView(si);
                        if (!cps.getText().toString().equals(""))
                            frameLayout.addView(cps, new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.BOTTOM | Gravity.CENTER));
                        contentsEditView.addView(frameLayout);
                        addEditText();

                        // Do something with the selected audio
                        break;
                }
                frameLayout.addView(del, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM | Gravity.CENTER));
                caption.setVisibility(View.GONE);
            }
        });

        Speech.init(this, getPackageName());
        Speech.getInstance().setGetPartialResults(true);

        linearLayout = findViewById(R.id.linearLayoutsv);

        button.setOnClickListener(view -> onButtonClick());

        text = (EditText) contentsEditView.getFocusedChild();

        progress = findViewById(R.id.speechView);
        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetSpeechToTextLanguage();
            }
        });

        int[] colors = {
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.darker_gray),
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.holo_orange_dark),
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
        };
        progress.setColors(colors);
       // contentsEditView= (LinearLayout) getIntent().getSerializableExtra("");

    }

    private void contentLoader() {
        String[] views = dbHelper.getDiaryById(diaryId).getViews();
        for (int i = 0; i < views.length; i++) {
            FrameLayout fm=new FrameLayout(DiaryEditActivity.this);
            ImageButton fdel=new ImageButton(DiaryEditActivity.this);
            fdel.setImageResource(R.drawable.delete_btn);
            fdel.setVisibility(View.GONE);
            fdel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contentsEditView.removeView((ViewGroup)v.getParent());
                }
            });
            String[] view = views[i].split("!@#");
            switch (view[0]) {
                case "IV":

                    ImageView imageView = new ImageView(this);
                    //imageView.setImageBitmap(StringToBitMap(view[1]));
                    imageView.setImageURI(Uri.parse(view[1]));
                    imageView.setTag(view[1]);
                    imageView.setBackgroundResource(R.drawable.player_bk);
                    imageView.setPadding(20,20,20,20);
                    imageView.setAdjustViewBounds(true);


                    imageView.setLayoutParams(layoutParams);
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            fdel.setVisibility(View.VISIBLE);
                            return false;
                        }
                    });
                    fm.addView(imageView);
                    if (i!=views.length-1 && views[i+1].split("!@#")[0].equals("TV")) {
                        TextView cpC = new TextView(DiaryEditActivity.this);
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
                    contentsEditView.addView(fm);
                    break;
                case "AV":

                    AudioPlayerView faudioPlayer = new AudioPlayerView(DiaryEditActivity.this);
                    faudioPlayer.setAudioUri(Uri.parse(view[1]));
                    faudioPlayer.setTag(view[1]);

                    faudioPlayer.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            fdel.setVisibility(View.VISIBLE);
                            return false;
                        }
                    });

                    fm.addView(faudioPlayer);
                    if (i!=views.length-1 && views[i+1].split("!@#")[0].equals("TV")) {
                        TextView cpC = new TextView(DiaryEditActivity.this);
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
                    contentsEditView.addView(fm);
                    break;
                case "VV":
                    PlayerView fplayerView = new PlayerView(DiaryEditActivity.this);
                    fplayerView.setLayoutParams(layoutParams);

                    fplayerView.setUseController(true);
                    fplayerView.setControllerAutoShow(true);
                    fplayerView.setControllerHideOnTouch(true);
                    fplayerView.setBackgroundResource(R.drawable.vplayer_bk);
                    fplayerView.setPadding(20,20,20,20);


                    fplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                    ExoPlayer player = new ExoPlayer.Builder(DiaryEditActivity.this).build();
                    player.setMediaItem(MediaItem.fromUri(Uri.parse(view[1])));
                    fplayerView.setTag(view[1]);
                    player.prepare();
                    fplayerView.setPlayer(player);
                    fplayerView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            fdel.setVisibility(View.VISIBLE);
                            return false;
                        }
                    });
                    fm.addView(fplayerView);
                    if (i!=views.length-1 && views[i+1].split("!@#")[0].equals("TV")) {
                        TextView cpC = new TextView(DiaryEditActivity.this);
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


                    contentsEditView.addView(fm);
                    break;
                case "ET":
                    EditText et = new EditText(this);
                    et.setBackgroundResource(android.R.color.transparent);
                    et.setHint("Write any things...");
                    et.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    int finalI = i;
                    et.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            //You can identify which key pressed by checking keyCode value with KeyEvent.KEYCODE_
                            if(keyCode == KeyEvent.KEYCODE_DEL) {
                                //this is for backspace
                                if (finalI !=0){
                                    contentsEditView.removeView(v);
                                }
                            }
                            if (keyCode==KeyEvent.KEYCODE_ENTER) {
                                resultTextView = new TextView(contentsEditView.getContext());
                                classify(et.getText().toString());
                            }
                            return false;
                        }
                    });

                    et.setSingleLine(false);

                    et.setGravity(Gravity.CENTER);
                    et.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (view.length==2)
                        et.setText(view[1]);
                    contentsEditView.addView(et);
                    break;

            }
            fm.addView(fdel, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM | Gravity.CENTER));
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        handler.post(
                () -> {
                    client.load();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        handler.post(
                () -> {
                    client.unload();
                });
    }

    /** Send input text to TextClassificationClient and get the classify messages. */
    private void classify(final String text) {
        handler.post(
                () -> {
                    // Run text classification with TF Lite.
                    List<TextClassificationHelper.Result> results = client.classify(text);

                    // Show classification result on screen
                    showResult(text, results);
                });
    }

    /** Show classification result on the screen. */
    private void showResult(final String inputText, final List<TextClassificationHelper.Result> results) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread(
                () -> {
                    if (lastSent!=0) {
                        contentsEditView.removeViewAt(lastSent);
                    }
                    /*String textToShow = "Input: " + inputText + "\nOutput:\n";
                    for (int i = 0; i < results.size(); i++) {
                        TextClassificationHelper.Result result = results.get(i);
                        textToShow +=
                                String.format("    %s: %s\n", result.getTitle(), result.getConfidence());
                    }
                    textToShow += "---------\n";*/

                    // Append the result to the UI.
                    resultTextView.setText(results.get(0).getTitle()+": "+results.get(0).getConfidence());


                    // Clear the input text.
                    //inputEditText.getText().clear();

                    if (!resultTextView.getText().equals("")) {
                        contentsEditView.addView(resultTextView);
                        lastSent = contentsEditView.getChildCount() - 1;
                    }
                    // Scroll to the bottom to show latest entry's classification result.
                    //scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                });
    }
    int lastSent=0;



    public Uri saveBitmapToCache(Bitmap bitmap, String baseFileName) {
        // Get reference to the cache directory
        File cacheDir = getCacheDir();

        String fileName = generateUniqueFileName(baseFileName);

        // Create file object with desired path and file name
        File file = new File(cacheDir, fileName);

        try {
            // Create output stream
            FileOutputStream outputStream = new FileOutputStream(file);

            // Compress bitmap to PNG and write to output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            // Close output stream
            return FileProvider.getUriForFile(this, getPackageName()+ ".fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String generateUniqueFileName(String baseFileName) {
        // Get the current timestamp in milliseconds
        long timestamp = System.currentTimeMillis();

        // Generate a random number between 0 and 9999
        int randomNumber = new Random().nextInt(10000);

        // Combine the timestamp and random number with the base file name
        return baseFileName + "_" + timestamp + "_" + randomNumber + ".jpg";
    }
    public static String getRealPathVFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public static String getRealPathIFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private void addEditText(){
        EditText ET =new EditText(DiaryEditActivity.this);

        ET.setBackgroundResource(android.R.color.transparent);

        ET.setHint("Write any things...");
        ET.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        ET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed by checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL && ET.getText().toString().equals("")) {
                    //this is for backspace
                    if (contentsEditView.getChildCount() !=0 ) {
                        contentsEditView.removeView(v);
                    }
                }
                if (keyCode==KeyEvent.KEYCODE_ENTER) {
                    resultTextView = new TextView(contentsEditView.getContext());
                    classify(ET.getText().toString());
                }
                return false;
            }
        });

        ET.setSingleLine(false);

        ET.setGravity(Gravity.CENTER);
        ET.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        contentsEditView.addView(ET);

        ET.requestFocus();
    }
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private static final int PICK_VIDEO_REQUEST_CODE = 2;
    private static final int PICK_AUDIO_REQUEST_CODE = 3;
    private static final int SIGNATURE_REQUEST_CODE = 5;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 4;
    private static final int RECORD_AUDIO_REQUEST_CODE=6;
    private static final int IMAGE_CAPTURE_REQUEST_CODE =7;

    // Launch the image picker when the user taps a button

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission at runtime
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, IMAGE_CAPTURE_REQUEST_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
            }
        }
    }

    private void launchImagePicker() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }else {
                // Permission is already granted
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }

        }else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission at runtime
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            } else {
                // Permission is already granted
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        }
    }

    // Launch the video picker when the user taps a button
    private void launchVideoPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_VIDEO}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }else {
                // Permission is already granted
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE);
            }

        }else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission at runtime
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }
            else {
                // Permission is already granted
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE);
            }
        }
    }

    // Launch the audio picker when the user taps a button
    private void launchAudioPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_AUDIO}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }else {
                // Permission is already granted
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_AUDIO_REQUEST_CODE);
            }

        }else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);

            } else {
                // Permission is already granted
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_AUDIO_REQUEST_CODE);
            }
        }
    }
    Uri selectedImageUri;
    Uri selectedVideoUri;
    Uri selectedAudioUri;
    Bitmap signatureBitmap;
    String recordFile;

    Bitmap camBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case IMAGE_CAPTURE_REQUEST_CODE:
                    // Get the selected image URI

                    Bundle extras = data.getExtras();
                    camBitmap = (Bitmap) extras.get("data");
                    reqCode=IMAGE_CAPTURE_REQUEST_CODE;

                    // Do something with the selected image
                    break;
                case PICK_IMAGE_REQUEST_CODE:
                    // Get the selected image URI

                    selectedImageUri = data.getData();
                    reqCode=PICK_IMAGE_REQUEST_CODE;

                    // Do something with the selected image
                    break;
                case PICK_VIDEO_REQUEST_CODE:
                    // Get the selected video URI

                    selectedVideoUri = data.getData();
                    reqCode=PICK_VIDEO_REQUEST_CODE;

                    // Do something with the selected video
                    break;
                case PICK_AUDIO_REQUEST_CODE:
                    // Get the selected audio URI
                    selectedAudioUri = data.getData();
                    reqCode=PICK_AUDIO_REQUEST_CODE;

                    // Do something with the selected audio
                    break;
                case RECORD_AUDIO_REQUEST_CODE:
                    // Get the selected audio URI
                    recordFile = data.getStringExtra(RecordActivity.EXTRA_OUTPUT_FILE);
                    reqCode=RECORD_AUDIO_REQUEST_CODE;

                    // Do something with the selected audio
                    break;
                case SIGNATURE_REQUEST_CODE:
                    // Get the selected audio URI
                    signatureBitmap = StringToBitMap(data.getStringExtra("signature"));
                    reqCode=SIGNATURE_REQUEST_CODE;



                    // Do something with the selected audio
                    break;
            }
            caption.setVisibility(View.VISIBLE);
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

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_RECORD_AUDIO_REQUEST && grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onRecordAudioPermissionGranted();
            } else {
                // permission denied, boo!
                Toast.makeText(DiaryEditActivity.this, "permission_required", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, launch the appropriate picker
            switch (requestCode) {
                case PICK_IMAGE_REQUEST_CODE:
                    launchImagePicker();
                    break;
                case PICK_VIDEO_REQUEST_CODE:
                    launchVideoPicker();
                    break;
                case PICK_AUDIO_REQUEST_CODE:
                    launchAudioPicker();
                    break;

            }
        } else {
            //Toast.makeText(this, "Cannot access media files without permission", Toast.LENGTH_SHORT).show();
        }
    }
    private void onSetSpeechToTextLanguage() {
        Speech.getInstance().getSupportedSpeechToTextLanguages(new SupportedLanguagesListener() {
            @Override
            public void onSupportedLanguages(List<String> supportedLanguages) {
                CharSequence[] items = new CharSequence[supportedLanguages.size()];
                supportedLanguages.toArray(items);

                new AlertDialog.Builder(DiaryEditActivity.this)
                        .setTitle("Current language: " + Speech.getInstance().getSpeechToTextLanguage())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Locale locale;

                                if (Build.VERSION.SDK_INT >= 21) {
                                    locale = Locale.forLanguageTag(supportedLanguages.get(i));
                                } else {
                                    String[] langParts = supportedLanguages.get(i).split("-");

                                    if (langParts.length >= 2) {
                                        locale = new Locale(langParts[0], langParts[1]);
                                    } else {
                                        locale = new Locale(langParts[0]);
                                    }
                                }

                                Speech.getInstance().setLocale(locale);
                                Toast.makeText(DiaryEditActivity.this, "Selected: " + items[i], Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("Cancel", null)
                        .create()
                        .show();
            }

            @Override
            public void onNotSupported(UnsupportedReason reason) {
                switch (reason) {
                    case GOOGLE_APP_NOT_FOUND:
                        showSpeechNotSupportedDialog();
                        break;

                    case EMPTY_SUPPORTED_LANGUAGES:
                        new AlertDialog.Builder(DiaryEditActivity.this)
                                .setTitle("set_stt_langs")
                                .setMessage("no_langs")
                                .setPositiveButton("OK", null)
                                .show();
                        break;
                }
            }
        });
    }



    private void onButtonClick() {
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                onRecordAudioPermissionGranted();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD_AUDIO_REQUEST);
            }
        }
    }


    private void onRecordAudioPermissionGranted() {
        text = (EditText) contentsEditView.getFocusedChild();
        button.setBackgroundColor(Color.BLUE);

        linearLayout.setVisibility(View.VISIBLE);


        try {
            Speech.getInstance().startListening(progress, DiaryEditActivity.this);

        } catch (SpeechRecognitionNotAvailable exc) {
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            showEnableGoogleVoiceTyping();
        }
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {
        //Log.d(getClass().getSimpleName(), "Speech recognition rms is now " + value +  "dB");
    }

    @Override
    public void onSpeechResult(String result) {

        button.setBackgroundColor(Color.GRAY);
        linearLayout.setVisibility(View.GONE);

        text.append(" "+result);

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        ((TextView)findViewById(R.id.tempTxt)).setText("");
        for (String partial : results) {
            ((TextView)findViewById(R.id.tempTxt)).append(partial + " ");
        }
    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(DiaryEditActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("speech_not_available")
                .setCancelable(false)
                .setPositiveButton("yes", dialogClickListener)
                .setNegativeButton("no", dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("enable_voice_typing")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }




    public void onSaveButtonClick(View view) {
        String title = titleEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();
        String tags = tagsEditText.getText().toString().trim();
        int mood=moodSpinner.getSelectedItemPosition();
        String contents = "";
        List<String> views= new ArrayList<>();
        for (int i = 0; i < contentsEditView.getChildCount(); i++) {
            View tview = contentsEditView.getChildAt(i);
            if (tview instanceof FrameLayout) {
                String captionV="";
                if (((FrameLayout) tview).getChildAt(1) instanceof TextView) {
                    TextView textView = (TextView) ((FrameLayout) tview).getChildAt(1);
                    captionV = textView.getText().toString();
                }

                tview=((FrameLayout) tview).getChildAt(0);

            if (tview instanceof ImageView) {
                ImageView imageView = (ImageView) tview;
                views.add("IV!@#"+imageView.getTag());
            } else if (tview instanceof PlayerView) {
                PlayerView videoView = (PlayerView) tview;
                views.add("VV!@#"+videoView.getTag());
            } else if (tview instanceof Button) {
                Button btn = (Button) tview;
                views.add("BT!@#"+btn.getText());
            } else if (tview instanceof AudioPlayerView) {
                AudioPlayerView audioPlayerView = (AudioPlayerView) tview;
                views.add("AV!@#" + audioPlayerView.getTag());
            }
            if (!captionV.equals("")) {
                views.add("TV!@#"+captionV);
                contents+=("\n  "+captionV+"  \n");
            }
            }
            else if (tview instanceof EditText) {
                EditText editTexte = (EditText) tview;
                views.add("ET!@#"+editTexte.getText());
                contents+=(" "+editTexte.getText());
            }

        }

        if (title.isEmpty() || category.isEmpty() /*|| contents.isEmpty()*/) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (diaryId == -1) {
            Diary diary;
            if (date == null)
                diary = new Diary();
            else
                diary = new Diary(date);
            diary.setTitle(title);
            diary.setCategory(category);
            diary.setTags(tags);
            diary.setMood(mood);
            diary.setContents(contents);
            diary.setViews(views.toArray(new String[0]));
            dbHelper.addDiary(diary);
        } else {
            Diary diary = dbHelper.getDiaryById(diaryId);
            diary.setTitle(title);
            if (date != null) {
                diary.setCreationDate(date);
            }
            diary.setCategory(category);
            diary.setTags(tags);
            diary.setMood(mood);
            diary.setContents(contents);
            diary.setViews(views.toArray(new String[0]));
            dbHelper.updateDiary(diary);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        Speech.getInstance().shutdown();
    }
}