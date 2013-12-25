package chk.android.drawgame.server;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static Point sWindowSize = new Point();

    private DrawView mView;
    private TextView mTimeText;
    private TextView mWordText;
    private TextView mInfoText;

    private Server mServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindowManager().getDefaultDisplay().getSize(sWindowSize);

        setContentView(R.layout.activity_main);

        mView = (DrawView) findViewById(R.id.draw_view);
        mInfoText = (TextView) findViewById(R.id.info);
        mTimeText = (TextView) findViewById(R.id.time);
        mWordText = (TextView) findViewById(R.id.word);

        mServer = new Server(this);
        mServer.start();

        mInfoText.setText("Waiting for connection...");
        mView.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServer != null) {
            mServer.close();
        }

        if (mView != null) {
            mView.onDestory();
        }
    }

    public void onConnected(final String ip) {
        mInfoText.post(new Runnable() {
            @Override
            public void run() {
                mInfoText.append("\nConnected with " + ip + ", waiting for operation...");
            }
        });
    }

    public void onDisconnected() {
        mInfoText.post(new Runnable() {
            @Override
            public void run() {
                mInfoText.append("\nDisconnected...Waiting for new connection...");
                mInfoText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onNewWord(final String word) {
        mInfoText.post(new Runnable() {
            @Override
            public void run() {
                mView.clear();
                mWordText.setText(word);
                mInfoText.setVisibility(View.GONE);
            }
        });
    }

    public void onUpdateTime(final String time) {
        mTimeText.post(new Runnable() {
            @Override
            public void run() {
                mTimeText.setText(time);
            }
        });
    }

    public void onDrawPoint(final float xScale, final float yScale) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                mView.drawPoint(xScale, yScale);
            }
        });
    }

    public void onDrawLine(final float xScale, final float yScale) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                mView.drawLine(xScale, yScale);
            }
        });
    }

    public void onClear() {
        mView.post(new Runnable() {
            @Override
            public void run() {
                mView.clear();
            }
        });
    }

    public void onError(final Throwable e) {
        mInfoText.post(new Runnable() {
            @Override
            public void run() {
                mInfoText.append("\nError : " + e.toString());
            }
        });
    }
}
