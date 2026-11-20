
package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RootFinder {

    private static final double F_EPSILON = 0.001;
    private static final double ALPHA_EPSILON = 0.0001;
    private static final double INTERVAL_EXPANSION_FACTOR = 2.0;
    private int iterationCount;
    private static final double INITIAL_STEP = 0.05;
    private static final double MAX_ALPHA = 1.5;
    private static final int MAX_ITERATIONS = 100;

    public static final double INITIAL_ALPHA = 0.25;

    private final List<Trajectory> trajectories = new ArrayList<>();

    private Trajectory finalTrajectory;

    private Trajectory calculateTrajectory(double alpha) {

        State initialState = new State(0, 0, 0);

        Simulation simulation = new Simulation(initialState, alpha);

        return simulation.rungeKuttaIteration();
    }

    public double calculateF(Trajectory trajectory) {

        List<State> states = trajectory.getState();

        State lastState = states.get(states.size() - 1);

        return lastState.getY() - 0.5 * Math.sin(0.5);
    }

    public double calculateF(double alpha) {

        Trajectory trajectory = calculateTrajectory(alpha);

        return calculateF(trajectory);
    }

    private Trajectory addIfAbsent(Trajectory candidate) {

        for (Trajectory trajectory : trajectories) {

            if (Math.abs(trajectory.getAlpha() - candidate.getAlpha()) < 1e-12) {
                return trajectory;
            }
        }

        trajectories.add(candidate);

        return candidate;
    }

    private Trajectory findBestTrajectory(Trajectory first, Trajectory second) {

        if (Math.abs(calculateF(first)) < Math.abs(calculateF(second))) {
            return first;
        }

        return second;
    }

    public void solver(double initialAlpha) {

        if (!Double.isFinite(initialAlpha) || Math.abs(initialAlpha) > MAX_ALPHA) {
            throw new IllegalArgumentException(
                    "Угол должен находиться в пределах от -1.5 до 1.5 радиан"
            );
        }


        trajectories.clear();
        iterationCount = 0;
        finalTrajectory = null;

        Trajectory initialTrajectory = calculateTrajectory(initialAlpha);

        trajectories.add(initialTrajectory);

        if (Math.abs(calculateF(initialTrajectory)) <= F_EPSILON) {
            finalTrajectory = initialTrajectory;
            return;
        }

        double step = INITIAL_STEP;

        Trajectory leftTrajectory;
        Trajectory rightTrajectory;

        while (true) {
            double left = Math.max(-MAX_ALPHA, initialAlpha - step);
            double right = Math.min(MAX_ALPHA, initialAlpha + step);

            leftTrajectory = calculateTrajectory(left);
            rightTrajectory = calculateTrajectory(right);

            double fLeft = calculateF(leftTrajectory);
            double fRight = calculateF(rightTrajectory);

            if (Math.abs(fLeft) <= F_EPSILON) {
                finalTrajectory = addIfAbsent(leftTrajectory);
                return;
            }

            if (Math.abs(fRight) <= F_EPSILON) {

                finalTrajectory = addIfAbsent(rightTrajectory);
                return;
            }

            if (fLeft * fRight < 0) {

                leftTrajectory = addIfAbsent(leftTrajectory);
                rightTrajectory = addIfAbsent(rightTrajectory);

                break;
            }

            if (left == -MAX_ALPHA && right == MAX_ALPHA) {

                throw new IllegalArgumentException(
                        "Не удалось подобрать интервал для поиска корня"
                );
            }

            step *= INTERVAL_EXPANSION_FACTOR;
        }

        // Метод дихотомии
        for (int i = 0; i < MAX_ITERATIONS; i++) {

            iterationCount++;
            double left = leftTrajectory.getAlpha();
            double right = rightTrajectory.getAlpha();

            double middle = (left + right) / 2;

            // Проверка возможности дальнейшего деления
            if (middle == left || middle == right) {
                finalTrajectory = findBestTrajectory(leftTrajectory, rightTrajectory);
                return;
            }

            Trajectory middleTrajectory = calculateTrajectory(middle);

            middleTrajectory = addIfAbsent(middleTrajectory);

            double fMiddle = calculateF(middleTrajectory);

            // Проверка невязки
            if (Math.abs(fMiddle) <= F_EPSILON) {
                finalTrajectory = middleTrajectory;
                return;
            }

            if (calculateF(leftTrajectory) * fMiddle < 0) {
                rightTrajectory = middleTrajectory;

            } else {
                leftTrajectory = middleTrajectory;
            }

            if (rightTrajectory.getAlpha() - leftTrajectory.getAlpha() <= ALPHA_EPSILON) {

                finalTrajectory = findBestTrajectory(leftTrajectory, rightTrajectory);
                return;
            }
        }

        throw new IllegalArgumentException(
                "Не удалось найти корень за допустимое число итераций"
        );
    }

    public List<Trajectory> getTrajectories() {
        return List.copyOf(trajectories);
    }

    public Trajectory getFinalTrajectory() {
        if (finalTrajectory == null) {
            throw new IllegalStateException("Расчёт ещё не выполнен");
        }

        return finalTrajectory;
    }

    public double getSolutionAlpha() {

        return getFinalTrajectory().getAlpha();
    }

    public boolean hasResult() {
        return finalTrajectory != null;
    }

    public int getIterationCount() {
        return iterationCount;
    }
}
