package ir.derasat.mydiary;


import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class DiaryStatistics {
    private int diaryCount;
    private Map<String, Integer> categoryCount;
    private Map<String, Integer> moodDistribution;
    private Map<String, Integer> tagFrequency;
    private int totalWordCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<DayOfWeek, Integer> dayOfWeekDistribution;
    private int averageDiaryLength;
    private Map<LocalTime, Integer> timeOfDayDistribution;
    private Map<String, Integer> keywordFrequency;
    private int averageWordCountPerDiary;
    private String mostActiveDayOfWeek;
    private String mostActiveMonth;
    private double averageDiaryCreationRatePerWeek;
    private double averageDiaryCreationRatePerMonth;
    private int diariesSentencesCount;
    private double averageSentencesCount;
    private int diariesSentencesLength;
    private double averageSentencesLength;
    private double moodScore;
    private double moodStability;
    private Map<String, Double> moodDistributionPercent;

    private Map<Integer, Integer> diariesDistributionByMonthOfYear ;

    private Map<String, Double> diaryLengthByMood;

    private Map<String, Double> moodByDayOfWeek;
    private Map<String, Integer> mediaFrequency;


    public DiaryStatistics() {
        diaryCount = 0;
        categoryCount = new HashMap<>();
        moodDistribution = new HashMap<>();
        tagFrequency = new HashMap<>();
        totalWordCount = 0;
        startDate = null;
        endDate = null;
        dayOfWeekDistribution = new HashMap<>();
        averageDiaryLength = 0;
        timeOfDayDistribution = new HashMap<>();
        keywordFrequency = new HashMap<>();
        averageWordCountPerDiary = 0;
        mostActiveDayOfWeek="";
        mostActiveMonth="";
        averageDiaryCreationRatePerWeek=0;
        averageDiaryCreationRatePerMonth=0;
        diariesSentencesCount=0;
        averageSentencesCount=0;
        diariesSentencesLength=0;
        averageSentencesLength=0;
        moodScore=0;
        moodStability=0;
        moodDistributionPercent = new HashMap<>();
        diariesDistributionByMonthOfYear = new HashMap<>();
        diaryLengthByMood = new HashMap<>();
        moodByDayOfWeek = new HashMap<>();
        mediaFrequency = new HashMap<>();

    }
        public void calculateStatistics(List < Diary > diaries) {

            // Calculate diary count.............

            diaryCount = diaries.size();

            // Calculate category count
            for (Diary diary : diaries) {
                String category = diary.getCategory();
                if (categoryCount.containsKey(category)) {
                    categoryCount.put(category, categoryCount.get(category) + 1);
                } else {
                    categoryCount.put(category, 1);
                }
            }

            // Calculate mood distribution
            for (Diary diary : diaries) {
                String mood = diary.getStringMood();
                if (moodDistribution.containsKey(mood)) {
                    moodDistribution.put(mood, moodDistribution.get(mood) + 1);
                } else {
                    moodDistribution.put(mood, 1);
                }
            }

            // Calculate tag frequency...........
            for (Diary diary : diaries) {
                String[] tags = diary.getTags().split("\\s+");
                for (String tag : tags) {
                    if (tagFrequency.containsKey(tag)) {
                        tagFrequency.put(tag, tagFrequency.get(tag) + 1);
                    } else {
                        tagFrequency.put(tag, 1);
                    }
                }
            }

            // Calculate total word count and average diary length
            for (Diary diary : diaries) {
                String content = diary.getContents();
                String[] words = content.split("\\s+");
                totalWordCount += words.length;
            }
            if (diaryCount > 0) {
                averageDiaryLength = totalWordCount / diaryCount;
            }

            // Calculate data range
            if (diaries.size() > 0) {
                startDate = Instant.ofEpochMilli(diaries.get(0).getCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                endDate = Instant.ofEpochMilli(diaries.get(diaries.size() - 1).getCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            }

            // Calculate day of week distribution
            for (Diary diary : diaries) {
                DayOfWeek dayOfWeek = Instant.ofEpochMilli(diary.getCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek();
                if (dayOfWeekDistribution.containsKey(dayOfWeek)) {
                    dayOfWeekDistribution.put(dayOfWeek, dayOfWeekDistribution.get(dayOfWeek) + 1);
                } else {
                    dayOfWeekDistribution.put(dayOfWeek, 1);
                }
            }

            // Calculate average word count per diary........
            if (diaryCount > 0) {
                averageWordCountPerDiary = totalWordCount / diaryCount;
            }

            // Calculate time of day distribution
            for (Diary diary : diaries) {
                LocalTime timeOfDay = Instant.ofEpochMilli(diary.getCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay().toLocalTime().withMinute(0).withSecond(0);
                if (timeOfDayDistribution.containsKey(timeOfDay)) {
                    timeOfDayDistribution.put(timeOfDay, timeOfDayDistribution.get(timeOfDay) + 1);
                } else {
                    timeOfDayDistribution.put(timeOfDay, 1);
                }
            }

            // Calculate keyword frequency.........
            for (Diary diary : diaries) {
                String content = diary.getContents();
                String[] words = content.split("\\s+");
                for (String word:words) {
                    if (keywordFrequency.containsKey(word)) {
                        keywordFrequency.put(word, keywordFrequency.get(word) + 1);
                    } else {
                        keywordFrequency.put(word, 1);
                    }
                }
            }

            calculateMoodScore(diaries);
            calculateMoodStability(diaries);
            calculateDiariesSentencesCount(diaries);
            calculateAverageSentencesCount(diaries);
            calculateDiariesSentencesLength(diaries);
            calculateAverageSentencesLength(diaries);
            calculateMoodDistributionPercent(diaries);
            calculateDiariesDistributionByMonthOfYear(diaries);
            calculateDiaryLengthByMood(diaries);
            calculateMoodByDayOfWeek(diaries);
            calculateMediaFrequency(diaries);

            // Calculate existing statistics
            calculateMostActiveDayOfWeek(diaries);
            calculateMostActiveMonth(diaries);
            calculateAverageDiaryCreationRatePerWeek(diaries);
            calculateAverageDiaryCreationRatePerMonth(diaries);
        }



    public int getDiaryCount () {
            return diaryCount;
        }

        public Map<String, Integer> getCategoryCount () {
            return categoryCount;
        }

        public Map<String, Integer> getMoodDistribution () {
            return moodDistribution;
        }

        public Map<String, Integer> getTagFrequency () {
            return tagFrequency;
        }

        public int getTotalWordCount () {
            return totalWordCount;
        }

        public String getDateRange () {
            if (startDate == null || endDate == null) {
                return "N/A";
            } else {
                DateTimeFormatter formatter = null;
                    formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);

                String startDateStr = startDate.format(formatter);
                String endDateStr = endDate.format(formatter);

                return startDateStr + " - " + endDateStr;
            }
        }

        public Map<DayOfWeek, Integer> getDayOfWeekDistribution () {
            return dayOfWeekDistribution;
        }

        public int getAverageDiaryLength () {
            return averageDiaryLength;
        }

        public Map<LocalTime, Integer> getTimeOfDayDistribution () {
            return timeOfDayDistribution;
        }

        public Map<String, Integer> getKeywordFrequency () {
            return keywordFrequency;
        }

        public int getAverageWordCountPerDiary () {
            return averageWordCountPerDiary;
        }

        // Helper method to get the most frequent item from a map
        private <K, V extends Comparable<V>>K getMostFrequentItem (Map < K, V > map){
            List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
            Collections.sort(entries, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            return entries.get(0).getKey();
        }

        // Helper method to get the time of day as a string
        private String getTimeOfDayAsString (LocalTime time){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            return time.format(formatter);
        }

        // Helper method to get the day of week as a string
        private String getDayOfWeekAsString (DayOfWeek dayOfWeek){
            return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        }

        // Helper method to get the keyword frequency as a list of strings
        public List<String> getTopKeywords ( int count){
            List<String> keywords = new ArrayList<>(keywordFrequency.keySet());
            Collections.sort(keywords, (k1, k2) -> keywordFrequency.get(k2).compareTo(keywordFrequency.get(k1)));
            return keywords.subList(0, Math.min(count, keywords.size()));
        }

        // Helper method to get the time of day distribution as alist of strings
        public List<String> getTimeOfDayDistributionAsString () {
            List<LocalTime> times = new ArrayList<>(timeOfDayDistribution.keySet());
            Collections.sort(times);
            List<String> distribution = new ArrayList<>();
            for (LocalTime time : times) {
                String timeStr = getTimeOfDayAsString(time);
                int count = timeOfDayDistribution.get(time);
                String entry = String.format("%s: %d", timeStr, count);
                distribution.add(entry);
            }
            return distribution;
        }

        // Helper method to get the day of week distribution as a list of strings
        public List<String> getDayOfWeekDistributionAsString () {
            List<DayOfWeek> daysOfWeek = new ArrayList<>(dayOfWeekDistribution.keySet());
            Collections.sort(daysOfWeek);
            List<String> distribution = new ArrayList<>();
            for (DayOfWeek dayOfWeek : daysOfWeek) {
                String dayOfWeekStr = getDayOfWeekAsString(dayOfWeek);
                int count = dayOfWeekDistribution.get(dayOfWeek);
                String entry = String.format("%s: %d", dayOfWeekStr, count);
                distribution.add(entry);
            }
            return distribution;
        }


    private void calculateMostActiveDayOfWeek(List<Diary> diaries) {
        // Initialize a Map to store the diary counts for each day of the week
        Map<Integer, Integer> dayOfWeekCounts = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            dayOfWeekCounts.put(i, 0);
        }

        // Count the number of diaries created on each day of the week
        for (Diary diary : diaries) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(diary.getCreationDate());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            dayOfWeekCounts.put(dayOfWeek, dayOfWeekCounts.get(dayOfWeek) + 1);
        }

        // Find the day of the week with the highest diary count
        int maxCount = 0;
        int mostActiveDay = -1;
        for (Map.Entry<Integer, Integer> entry : dayOfWeekCounts.entrySet()) {
            int count = entry.getValue();
            if (count > maxCount) {
                maxCount = count;
                mostActiveDay = entry.getKey();
            }
        }

        // Map the day of the week to a string representation
        switch (mostActiveDay) {
            case Calendar.SUNDAY:
                mostActiveDayOfWeek = "Sunday";
                break;
            case Calendar.MONDAY:
                mostActiveDayOfWeek = "Monday";
                break;
            case Calendar.TUESDAY:
                mostActiveDayOfWeek = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                mostActiveDayOfWeek = "Wednesday";
                break;
            case Calendar.THURSDAY:
                mostActiveDayOfWeek = "Thursday";
                break;
            case Calendar.FRIDAY:
                mostActiveDayOfWeek = "Friday";
                break;
            case Calendar.SATURDAY:
                mostActiveDayOfWeek = "Saturday";
                break;
        }
    }

    private void calculateMostActiveMonth(List<Diary> diaries) {
        // Initialize a Map to store the diary counts for each month
        Map<Integer, Integer> monthCounts = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            monthCounts.put(i, 0);
        }

        // Count the number of diaries created in each month
        for (Diary diary : diaries) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(diary.getCreationDate());
            int month = calendar.get(Calendar.MONTH);
            monthCounts.put(month, monthCounts.get(month) + 1);
        }

        // Find the month with the highest diary count
        int maxCount = 0;
        int mostActiveMonthIndex = -1;
        for (Map.Entry<Integer, Integer> entry : monthCounts.entrySet()) {
            int count = entry.getValue();
            if (count > maxCount) {
                maxCount = count;
                mostActiveMonthIndex = entry.getKey();
            }
        }

        // Map the month index to a string representation
        switch (mostActiveMonthIndex) {
            case Calendar.JANUARY:
                mostActiveMonth = "January";
                break;
            case Calendar.FEBRUARY:
                mostActiveMonth = "February";
                break;
            case Calendar.MARCH:
                mostActiveMonth = "March";
                break;
            case Calendar.APRIL:
                mostActiveMonth = "April";
                break;
            case Calendar.MAY:
                mostActiveMonth = "May";
                break;
            case Calendar.JUNE:
                mostActiveMonth = "June";
                break;
            case Calendar.JULY:
                mostActiveMonth = "July";
                break;
            case Calendar.AUGUST:
                mostActiveMonth = "August";
                break;
            case Calendar.SEPTEMBER:
                mostActiveMonth = "September";
                break;
            case Calendar.OCTOBER:
                mostActiveMonth = "October";
                break;
            case Calendar.NOVEMBER:
                mostActiveMonth = "November";
                break;
            case Calendar.DECEMBER:
                mostActiveMonth = "December";
                break;
        }
    }

    private void calculateAverageDiaryCreationRatePerWeek(List<Diary>diaries) {
        // Calculate the number of weeks between the creation date of the first and last diary
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(diaries.get(0).getCreationDate());
        int startWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int startYear = calendar.get(Calendar.YEAR);
        calendar.setTime(diaries.get(diaries.size() - 1).getCreationDate());
        int endWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int endYear = calendar.get(Calendar.YEAR);
        int numWeeks = (endYear - startYear) * 52 + (endWeek - startWeek) + 1;

        // Calculate the average diary creation rate per week
        averageDiaryCreationRatePerWeek = (double) diaryCount / numWeeks;
    }

    private void calculateAverageDiaryCreationRatePerMonth(List<Diary> diaries) {
        // Calculate the number of months between the creation date of the first and last diary
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(diaries.get(0).getCreationDate());
        int startMonth = calendar.get(Calendar.MONTH);
        int startYear = calendar.get(Calendar.YEAR);
        calendar.setTime(diaries.get(diaries.size() - 1).getCreationDate());
        int endMonth = calendar.get(Calendar.MONTH);
        int endYear = calendar.get(Calendar.YEAR);
        int numMonths = (endYear - startYear) * 12 + (endMonth - startMonth)+ 1;

        // Calculate the average diary creation rate per month
        averageDiaryCreationRatePerMonth = (double) diaryCount / numMonths;
    }

    public String getMostActiveDayOfWeek() {
        return mostActiveDayOfWeek;
    }

    public String getMostActiveMonth() {
        return mostActiveMonth;
    }

    public double getAverageDiaryCreationRatePerWeek() {
        return averageDiaryCreationRatePerWeek;
    }

    public double getAverageDiaryCreationRatePerMonth() {
        return averageDiaryCreationRatePerMonth;
    }

    private void calculateDiariesSentencesCount(List<Diary> diaries) {
        diariesSentencesCount = 0;
        for (Diary diary : diaries) {
            String[] sentences = diary.getContents().split("[\\.!\\?]");
            diariesSentencesCount += sentences.length;
        }
    }


    private void calculateAverageSentencesCount(List<Diary> diaries) {
        int totalSentencesCount = 0;
        for (Diary diary : diaries) {
            String[] sentences = diary.getContents().split("[\\.!\\?]");
            totalSentencesCount += sentences.length;
        }
        averageSentencesCount = (double) totalSentencesCount / diaryCount;
    }

    private void calculateDiariesSentencesLength(List<Diary> diaries) {
        diariesSentencesLength = 0;
        for (Diary diary : diaries) {
            String[] sentences = diary.getContents().split("[\\.!\\?]");
            for (String sentence : sentences) {
                diariesSentencesLength += sentence.trim().length();
            }
        }
    }

    private void calculateAverageSentencesLength(List<Diary> diaries) {
        int totalSentencesLength = 0;
        int totalSentencesCount = 0;
        for (Diary diary : diaries) {
            String[] sentences = diary.getContents().split("[\\.!\\?]");
            for (String sentence : sentences) {
                totalSentencesLength += sentence.trim().length();
            }
            totalSentencesCount += sentences.length;
        }
        averageSentencesLength = (double) totalSentencesLength / totalSentencesCount;
    }

    public int getDiariesSentencesCount() {
        return diariesSentencesCount;
    }

    public double getAverageSentencesCount() {
        return averageSentencesCount;
    }

    public int getDiariesSentencesLength() {
        return diariesSentencesLength;
    }

    public double getAverageSentencesLength() {
        return averageSentencesLength;
    }
    private void calculateMoodScore(List<Diary> diaries) {
        double moodScoreSum = 0;
        int moodScoreCount = 0;
        for (Diary diary : diaries) {
                moodScoreSum += getSMood(diary.getMood());
                moodScoreCount++;
        }
        if (moodScoreCount > 0) {
            moodScore = moodScoreSum / moodScoreCount;
        } else {
            moodScore = 0;
        }
    }

    private void calculateMoodStability(List<Diary> diaries) {
        double moodStabilitySum = 0;
        int moodStabilityCount = 0;
        for (int i = 0; i < diaries.size() - 1; i++) {
            Diary diary1 = diaries.get(i);
            Diary diary2 = diaries.get(i+1);
                moodStabilitySum += Math.abs(getSMood(diary2.getMood()) - getSMood(diary1.getMood()));
                moodStabilityCount++;
            
        }
        if (moodStabilityCount > 0) {
            moodStability = moodStabilitySum / moodStabilityCount;
        } else {
            moodStability = 0;
        }
    }
    private int getSMood(int mood) {
        switch (mood) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 0;
            default:
                return 0;
        }
    }

    public String getMoodScore() {
        if (moodScore <= 0.5) {
            return "Negative";
        }else if (moodScore >0.5 && moodScore <= 1.5) {
            return "Neutral";
        } else if (moodScore > 1.5) {
            return "Positive";
        }else {
            return "Neutral";
        }
    }

    public double getMoodStability() {
        return moodStability;
    }
    private void calculateMoodDistributionPercent(List<Diary> diaries) {
        int totalMoods = 0;
        for (int count : moodDistribution.values()) {
            totalMoods += count;
        }
        for (Map.Entry<String,Integer> entry : moodDistribution.entrySet()) {
            String mood = entry.getKey();
            int count = entry.getValue();
            double percent = ((double) count / totalMoods) * 100;
            moodDistributionPercent.put(mood, percent);
        }
    }

    private void calculateDiariesDistributionByMonthOfYear(List<Diary> diaries) {
        for (Diary diary : diaries) {
            LocalDate date = Instant.ofEpochMilli(diary.getCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            int month = date.getMonthValue();
            int count = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                count = diariesDistributionByMonthOfYear.getOrDefault(month, 0);
            }else {
                if (diariesDistributionByMonthOfYear.containsKey(month)) {
                    count = diariesDistributionByMonthOfYear.get(month);

                }
            }
            diariesDistributionByMonthOfYear.put(month, count + 1);
        }
    }

    private void calculateDiaryLengthByMood(List<Diary> diaries) {
        Map<String, Integer> moodDiaryCount = new HashMap<>();
        Map<String, Integer> moodDiaryLengthSum = new HashMap<>();
        for (Diary diary : diaries) {
            String mood = diary.getStringMood();
            if (mood != null) {
                int length = diary.getContents().length();
                int count = getOrDefault(moodDiaryCount,mood);
                int lengthSum =getOrDefault(moodDiaryLengthSum,mood);
                moodDiaryCount.put(mood, count + 1);
                moodDiaryLengthSum.put(mood, lengthSum + length);
            }
        }
        for (Map.Entry<String, Integer> entry : moodDiaryCount.entrySet()) {
            String mood = entry.getKey();
            double averageLength = (double) moodDiaryLengthSum.get(mood) / entry.getValue();
            diaryLengthByMood.put(mood, averageLength);
        }
    }

    public Map<String, Integer> getMediaFrequency() {
        return mediaFrequency;
    }

    private void calculateMediaFrequency(List<Diary> diaries) {

        for (Diary diary : diaries) {
            String[] views =diary.getViews();
            for (String s : views) {
                String[] view = s.split("!@#");
                if (!view[0].equals("ET")) {
                    if (mediaFrequency.containsKey(view[0])) {
                        mediaFrequency.put(view[0], mediaFrequency.get(view[0]) + 1);
                    } else {
                        mediaFrequency.put(view[0], 1);
                    }
                }

            }
        }
    }


    private void calculateMoodByDayOfWeek(List<Diary> diaries) {
        Map<String, Integer> moodCountByDayOfWeek = new HashMap<>();
        for (Diary diary : diaries) {
            int mood = getSMood(diary.getMood());
            if (mood != -1) {
                LocalDateTime dateTime = Instant.ofEpochMilli(diary.getCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();;
                String dayOfWeek = dateTime.getDayOfWeek().toString();
                int count = getOrDefault(moodCountByDayOfWeek,dayOfWeek);
                moodCountByDayOfWeek.put(dayOfWeek, count + mood);
            }
        }
        int totalMoods = 0;
        for (int count : moodCountByDayOfWeek.values()) {
            totalMoods += count;
        }
        for (Map.Entry<String, Integer> entry : moodCountByDayOfWeek.entrySet()) {
            String dayOfWeek = entry.getKey();
            int count = entry.getValue();
            double percent = ((double) count / totalMoods)*100;
            moodByDayOfWeek.put(dayOfWeek, percent);
        }
    }
    private int getOrDefault(Map<String, Integer> map,String key) {
        if (map.containsKey(key))
            return map.get(key);
        else
            return 0;
    }

    public Map<String, Double> getMoodDistributionPercent() {
        return moodDistributionPercent;
    }

    public Map<Integer, Integer> getDiariesDistributionByMonthOfYear() {
        return diariesDistributionByMonthOfYear;
    }

    public Map<String, Double> getDiaryLengthByMood() {
        return diaryLengthByMood;
    }

    public Map<String, Double> getMoodByDayOfWeek() {
        return moodByDayOfWeek;
    }

}


