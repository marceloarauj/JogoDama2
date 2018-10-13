package com.book.jogodama;

import android.graphics.RectF;

public class Casa {

    private Peça peça;
    private RectF posicao;
    private int x;
    private int y;

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setPosicao(RectF pos){this.posicao = pos;}
    public void setPeça(Peça p){peça = p;}
    public void removePeça(){peça = null;}

    public int getX(){return x;}
    public int getY(){return y;}
    public RectF getPosicao() { return posicao; }
    public Peça getPeça() { return peça; }
}
