package edu.uic.srikanth.cs478.myapplication;

import android.os.Handler;
import android.os.Message;

public class Game {

    private static int[][] gameBoard;
    public static int handlerInit;
    private static Handler mainHandler;

    public Game(Handler mainHandler) {
        //set handler variable to 0
        handlerInit = 0;
        //init the game board
        createGameBoard();
        Game.mainHandler = mainHandler;
    }

     int isGameOver() {
        int[][] board = Game.getGameBoard();
        for(int i = 0; i < Constants.WIDTH; i++) {
            int x = board[i][0];
            int y = board[0][i];
            int countX = 0;
            int countY = 0;
            // Check Horizontally and Vertically and get Count
            for (int j = 0; j < Constants.HEIGHT; j++) {
                if (board[i][j] == x)
                    countX++;
                if (board[j][i] == y)
                    countY++;
            }
            if (x != Constants.EMPTY && countX == Constants.TOTAL_MOVES) {
                return x;
            }
            if (y != Constants.EMPTY && countY == Constants.TOTAL_MOVES) {
                return y;
            }
        }
        return -1;

    }
    private void createGameBoard() {
        gameBoard = new int[Constants.WIDTH][Constants.HEIGHT];
        for(int i = 0; i < Constants.WIDTH; i++)
            for(int j = 0; j < Constants.HEIGHT; j++)
                gameBoard[i][j] = Constants.EMPTY;
    }
    public synchronized static void movePiece(PositionData oldPosition, PositionData newPosition) {
        // Wait for 2 sec so that user can move
        try{
            Thread.sleep(2000);
        }catch(InterruptedException i) {
           System.out.println(i.getMessage());
        }
        //get position values
        int oldX = oldPosition.getPosX();
        int oldY = oldPosition.getPosY();

        int newX = newPosition.getPosX();
        int newY = newPosition.getPosY();
        if(oldX != -1)
            gameBoard[oldX][oldY] = Constants.EMPTY;

        // Get Player ID in the new Location
        gameBoard[newX][newY] = newPosition.getPlayerId();
        oldPosition.setPosX(newX);
        oldPosition.setPosY(newY);

        //Send Message that Move is completed
        Message msg = mainHandler.obtainMessage(Constants.MOVE_COMPLETED, oldX, oldY, oldPosition);
        mainHandler.sendMessage(msg);
    }
    public synchronized static int[][] getGameBoard() {
        return gameBoard;
    }
}
