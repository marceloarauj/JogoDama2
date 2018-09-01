package com.book.jogodama;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.book.simplegameenginev1.SGAcivity;
import com.book.simplegameenginev1.SGPreferences;

public class AtividadeJogo extends SGAcivity {
    private VisaoJogo visao; //visão do jogo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableFullScreen(); // tela cheia
        enableKeepScreenOn(); // tela acesa

        visao = new VisaoJogo(this);// passa essa atividade como contexto para gameview

        setContentView(visao);


        SGPreferences preferences = new SGPreferences(this);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       // visao.movePlayer(event.getX(),event.getY()); // movimentos
        return true;
    }
}
