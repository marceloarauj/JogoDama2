package com.book.jogodama;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

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
    private Peça peçaSelecionada = null;

    private SGImage tabuleiro;

    private boolean selecionar = false;

    private int larguraTabuleiro = 680;
    private int alturaTabuleiro = 680;

    private int larguraCasa = larguraTabuleiro/8;
    private int alturaCasa = alturaTabuleiro/8;

    private Rect mTempImageSource = new Rect();

    private RectF posicaoTabuleiro = new RectF();
    private RectF posicaoPeça = new RectF();

    Point inicioTabuleiro = new Point();
    private boolean minhaVez=true;

    //desenhar retangulos no local da movimentação
    Casa esquerda = null;
    Casa direita = null;

    @Override
    protected void setup(){ // usado para criar as configurações do desenho

        larguraTabuleiro = getDimensions().x - 40;
        alturaTabuleiro =  larguraTabuleiro;

        SGImageFactory imageFactory = getImageFactory();
        //adicionar imagem do tabuleiro
        tabuleiro = imageFactory.createImage(R.drawable.tabuleiro);
        // adicionar imagem das peças vermelhas
        for(int i =0; i < 12; i++){

            Peça peça = new Peça();
            peça.setImagemPeça(imageFactory.createImage(R.drawable.pecavermelha));
            peça.setChaveDaPeça(i);
            peça.setJogador(1);
            peçasVermelhas.add(peça);

        }
        // adicionar imagem das peças azuis
        for(int i =0; i < 12; i++){

            Peça peça = new Peça();
            peça.setImagemPeça(imageFactory.createImage(R.drawable.pecaazul));
            peça.setChaveDaPeça(i);
            peça.setJogador(2);
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



        configurarPeças(viewCenter); // posição das peças com o canvas
        configurarTabuleiro(viewCenter); // matriz
    }

    @Override
    public void step(Canvas canvas, float elapsedTimeInSeconds) { // agora utilizando o SGRenderer para desenhar

        //metodo step é sobrescrito da classe SGView e vai desenhar os retangulos
        // metodo do loop de jogo

        SGRenderer renderer = getRenderer();
        renderer.beginDrawing(canvas, Color.BLACK);

        //mTempImageSource = tamanho do tabuleiro => (0,0) até (486,440)
        //posicao do tabuleiro é iniciada em setup e fica no meio
        mTempImageSource.set(0,0,486,440);
        renderer.drawImage(tabuleiro, mTempImageSource,posicaoTabuleiro);

        // desenhar as peças em suas posições atuais

        for(int i =0; i < peçasVermelhas.size();i++) {
            mTempImageSource.set(0, 0, 61, 62);
            renderer.drawImage(peçasVermelhas.get(i).getImagemPeça(), mTempImageSource, peçasVermelhas.get(i).getPosicaoPeça());

        }
        for(int i =0; i < peçasVermelhas.size();i++) {
            mTempImageSource.set(0, 0, 61, 62);
            renderer.drawImage(peçasBrancas.get(i).getImagemPeça(), mTempImageSource, peçasBrancas.get(i).getPosicaoPeça());

        }
        //desenhar quadros amarelos nos possíveis locais de movimentação
        if(esquerda != null){
            renderer.drawRect(esquerda.getPosicao(),Color.YELLOW);
        }
        if(direita != null){
            renderer.drawRect(direita.getPosicao(), Color.YELLOW);
        }
        //finalizar desenho
        renderer.endDrawing();
    }

    public void configurarTabuleiro(Point centro){

        int x =1;
        int y=1;
        RectF posicaoCasa;
        Point referencial = new Point();

        referencial.x = centro.x - larguraTabuleiro /2;
        referencial.y = centro.y - alturaTabuleiro /2;

        for(int i =0; i < 8; i++){

            y = 1;

            for(int j =0; j < 8; j++){

                posicaoCasa = this.calcularPosicao(referencial,x,y);

                casa[i][j] = new Casa();

                casa[i][j].setPosicao(posicaoCasa);

                y = y + 2;

            }
            x = x + 2;
        }

        //posição das peças vermelhas na matriz      // peça armazena a sua posição
        casa[2][0].setPeça(peçasVermelhas.get(1));   peçasVermelhas.get(1).setXY(2,0);
        casa[4][0].setPeça(peçasVermelhas.get(2));   peçasVermelhas.get(2).setXY(4,0);
        casa[6][0].setPeça(peçasVermelhas.get(3));   peçasVermelhas.get(3).setXY(6,0);
        casa[1][1].setPeça(peçasVermelhas.get(4));   peçasVermelhas.get(4).setXY(1,1);
        casa[3][1].setPeça(peçasVermelhas.get(5));   peçasVermelhas.get(5).setXY(3,1);
        casa[5][1].setPeça(peçasVermelhas.get(6));   peçasVermelhas.get(6).setXY(5,1);
        casa[7][1].setPeça(peçasVermelhas.get(7));   peçasVermelhas.get(7).setXY(7,1);
        casa[0][2].setPeça(peçasVermelhas.get(8));   peçasVermelhas.get(8).setXY(0,2);
        casa[2][2].setPeça(peçasVermelhas.get(9));   peçasVermelhas.get(9).setXY(2,2);
        casa[4][2].setPeça(peçasVermelhas.get(10));  peçasVermelhas.get(10).setXY(4,2);
        casa[6][2].setPeça(peçasVermelhas.get(11));  peçasVermelhas.get(11).setXY(6,2);

        // posição das peças brancas na matriz

        casa[1][5].setPeça(peçasBrancas.get(0));     peçasBrancas.get(0).setXY(1,5);
        casa[3][5].setPeça(peçasBrancas.get(1));     peçasBrancas.get(1).setXY(3,5);
        casa[5][5].setPeça(peçasBrancas.get(2));     peçasBrancas.get(2).setXY(5,5);
        casa[7][5].setPeça(peçasBrancas.get(3));     peçasBrancas.get(3).setXY(7,5);
        casa[0][6].setPeça(peçasBrancas.get(4));     peçasBrancas.get(4).setXY(0,6);
        casa[2][6].setPeça(peçasBrancas.get(5));     peçasBrancas.get(5).setXY(2,6);
        casa[4][6].setPeça(peçasBrancas.get(6));     peçasBrancas.get(6).setXY(4,6);
        casa[6][6].setPeça(peçasBrancas.get(7));     peçasBrancas.get(7).setXY(6,6);
        casa[1][7].setPeça(peçasBrancas.get(8));     peçasBrancas.get(8).setXY(1,7);
        casa[3][7].setPeça(peçasBrancas.get(9));     peçasBrancas.get(9).setXY(3,7);
        casa[5][7].setPeça(peçasBrancas.get(10));    peçasBrancas.get(10).setXY(5,7);
        casa[7][7].setPeça(peçasBrancas.get(11));    peçasBrancas.get(11).setXY(7,7);
    }

    // invocado pelo setup e desenha as peças em suas posições corretas
    public void configurarPeças(Point point){

        // posicionar peças, encontrar fórmula matematica
       Point p = new Point();

       // ponto (0,0) da imagem do tabuleiro mas não da tela
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

        float esquerda = p1.x - (larguraTabuleiro/8)/2;
        float cima = p1.y - (alturaTabuleiro/8)/2;
        float direita=p1.x +(larguraTabuleiro/8)/2;
        float baixo= p1.y + (alturaTabuleiro/8)/2;

        return new RectF(esquerda,cima,direita,baixo);
    }


    public void realizarJogada(float x, float y) {

        if(minhaVez){

        RectF peçaAtual;
        SGImageFactory imageFactory = getImageFactory();


        if (selecionar == false) {


            for (int i = 0; i < peçasBrancas.size(); i++) {

                peçaAtual = peçasBrancas.get(i).getPosicaoPeça();


                if (x >= peçaAtual.left & x <= peçaAtual.right &
                        y >= peçaAtual.top & y <= peçaAtual.bottom) {

                    this.peçaSelecionada = peçasBrancas.get(i);
                    selecionar = true;

                    if (peçaSelecionada.getPosX() < 7 & peçaSelecionada.getPosY() > 0) {

                        direita = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1];

                    }

                    //casa para movimentar à esquerda
                    if (peçaSelecionada.getPosX() > 0 & peçaSelecionada.getPosY() > 0) {

                        esquerda = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1];


                    }

                    break;
                }

                  minhaVez = true;
            }

        } else {
            // casa atual da minha peça
            Casa c = casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()];
            // casa À direita
            Casa c1 = null;
            // casa à esquerda
            Casa c2 = null;

            // casa para movimentar à direita
            if (peçaSelecionada.getPosX() < 7 & peçaSelecionada.getPosY() > 0) {

                if(casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1]
                        .getPeça()!= null) {

                    if(casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1]
                            .getPeça().getJogador()!= 2){

                    c1 = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1];

                    }

                }else{

                    c1 = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1];
                }
            }

            //casa para movimentar à esquerda
            if (peçaSelecionada.getPosX() > 0 & peçaSelecionada.getPosY() > 0) {

                // impedir passar por cima de uma peça própria
                if(casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1]
                        .getPeça() != null) {

                    if(casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1]
                            .getPeça().getJogador() !=2){

                    c2 = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1];
                    }

                }else{
                    c2 = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1];
                }

            }

            RectF atualizar;

            if (c1 != null) { // jogada para à direita

                atualizar = c1.getPosicao();

                if (x >= atualizar.left & x <= atualizar.right &
                        y >= atualizar.top & y <= atualizar.bottom) {

                    //avançar 1 casa à cima e à direita na matriz
                    c1.setPeça(peçaSelecionada);

                    // na classe da peça atualizar o seu local na matriz
                    peçaSelecionada.setXY
                            (peçaSelecionada.getPosX() + 1, peçaSelecionada.getPosY() - 1);

                    //atualizar posição da peça
                    peçaSelecionada.setPosicaoPeça
                            (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].getPosicao());

                    c.removePeça();
                    minhaVez = false;

                    if(peçaSelecionada.getPosY() == 0){

                        peçaSelecionada.setImagemPeça(imageFactory.createImage
                                (R.drawable.pecaazulrainha));
                        peçaSelecionada.setRainha(true);
                    }


                }

            }
            if (c2 != null) {// jogada para à esquerda

                atualizar = c2.getPosicao();

                if (x >= atualizar.left & x <= atualizar.right &
                        y >= atualizar.top & y <= atualizar.bottom) {

                    //avançar 1 casa à cima e à direita na matriz
                    casa[peçaSelecionada.getPosX() - 1]
                            [peçaSelecionada.getPosY() - 1].setPeça(peçaSelecionada);

                    // na classe da peça atualizar o seu local na matriz
                    peçaSelecionada.setXY
                            (peçaSelecionada.getPosX() - 1, peçaSelecionada.getPosY() - 1);

                    //atualizar posição da peça
                    peçaSelecionada.setPosicaoPeça
                            (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].getPosicao());

                    c.removePeça();
                    minhaVez = false;

                    //verificar se a peça se tornou rainha e atualizar a imagem
                    if(peçaSelecionada.getPosY() == 0){

                        peçaSelecionada.setImagemPeça(imageFactory.createImage
                                (R.drawable.pecaazulrainha));
                        peçaSelecionada.setRainha(true);
                    }

                }
            }

            selecionar = false;
            peçaSelecionada = null;
            esquerda = direita = null;

            inteligenciaArtificial();

        }

    }
    }
    private void movimentacaoPecaNormal(){}
    private void movimentacaoRainha(){}

    // jogada do computador
    private void inteligenciaArtificial(){

        minhaVez = true;
    }


}
