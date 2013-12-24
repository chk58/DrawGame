package chk.android.drawgame;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Words {

    private static final String PREFS_WORDS = "words";

    public static final String WORDS_CHENGYU = "刻舟求剑;狐假虎威;狗急跳墙";
    public static final String WORDS_RENWU = "牛顿;刘翔";
    public static final String WORDS_DONGXI = "电脑;大树;南瓜;圣诞树;蝴蝶;手套;蛋炒饭;啤酒;饮水机;眉毛;红包;草泥马";
    public static final String WORDS_DIFANG = "天安门;沙滩;中国;太阳系";
    public static final String WORDS_SHIQING = "电视购物;打麻将";
    public static final String WORDS_YINGSHI = "还珠格格;喜羊羊;西游记";

    public static final String WORDS =
             WORDS_CHENGYU + ";" +
             WORDS_RENWU + ";" +
             WORDS_DONGXI + ";" +
             WORDS_DIFANG + ";" +
             WORDS_SHIQING + ";" +
             WORDS_YINGSHI;

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

    public static String getType(String word) {
        String result = "";
        if (WORDS_CHENGYU.contains(word)) {
            result = "成语";
        } else if (WORDS_RENWU.contains(word)) {
            result = "人物";
        } else if (WORDS_DIFANG.contains(word)) {
            result = "地方";
        } else if (WORDS_SHIQING.contains(word)) {
            result = "事情";
        } else if (WORDS_YINGSHI.contains(word)) {
            result = "影视作品";
        } else if (WORDS_DONGXI.contains(word)) {
            result = "东西";
        }
        return result;
    }
}
