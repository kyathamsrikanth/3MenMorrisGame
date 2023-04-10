package edu.uic.srikanth.cs478.myapplication;
import android.annotation.SuppressLint;
import android.os.Looper;
import java.util.Random;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PlayerOne implements Runnable {

    private int numPieces = 0;
    private PositionData[] pieces = null;
    public static Handler playerOneHandler;
    public void run() {
        Looper.prepare();
        createHandler();
        Game.handlerInit++;
        Looper.loop();
    }
    @SuppressLint("HandlerLeak")
    private void createHandler() {
        playerOneHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case Constants.MOVE_COMPLETED:
                        makeMove();
                        break;
                    default: break;
                }
            }
        };
    }
    private void makeMove() {
        if(numPieces < 3) {
            if(pieces == null)
                pieces = new PositionData[Constants.TOTAL_MOVES];
            placePiece();
            return;
        }
        int pieceNumber = getRandomPiece();
        PositionData randomPoint = getRandomPoint();
        Game.movePiece(pieces[pieceNumber], randomPoint);
    }
    private void placePiece() {
        pieces[numPieces] = getRandomPoint();
        Game.movePiece(new PositionData(-1,-1,Constants.PLAYER_One_ID), pieces[numPieces]);
        numPieces++;
    }
    private int getRandomPiece() {
        return new Random().nextInt(Constants.TOTAL_MOVES);
    }
    private PositionData getRandomPoint() {

        int[] move = new int[2];
        Random random = new Random();
        do {
            move[0] = random.nextInt(3);
            move[1] = random.nextInt(3);
        } while (!isMoveValid(move[0], move[1]));
        Log.i("PlayerOne", move[0] +String.valueOf(move[1]));
        return new PositionData(move[0], move[1], Constants.PLAYER_One_ID);
    }
    public boolean isMoveValid(int row, int col) {
        return (row >= 0 && row < 3 && col >= 0 && col < 3 && Game.getGameBoard()[row][col] == 0);
    }

    public static void closeLoop() {
        playerOneHandler.removeCallbacksAndMessages(null);
        playerOneHandler.getLooper().quit();
    }
}
