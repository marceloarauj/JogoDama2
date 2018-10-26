package com.book.jogodama;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import com.book.simplegameenginev1.SGImage;
import com.book.simplegameenginev1.SGImageFactory;
import com.book.simplegameenginev1.SGRenderer;
import com.book.simplegameenginev1.SGView;

public class VisaoMenu extends SGView {

    public VisaoMenu(Context context) {
        super(context);
    }

    SGImage fundo;
    SGImage solo;
    SGImage multiplayer;
    SGImage sair;

    RectF fundo1 = new RectF();
    RectF solo1 = new RectF();
    RectF multiplayer1 = new RectF();
    RectF sair1 = new RectF();

    Rect retang = new Rect();

    @Override
    protected void setup() {

        SGImageFactory imageFactory = getImageFactory();

        //definir as imagens do menu
        fundo = imageFactory.createImage(R.drawable.menufundo);
        solo = imageFactory.createImage(R.drawable.solo);
        multiplayer = imageFactory.createImage(R.drawable.multiplayer);
        sair = imageFactory.createImage(R.drawable.sair);

        // definir o retangulo do fundo e de cada botão

        Point dimensoesImg = fundo.getDimensions();
        Point centro = new Point();

        centro.set(getDimensions().x /2, getDimensions().y/2);

        fundo1.set(0,0,getDimensions().x,getDimensions().y);

        dimensoesImg = multiplayer.getDimensions();

        multiplayer1.set(centro.x - dimensoesImg.x/2,
                         centro.y+0,
                        centro.x+dimensoesImg.x/2,
                     centro.y + dimensoesImg.y);

        dimensoesImg = solo.getDimensions();


        solo1.set(centro.x - dimensoesImg.x/2,
                multiplayer1.top - dimensoesImg.y - (dimensoesImg.y/2),
                centro.x+dimensoesImg.x/2,
                multiplayer1.top - dimensoesImg.y - (dimensoesImg.y/2) + dimensoesImg.y);

        dimensoesImg = sair.getDimensions();

        sair1.set(centro.x - dimensoesImg.x/2,
                multiplayer1.bottom + (dimensoesImg.y/2) ,
                centro.x+dimensoesImg.x/2,
                multiplayer1.bottom + (dimensoesImg.y/2)+ dimensoesImg.y);

    }

    @Override
    public void step(Canvas canvas, float elapsedTimeinSeconds) {

        SGRenderer renderizador = getRenderer();
        renderizador.beginDrawing(canvas, Color.BLACK);

        retang.set(0,0,fundo.getDimensions().x,fundo.getDimensions().y);
        renderizador.drawImage(fundo,retang,fundo1);

        retang.set(0,0,sair.getDimensions().x,sair.getDimensions().y);
        renderizador.drawImage(sair,retang,sair1);

        retang.set(0,0,solo.getDimensions().x,solo.getDimensions().y);
        renderizador.drawImage(solo,retang,solo1);

        retang.set(0,0,multiplayer.getDimensions().x,multiplayer.getDimensions().y);
        renderizador.drawImage(multiplayer,retang,multiplayer1);

    }


    public Acao selecionarOpcao(float x, float y){


        if(x >=  solo1.left & y>= solo1.top & x <= solo1.right & y <= solo1.bottom){

            return Acao.Solo;

        }else

        if(x >=  sair1.left & y>= sair1.top & x <= sair1.right & y <= sair1.bottom){

           return Acao.Sair;

        }else

        if(x >=  multiplayer1.left & y>= multiplayer1.top &
                x <= multiplayer1.right & y <= multiplayer1.bottom){

          return Acao.Multiplayer;
        }

        return null;
    }

    public enum Acao{

        Solo, Multiplayer, Sair
    }

}
