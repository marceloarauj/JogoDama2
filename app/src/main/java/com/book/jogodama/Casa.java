package com.book.jogodama;

import android.graphics.RectF;

public class Casa {

    private Peça peça;
    private RectF posicao;



    public void setPosicao(RectF pos){this.posicao = pos;}
    public void setPeça(Peça p){peça = p;}
    public void removePeça(){peça = null;}

    public RectF getPosicao() { return posicao; }
    public Peça getPeça() { return peça; }
}
