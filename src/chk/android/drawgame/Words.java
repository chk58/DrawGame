package chk.android.drawgame;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Words {

    private static final String PREFS_WORDS = "words";

    public static final String WORDS_CHENGYU = "刻舟求剑;狐假虎威;狗急跳墙;三长二短;眼高手低;头破血流;画龙点睛;对牛弹琴;三头六臂;打草惊蛇";
    public static final String WORDS_RENWU = "牛顿;刘翔;外星人;超人";
    public static final String WORDS_JIERI = "圣诞节;端午节;中秋节";
    public static final String WORDS_DIFANG = "天安门;沙滩;中国;太阳系;长城;上海";
    public static final String WORDS_SHIQING = "电视购物;打麻将;加班;抽烟";
    public static final String WORDS_YINGSHI = "还珠格格;喜羊羊;西游记;葫芦娃";
    public static final String WORDS_DONGWU = "蝴蝶;草泥马;娃娃鱼;东北虎";
    public static final String WORDS_CHIDE = "南瓜;蛋炒饭;肯德基;冰激凌;煎饼";
    public static final String WORDS_YINLIAO = "啤酒;茅台;白开水;可乐";
    public static final String WORDS_DIANQI = "电脑;饮水机;空调;洗衣机;吸尘器";
    public static final String WORDS_YUNDONG = "足球;羽毛球;举重;跳水;太极拳;台球";

    public static final String WORDS =
             WORDS_CHENGYU + ";" +
             WORDS_RENWU + ";" +
             WORDS_JIERI + ";" +
             WORDS_DIFANG + ";" +
             WORDS_SHIQING + ";" +
             WORDS_YINGSHI + ";" +
             WORDS_DONGWU + ";" +
             WORDS_CHIDE + ";" +
             WORDS_YINLIAO + ";" +
             WORDS_DIANQI + ";" +
             WORDS_YUNDONG;

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
        } else if (WORDS_JIERI.contains(word)) {
            result = "节日";
        } else if (WORDS_DONGWU.contains(word)) {
            result = "动物";
        } else if (WORDS_CHIDE.contains(word)) {
            result = "吃的";
        } else if (WORDS_YINLIAO.contains(word)) {
            result = "饮料";
        } else if (WORDS_DIANQI.contains(word)) {
            result = "电器";
        } else if (WORDS_YUNDONG.contains(word)) {
            result = "运动";
        }
        return result;
    }
}
