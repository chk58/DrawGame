package chk.android.drawgame.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class Server extends Thread {

    private static final int WHAT_NEW_WORD = 2;
    private static final int WHAT_UPDATE_TIME = 3;
    private static final int WHAT_DRAW_POINT = 4;
    private static final int WHAT_DRAW_LINE = 5;
    private static final int WHAT_CLEAR = 6;

    private ServerSocket mServerSocket;
    private boolean mIsClosed;
    private MainActivity mActivity;

    public Server(MainActivity a) {
        mActivity = a;
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(55555);
        } catch (IOException e) {
            Log.e("chk", e.toString());
            mActivity.onError(e);
            return;
        }

        Socket s = null;
        DataInputStream input = null;
        int what;
        String word;
        String time;
        float xScale, yScale;
        while (!mIsClosed) {
            try {
                s = mServerSocket.accept();
                input = new DataInputStream(new BufferedInputStream(s.getInputStream()));

                mActivity.onConnected(s.getInetAddress().getHostAddress());
                while (true) {
                    try {
                        what = input.readInt();
                        switch (what) {
                            case WHAT_NEW_WORD:
                                word = input.readUTF();
                                mActivity.onNewWord(word);
                                break;
                            case WHAT_UPDATE_TIME:
                                time = input.readUTF();
                                mActivity.onUpdateTime(time);
                                break;
                            case WHAT_DRAW_POINT:
                                xScale = input.readFloat();
                                yScale = input.readFloat();
                                mActivity.onDrawPoint(xScale, yScale);
                                break;
                            case WHAT_DRAW_LINE:
                                xScale = input.readFloat();
                                yScale = input.readFloat();
                                mActivity.onDrawLine(xScale, yScale);
                                break;
                            case WHAT_CLEAR:
                                mActivity.onClear();
                                break;
                        }
                    } catch (IOException e) {
                        Log.e("chk", e.toString());
                        mActivity.onDisconnected();
                        break;
                    }
                }
            } catch (IOException e) {
                Log.e("chk", e.toString());
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        Log.e("chk", e.toString());
                    }
                    input = null;
                }
                if (s != null) {
                    try {
                        s.close();
                    } catch (IOException e) {
                        Log.e("chk", e.toString());
                    }
                    s = null;
                }
            }
        }
    }

    public void close() {
        mIsClosed = true;
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                Log.e("chk", e.toString());
            }
            mServerSocket = null;
        }
    }
}
