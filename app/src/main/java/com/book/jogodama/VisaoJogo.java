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

import java.util.ArrayList;

public class VisaoJogo extends SGView {

    public VisaoJogo(Context context) {
        super(context);
    }

    private ArrayList<Peça> peçasVermelhas = new ArrayList<>();
    private ArrayList<Peça> peçasBrancas = new ArrayList<>();
    private Casa[][] casa = new Casa[8][8];

    private SGImage tabuleiro;

    private int larguraTabuleiro = 680;
    private int alturaTabuleiro = 680;
    private int larguraCasa = larguraTabuleiro/8;
    private int alturaCasa = alturaTabuleiro/8;

    private Rect mTempImageSource = new Rect();

    private RectF posicaoTabuleiro = new RectF();
    private RectF posicaoPeça = new RectF();

    Point inicioTabuleiro = new Point();

    @Override
    protected void setup(){ // usado para criar as configurações do desenho

        SGImageFactory imageFactory = getImageFactory();
        //adicionar imagem do tabuleiro
        tabuleiro = imageFactory.createImage(R.drawable.tabuleiro);
        // adicionar imagem das peças vermelhas
        for(int i =0; i < 12; i++){

            Peça peça = new Peça();
            peça.setImagemPeça(imageFactory.createImage(R.drawable.pvermelharainha));
            peça.setChaveDaPeça(i);
            peçasVermelhas.add(peça);

        }
        // adicionar imagem das peças brancas
        for(int i =0; i < 12; i++){

            Peça peça = new Peça();
            peça.setImagemPeça(imageFactory.createImage(R.drawable.pbrancarainha));
            peça.setChaveDaPeça(i);
            peçasBrancas.add(peça);

        }

        Point viewDimensions = getDimensions();
        //bolinha no centro da tela
        Point viewCenter = new Point(viewDimensions.x / 2, viewDimensions.y/2);

       // posição do tabuleiro na tela
        int esquerda = viewCenter.x - larguraTabuleiro/2;
        int cima= viewCenter.y - alturaTabuleiro/2;
        int direita=viewCenter.x+larguraTabuleiro/2;
        int baixo=viewCenter.y+alturaTabuleiro/2;

        posicaoTabuleiro.set(esquerda,cima,direita,baixo);


        //configurarTabuleiro();
        configurarPeças(viewCenter);

    }

    @Override
    public void step(Canvas canvas, float elapsedTimeInSeconds) { // agora utilizando o SGRenderer para desenhar

        //metodo step é sobrescrito da classe SGView e vai desenhar os retangulos
        // metodo do loop de jogo

        SGRenderer renderer = getRenderer();
        renderer.beginDrawing(canvas, Color.BLACK);

        //mTempImageSource = tamanho do tabuleiro => (0,0) até (486,440)
        //posicao do tabuleiro é iniciada em setup e fica no meio
        mTempImageSource.set(0,0,larguraTabuleiro,alturaTabuleiro);
        renderer.drawImage(tabuleiro, mTempImageSource,posicaoTabuleiro);

        for(int i =0; i < peçasVermelhas.size();i++) {
            mTempImageSource.set(0, 0, 61, 62);
            renderer.drawImage(peçasVermelhas.get(i).getImagemPeça(), mTempImageSource, peçasVermelhas.get(i).getPosicaoPeça());

        }
        for(int i =0; i < peçasVermelhas.size();i++) {
            mTempImageSource.set(0, 0, 61, 62);
            renderer.drawImage(peçasBrancas.get(i).getImagemPeça(), mTempImageSource, peçasBrancas.get(i).getPosicaoPeça());

        }
        //finalizar desenho
        renderer.endDrawing();
    }

