package edu.uic.srikanth.cs478.myapplication;

public class MoveData {

    private int posX;
    private int playerId;

    public MoveData(int posX, int posY, int playerId) {
        this.posX = posX;
        this.posY = posY;
        this.playerId = playerId;
    }

    private int posY;

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }


}
