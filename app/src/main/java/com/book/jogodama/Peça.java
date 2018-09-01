package com.book.jogodama;

import android.graphics.RectF;

import com.book.simplegameenginev1.SGImage;

public class Peça {

    private final  int larguraPeça = 61;
    private final  int alturaPeça = 62;
    private SGImage imagemPeça;
    boolean rainha = false;
    private int chaveDaPeça;
    private RectF posicaoPeça;

    public int getLargura(){return larguraPeça;}
    public int getAltura(){return alturaPeça;}
    public SGImage getImagemPeça(){return imagemPeça;}
    public boolean isRainha(){return rainha;}
    public int getChaveDaPeça(){return chaveDaPeça;}
    public RectF getPosicaoPeça() { return posicaoPeça; }


    public void setRainha(boolean rainha){ this.rainha = rainha;}
    public void setImagemPeça(SGImage imagem){this.imagemPeça = imagem;}
    public void setChaveDaPeça(int chave){chaveDaPeça = chave;}

    public void setPosicaoPeça(RectF posicao){posicaoPeça = posicao;}
}
