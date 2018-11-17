package com.book.jogodama;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.book.simplegameenginev1.SGAcivity;

public class AtividadeMenu extends SGAcivity {

    private VisaoMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableFullScreen();

        menu = new VisaoMenu(this);

        setContentView(menu);

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getActionMasked() == MotionEvent.ACTION_UP) {

            Enum e = menu.selecionarOpcao(event.getX(),event.getY());

            if(e == VisaoMenu.Acao.Solo){

                Intent intent = new Intent(this, AtividadeJogo.class);
                startActivity(intent);

            }else if(e == VisaoMenu.Acao.Multiplayer){

                Intent inter = new Intent(this,AtividadeMultiplayer.class);
                startActivity(inter);

            }else if (e == VisaoMenu.Acao.Sair){

                //this.finish();
                System.exit(0);
            }

        }


        return true;
    }
}
