package chk.android.drawgame;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    private final static String SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/drawgame";
    private final static int MAX_DRAW_TIME = 5000;
    private final static int MAX_GUESS_TIME = 10000;
    private final static int TIME_PERIOD = 10;
    private final static int WHAT_UPDATE_DRAW_TIME = 0;
    private final static int WHAT_UPDATE_GUESS_TIME = 1;
    private final static int WHAT_DRAW_TIME_UP = 2;
    private final static int WHAT_GUESS_TIME_UP = 3;
    private final static DecimalFormat FORMATTER = new DecimalFormat("#0.00");

    private Toast mNoMoreToast;
    private DrawView mView;
    private Button mClearButton;
    private Button mDrawButton;
    private Button mGuessButton;
    private Button mNextButton;
    private TextView mTimeText;
    private TextView mWordText;
    private View mCover;
    private View mActionContainer;

    private String mWord;
    private Timer mTimer;
    private long mTime;
    private MainHandler mMainHandler;

    private class MainHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
            case WHAT_UPDATE_DRAW_TIME:
                doUpdateTime(MAX_DRAW_TIME);
                break;
            case WHAT_UPDATE_GUESS_TIME:
                doUpdateTime(MAX_GUESS_TIME);
                break;
            case WHAT_DRAW_TIME_UP:
                doDrawTimeUp();
                break;
            case WHAT_GUESS_TIME_UP:
                doGuessTimeUp();
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // int width = getWindow().getDecorView().getHeight();
        mView = (DrawView) findViewById(R.id.draw_view);
        mClearButton = (Button) findViewById(R.id.clear);
        mDrawButton = (Button) findViewById(R.id.draw);
        mGuessButton = (Button) findViewById(R.id.guess);
        mNextButton = (Button) findViewById(R.id.next);
        mTimeText = (TextView) findViewById(R.id.time);
        mWordText = (TextView) findViewById(R.id.word);
        mCover = findViewById(R.id.cover);
        mActionContainer = findViewById(R.id.action_container);

        mClearButton.setOnClickListener(this);
        mDrawButton.setOnClickListener(this);
        mGuessButton.setOnClickListener(this);
        mWordText.setOnClickListener(this);
        mNextButton.setOnClickListener(this);

        mMainHandler = new MainHandler();

        mWord = null;
        Words.loadWords(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mClearButton) {
            mView.clear();
        } else if (v == mDrawButton) {
            startDraw();
        } else if (v == mGuessButton) {
            startGuess();
        } else if (v == mNextButton) {
            startNew();
        } else if (v == mWordText) {
            mWordText.setAlpha(1f);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }

        if (mView != null) {
            mView.onDestory();
        }
    }

    private void startNew() {
        mWord = null;
        mView.clear();
        mNextButton.setVisibility(View.INVISIBLE);
        mGuessButton.setVisibility(View.GONE);
        mCover.setVisibility(View.VISIBLE);
    }

    private void startDraw() {
        if (TextUtils.isEmpty(mWord)) {
            mWord = Words.generateWord(this);
            if (TextUtils.isEmpty(mWord)) {
                if (mNoMoreToast != null) {
                    mNoMoreToast.cancel();
                }
                mNoMoreToast = Toast.makeText(this, "No more word...",
                        Toast.LENGTH_SHORT);
                mNoMoreToast.show();
                return;
            }
        }
        mWordText.setText(mWord);
        mWordText.setAlpha(1f);
        mWordText.setVisibility(View.VISIBLE);
        mActionContainer.setVisibility(View.VISIBLE);
        mView.setEnabled(true);
        mCover.setVisibility(View.GONE);

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }

        mTime = 0;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                mMainHandler.sendEmptyMessage(WHAT_UPDATE_DRAW_TIME);
                if (mTime >= MAX_DRAW_TIME) {
                    mMainHandler.sendEmptyMessage(WHAT_DRAW_TIME_UP);
                    mTimer.cancel();
                }
                mTime += TIME_PERIOD;
            }
        }, 0, TIME_PERIOD);
    }

    private void startGuess() {


        mView.setEnabled(false);
        mActionContainer.setVisibility(View.INVISIBLE);
        mWordText.setVisibility(View.INVISIBLE);
        mCover.setVisibility(View.GONE);

        saveBitmap(mWord);

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }

        mTime = 0;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                mMainHandler.sendEmptyMessage(WHAT_UPDATE_GUESS_TIME);
                if (mTime >= MAX_GUESS_TIME) {
                    mMainHandler.sendEmptyMessage(WHAT_GUESS_TIME_UP);
                    mTimer.cancel();
                }
                mTime += TIME_PERIOD;
            }
        }, 0, TIME_PERIOD);
    }

    private void doUpdateTime(long max) {
        long left = max - mTime;
        if (left < 0) {
            left = 0;
        }
        mTimeText.setText(FORMATTER.format(left / 1000f));
    }

    private void doDrawTimeUp() {
        if (!TextUtils.isEmpty(mWord)) {
            mGuessButton.setVisibility(View.VISIBLE);
        }
        mGuessButton.setEnabled(false);
        mDrawButton.setEnabled(false);
        mCover.setVisibility(View.VISIBLE);

        mCover.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGuessButton.setEnabled(true);
                mDrawButton.setEnabled(true);
            }
        }, 2000);
    }

    private void doGuessTimeUp() {
        mWordText.setAlpha(0f);
        mWordText.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.VISIBLE);
    }

    private void saveBitmap(String word) {
        File dir = new File(SAVE_PATH);
        BufferedOutputStream bos = null;
        try {
            if (!dir.exists() && !dir.mkdirs()) {
                Log.e("chk", "can not save bitmap");
                return;
            }
            File f = new File(dir, System.currentTimeMillis() + word + ".png");
            bos = new BufferedOutputStream(new FileOutputStream(f));
            mView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            Log.e("chk", e.toString());
        } catch (IOException e) {
            Log.e("chk", e.toString());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    Log.e("chk", e.toString());
                }
            }
        }
    }
}
