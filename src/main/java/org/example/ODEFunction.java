package org.example;

public class ODEFunction {

    public static double odeFunction(double x, double y, double v) {
        return 2 * (Math.pow(x, 2) + 1) * Math.cos(x) + y - 2 * x * v;
    }
}
