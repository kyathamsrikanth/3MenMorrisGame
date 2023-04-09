/*
*   Author: Arbaaz Meghani
*   Description: This is the main activity which initializes the game and the thread.  It also manages
*                   the UI.
 */

//package
package edu.uic.srikanth.cs478.myapplication;

//import statements
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

    //store threads and imageviews
    private static TextView[][] gameBoard;
    private static Thread playerAThread;
    private static Thread playerBThread;

    private Game game;

    //create handler for UI thread
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case Constants.MADE_MOVE:
                    updateUI((PositionData)msg.obj, msg.arg1, msg.arg2);
                    break;
                default: break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set threads to null
        playerAThread = null;
        playerBThread = null;
        //initialize the imageviews
        initGameBoard();
    }

    /*
    *   Function: initialize the image views
    *   Parameters: none
    *   Return: none
     */
    private void initGameBoard() {
        gameBoard = new TextView[Constants.WIDTH][Constants.HEIGHT];
        findImageViews();
    }

    /*
    *   Function: Update the UI
    *   Parameters: Old Position of piece and new position of piece
    *   Return: none
     */
    private void updateUI(PositionData newPosition, int oldX, int oldY) {
        //check if newly placed
        if(oldX != -1)
            gameBoard[oldX][oldY].setAlpha(0.0f);

        //get positions
        int newX = newPosition.getPosX();
        int newY = newPosition.getPosY();

        //Log.i("main", String.valueOf(newPosition.getPlayerId()));

        //place piece
        if(newPosition.getPlayerId() == Constants.PLAYER_A_ID)
            gameBoard[newX][newY].setText("X");
        else
            gameBoard[newX][newY].setText("O");

        //make visible
        gameBoard[newX][newY].setAlpha(1.0f);

        //check for victory
        int id = game.checkVictoryCondition();

        //if victory reached then break out of function and call victory reached
        if(id != -1) {
            victoryReached(id, this);
            return;
        }

        //otherwise get next move
        nextMove(newPosition, oldX, oldY);

    }

    /*
    *   Function: get the next move
    *   Parameters: Old Position of piece and new position of piece
    *   Return: none
     */
    private static void nextMove(PositionData newPosition, int oldX, int oldY) {
        if(newPosition.getPlayerId() == Constants.PLAYER_B_ID)
            PlayerA.playerAHandler.sendMessage(PlayerA.playerAHandler.obtainMessage(Constants.MADE_MOVE, oldX, oldY, newPosition));
        else
            PlayerB.playerBHandler.sendMessage(PlayerB.playerBHandler.obtainMessage(Constants.MADE_MOVE, oldX, oldY, newPosition));
    }

    /*
    *   Function: Find all the imageviews
    *   Parameters: none
    *   Return: none
     */
    private void findImageViews() {
        gameBoard[0][0] = (TextView)findViewById(R.id.colA1);
        gameBoard[0][1] = (TextView)findViewById(R.id.colA2);
        gameBoard[0][2] = (TextView)findViewById(R.id.colA3);
        gameBoard[1][0] = (TextView)findViewById(R.id.colB1);
        gameBoard[1][1] = (TextView)findViewById(R.id.colB2);
        gameBoard[1][2] = (TextView)findViewById(R.id.colB3);
        gameBoard[2][0] = (TextView)findViewById(R.id.colC1);
        gameBoard[2][1] = (TextView)findViewById(R.id.colC2);
        gameBoard[2][2] = (TextView)findViewById(R.id.colC3);
    }

    /*
    *   Function: start button on click handler
    *   Parameters: the view
    *   Return: none
     */
    public void onStartGameClicked(View view) {
        if(playerAThread != null)
            stopThreads();
        game = new Game(mainHandler);
        initThreads();
        clearGameBoard();
        startGame();
    }

    /*
    *   Function: clear the game board by setting everything to invisible
    *   Parameters: none
    *   Return: none
     */
    private void clearGameBoard() {
        for(int i = 0; i < Constants.WIDTH; i++)
            for(int j = 0; j < Constants.HEIGHT; j++)
                gameBoard[i][j].setAlpha(0.0f);
    }

    /*
    *   Function: create new threads
    *   Parameters: none
    *   Return: none
     */
    private void initThreads() {
        playerAThread = new Thread(new PlayerA());
        playerBThread = new Thread(new PlayerB());
    }

    /*
    *   Function: stop threads
    *   Parameters: none
    *   Return: none
     */
    private void stopThreads() {
        //post runnables to exit threads
        PlayerA.playerAHandler.post(new Runnable() {
            @Override
            public void run() {
                PlayerA.quitLooper();
            }
        });
        PlayerB.playerBHandler.post(new Runnable() {
            @Override
            public void run() {
                PlayerB.quitLooper();
            }
        });

        //make sure threads are dead
        while(playerAThread.isAlive());
        while(playerBThread.isAlive());

        //set threads to null
        playerAThread = null;
        playerBThread = null;

        //clear ui queue
        mainHandler.removeCallbacksAndMessages(null);
    }

    /*
    *   Function: start the game
    *   Parameters: none
    *   Return: none
     */
    private void startGame() {
        //set looper init count to 0
        Game.handlerInit = 0;
        //start threads
        playerAThread.start();
        playerBThread.start();

        //get starting thread
        int startingThread = new Random().nextInt(2);
        //wait for loopers to initialize
        while(Game.handlerInit < 2);

        if(startingThread == 0)
            PlayerA.playerAHandler.sendMessage(PlayerA.playerAHandler.obtainMessage(Constants.MADE_MOVE));
        else
            PlayerB.playerBHandler.sendMessage(PlayerB.playerBHandler.obtainMessage(Constants.MADE_MOVE));
    }

    /*
    *   Function: check for victory condition
    *   Parameters: none
    *   Return: id of winner or -1
     */


    /*
    *   Function: print toast of victor
    *   Parameters: the id of the winner and the activities context
    *   Return: none
     */
    private void victoryReached(int winnerId, Context activity) {
        stopThreads();
        if(winnerId == Constants.PLAYER_A_ID)
            Toast.makeText(activity, "X is the Winner!!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(activity, "O is the Winner!!", Toast.LENGTH_LONG).show();
    }

}
