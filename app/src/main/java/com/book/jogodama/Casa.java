package com.book.jogodama;

public class Casa {

    private Peça peça;
    private float left;
    private float top;
    private float right;
    private float bottom;

    public void setDimensoes(float left,float top, float right,float bottom){

        this.top = top;
        this.left= left;
        this.right = right;
        this.bottom = bottom;
    }

    public void setPeça(Peça p){peça = p;}
    public void removePeça(){peça = null;}

    public boolean temPeça(){

        if(peça != null){

            return true;
        }else{

            return false;
        }
    }
}
