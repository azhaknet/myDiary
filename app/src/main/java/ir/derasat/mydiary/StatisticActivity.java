package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jakewharton.threetenabp.AndroidThreeTen;



import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticActivity extends AppCompatActivity {
    private DiaryStatistics diaryStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        DiariesDatabaseHelper dbHelper = new DiariesDatabaseHelper(this);

        // Retrieve list of diaries from database or wherever they are stored
        List<Diary> diaries = dbHelper.getAllDiaries();

        // Create instance of DiaryStatistics and calculate statistics
        AndroidThreeTen.init(this);
        diaryStatistics = new DiaryStatistics();
        diaryStatistics.calculateStatistics(diaries);

        // Use getter methods of DiaryStatistics to retrieve calculated statistics
        int diaryCount = diaryStatistics.getDiaryCount();
        int wordCount = diaryStatistics.getTotalWordCount();
        int aveDiaryLength = diaryStatistics.getAverageDiaryLength();
        int sentenceCount = diaryStatistics.getDiariesSentencesCount();
        float aveSentenceCount = (float) diaryStatistics.getAverageSentencesCount();
        int sentenceLength = diaryStatistics.getDiariesSentencesLength();
        float aveSentenceLength = (float) diaryStatistics.getAverageSentencesLength();
        String mostActiveDay = diaryStatistics.getMostActiveDayOfWeek();
        String mostActiveMonth = diaryStatistics.getMostActiveMonth();
        float aveDiaryCreationRatePerWeek = (float) diaryStatistics.getAverageDiaryCreationRatePerWeek();
        float aveDiaryCreationRatePerMonth = (float) diaryStatistics.getAverageDiaryCreationRatePerMonth();
        String moodScore = diaryStatistics.getMoodScore();
        float stabilityMood = (float) diaryStatistics.getMoodStability()*100;


        Map<String, Double> moodPercent = diaryStatistics.getMoodDistributionPercent();
        ArrayList<PieEntry> mpentries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : moodPercent.entrySet()) {
            mpentries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }
        PieDataSet mpdataSet = new PieDataSet(mpentries, "Mood Percent");
        mpdataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData mpdata = new PieData(mpdataSet);
        PieChart mpPieChart = findViewById(R.id.mood_pie_chart);
        mpPieChart.setCenterText("Moods");
        mpPieChart.setCenterTextSize(20);
        mpPieChart.setUsePercentValues(true);
        mpPieChart.setData(mpdata);
        mpPieChart.setEntryLabelColor(Color.BLACK);
        mpPieChart.setEntryLabelTextSize(12f);
        mpPieChart.getDescription().setEnabled(false);
        mpPieChart.setDrawEntryLabels(true);
        mpPieChart.invalidate();


        Map<String, Double> moodDistributionInWeek = diaryStatistics.getMoodByDayOfWeek();
        int totalWordCount = diaryStatistics.getTotalWordCount();
        String dateRange = diaryStatistics.getDateRange();
        TextView dateRangeTextView = findViewById(R.id.date_range_text_view);
        dateRangeTextView.setText(String.format(Locale.getDefault(), "Date Range: %s", dateRange));

        // Display the statistics in the UI
        TextView diaryGeneralTextView = findViewById(R.id.diary_general_text_view);
        diaryGeneralTextView.setText(String.format(Locale.getDefault(), "Diary Count: %d", diaryCount));
        diaryGeneralTextView.append("\n"+String.format(Locale.getDefault(), "Total Word Count: %d", wordCount));
        diaryGeneralTextView.append("\n"+String.format(Locale.getDefault(), "Average Diary Length(word): %d", aveDiaryLength));
        diaryGeneralTextView.append("\n"+String.format(Locale.getDefault(), "Total Sentence Count: %d", sentenceCount));
        diaryGeneralTextView.append("\n"+String.format(Locale.getDefault(), "Average Diary Sentence Count: %.2f", aveSentenceCount));
        diaryGeneralTextView.append("\n"+String.format(Locale.getDefault(), "Sentence Length: %d", sentenceLength));
        diaryGeneralTextView.append("\n"+String.format(Locale.getDefault(), "Average Diary Sentence Length: %.2f", aveSentenceLength));
        diaryGeneralTextView.append("\n"+ "Most Active Day Of Week: "+mostActiveDay );
        diaryGeneralTextView.append("\n"+ "Most Active Day Of Month: "+ mostActiveMonth);
        //diaryGeneralTextView.append("\n"+String.format(Locale.getDefault(), "Average Diary Creation Rate Per Week: %.2f", aveDiaryCreationRatePerWeek));
        //diaryGeneralTextView.append("\n"+String.format(Locale.getDefault(), "Average Diary Creation Rate Per Month: %.2f", aveDiaryCreationRatePerMonth));
        diaryGeneralTextView.append("\n"+"Dominant Mood: "+ moodScore);
        diaryGeneralTextView.append("\n"+String.format(Locale.getDefault(), "Mood Stability: %.2f",stabilityMood));
        diaryGeneralTextView.append("%");
        diaryGeneralTextView.setLineSpacing(6,2);


        Map<DayOfWeek, Integer> DiaryDistributionInWeek = diaryStatistics.getDayOfWeekDistribution();
        List<BarEntry> diwentries = new ArrayList<>();
        List<String> diwlabels = new ArrayList<>();

        int diwindex = 0;
        for (Map.Entry<DayOfWeek, Integer> entry : DiaryDistributionInWeek.entrySet()) {
            diwlabels.add(entry.getKey().getDisplayName(TextStyle.FULL, Locale.getDefault()));
            diwentries.add(new BarEntry(diwindex++, Float.parseFloat(String.valueOf(entry.getValue()))));
        }

        BarDataSet diwdataSet = new BarDataSet(diwentries, "Diary Distribution In Week");
        diwdataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData diwdata = new BarData(diwdataSet);
        diwdata.setBarWidth(0.9f); // set custom bar width
        diwdata.setValueFormatter(new DefaultValueFormatter(0)); // format values as integers

        BarChart diwchart = findViewById(R.id.diary_week_bar_chart);
        diwchart.setData(diwdata);
        diwchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(diwlabels));
        diwchart.getXAxis().setGranularity(1f); // set minimum axis-step (interval) to 1
        diwchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        diwchart.getDescription().setEnabled(false); // hide tfchart description
        diwchart.setFitBars(true); // make the bars fit into the available space
        diwchart.animateY(1000); // animate vertical axis
        diwchart.invalidate(); //refresh tfchart


        // Create a LineChart object
        Map<Integer, Integer> diaryDistributionInYear = diaryStatistics.getDiariesDistributionByMonthOfYear();

        LineChart diyLineChart = findViewById(R.id.diary_year_line_chart);

