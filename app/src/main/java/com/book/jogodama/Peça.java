package com.book.jogodama;

import android.graphics.RectF;

import com.book.simplegameenginev1.SGImage;

public class Peça {


    private SGImage imagemPeça;
    boolean rainha = false;
    private int chaveDaPeça;
    private RectF posicaoPeça; // posição da imagem
    private int posX; //posição da peça na matriz X
    private int posY; // posição da peça na matriz Y;
    private int jogador;


    public SGImage getImagemPeça(){return imagemPeça;}
    public boolean isRainha(){return rainha;}
    public int getChaveDaPeça(){return chaveDaPeça;}
    public RectF getPosicaoPeça() { return posicaoPeça; }
    public int getPosX(){return posX;}
    public int getPosY(){return posY;}
    public int getJogador(){return jogador;}

    public void setJogador(int jog){jogador = jog;}
    public void setRainha(boolean rainha){ this.rainha = rainha;}
    public void setImagemPeça(SGImage imagem){this.imagemPeça = imagem;}
    public void setChaveDaPeça(int chave){chaveDaPeça = chave;}

    public void setPosicaoPeça(RectF posicao){posicaoPeça = posicao;}

    //posição da peça na matriz

    public void setXY(int x, int y){

        this.posX = x;
        this.posY = y;
    }
}
