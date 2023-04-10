package edu.uic.srikanth.cs478.myapplication;

import android.annotation.SuppressLint;
import android.os.Looper;
import java.util.Random;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PlayerTwo implements Runnable {

    //instance variables
    private int numPieces = 0;
    private MoveData[] pieces = null;
    public static Handler playerTwoHandler;

    //keep track of rows
    int[][] gameMatrix;

    //run method
    public void run() {
        Looper.prepare();
        createHandler();
        //init Game Matrix
        initGameMatrix();
        Game.handlerInit++;
        Looper.loop();
    }

    private void initGameMatrix() {
        // Initiate matrix with Zero values
        gameMatrix = new int[Constants.WIDTH][Constants.HEIGHT];
        for(int i = 0; i < Constants.WIDTH; i++)
            for(int j = 0; j < Constants.HEIGHT; j++)
                gameMatrix[i][j] = 0;
    }

    @SuppressLint("HandlerLeak")
    private void createHandler() {
        playerTwoHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case Constants.MOVE_COMPLETED:
                        updateMatrix(new MoveData(msg.arg1, msg.arg2, Constants.PLAYER_One_ID), (MoveData)msg.obj);
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
                pieces = new MoveData[Constants.TOTAL_MOVES];
            placePiece();
            return;
        }
        // get random piece
        int pieceNumber = new Random().nextInt(Constants.TOTAL_MOVES);
        // get position by ReverseRowMajorTMMStrategy
        int[] pos = ReverseRowMajorStrategy();
        Log.i("PlayerTwo", pos[0] +String.valueOf(pos[1]));
        MoveData newPosition = new MoveData(pos[0], pos[1], Constants.PLAYER_Two_ID);
        updateMatrix(pieces[pieceNumber], newPosition);
        Game.movePiece(pieces[pieceNumber], newPosition);
    }


    public int[] ReverseRowMajorStrategy() {
        for (int row = Constants.HEIGHT -1 ; row >= 0; row--) {
            for (int col = Constants.WIDTH - 1; col >= 0; col--) {
                if (Game.getGameBoard()[row][col] == Constants.EMPTY) {
                    return new int[] { row, col };
                }
            }
        }
        // If no empty square found, return an invalid move
        return new int[] { -1, -1 };
    }
    private void placePiece() {
        pieces[numPieces] = getRandomPoint();
        Game.movePiece(new MoveData(-1,-1,Constants.PLAYER_Two_ID), pieces[numPieces]);
        updateMatrix(new MoveData(-1,-1,Constants.PLAYER_Two_ID), pieces[numPieces]);
        numPieces++;
    }
    private void updateMatrix(MoveData oldPos, MoveData newPos) {
        if(newPos == null)
            return;
        int oldX = oldPos.getPosX();
        int oldY = oldPos.getPosY();
        int newX = newPos.getPosX();
        int newY = newPos.getPosY();
        if(oldX != -1)
            gameMatrix[oldX][oldY] = 0;
        if(newPos.getPlayerId() == Constants.PLAYER_Two_ID)
            gameMatrix[newX][newY] = 1;
        else
            gameMatrix[newX][newY] = -1;
    }
    private MoveData getRandomPoint() {
        int[] move = new int[2];
        Random random = new Random();
        do {
            move[0] = random.nextInt(3);
            move[1] = random.nextInt(3);
        } while (!isMoveValid(move[0], move[1]));
        Log.i("PlayerTwo", move[0] +String.valueOf(move[1]));
        return new MoveData(move[0], move[1], Constants.PLAYER_Two_ID);
    }
    public boolean isMoveValid(int row, int col) {
        return (row >= 0 && row < 3 && col >= 0 && col < 3 && Game.getGameBoard()[row][col] == 0);
    }

    public static void closeLoop() {
        playerTwoHandler.removeCallbacksAndMessages(null);
        playerTwoHandler.getLooper().quit();
    }


}
