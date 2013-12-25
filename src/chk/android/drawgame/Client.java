package chk.android.drawgame;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;

public class Client extends Thread {

    private static final int WHAT_NEW_WORD = 2;
    private static final int WHAT_UPDATE_TIME = 3;
    private static final int WHAT_DRAW_POINT = 4;
    private static final int WHAT_DRAW_LINE = 5;
    private static final int WHAT_CLEAR = 6;

    private Socket mSocket;
    private DataOutputStream mOutput;
    private ClientHandler mHanlder;
    private HandlerThread mThread;
    private Context mContext;

    private class ClientHandler extends Handler {
        public ClientHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            int what = msg.what;
            try {
                mOutput.writeInt(what);
                switch (what) {
                    case WHAT_NEW_WORD:
                        mOutput.writeUTF(msg.obj.toString());
                        break;
                    case WHAT_UPDATE_TIME:
                        mOutput.writeUTF(msg.obj.toString());
                        break;
                    case WHAT_DRAW_POINT:
                    case WHAT_DRAW_LINE:
                        float[] xy = (float[]) msg.obj;
                        mOutput.writeFloat(xy[0]);
                        mOutput.writeFloat(xy[1]);
                        break;
                    case WHAT_CLEAR:
                        break;
                }
                mOutput.flush();

            } catch (IOException e) {
                Log.e("chk", e.toString());
                close();
            }
        }
    }

    public Client(Context c) {
        mContext = c;
    }

    public void close() {
        if (mOutput != null) {
            try {
                mOutput.close();
            } catch (IOException e) {
                Log.e("chk", e.toString());
            }
            mOutput = null;
        }
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e("chk", e.toString());
            }
            mSocket = null;
        }

        if (mThread != null) {
            mThread.quit();
            mThread = null;
        }
        mHanlder = null;
        mContext = null;
    }

    @Override
    public void run() {
        String ip = getServerAddress(mContext);
        try {
            mSocket = new Socket(ip, 55555);
            mOutput = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
        } catch (Exception e) {
            Log.e("chk", e.toString());
            return;
        }
        mThread = new HandlerThread("Client");
        mThread.start();
        mHanlder = new ClientHandler(mThread.getLooper());
    }

    @SuppressWarnings("deprecation")
    public String getServerAddress(Context ctx) {
        WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);  
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();  
        // dhcpInfo.gateway;
        return Formatter.formatIpAddress(dhcpInfo.serverAddress);  
    }

    public void onNewWord(final String word) {
        if (mThread != null && mThread.isAlive() && mHanlder != null) {
            mHanlder.obtainMessage(WHAT_NEW_WORD, word).sendToTarget();
        }
    }

    public void onUpdateTime(final String time) {
        if (mThread != null && mThread.isAlive() && mHanlder != null) {
            mHanlder.obtainMessage(WHAT_UPDATE_TIME, time).sendToTarget();
        }
    }

    public void onDrawPoint(final float xScale, final float yScale) {
        if (mThread != null && mThread.isAlive() && mHanlder != null) {
            mHanlder.obtainMessage(WHAT_DRAW_POINT, new float[] { xScale, yScale }).sendToTarget();
        }
    }

    public void onDrawLine(final float xScale, final float yScale) {
        if (mThread != null && mThread.isAlive() && mHanlder != null) {
            mHanlder.obtainMessage(WHAT_DRAW_LINE, new float[] { xScale, yScale }).sendToTarget();
        }
    }

    public void onClear() {
        if (mThread != null && mThread.isAlive() && mHanlder != null) {
            mHanlder.obtainMessage(WHAT_CLEAR).sendToTarget();
        }
    }
}
