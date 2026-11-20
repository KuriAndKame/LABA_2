package org.example;

public class State {

    private double x;
    private double y;
    private double v;

    public State(double x, double y, double v) {
        this.x = x;
        this.y = y;
        this.v = v;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

}