/*
    In this implementation, the DiaryStatistics class has the following properties and methods:

            - diaryCount: The total number of diaries.
- categoryCount: A map of category names to the number of diaries in each category.
- moodDistribution: A map of mood names to the number of diaries with each mood.
- tagFrequency: Amap of tag names to the number of diaries that contain each tag.
- totalWordCount: The total number of words across all diaries.
            - startDate: The date of the earliest diary.
            - endDate: The date of the latest diary.
            - dayOfWeekDistribution: A map of days of the week to the number of diaries created on each day.
            - averageDiaryLength: The average number of words per diary.
- timeOfDayDistribution: A map of times of the day to the number of diaries created at each time.
            - keywordFrequency: A map of keywords to the number of diaries that contain each keyword.
- averageWordCountPerDiary: The average number of words per diary.

            The class also has the following methods:

            - calculateStatistics: Calculates all the statistics based on a list of diaries.
            - getDiaryCount: Returns the total number of diaries.
            - getCategoryCount: Returns the category count map.
- getMoodDistribution: Returns the mood distribution map.
- getTagFrequency: Returns the tag frequency map.
- getTotalWordCount: Returns the total word count.
- getDateRange: Returns the date range as a formatted string.
            - getDayOfWeekDistribution: Returns the day of week distribution map.
- getAverageDiary*/