// Create a list to hold the data points
        List<Entry> diyentries = new ArrayList<>();
        List<String> diylabels = new ArrayList<>();

// Iterate over the map and add the entries to the list
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : diaryDistributionInYear.entrySet()) {
            diylabels.add(monthTOString(entry.getKey()));
            diyentries.add(new Entry(i++, entry.getValue().floatValue()));

        }

// Create a LineDataSet object with the data points and set the label
        LineDataSet diydataSet = new LineDataSet(diyentries, "Diary Distribution In Month Of Years");

// Set the line color, width, and cubic interpolation mode

        diydataSet.setColor(Color.BLUE);
        diydataSet.setLineWidth(2f);
        diydataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

// Create a LineData object with the LineDataSet object
        LineData diylineData = new LineData(diydataSet);
        diylineData.setValueFormatter(new DefaultValueFormatter(0)); // format values as integers


// Set the LineData object to the tfchart and refresh it
        diyLineChart.setData(diylineData);
        diyLineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(diylabels));
        diyLineChart.getXAxis().setGranularity(1f); // set minimum axis-step (interval) to 1
        diyLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        diyLineChart.getDescription().setEnabled(false); // hide tfchart description
        diyLineChart.animateY(1000);
        diyLineChart.invalidate();


        Map<String, Integer> diaryInCategory = diaryStatistics.getCategoryCount();
        List<BarEntry> dicentries = new ArrayList<>();
        List<String> diclabels = new ArrayList<>();

        int dicindex = 0;
        for (Map.Entry<String, Integer> entry : diaryInCategory.entrySet()) {
            diclabels.add(entry.getKey());
            dicentries.add(new BarEntry(dicindex++, Float.parseFloat(String.valueOf(entry.getValue()))));
        }

        BarDataSet dicdataSet = new BarDataSet(dicentries, "Diary Distribution In Categories");
        dicdataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData dicdata = new BarData(dicdataSet);
        dicdata.setBarWidth(0.9f); // set custom bar width
        dicdata.setValueFormatter(new DefaultValueFormatter(0)); // format values as integers

        BarChart dicchart = findViewById(R.id.diary_cat_bar_chart);
        dicchart.setData(dicdata);
        dicchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(diclabels));
        dicchart.getXAxis().setGranularity(1f); // set minimum axis-step (interval) to 1
        dicchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        dicchart.getDescription().setEnabled(false); // hide tfchart description
        dicchart.setFitBars(true); // make the bars fit into the available space
        dicchart.animateY(1000); // animate vertical axis
        dicchart.invalidate(); //refresh tfchart





        Map<String, Double> diaryLengthInMood = diaryStatistics.getDiaryLengthByMood();
        List<BarEntry> dimentries = new ArrayList<>();
        List<String> dimlabels = new ArrayList<>();

        int dimindex = 0;
        for (Map.Entry<String, Double> entry : diaryLengthInMood.entrySet()) {
            dimlabels.add(entry.getKey());
            dimentries.add(new BarEntry(dimindex++, Float.parseFloat(String.valueOf(entry.getValue()))));
        }

        BarDataSet dimdataSet = new BarDataSet(dimentries, "Diary Distribution In Categories");
        dimdataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData dimdata = new BarData(dimdataSet);
        dimdata.setBarWidth(0.9f); // set custom bar width
        dimdata.setValueFormatter(new DefaultValueFormatter(0)); // format values as integers

        HorizontalBarChart dimchart = findViewById(R.id.diary_length_mood_hbar_chart);
        dimchart.setData(dimdata);
        dimchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dimlabels));
        dimchart.getXAxis().setGranularity(1f); // set minimum axis-step (interval) to 1
        dimchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        dimchart.getDescription().setEnabled(false); // hide tfchart description
        dimchart.setFitBars(true); // make the bars fit into the available space
        dimchart.animateY(1000); // animate vertical axis
        dimchart.invalidate(); //refresh tfchart



        // Assuming you have a Map<String, Integer> dataMap
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Double> entry : moodDistributionInWeek.entrySet()) {
            labels.add(entry.getKey());
            entries.add(new BarEntry(index++, Float.parseFloat(String.valueOf(entry.getValue()))));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Mood Distribution In Week");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f); // set custom bar width
        data.setValueFormatter(new DefaultValueFormatter(0)); // format values as integers

        BarChart chart = findViewById(R.id.mood_week_bar_chart);
        chart.setData(data);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setGranularity(1f); // set minimum axis-step (interval) to 1
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getDescription().setEnabled(false); // hide tfchart description
        chart.setFitBars(true); // make the bars fit into the available space
        chart.animateY(1000); // animate vertical axis
        chart.invalidate(); //refresh tfchart

      String[] stopWords={  "a"
,"about"
,"above"
,"after"
,"again"
,"against"
,"all"
,"am"
,"an"
,"and"
,"any"
,"are"
,"aren't"
,"as"
,"at"
,"be"
,"because"
,"been"
,"before"
,"being"
,"below"
,"between"
,"both"
,"but"
,"by"
,"can't"
,"cannot"
,"could"
,"couldn't"
,"did"
,"didn't"
,"do"
,"    does"
,"doesn't"
,"doing"
,"don't"
,"down"
,"during"
,"each"
,"few"
,"for"
,"from"
,"further"
,"had"
,"hadn't"
,"has"
,"hasn't"
,"have"
,"haven't"
,"having"
,"he"
,"he'd"
,"he'll"
,"he's"
,"her"
,"here"
,"here's"
,"hers"
,"herself"
,"him"
,"himself"
,"his"
,"how"
,"how's"
,"i"
,"i'd"
,"i'll"
,"i'm"
,"i've"
,"if"
,"in"
,"into"
,"is"
,"isn't"
,"it"
,"it's"
,"its"
,"itself"
,"let's"
,"me"
,"more"
,"most"
,"mustn't"
,"my"
,"myself"
,"no"
,"nor"
,"not"
,"of"
,"off"
,"on"
,"once"
,"only"
,"or"
,"other"
,"ought"
,"our"
,"ours	ourselves"
,"out"
,"over"
,"own"
,"same"
,"shan't"
,"she"
,"she'd"
,"she'll"
,"she's"
,"should"
,"shouldn't"
,"so"
,"some"
,"such"
,"than"
,"that"
,"that's"
,"the"
,"their"
,"theirs"
,"them"
,"themselves"
,"then"
,"there"
,"there's"
,"these"
,"they"
,"they'd"
,"they'll"
,"they're"
,"they've"
,"this"
,"those"
,"through"
,"to"
,"too"
,"under"
,"until"
,"up"
,"very"
,"was"
,"wasn't"
,"we"
,"we'd"
,"we'll"
,"we're"
,"we've"
,"were"
,"weren't"
,"what"
,"what's"
,"when"
,"when's"
,"where"
,"where's"
,"which"
,"while"
,"who"
,"who's"
,"whom"
,"why"
,"why's"
,"with"
,"won't"
,"would"
,"wouldn't"
,"you"
,"you'd"
,"you'll"
,"you're"
,"you've"
,"your"
,"yours"
,"yourself"
,"yourselves"
      ,"can" ,"comes","will","want","like","bought","get","just","much","comes"


      };
        Map<String, Integer> wordFrequency = diaryStatistics.getKeywordFrequency();

        wordFrequency=sortMapByValue(wordFrequency);
        List<BarEntry> wfentries = new ArrayList<>();
        List<String> wflabels = new ArrayList<>();

        int wfindex = 0;
        int wflimit =10;
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            if (!Arrays.asList(stopWords).contains(entry.getKey().toLowerCase())) {
                wflabels.add(entry.getKey());
                wfentries.add(new BarEntry(wfindex++, Float.parseFloat(String.valueOf(entry.getValue()))));
                wflimit--;
                if(wflimit==0){
                    break;
                }
            }

        }

        BarDataSet wfdataSet = new BarDataSet(wfentries, "Words Frequency");
        wfdataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData wfdata = new BarData(wfdataSet);
        wfdata.setBarWidth(0.5f); // set custom bar width
        wfdata.setValueFormatter(new DefaultValueFormatter(0)); // format values as integers

        BarChart wfchart = findViewById(R.id.words_cloud);
        wfchart.setData(wfdata);
        wfchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(wflabels));
        wfchart.getXAxis().setGranularity(1f); // set minimum axis-step (interval) to 1
        wfchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        wfchart.getDescription().setEnabled(false); // hide tfchart description
        wfchart.setFitBars(true); // make the bars fit into the available space
        wfchart.animateY(1000); // animate vertical axis
        wfchart.invalidate(); //refresh tfchart







        Map<String, Integer> tagFrequency = diaryStatistics.getTagFrequency();
        tagFrequency=sortMapByValue(tagFrequency);
        List<BarEntry> tfentries = new ArrayList<>();
        List<String> tflabels = new ArrayList<>();

        int tfindex = 0;
        int tflimit =10;
        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
                tflabels.add(entry.getKey());
                tfentries.add(new BarEntry(tfindex++, Float.parseFloat(String.valueOf(entry.getValue()))));

            tflimit--;
            if(tflimit==0){
                break;
            }
        }

        BarDataSet tfdataSet = new BarDataSet(tfentries, "Tags Frequency");
        tfdataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData tfdata = new BarData(tfdataSet);
        tfdata.setBarWidth(0.5f); // set custom bar width
        tfdata.setValueFormatter(new DefaultValueFormatter(0)); // format values as integers

        BarChart tfchart = findViewById(R.id.tags_cloud);
        tfchart.setData(tfdata);
        tfchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(tflabels));
        tfchart.getXAxis().setGranularity(1f); // set minimum axis-step (interval) to 1
        tfchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        tfchart.getDescription().setEnabled(false); // hide tfchart description
        tfchart.setFitBars(true); // make the bars fit into the available space
        tfchart.animateY(1000); // animate vertical axis
        tfchart.invalidate(); //refresh tfchart

        Map<String, Integer> mediaRadar=diaryStatistics.getMediaFrequency();


        List<BarEntry> rcentries = new ArrayList<>();
        List<String> rclabels = new ArrayList<>();

        int rcindex = 0;
        for (Map.Entry<String, Integer> entry : mediaRadar.entrySet()) {
            switch (entry.getKey()){
                case "IV":
                    rclabels.add("Images");
                    rcentries.add(new BarEntry(rcindex++, entry.getValue()));
            break;
            case "AV":
                rclabels.add("Audios");
                rcentries.add(new BarEntry(rcindex++, entry.getValue()));
                break;
            case "VV":
                rclabels.add("Videos");
                rcentries.add(new BarEntry(rcindex++, entry.getValue()));
                break;
        }
        }

        BarDataSet rcdataSet = new BarDataSet(rcentries, "Media Distribution");
        rcdataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData rcdata = new BarData(rcdataSet);
        rcdata.setBarWidth(0.9f); // set custom bar width
        rcdata.setValueFormatter(new DefaultValueFormatter(0)); // format values as integers

        HorizontalBarChart rcchart = findViewById(R.id.media_c_h_bar_chart);
        rcchart.setData(rcdata);
        rcchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(rclabels));
        rcchart.getXAxis().setGranularity(1f); // set minimum axis-step (interval) to 1
        rcchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        rcchart.getDescription().setEnabled(false); // hide tfchart description
        rcchart.setFitBars(true); // make the bars fit into the available space
        rcchart.animateY(1000); // animate vertical axis
        rcchart.invalidate(); //refresh tfchart


    }


    private String monthTOString(Integer key) {
        switch (key) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "";
        }
    }
    public static LinkedHashMap<String, Integer> sortMapByValue(Map<String, Integer> unsortedMap) {
        // Create a list of the map's entries
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(unsortedMap.entrySet());

        // Sort the list by value using a custom Comparator
        Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Create a new LinkedHashMap to store the sorted entries
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        // Iterate over the sorted list and add the entries to the new map
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}