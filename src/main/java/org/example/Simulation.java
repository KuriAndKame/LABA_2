package org.example;

import java.util.ArrayList;

public class Simulation {

    private final State state;
    private final int N = 1000;
    private final double alpha;

    public Simulation(State state, double alpha) {
        this.state = state;
        this.alpha = alpha;
    }

    public void setInitialV(){
        this.state.setV(Math.tan(alpha));
    }

    public State rungeKuttaIntegrator(State state) {

        double h = (0.5 - 0) / N;

        double k1y = state.getV();
        double k1v = ODEFunction.odeFunction(state.getX(), state.getY(), state.getV());

        double k2y = k1v * h / 2 + state.getV();
        double k2v = ODEFunction.odeFunction(state.getX() + h/2, state.getY() + h / 2 * k1y, state.getV() + h / 2 * k1v);

        double k3y = k2v * h / 2 + state.getV();
        double k3v = ODEFunction.odeFunction(state.getX() + h/2, state.getY() + h / 2 * k2y, state.getV() + h / 2 * k2v);

        double k4y = k3v * h + state.getV();
        double k4v = ODEFunction.odeFunction(state.getX() + h, state.getY() + h * k3y, state.getV() + h * k3v);

        double yNew = state.getY() + h / 6 * (k1y + 2 * k2y + 2 * k3y + k4y);
        double vNew = state.getV() + h / 6 * (k1v + 2 * k2v + 2 * k3v + k4v);
        double xNew = state.getX() + h;

        return new State(xNew, yNew, vNew);

    }

    public Trajectory rungeKuttaIteration() {
        setInitialV();
        ArrayList<State> states = new ArrayList<>(N + 1);
        states.add(state);
        for(int i = 0; i < N; i++){
            states.add(rungeKuttaIntegrator(states.get(i)));
        }

        return new Trajectory(states, alpha);
    }






}
