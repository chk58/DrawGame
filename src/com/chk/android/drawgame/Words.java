package com.chk.android.drawgame;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Words {

    private static final String PREFS_WORDS = "words";

    public static final String WORDS_CHENGYU = "刻舟求剑;狐假虎威";
    public static final String WORDS_RENWU = "牛顿";
    public static final String WORDS_DONGXI = "电脑;大树;西游记;南瓜;圣诞树;;蝴蝶;手套;蛋炒饭;啤酒;饮水机";
    public static final String WORDS_DIFANG = "天安门;沙滩";

    public static final String WORDS =
            WORDS_CHENGYU + ";" +
            WORDS_RENWU + ";" +
            WORDS_DONGXI + ";" +
            WORDS_DIFANG;

    public static String generateWord(Context context) {
        String result = null;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String words = prefs.getString(PREFS_WORDS, null);
        if (!TextUtils.isEmpty(words)) {
            String[] arr = words.split(";");
            if (arr.length > 0) {
                int index = getRandomIndex(arr.length);
                result = arr[index];

                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < arr.length; i++) {
                    if (i != index) {
                        list.add(arr[i]);
                    }
                }
                StringBuilder new_words = new StringBuilder();
                for (String w : list) {
                    if (new_words.length() > 0) {
                        new_words.append(";");
                    }
                    new_words.append(w);
                }
                prefs.edit().putString(PREFS_WORDS, new_words.toString())
                        .commit();
            }
        }
        return result;
    }

    public static void loadWords(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String words = prefs.getString(PREFS_WORDS, null);
        if (TextUtils.isEmpty(words)) {
            prefs.edit().putString(PREFS_WORDS, WORDS).commit();
        }
    }

    public static int getRandomIndex(int range) {
        Random random = new Random();
        return Math.abs(random.nextInt()) % range;
    }
}
