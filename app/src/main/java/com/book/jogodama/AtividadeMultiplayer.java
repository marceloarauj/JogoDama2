package com.book.jogodama;

import android.os.Bundle;
import android.view.MotionEvent;

import com.book.simplegameenginev1.SGAcivity;

public class AtividadeMultiplayer extends SGAcivity {

    private VisaoMultiplayer vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableFullScreen();

        vm = new VisaoMultiplayer(this,this);

        setContentView(vm);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {

            vm.realizarJogada(event.getX(),event.getY());
        }

        return true;

    }
}