    public void configurarTabuleiro(){

        // posição das peças vermelhas na matriz
        casa[0][0].setPeça(peçasVermelhas.get(0));
        casa[2][0].setPeça(peçasVermelhas.get(1));
        casa[4][0].setPeça(peçasVermelhas.get(2));
        casa[6][0].setPeça(peçasVermelhas.get(3));
        casa[1][1].setPeça(peçasVermelhas.get(4));
        casa[3][1].setPeça(peçasVermelhas.get(5));
        casa[5][1].setPeça(peçasVermelhas.get(6));
        casa[7][1].setPeça(peçasVermelhas.get(7));
        casa[0][2].setPeça(peçasVermelhas.get(8));
        casa[2][2].setPeça(peçasVermelhas.get(9));
        casa[4][2].setPeça(peçasVermelhas.get(10));
        casa[6][2].setPeça(peçasVermelhas.get(11));

        // posição das peças brancas na matriz

        casa[1][5].setPeça(peçasBrancas.get(0));
        casa[3][5].setPeça(peçasBrancas.get(1));
        casa[5][5].setPeça(peçasBrancas.get(2));
        casa[7][5].setPeça(peçasBrancas.get(3));
        casa[0][6].setPeça(peçasBrancas.get(4));
        casa[2][6].setPeça(peçasBrancas.get(5));
        casa[4][6].setPeça(peçasBrancas.get(6));
        casa[6][6].setPeça(peçasBrancas.get(7));
        casa[1][7].setPeça(peçasBrancas.get(8));
        casa[3][7].setPeça(peçasBrancas.get(9));
        casa[5][7].setPeça(peçasBrancas.get(10));
        casa[7][7].setPeça(peçasBrancas.get(11));
    }

    public void configurarPeças(Point point){

        // posicionar peças, encontrar fórmula matematica
       Point p = new Point();

       p.x = point.x - larguraTabuleiro /2 ;
       p.y = point.y - alturaTabuleiro / 2 ;

       RectF rec = new RectF();

       rec = calcularPosicao(p,1,1);
       peçasVermelhas.get(0).setPosicaoPeça(rec);

        rec = calcularPosicao(p,5,1);
        peçasVermelhas.get(1).setPosicaoPeça(rec);

        rec = calcularPosicao(p,9,1);
        peçasVermelhas.get(2).setPosicaoPeça(rec);

        rec = calcularPosicao(p,13,1);
        peçasVermelhas.get(3).setPosicaoPeça(rec);

        rec = calcularPosicao(p,3,3);
        peçasVermelhas.get(4).setPosicaoPeça(rec);

        rec = calcularPosicao(p,7,3);
        peçasVermelhas.get(5).setPosicaoPeça(rec);

        rec = calcularPosicao(p,11,3);
        peçasVermelhas.get(6).setPosicaoPeça(rec);

        rec = calcularPosicao(p,15,3);
        peçasVermelhas.get(7).setPosicaoPeça(rec);

        rec = calcularPosicao(p,1,5);
        peçasVermelhas.get(8).setPosicaoPeça(rec);

        rec = calcularPosicao(p,5,5);
        peçasVermelhas.get(9).setPosicaoPeça(rec);

        rec = calcularPosicao(p,9,5);
        peçasVermelhas.get(10).setPosicaoPeça(rec);

        rec = calcularPosicao(p,13,5);
        peçasVermelhas.get(11).setPosicaoPeça(rec);

        // peças brancas

        rec = calcularPosicao(p,3,11);
        peçasBrancas.get(0).setPosicaoPeça(rec);

        rec = calcularPosicao(p,7,11);
        peçasBrancas.get(1).setPosicaoPeça(rec);

        rec = calcularPosicao(p,11,11);
        peçasBrancas.get(2).setPosicaoPeça(rec);

        rec = calcularPosicao(p,15,11);
        peçasBrancas.get(3).setPosicaoPeça(rec);

        rec = calcularPosicao(p,1,13);
        peçasBrancas.get(4).setPosicaoPeça(rec);

        rec = calcularPosicao(p,5,13);
        peçasBrancas.get(5).setPosicaoPeça(rec);

        rec = calcularPosicao(p,9,13);
        peçasBrancas.get(6).setPosicaoPeça(rec);

        rec = calcularPosicao(p,13,13);
        peçasBrancas.get(7).setPosicaoPeça(rec);

        rec = calcularPosicao(p,3,15);
        peçasBrancas.get(8).setPosicaoPeça(rec);

        rec = calcularPosicao(p,7,15);
        peçasBrancas.get(9).setPosicaoPeça(rec);

        rec = calcularPosicao(p,11,15);
        peçasBrancas.get(10).setPosicaoPeça(rec);

        rec = calcularPosicao(p,15,15);
        peçasBrancas.get(11).setPosicaoPeça(rec);
    }

    private RectF calcularPosicao(Point p,int x, int y){

        Point p1 = new Point();

        p1.x = p.x + (larguraTabuleiro/16) *x;
        p1.y = p.y + (alturaTabuleiro/16) * y;

        float esquerda = p1.x - 61/2;
        float cima = p1.y - 62/2;
        float direita=p1.x +61/2;
        float baixo= p1.y + 62/2;

        return new RectF(esquerda,cima,direita,baixo);
    }
}
