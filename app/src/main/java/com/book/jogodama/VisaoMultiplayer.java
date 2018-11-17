package com.book.jogodama;

import android.app.AlertDialog;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class VisaoMultiplayer extends SGView{

    //-------------------VARIAVEIS DO MULTIPLAYER--------------------------------------------------
    private PrintWriter escrever;
    private BufferedReader ler;
    private Socket jogador;
    private AtividadeMultiplayer atv;
    private boolean iniciarJogo = false;
    private Rect areaDeDesenho = new Rect();

    int idJogador;
    SGImage procurando;
    RectF procurandoPartida = new RectF();
   //--------------------VARIAVEIS COMUNS-----------------------------------------------------------

    private ArrayList<Peça> peçasVermelhas = new ArrayList<>(); // oponente
    private ArrayList<Peça> peçasBrancas = new ArrayList<>();
    ArrayList<Peça> peçasPossiveis = new ArrayList<>(); // peças que podem comer outras peças
    ArrayList<Casa> lugaresPossiveisRainha = new ArrayList<>();
    ArrayList<Casa> comerPecaRainha = new ArrayList<>();


    private Casa[][] casa = new Casa[8][8];
    private Peça peçaSelecionada = null;
    private Peça peçaDeletar = null;

    private SGImage tabuleiro;
    private SGImage minhaVezImg;
    private SGImage vezDoOponenteImg;

    private boolean selecionar = false;

    private int larguraTabuleiro = 680;
    private int alturaTabuleiro = 680;

    private int larguraCasa = larguraTabuleiro/8;
    private int alturaCasa = alturaTabuleiro/8;

    private RectF posicaoTabuleiro = new RectF();
    private RectF posicaoPeça = new RectF();

    Point inicioTabuleiro = new Point();
    private boolean minhaVez=false;

    //desenhar retangulos no local da movimentação
    Casa esquerda = null;
    Casa direita = null;

    // retangulos float do aviso de vez
    RectF minhaVezRect = new RectF();
    RectF vezDoOponent = new RectF();

    private boolean jogarNovamente = false;
    private boolean jogarNovamenteRainha = false;

    String jogada;
    //CONSTRUTOR
    public VisaoMultiplayer(Context context,AtividadeMultiplayer atv) {
        super(context);
        this.atv = atv;
    }


    @Override
    protected void setup() {

        larguraTabuleiro = getDimensions().x - 40;
        alturaTabuleiro =  larguraTabuleiro;

        SGImageFactory fabrica = getImageFactory();

        // adicionado -> Procurando partida
        procurando = fabrica.createImage(R.drawable.procurandopartida);

        Point viewDimensions = getDimensions();
        //bolinha no centro da tela
        Point viewCenter = new Point(viewDimensions.x / 2, viewDimensions.y/2);

        procurandoPartida.set(viewCenter.x-105,
                              viewCenter.y-60,
                             viewCenter.x+105,
                              viewCenter.y+60);

        // iniciar conexão
        iniciarSocket();

        tabuleiro = fabrica.createImage(R.drawable.tabuleirov2);
        minhaVezImg = fabrica.createImage(R.drawable.suavez);
        vezDoOponenteImg= fabrica.createImage(R.drawable.vezdooponente);

        // adicionar imagem das peças vermelhas, id do jogador
        for(int i =0; i < 12; i++){

            Peça peça = new Peça();
            peça.setImagemPeça(fabrica.createImage(R.drawable.pecavermelha));
            peça.setChaveDaPeça(i);
            peça.setJogador(2);
            peçasVermelhas.add(peça);

        }
        // adicionar imagem das peças azuis
        for(int i =0; i < 12; i++){

            Peça peça = new Peça();
            peça.setImagemPeça(fabrica.createImage(R.drawable.pecaazul));
            peça.setChaveDaPeça(i);
            peça.setJogador(1);
            peçasBrancas.add(peça);

        }

        // posição do tabuleiro na tela
        int esquerda = viewCenter.x - larguraTabuleiro/2;
        int cima= viewCenter.y - alturaTabuleiro/2;
        int direita=viewCenter.x+larguraTabuleiro/2;
        int baixo=viewCenter.y+alturaTabuleiro/2;

        posicaoTabuleiro.set(esquerda,cima,direita,baixo);

        // posicao dos avisos de turno
        minhaVezRect.set(viewCenter.x - 105,
                posicaoTabuleiro.bottom,
                viewCenter.x+105,
                posicaoTabuleiro.bottom+120);

        vezDoOponent.set(viewCenter.x - 105,
                posicaoTabuleiro.top-120,
                viewCenter.x+120,
                posicaoTabuleiro.top);

        configurarPeças(viewCenter); // posição das peças com o canvas
        configurarTabuleiro(viewCenter); // matriz
    }

    @Override
    public void step(Canvas canvas, float elapsedTimeinSeconds) {

        SGRenderer rend = this.getRenderer();
        rend.beginDrawing(canvas, Color.BLACK);

        // antes de iniciar o jogo
        if(!iniciarJogo) {

            areaDeDesenho.set(0, 0, 210, 120);
            rend.drawImage(procurando,areaDeDesenho,procurandoPartida);
        }else{ // depois de iniciar o jogo

            areaDeDesenho.set(0,0,486,485);
            rend.drawImage(tabuleiro,areaDeDesenho,posicaoTabuleiro);

            if(peçaDeletar != null){

                peçasBrancas.remove(peçaDeletar);
                peçasVermelhas.remove(peçaDeletar);
                peçaDeletar = null;


            }

            for(int i =0; i < peçasVermelhas.size();i++) {


                areaDeDesenho.set(0, 0, 61, 62);
                rend.drawImage(peçasVermelhas.get(i).getImagemPeça(),
                        areaDeDesenho, peçasVermelhas.get(i).getPosicaoPeça());


            }

            for(int i =0; i < peçasBrancas.size();i++) {

                areaDeDesenho.set(0, 0, 61, 62);
                rend.drawImage(peçasBrancas.get(i).getImagemPeça(), areaDeDesenho,
                        peçasBrancas.get(i).getPosicaoPeça());


            }

            //desenhar quadros amarelos nos possíveis locais de movimentação
            if(esquerda != null){
                rend.drawRect(esquerda.getPosicao(),Color.YELLOW);

            }

            if(direita != null){

                rend.drawRect(direita.getPosicao(), Color.YELLOW);

            }

            if(!this.comerPecaRainha.isEmpty()){

                for(Casa c: comerPecaRainha){

                    rend.drawRect(c.getPosicao(),Color.YELLOW);
                }
            }

            if(!this.lugaresPossiveisRainha.isEmpty()){

                for(Casa c: lugaresPossiveisRainha){

                    rend.drawRect(c.getPosicao(),Color.YELLOW);
                }
            }
            areaDeDesenho.set(0,0,210,120);

            if(minhaVez){

                rend.drawImage(minhaVezImg,areaDeDesenho,minhaVezRect);
            }else{

                rend.drawImage(vezDoOponenteImg,areaDeDesenho,vezDoOponent);
            }



        }

        rend.endDrawing();
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

                casa[i][j].setX(i);
                casa[i][j].setY(j);

                y = y + 2;

            }
            x = x + 2;
        }
        //posição das peças vermelhas na matriz      // peça armazena a sua posição
        casa[0][0].setPeça(peçasVermelhas.get(0));   peçasVermelhas.get(0).setXY(0,0);
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
    //iniciar conexão e jogo
    public void iniciarSocket(){

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    jogador = new Socket("191.190.204.110",12345);

                    iniciarLeitor();
                } catch (IOException e) {
                }
            }
        });th.start();


    }

    // retorna um retangulo à partir de um ponto
    private RectF calcularPosicao(Point p,int x, int y){

        Point p1 = new Point();

        p1.x = p.x + (larguraTabuleiro/16) *x;
        p1.y = p.y + (alturaTabuleiro/16) * y;

        float esquerda = p1.x - (larguraTabuleiro/8)/2;
        float cima = p1.y - (alturaTabuleiro/8)/2;
        float direita=p1.x +((larguraTabuleiro/8))/2;
        float baixo= p1.y + ((alturaTabuleiro/8))/2;

        return new RectF(esquerda,cima,direita+3f,baixo+3f);
    }

    //recebe entradas por socket
    public void iniciarLeitor(){

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {



                try {
                    ler = new BufferedReader(new InputStreamReader(jogador.getInputStream()));
                    escrever = new PrintWriter(jogador.getOutputStream(),true);
                    String jogada="";

                    while((jogada = ler.readLine())!= null){
                  // iniciar jogo
                        if(jogada.equals("iniciar!")){
                            iniciarJogo = true;

                            // definir jogador 1
                        }else if(jogada.equals("1")){
                  // definir jogador ( jogador 1 começa )

                          minhaVez = true;
                          idJogador = 1;
                        }else if(jogada.equals("2")){

                          idJogador = 2;
                          minhaVez = false;

                        }else{
                            // aqui vão as jogadas
                            atualizarJogadaOponente(jogada);

                        }
                    }

                } catch (IOException e) {

                }

            }
        }); th.start();
    }


    /*############################################################################################
    ----------------------------------MÉTODOS DO JOGO---------------------------------------------
     ############################################################################################*/

    public void realizarJogada(float x, float y) {

        // verificar se é minha vez
        if(minhaVez){
            // verificar se alguma(s) peça(s) podem comer outras peças

            /*AlertDialog.Builder alert = new AlertDialog.Builder(atv);
            alert.setMessage(idJogador+"");
            alert.create();
            alert.show();*/

            boolean comerPeca = false;
            Peça peça=null;
            ArrayList<Peça> minhasPeças=null;
            RectF peçaAtual;
            SGImageFactory imageFactory = getImageFactory();

            if(idJogador == 1){

                minhasPeças = peçasBrancas;
            }else{

                minhasPeças = peçasVermelhas;
            }


            if (selecionar == false) {

                peçasPossiveis = new ArrayList<>();
                // verificar a possibilidade de comer alguma peça
                for(int i =0; i < minhasPeças.size(); i ++){

                    peça = minhasPeças.get(i);

                    if(!peça.isRainha()) {

                        // jogador 1
                        if (idJogador == 1){
                            // verificar se pode comer alguma peça à direita
                            if (peça.getPosX() < 6 & peça.getPosY() > 1) {
                                if (casa[peça.getPosX() + 1][peça.getPosY() - 1].getPeça() != null) {
                                    if (casa[peça.getPosX() + 1][peça.getPosY() - 1].getPeça().getJogador() == 2) {
                                        if (casa[peça.getPosX() + 2][peça.getPosY() - 2].getPeça() == null) {

                                            comerPeca = true;
                                            peça.setDireita(true);
                                            peçasPossiveis.add(peça);
                                        }

                                    }

                                }

                            }

                        // verificar se pode comer alguma peça à  esquerda
                        if (peça.getPosX() > 1 & peça.getPosY() > 1) {
                            if (casa[peça.getPosX() - 1][peça.getPosY() - 1].getPeça() != null) {
                                if (casa[peça.getPosX() - 1][peça.getPosY() - 1].getPeça().getJogador() == 2) {
                                    if (casa[peça.getPosX() - 2][peça.getPosY() - 2].getPeça() == null) {

                                        comerPeca = true;
                                        peça.setEsquerda(true);
                                        peçasPossiveis.add(peça);
                                    }

                                }

                            }
                        }

                        // se a vez for do jogador 2
                    }else {


                                // verificar se pode comer alguma peça à direita
                                if (peça.getPosX() < 6 & peça.getPosY() < 6) {
                                    if (casa[peça.getPosX() + 1][peça.getPosY() + 1].getPeça() != null) {
                                        if (casa[peça.getPosX() + 1][peça.getPosY() + 1].getPeça().getJogador() == 1) {
                                            if (casa[peça.getPosX() + 2][peça.getPosY() + 2].getPeça() == null) {

                                                comerPeca = true;
                                                peça.setDireita(true);
                                                peçasPossiveis.add(peça);
                                            }

                                        }

                                    }

                                }

                                // verificar se pode comer alguma peça à  esquerda
                                if (peça.getPosX() > 1 & peça.getPosY() < 6) {
                                    if (casa[peça.getPosX() - 1][peça.getPosY() + 1].getPeça() != null) {
                                        if (casa[peça.getPosX() - 1][peça.getPosY() + 1].getPeça().getJogador() == 1) {
                                            if (casa[peça.getPosX() - 2][peça.getPosY() + 2].getPeça() == null) {

                                                comerPeca = true;
                                                peça.setEsquerda(true);
                                                peçasPossiveis.add(peça);
                                            }

                                        }

                                    }
                                }
                    }

                        } else{
                        peça.setArrayRainha(this.comerPecaComoRainha(peça));

                        if(!peça.getComerPecaRainha().isEmpty()){
                            peçasPossiveis.add(peça);
                        }
                    }
                }

                // se existe peça para pegar
                if(!peçasPossiveis.isEmpty()){

                    for (int i = 0; i < peçasPossiveis.size(); i++) {

                        peçaAtual = peçasPossiveis.get(i).getPosicaoPeça();


                        if (x >= peçaAtual.left & x <= peçaAtual.right &
                                y >= peçaAtual.top & y <= peçaAtual.bottom) {

                            this.peçaSelecionada = peçasPossiveis.get(i);
                            selecionar = true;

                            if (!peçaSelecionada.isRainha()) {

                                if (peçaSelecionada.getDireita()) {

                                    if(idJogador == 1) {

                                        direita = casa[peçaSelecionada.getPosX() + 2]
                                                      [peçaSelecionada.getPosY() - 2];

                                    }else{
                                        direita = casa[peçaSelecionada.getPosX() + 2]
                                                      [peçaSelecionada.getPosY() + 2];
                                    }
                                }

                                //casa para movimentar à esquerda
                                if (peçaSelecionada.getEsquerda()) {

                                    if(idJogador == 1) {

                                        esquerda = casa[peçaSelecionada.getPosX() - 2]
                                                       [peçaSelecionada.getPosY() - 2];
                                    }else{
                                        esquerda = casa[peçaSelecionada.getPosX() - 2]
                                                       [peçaSelecionada.getPosY() + 2];
                                    }

                                }

                            }else{

                                this.comerPecaRainha = peçasPossiveis.get(i).getComerPecaRainha();
                            }

                            break;
                        }

                        minhaVez = true;
                    }

                }else {

                    // se não existir peça para pegar
                    for (int i = 0; i < minhasPeças.size(); i++) {

                        peçaAtual = minhasPeças.get(i).getPosicaoPeça();


                        if (x >= peçaAtual.left & x <= peçaAtual.right &
                                y >= peçaAtual.top & y <= peçaAtual.bottom) {


                            this.peçaSelecionada = minhasPeças.get(i);
                            selecionar = true;

                            if (peçaSelecionada.isRainha()) {

                                this.movimentacaoRainha();
                            } else {

                                /**/
                                if(idJogador == 1) {

                                    if (peçaSelecionada.getPosX() < 7 & peçaSelecionada.getPosY() > 0) {


                                        if (casa[peçaSelecionada.getPosX() + 1]
                                                [peçaSelecionada.getPosY() - 1].getPeça() == null) {



                                            direita = casa[peçaSelecionada.getPosX() + 1]
                                                    [peçaSelecionada.getPosY() - 1];

                                        }

                                    }

                                    //casa para movimentar à esquerda
                                    if (peçaSelecionada.getPosX() > 0 & peçaSelecionada.getPosY() > 0) {

                                        if (casa[peçaSelecionada.getPosX() - 1]
                                                [peçaSelecionada.getPosY() - 1].getPeça() == null) {


                                            esquerda = casa[peçaSelecionada.getPosX() - 1]
                                                    [peçaSelecionada.getPosY() - 1];
                                        }

                                    }

                                }else{

                                    if (peçaSelecionada.getPosX() < 7 & peçaSelecionada.getPosY() < 7) {


                                        if (casa[peçaSelecionada.getPosX() + 1]
                                                [peçaSelecionada.getPosY() + 1].getPeça() == null) {


                                            direita = casa[peçaSelecionada.getPosX() + 1]
                                                    [peçaSelecionada.getPosY() + 1];

                                        }

                                    }

                                    //casa para movimentar à esquerda
                                    if (peçaSelecionada.getPosX() > 0 & peçaSelecionada.getPosY() < 7) {

                                        if (casa[peçaSelecionada.getPosX() - 1]
                                                [peçaSelecionada.getPosY() + 1].getPeça() == null) {


                                            esquerda = casa[peçaSelecionada.getPosX() - 1]
                                                    [peçaSelecionada.getPosY() + 1];
                                        }

                                    }
                                }
                             /**/
                            }
                            break;
                        }

                        minhaVez = true;
                    }
                }


                jogada ="";
                if(peçaSelecionada != null) {
                    // inicio do envio de dados
                    if (peçaSelecionada.getChaveDaPeça() < 10) {

                        jogada = peçaSelecionada.getChaveDaPeça() + " ";
                    } else {
                        jogada = "" + peçaSelecionada.getChaveDaPeça();
                    }
                }

            } else {


                if (peçaSelecionada.isRainha()) {

                    if(peçasPossiveis.isEmpty()) {

                        this.andarComARainha(x, y);

                    }else{

                        this.comerPeçaRainha(x,y);
                    }

                } else {
                    // casa atual da minha peça
                    Casa c = casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()];
                    // casa À direita
                    Casa c1 = null;
                    // casa à esquerda
                    Casa c2 = null;

                    if (jogarNovamente) {

                        peçasPossiveis.add(peçaSelecionada);
                        jogada = jogada+"1";
                        comerPeca = true;

                    } else if (!peçasPossiveis.isEmpty()) {

                        jogada = jogada +"1";
                        comerPeca = true;
                    }
                    // se for possível comer peça
                    if (comerPeca) {


                        if (peçaSelecionada.getDireita() == true) {

                            if(idJogador==1) {
                                c1 = casa[peçaSelecionada.getPosX() + 2]
                                         [peçaSelecionada.getPosY() - 2];
                            }else{

                                c1 = casa[peçaSelecionada.getPosX() + 2]
                                        [peçaSelecionada.getPosY() + 2];
                            }
                        } else {
                            c1 = null;
                        }
                        if (peçaSelecionada.getEsquerda() == true) {

                            if(idJogador==1){

                                c2 = casa[peçaSelecionada.getPosX() - 2]
                                     [peçaSelecionada.getPosY() - 2];

                            }else{

                                c2 = casa[peçaSelecionada.getPosX() - 2]
                                        [peçaSelecionada.getPosY() + 2];
                            }

                        } else {
                            c2 = null;
                        }
                    } else {
                        jogada = jogada+"0";
                        // casa para movimentar à direita
                        if (idJogador == 1){

                            if (peçaSelecionada.getPosX() < 7 & peçaSelecionada.getPosY() > 0) {

                                if (casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1]
                                        .getPeça() != null) {

                                    c1 = null;

                                } else {

                                    c1 = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1];
                                }


                            }

                        //casa para movimentar à esquerda
                        if (peçaSelecionada.getPosX() > 0 & peçaSelecionada.getPosY() > 0) {

                            // impedir passar por cima de uma peça própria
                            if (casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1]
                                    .getPeça() != null) {

                                c2 = null;
                            } else {
                                c2 = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1];
                            }

                        }

                    }else{ // jogador 2

                            if (peçaSelecionada.getPosX() < 7 & peçaSelecionada.getPosY() < 7) {

                                if (casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() + 1]
                                        .getPeça() != null) {

                                    c1 = null;

                                } else {

                                    c1 = casa[peçaSelecionada.getPosX() + 1]
                                             [peçaSelecionada.getPosY() + 1];
                                }


                            }

                            //casa para movimentar à esquerda
                            if (peçaSelecionada.getPosX() > 0 & peçaSelecionada.getPosY() < 7) {

                                // impedir passar por cima de uma peça própria
                                if (casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY()+ 1]
                                        .getPeça() != null) {

                                    c2 = null;
                                } else {
                                    c2 = casa[peçaSelecionada.getPosX() - 1]
                                             [peçaSelecionada.getPosY() + 1];
                                }

                            }
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

                            // se foi possivel comer peça entao
                            if (peçasPossiveis.isEmpty() == false) {

                             int chave;
                             String chaveEsp;

                                if(idJogador == 1) {
                                    peçaSelecionada.setXY
                                            (peçaSelecionada.getPosX() + 2, peçaSelecionada.getPosY() - 2);

                                    // deletar peça do array inimigo
                                    chave = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() + 1]
                                            .getPeça().getChaveDaPeça();

                                    peçaDeletar = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() + 1]
                                            .getPeça();

                                    casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() + 1].removePeça();

                                }else{ // se for o jogador 2

                                    peçaSelecionada.setXY
                                            (peçaSelecionada.getPosX() + 2, peçaSelecionada.getPosY() + 2);

                                    // deletar peça do array inimigo
                                    chave = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() + 1]
                                            .getPeça().getChaveDaPeça();

                                    peçaDeletar = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1]
                                            .getPeça();

                                    casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1].removePeça();
                                }

                                peçaSelecionada.setPosicaoPeça
                                        (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()]
                                                .getPosicao());

                                  chaveEsp =""+chave;
                                  if(chave < 10){
                                      chaveEsp = chaveEsp+" ";
                                  }
                                  jogada = jogada+chaveEsp+peçaSelecionada.getPosX()
                                                  +peçaSelecionada.getPosY()+"";

                            } else {
                                // se não foi possível comer peça
                                if(idJogador==1) {
                                    peçaSelecionada.setXY
                                            (peçaSelecionada.getPosX() + 1, peçaSelecionada.getPosY() - 1);

                                }else{

                                    peçaSelecionada.setXY
                                            (peçaSelecionada.getPosX() + 1, peçaSelecionada.getPosY() + 1);
                                }
                                // para enviar os dados ao oponente
                                jogada = ""+jogada+peçaSelecionada.getPosX()+peçaSelecionada.getPosY();
                                //atualizar posição da peça
                                peçaSelecionada.setPosicaoPeça
                                        (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].getPosicao());
                            }


                            c.removePeça();
                            minhaVez = false;

                            if(idJogador == 1) {

                                if (peçaSelecionada.getPosY() == 0) {

                                    peçaSelecionada.setImagemPeça(imageFactory.createImage
                                            (R.drawable.pecaazulrainha));
                                    peçaSelecionada.setRainha(true);
                                }

                            }else{ // se for o jogador 2

                                if (peçaSelecionada.getPosY() == 7) {

                                    peçaSelecionada.setImagemPeça(imageFactory.createImage
                                            (R.drawable.pecavermelharainha));
                                    peçaSelecionada.setRainha(true);
                                }
                            }

                        }

                    }
                    if (c2 != null) {// jogada para à esquerda

                        atualizar = c2.getPosicao();

                        if (x >= atualizar.left & x <= atualizar.right &
                                y >= atualizar.top & y <= atualizar.bottom) {

                            //avançar 1 casa à cima e à esquerda na matriz
                            c2.setPeça(peçaSelecionada);

                            // na classe da peça atualizar o seu local na matriz

                            //se for possivel comer peça
                            if (peçasPossiveis.isEmpty() == false) {

                                int chave;
                                String chaveEsp;

                                if(idJogador == 1) {
                                    peçaSelecionada.setXY
                                            (peçaSelecionada.getPosX() - 2, peçaSelecionada.getPosY() - 2);

                                    chave = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() + 1]
                                            .getPeça().getChaveDaPeça();

                                    // deletar peça do array inimigo
                                    peçaDeletar = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() + 1]
                                            .getPeça();

                                    casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() + 1].removePeça();

                                }else{ // jogador 2

                                    peçaSelecionada.setXY
                                            (peçaSelecionada.getPosX() - 2, peçaSelecionada.getPosY() + 2);

                                    // deletar peça do array inimigo
                                    chave = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1]
                                            .getPeça().getChaveDaPeça();

                                    peçaDeletar = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1]
                                            .getPeça();

                                    casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1].removePeça();
                                }
                                //atualizar posição da peça
                                peçaSelecionada.setPosicaoPeça
                                        (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].getPosicao());


                                chaveEsp = chave+"";

                                if(chave < 10){
                                   chaveEsp = chave +" ";
                                }

                                jogada = jogada+chaveEsp+peçaSelecionada.getPosX()
                                        +peçaSelecionada.getPosY()+"";

                            } else {
                                // se não for
                                if(idJogador == 1) {
                                    peçaSelecionada.setXY
                                            (peçaSelecionada.getPosX() - 1, peçaSelecionada.getPosY() - 1);
                                }else{

                                    peçaSelecionada.setXY
                                            (peçaSelecionada.getPosX() - 1, peçaSelecionada.getPosY() + 1);
                                }
                                //atualizar posição da peça
                                peçaSelecionada.setPosicaoPeça
                                        (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].getPosicao());

                                jogada = jogada+peçaSelecionada.getPosX()+
                                        peçaSelecionada.getPosY()+"";
                            }

                            c.removePeça();
                            minhaVez = false;

                            //verificar se a peça se tornou rainha e atualizar a imagem
                            if(idJogador==1) {
                                if (peçaSelecionada.getPosY() == 0) {

                                    peçaSelecionada.setImagemPeça(imageFactory.createImage
                                            (R.drawable.pecaazulrainha));
                                    peçaSelecionada.setRainha(true);
                                }
                            }else{

                                if (peçaSelecionada.getPosY() == 7) {

                                    peçaSelecionada.setImagemPeça(imageFactory.createImage
                                            (R.drawable.pecavermelharainha));
                                    peçaSelecionada.setRainha(true);
                                }
                            }
                        }
                    }


                    //c.removePeça();

                    for (Peça p : peçasPossiveis) {
                        p.setEsquerda(false);
                        p.setDireita(false);
                    }

                    boolean passarVez = false;
                    jogarNovamente = false;

                    if (comerPeca == true) {

                        passarVez = this.comerPeçaNovamente();

                    }

                    if (!passarVez) {

                        selecionar = false;
                        peçaSelecionada = null;
                        esquerda = direita = null;
                        this.lugaresPossiveisRainha.clear();

                        if (minhaVez == false) { // jogada do computador

                            /*FIM DA MINHA JOGADA -> ENVIAR JOGADAS*/
                            this.enviarJogada(jogada);

                        }

                    } // se for para passar a vez

                } // se a peça não for rainha
            }// fim do else " if selecionar == false "

        } // se for a minha vez

    } // fim do metodo realizar jogada


    public ArrayList<Casa> comerPecaComoRainha(Peça pe){

        int x = pe.getPosX();
        int y = pe.getPosY();
        int jogador = idJogador;

        ArrayList<Casa> casas = new ArrayList<>();

        int posX = x;
        int posY = y;

        Casa casa1 = null;
        // movimentação cima esquerda
        while(posX > 1 & posY > 1){

            posX = posX -1;
            posY = posY -1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() != null){
                if(casa1.getPeça().getJogador() != jogador){
                    if(casa[casa1.getX()-1][casa1.getY()-1].getPeça() == null){


                        casas.add(casa[casa1.getX()-1][casa1.getY()-1]);
                        break;
                    }else{
                        break;
                    }

                }else{
                    break;
                }

            }

        }

        // movimentação cima direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 6 & posY > 1){

            posX = posX +1;
            posY = posY -1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() != null){
                if(casa1.getPeça().getJogador() != jogador){
                    if(casa[casa1.getX()+1][casa1.getY()-1].getPeça() == null){


                        casas.add(casa[casa1.getX()+1][casa1.getY()-1]);
                        break;
                    }else{
                        break;
                    }

                }else{
                    break;
                }

            }

        }
        // movimentação baixo esquerda

        posX = x;
        posY = y;
        casa1 = null;

        while(posX > 1 & posY < 6){

            posX = posX -1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() != null){
                if(casa1.getPeça().getJogador() != jogador){
                    if(casa[casa1.getX()-1][casa1.getY()+1].getPeça() == null){


                        casas.add(casa[casa1.getX()-1][casa1.getY()+1]);
                        break;
                    }else{
                        break;
                    }

                }else{
                    break;
                }

            }

        }
        //movimentação baixo direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 6 & posY < 6){

            posX = posX +1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() != null){
                if(casa1.getPeça().getJogador() != jogador){
                    if(casa[casa1.getX()+1][casa1.getY()+1].getPeça() == null){


                        casas.add(casa[casa1.getX()+1][casa1.getY()+1]);
                        break;
                    }else{
                        break;
                    }

                }else{
                    break;
                }

            }

        }

        return casas;
    }

    public void movimentacaoRainha(){ // lugares possiveis para movimentação da rainha

        int x = peçaSelecionada.getPosX();
        int y = peçaSelecionada.getPosY();

        int posX = x;
        int posY = y;

        Casa casa1 = null;
        // movimentação cima esquerda
        while(posX > 0 & posY > 0){

            posX = posX -1;
            posY = posY -1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                this.lugaresPossiveisRainha.add(casa1);

            }else{
                break;
            }

        }

        // movimentação cima direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 7 & posY > 0){

            posX = posX +1;
            posY = posY -1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                this.lugaresPossiveisRainha.add(casa1);

            }else{
                break;
            }

        }
        // movimentação baixo esquerda

        posX = x;
        posY = y;
        casa1 = null;

        while(posX > 0 & posY < 7){

            posX = posX -1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                this.lugaresPossiveisRainha.add(casa1);

            }else{
                break;
            }

        }
        //movimentação baixo direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 7 & posY < 7){

            posX = posX +1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                this.lugaresPossiveisRainha.add(casa1);

            }else{
                break;
            }

        }

    }

    public void andarComARainha(float x, float y){

        Casa c = null;

        for(Casa casa: lugaresPossiveisRainha){

            if(x >= casa.getPosicao().left & x <= casa.getPosicao().right &
                    y >= casa.getPosicao().top & y <= casa.getPosicao().bottom){

                c = casa;
                break;
            }
        }

        lugaresPossiveisRainha.clear();

        if(c != null){
            casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();

            peçaSelecionada.setXY(c.getX(),c.getY());
            peçaSelecionada.setPosicaoPeça(c.getPosicao());
            c.setPeça(peçaSelecionada);

            selecionar = false;
            minhaVez = false;
            jogada = jogada + peçaSelecionada.getPosX()+peçaSelecionada.getPosY()+"";
            /*FIM DA MINHA JOGADA => ENVIAR DADOS PARA PEÇASELECIONADA = NULL ???*/
              enviarJogada(jogada);
        }else{

            selecionar = false;
            minhaVez = true;
        }
    }

    public void comerPeçaRainha(float x, float y){

        boolean comer = false;

        for(Casa c: this.comerPecaRainha){

            if(x >= c.getPosicao().left & x <= c.getPosicao().right &
                    y >= c.getPosicao().top & y <= c.getPosicao().bottom){

                comer = true;
                int chave=99;
                String chavePos;
                // baixo direita
                if(c.getX() > peçaSelecionada.getPosX() & c.getY() > peçaSelecionada.getPosY()){

                    peçasVermelhas.remove(casa[c.getX()-1][c.getY()-1].getPeça());

                    chave = casa[c.getX()-1][c.getY()-1].getPeça().getChaveDaPeça();
                    casa[c.getX()-1][c.getY()-1].removePeça();

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();
                    peçaSelecionada.setXY(c.getX(),c.getY());
                    peçaSelecionada.setPosicaoPeça(c.getPosicao());

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].
                            setPeça(peçaSelecionada);


                   //jogada = jogada+peçaSelecionada.getPosX()+peçaSelecionada.getPosY()+"";
                }
                //baixo esquerda
                else if(c.getX() < peçaSelecionada.getPosX() & c.getY() > peçaSelecionada.getPosY()){

                    peçasVermelhas.remove(casa[c.getX()+1][c.getY()-1].getPeça());

                    chave = casa[c.getX()+1][c.getY()-1].getPeça().getChaveDaPeça();
                    casa[c.getX()+1][c.getY()-1].removePeça();

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();
                    peçaSelecionada.setXY(c.getX(),c.getY());
                    peçaSelecionada.setPosicaoPeça(c.getPosicao());

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].
                            setPeça(peçaSelecionada);

                    //jogada = jogada+peçaSelecionada.getPosX()+peçaSelecionada.getPosY()+"";
                }
                // cima esquerda
                else if(c.getX() < peçaSelecionada.getPosX() & c.getY() < peçaSelecionada.getPosY()){


                    peçasVermelhas.remove(casa[c.getX()+1][c.getY()+1].getPeça());

                    chave = casa[c.getX()+1][c.getY()+1].getPeça().getChaveDaPeça();
                    casa[c.getX()+1][c.getY()+1].removePeça();

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();
                    peçaSelecionada.setXY(c.getX(),c.getY());
                    peçaSelecionada.setPosicaoPeça(c.getPosicao());

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].
                            setPeça(peçaSelecionada);

                    //jogada = jogada+peçaSelecionada.getPosX()+peçaSelecionada.getPosY()+"";
                }
                // cima direita
                else if(c.getX() > peçaSelecionada.getPosX() & c.getY() < peçaSelecionada.getPosY()){

                    peçasVermelhas.remove(casa[c.getX()-1][c.getY()+1].getPeça());

                    chave = casa[c.getX()-1][c.getY()+1].getPeça().getChaveDaPeça();
                    casa[c.getX()-1][c.getY()+1].removePeça();

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();
                    peçaSelecionada.setXY(c.getX(),c.getY());
                    peçaSelecionada.setPosicaoPeça(c.getPosicao());

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].
                            setPeça(peçaSelecionada);

                    //jogada = jogada+peçaSelecionada.getPosX()+peçaSelecionada.getPosY()+"";

                }
                chavePos = chave+"";
                if(chave < 10){
                    chavePos = chave+" ";
                }

                jogada = jogada+chavePos+peçaSelecionada.getPosX() + peçaSelecionada.getPosY()+"";
            }

        }

        for(Peça p:peçasPossiveis){

            if(p.isRainha()){
                p.getComerPecaRainha().clear();
            }else{

                p.setEsquerda(false);
                p.setDireita(false);
            }

        }


        if(comer == false){

            minhaVez = true;
            selecionar = false;

            if(jogarNovamenteRainha) {

                selecionar = true;

            }




        }else{

            if(!this.comerPecaComoRainha(peçaSelecionada).isEmpty()){

                minhaVez = true;
                selecionar = true;
                comerPecaRainha = this.comerPecaComoRainha(peçaSelecionada);
                peçasPossiveis.clear();
                peçasPossiveis.add(peçaSelecionada);
                jogarNovamenteRainha = true;

            }else {

                jogarNovamenteRainha = false;
                minhaVez = false;
                selecionar = false;


                /*FIM DA MINHA JOGADA */
                enviarJogada(jogada);
                peçaSelecionada = null;
                comerPecaRainha.clear();

            }
        }



    }

    public boolean comerPeçaNovamente() {

        esquerda = direita = null;

        boolean dir = false;
        boolean esquerd = false;
        //comer novamente uma peça à direita
        if (idJogador == 1){

            if (peçaSelecionada.getPosX() < 6 & peçaSelecionada.getPosY() > 1) {

                if (casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1].getPeça() != null) {

                    if (casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1].getPeça().
                            getJogador() == 1) {

                        if (casa[peçaSelecionada.getPosX() + 2][peçaSelecionada.getPosY() - 2].
                                getPeça() == null) {

                            direita = casa[peçaSelecionada.getPosX() + 2][peçaSelecionada.getPosY() - 2];
                            dir = true;
                        }


                    }
                }
            }
        if (peçaSelecionada.getPosX() > 1 & peçaSelecionada.getPosY() > 1) {

            if (casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1].getPeça() != null) {

                if (casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1].getPeça().
                        getJogador() == 1) {

                    if (casa[peçaSelecionada.getPosX() - 2][peçaSelecionada.getPosY() - 2].
                            getPeça() == null) {

                        esquerda = casa[peçaSelecionada.getPosX() - 2][peçaSelecionada.getPosY() - 2];
                        esquerd = true;
                    }


                }
            }
        }

          }else{ // JOGADOR 2

        if (peçaSelecionada.getPosX() < 6 & peçaSelecionada.getPosY() < 6) {

            if (casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() + 1].getPeça() != null) {

                if (casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() + 1].getPeça().
                        getJogador() == 1) {

                    if (casa[peçaSelecionada.getPosX() + 2][peçaSelecionada.getPosY() + 2].
                            getPeça() == null) {

                        direita = casa[peçaSelecionada.getPosX() + 2][peçaSelecionada.getPosY() + 2];
                        dir = true;
                    }


                }
            }
        }
        if (peçaSelecionada.getPosX() > 1 & peçaSelecionada.getPosY() < 6) {

            if (casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() + 1].getPeça() != null) {

                if (casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() + 1].getPeça().
                        getJogador() == 1) {

                    if (casa[peçaSelecionada.getPosX() - 2][peçaSelecionada.getPosY() + 2].
                            getPeça() == null) {

                        esquerda = casa[peçaSelecionada.getPosX() - 2][peçaSelecionada.getPosY() + 2];
                        esquerd = true;
                    }


                }
            }
        }
           }

        if(esquerd == true || dir == true){

            peçaSelecionada.setDireita(dir);
            peçaSelecionada.setEsquerda(esquerd);
            jogarNovamente = true;
            minhaVez = true;

            return true;
        }

        return false;
    }

    public void atualizarJogadaOponente(String jogada){

        String caracterAtual="";
        String proximoCarac="";
        int indiceChave=2; // a chave é sempre +5 à partir do 2
        Peça peçaEscolhidaInimigo=null;
        char atual;


        // andar ou comer peça

        caracterAtual = Character.toString(jogada.charAt(2));

        // andar
        if(caracterAtual.equals("0")){

            caracterAtual = Character.toString(jogada.charAt(0));
            proximoCarac = Character.toString(jogada.charAt(1));

            // duvida
            if(proximoCarac.equals(" ")){

                if(idJogador == 1){

                    for(Peça p: peçasVermelhas){

                        if(p.getChaveDaPeça() == Integer.parseInt(caracterAtual)){
                            peçaEscolhidaInimigo = p;
                        }
                    }

                }else{

                    for(Peça p: peçasBrancas){

                        if(p.getChaveDaPeça() == Integer.parseInt(caracterAtual)){
                            peçaEscolhidaInimigo = p;
                        }
                    }

                }

            }else{

                caracterAtual = caracterAtual + proximoCarac;

                if(idJogador == 1){

                    for(Peça p: peçasVermelhas){

                        if(p.getChaveDaPeça() == Integer.parseInt(caracterAtual)){
                            peçaEscolhidaInimigo = p;
                        }
                    }

                }else{

                    for(Peça p: peçasBrancas){

                        if(p.getChaveDaPeça() == Integer.parseInt(caracterAtual)){
                            peçaEscolhidaInimigo = p;
                        }
                    }

                }

            }
            //----------------------------------------- peça ja escolhida --------------------------

            int x = Integer.parseInt(Character.toString(jogada.charAt(3)));
            int y = Integer.parseInt(Character.toString(jogada.charAt(4)));;

            casa[peçaEscolhidaInimigo.getPosX()][peçaEscolhidaInimigo.getPosY()].removePeça();
            casa[x][y].setPeça(peçaEscolhidaInimigo);
            peçaEscolhidaInimigo.setXY(x,y);
            peçaEscolhidaInimigo.setPosicaoPeça(casa[x][y].getPosicao());

        }else{// comer

            int posAtual=3;
            int tamanho = jogada.length();
            Peça comida=null;

            /*------------------------INICIO DA SELEÇÃO DE PEÇA----------------------------------*/

            caracterAtual = Character.toString(jogada.charAt(0));
            proximoCarac = Character.toString(jogada.charAt(1));

            // duvida
            if(proximoCarac.equals(" ")){

                if(idJogador == 1){

                    for(Peça p: peçasVermelhas){

                        if(p.getChaveDaPeça() == Integer.parseInt(caracterAtual)){
                            peçaEscolhidaInimigo = p;
                        }
                    }

                }else{

                    for(Peça p: peçasBrancas){

                        if(p.getChaveDaPeça() == Integer.parseInt(caracterAtual)){
                            peçaEscolhidaInimigo = p;
                        }
                    }

                }

            }else{

                caracterAtual = caracterAtual + proximoCarac;

                if(idJogador == 1){

                    for(Peça p: peçasVermelhas){

                        if(p.getChaveDaPeça() == Integer.parseInt(caracterAtual)){
                            peçaEscolhidaInimigo = p;
                        }
                    }

                }else{

                    for(Peça p: peçasBrancas){

                        if(p.getChaveDaPeça() == Integer.parseInt(caracterAtual)){
                            peçaEscolhidaInimigo = p;
                        }
                    }

                }

            }

            /*--------------------FIM DA SELEÇÃO DE PEÇA------------------------------------------*/

            /*-------------------INICIO DA SELEÇÃO DE PEÇA COMIDA--------------------------------*/

            while(posAtual < tamanho){

                String chave1 = Character.toString(jogada.charAt(posAtual));
                String chave2 = Character.toString(jogada.charAt(posAtual+1));

                //peça que foi comida

                if(chave2.equals(" ")){

                    if(idJogador == 1){

                        for(Peça p: peçasBrancas){

                            if(p.getChaveDaPeça() == Integer.parseInt(chave1)){
                                comida = p;
                            }
                        }

                    }else{

                        for(Peça p: peçasVermelhas){

                            if(p.getChaveDaPeça() == Integer.parseInt(chave1)){
                               comida = p;
                            }
                        }

                    }

                }else{

                    chave1 = chave1+chave2;

                    if(idJogador == 1){

                        for(Peça p: peçasBrancas){

                            if(p.getChaveDaPeça() == Integer.parseInt(chave1)){
                                comida = p;
                            }
                        }

                    }else{

                        for(Peça p: peçasVermelhas){

                            if(p.getChaveDaPeça() == Integer.parseInt(chave1)){
                                comida = p;
                            }
                        }

                    }

                }
                //---------------FIM DA SELECAO DE PEÇA COMIDA-------------------------------------


                //---------------LOCAL QUE A PEÇA DO OPONENTE PAROU--------------------------------

                int x = Integer.parseInt(Character.toString(jogada.charAt(posAtual+2)));
                int y = Integer.parseInt(Character.toString(jogada.charAt(posAtual+3)));

                casa[peçaEscolhidaInimigo.getPosX()][peçaEscolhidaInimigo.getPosY()].removePeça();

                casa[x][y].setPeça(peçaEscolhidaInimigo);
                peçaEscolhidaInimigo.setXY(x,y);

                peçaEscolhidaInimigo.setPosicaoPeça(casa[x][y].getPosicao());

                //--------------Peça que foi comida-----------------------------------------------

                casa[comida.getPosX()][comida.getPosY()].removePeça();
                peçaDeletar = comida;

                if(idJogador == 1){
                    if(y == 0){
                        peçaEscolhidaInimigo.setRainha(true);
                        peçaEscolhidaInimigo.setImagemPeça
                                (getImageFactory().createImage(R.drawable.pecavermelharainha));
                    }

                }else{

                    if(y == 7){
                        peçaEscolhidaInimigo.setRainha(true);
                        peçaEscolhidaInimigo.setImagemPeça
                                (getImageFactory().createImage(R.drawable.pecaazulrainha));
                    }
                }

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                }

                posAtual = posAtual +5;
            }

        }

        minhaVez = true;
    }

    public void enviarJogada(String jgd){

        this.jogada = jgd;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                escrever.println(jogada);
            }

        });
        t.start();
    }
}
