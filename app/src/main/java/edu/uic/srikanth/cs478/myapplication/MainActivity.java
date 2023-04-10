package edu.uic.srikanth.cs478.myapplication;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

    private static TextView[][] gameBoard;
    private static Thread playerOneThread;
    private static Thread playerTwoThread;
    private TextView resultView;

    private Game game;

    //create handler for UI thread
    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case Constants.MOVE_COMPLETED:
                    int oldX = msg.arg1;
                    int oldY = msg.arg2;
                    MoveData newPosition = (MoveData)msg.obj;
                    if(oldX != -1)
                        gameBoard[oldX][oldY].setAlpha(0.0f);

                    //get positions
                    int newX = newPosition.getPosX();
                    int newY = newPosition.getPosY();

                    Log.i("Main", String.valueOf(newPosition.getPlayerId()));

                    //place piece
                    if(newPosition.getPlayerId() == Constants.PLAYER_One_ID)
                        gameBoard[newX][newY].setText("X");
                    else
                        gameBoard[newX][newY].setText("O");

                    //make visible
                    gameBoard[newX][newY].setAlpha(1.0f);

                    //check if Game id Over
                    int id = game.isGameOver();

                    Log.i("Main", String.valueOf("isGameOver" + id));

                    // If Game is over Check for Win and send Toast Message
                    if(id != -1) {
                        closeThreads();
                        if(id == Constants.PLAYER_One_ID) {
                            Toast.makeText(MainActivity.this, "X is the Winner!!", Toast.LENGTH_LONG).show();
                            updateResultView("Result : Player X Won");
                        }
                        else {
                            Toast.makeText(MainActivity.this, "O is the Winner!!", Toast.LENGTH_LONG).show();
                            updateResultView("Result : Player O Won");
                        }
                        return;
                    }
                    // Check for the Player and trigger respective handler
                    if(newPosition.getPlayerId() == Constants.PLAYER_Two_ID)
                        PlayerOne.playerOneHandler.sendMessage(PlayerOne.playerOneHandler.obtainMessage(Constants.MOVE_COMPLETED, oldX, oldY, newPosition));
                    else
                        PlayerTwo.playerTwoHandler.sendMessage(PlayerTwo.playerTwoHandler.obtainMessage(Constants.MOVE_COMPLETED, oldX, oldY, newPosition));
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
        playerOneThread = null;
        playerTwoThread = null;
        //initialize the  Image Views
        gameBoard = new TextView[Constants.WIDTH][Constants.HEIGHT];
        gameBoard[0][0] = findViewById(R.id.colA1);
        gameBoard[0][1] = findViewById(R.id.colA2);
        gameBoard[0][2] = findViewById(R.id.colA3);
        gameBoard[1][0] = findViewById(R.id.colB1);
        gameBoard[1][1] = findViewById(R.id.colB2);
        gameBoard[1][2] = findViewById(R.id.colB3);
        gameBoard[2][0] = findViewById(R.id.colC1);
        gameBoard[2][1] = findViewById(R.id.colC2);
        gameBoard[2][2] = findViewById(R.id.colC3);
        resultView = findViewById(R.id.result_view);
    }

    public void updateResultView(String result) {
        resultView.setText(result);
    }
    public void onStartGameClicked(View view) {
        if(playerOneThread != null)
            closeThreads();
        game = new Game(mainHandler);
        // Start Threads
        updateResultView("");
        startThreads();
        // Clear Previous Game
        clearGame();
        // Start New Game
        beginGame();
    }
    private void clearGame() {
        for(int i = 0; i < Constants.WIDTH; i++)
            for(int j = 0; j < Constants.HEIGHT; j++)
                gameBoard[i][j].setAlpha(0.0f);
    }
    private void startThreads() {
        playerOneThread = new Thread(new PlayerOne());
        playerTwoThread = new Thread(new PlayerTwo());
    }
    private void closeThreads() {
        // Close Loops
        PlayerOne.playerOneHandler.post(PlayerOne::closeLoop);
        PlayerTwo.playerTwoHandler.post(PlayerTwo::closeLoop);
        while(playerOneThread.isAlive());
        while(playerTwoThread.isAlive());
        playerOneThread = null;
        playerTwoThread = null;

        //clear main handler queue
        mainHandler.removeCallbacksAndMessages(null);
    }
    private void beginGame() {
        // New Game with handler count zero
        Game.handlerInit = 0;
        //starting player threads
        playerOneThread.start();
        playerTwoThread.start();
        // wait for threads to start
        while(Game.handlerInit < 2);
        // Always First player is started
        PlayerOne.playerOneHandler.sendMessage(PlayerOne.playerOneHandler.obtainMessage(Constants.MOVE_COMPLETED));
    }

}
