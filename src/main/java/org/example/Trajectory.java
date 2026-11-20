package org.example;

import java.util.ArrayList;

public class Trajectory {

    private ArrayList<State> state;
    private double alpha;

    public Trajectory(ArrayList<State> state, double alpha) {
        this.state = state;
        this.alpha = alpha;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public ArrayList<State> getState() {
        return state;
    }

    public void setState(ArrayList<State> state) {
        this.state = state;
    }






}
