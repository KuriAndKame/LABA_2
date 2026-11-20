
package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class GraphPanel extends JPanel {

    private final double xMin = 0;
    private final double xMax = 0.5;

    private double yMin = -0.1;
    private double yMax = 0.5;

    private final int margin = 65;

    private final RootFinder rootFinder = new RootFinder();

    // Запуск расчёта
    public void calculateTrajectories(double initialAlpha) {

        rootFinder.solver(initialAlpha);

        repaint();
    }

    // Получение объекта для доступа к результатам
    public RootFinder getRootFinder() {
        return rootFinder;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        try {

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (getWidth() <= 2 * margin
                    || getHeight() <= 2 * margin) {
                return;
            }

            updateYBounds();

            drawGrid(g2);
            drawAxes(g2);

            // Ограничиваем область рисования графиков
            Shape oldClip = g2.getClip();

            g2.clipRect(
                    margin,
                    margin,
                    getWidth() - 2 * margin,
                    getHeight() - 2 * margin
            );

            drawTrajectories(g2);
            drawAnalyticalSolution(g2);

            g2.setClip(oldClip);

            drawLegend(g2);

        } finally {
            g2.dispose();
        }
    }

    // Автоматическое масштабирование оси Y
    private void updateYBounds() {

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        // Учитываем все рассчитанные траектории
        for (Trajectory trajectory : rootFinder.getTrajectories()) {

            for (State state : trajectory.getState()) {

                double y = state.getY();

                if (Double.isFinite(y)) {

                    min = Math.min(min, y);
                    max = Math.max(max, y);
                }
            }
        }

        // Учитываем аналитическое решение
        for (int i = 0; i <= 200; i++) {

            double x = xMin + (xMax - xMin) * i / 200.0;

            double y = x * Math.sin(x);

            min = Math.min(min, y);
            max = Math.max(max, y);
        }

        if (!Double.isFinite(min) || !Double.isFinite(max)) {

            yMin = -0.1;
            yMax = 0.5;

            return;
        }

        double range = max - min;

        double padding = Math.max(range * 0.1, 0.02);

        yMin = min - padding;
        yMax = max + padding;
    }

    // Преобразование координаты X в экранную
    private int toScreenX(double x) {

        return margin + (int) (
                (x - xMin) / (xMax - xMin)
                        * (getWidth() - 2 * margin)
        );
    }

    // Преобразование координаты Y в экранную
    private int toScreenY(double y) {

        return getHeight() - margin - (int) (
                (y - yMin) / (yMax - yMin)
                        * (getHeight() - 2 * margin)
        );
    }

    // Отрисовка сетки
    private void drawGrid(Graphics2D g2) {

        int width = getWidth();
        int height = getHeight();

        g2.setColor(Color.LIGHT_GRAY);

        // Вертикальные линии
        for (int i = 0; i <= 5; i++) {

            double x = xMin + i * 0.1;

            int screenX = toScreenX(x);

            g2.drawLine(
                    screenX,
                    margin,
                    screenX,
                    height - margin
            );
        }

        // Горизонтальные линии
        for (int i = 0; i <= 6; i++) {

            double y = yMin + (yMax - yMin) * i / 6.0;

            int screenY = toScreenY(y);

            g2.drawLine(
                    margin,
                    screenY,
                    width - margin,
                    screenY
            );
        }
    }

    // Отрисовка осей и подписей
    private void drawAxes(Graphics2D g2) {

        int width = getWidth();
        int height = getHeight();

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.5f));

        int zeroY = toScreenY(0);
        int zeroX = toScreenX(0);

        // Ось X
        g2.drawLine(
                margin,
                zeroY,
                width - margin,
                zeroY
        );

        // Ось Y
        g2.drawLine(
                zeroX,
                margin,
                zeroX,
                height - margin
        );

        // Деления X
        for (int i = 0; i <= 5; i++) {

            double x = xMin + i * 0.1;

            int screenX = toScreenX(x);

            g2.drawLine(
                    screenX,
                    zeroY - 5,
                    screenX,
                    zeroY + 5
            );

            g2.drawString(
                    String.format(Locale.US, "%.1f", x),
                    screenX - 10,
                    zeroY + 20
            );
        }

        // Деления Y
        for (int i = 0; i <= 6; i++) {

            double y = yMin + (yMax - yMin) * i / 6.0;

            int screenY = toScreenY(y);

            g2.drawLine(
                    zeroX - 5,
                    screenY,
                    zeroX + 5,
                    screenY
            );

            g2.drawString(
                    String.format(Locale.US, "%.3f", y),
                    zeroX - 55,
                    screenY + 5
            );
        }
    }

    // Отрисовка всех численных траекторий
    private void drawTrajectories(Graphics2D g2) {

        if (!rootFinder.hasResult()) {
            return;
        }

        List<Trajectory> trajectories =
                rootFinder.getTrajectories();

        Trajectory finalTrajectory =
                rootFinder.getFinalTrajectory();

        // Отрисовка промежуточных траекторий
        for (int i = 0; i < trajectories.size(); i++) {

            Trajectory trajectory = trajectories.get(i);

            // Итоговую траекторию рисуем отдельно
            if (trajectory == finalTrajectory) {
                continue;
            }

            // Начальное приближение
            if (i == 0) {
                g2.setColor(Color.BLUE);
            } else {
                g2.setColor(new Color(0, 140, 60));
            }

            g2.setStroke(new BasicStroke(1.5f));

            drawLine(
                    g2,
                    trajectory.getState()
            );
        }

        // Итоговое решение
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));

        drawLine(
                g2,
                finalTrajectory.getState()
        );
    }

    // Аналитическое решение y = x * sin(x)
    private void drawAnalyticalSolution(Graphics2D g2) {

        g2.setColor(Color.BLACK);

        g2.setStroke(
                new BasicStroke(
                        2,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND,
                        0,
                        new float[]{7, 5},
                        0
                )
        );

        int steps = 300;

        for (int i = 0; i < steps; i++) {

            double x1 = xMin
                    + (xMax - xMin) * i / steps;

            double x2 = xMin
                    + (xMax - xMin) * (i + 1) / steps;

            double y1 = x1 * Math.sin(x1);
            double y2 = x2 * Math.sin(x2);

            g2.drawLine(
                    toScreenX(x1),
                    toScreenY(y1),
                    toScreenX(x2),
                    toScreenY(y2)
            );
        }
    }

    // Соединение точек траектории
    private void drawLine(Graphics2D g2, List<State> states) {

        for (int i = 0; i < states.size() - 1; i++) {

            State current = states.get(i);
            State next = states.get(i + 1);

            if (!Double.isFinite(current.getY())
                    || !Double.isFinite(next.getY())) {
                continue;
            }

            g2.drawLine(
                    toScreenX(current.getX()),
                    toScreenY(current.getY()),
                    toScreenX(next.getX()),
                    toScreenY(next.getY())
            );
        }
    }

    // Легенда
    private void drawLegend(Graphics2D g2) {

        if (!rootFinder.hasResult()) {
            return;
        }

        int x = Math.max(
                margin + 10,
                getWidth() - margin - 260
        );

        int y = margin + 20;

        g2.setFont(new Font("Arial", Font.PLAIN, 12));

        // Начальное приближение
        g2.setColor(Color.BLUE);
        g2.drawLine(x, y, x + 25, y);

        g2.setColor(Color.BLACK);
        g2.drawString(
                "Начальное приближение",
                x + 35,
                y + 5
        );

        y += 22;

        // Промежуточные траектории
        g2.setColor(new Color(0, 140, 60));
        g2.drawLine(x, y, x + 25, y);

        g2.setColor(Color.BLACK);
        g2.drawString(
                "Промежуточные итерации",
                x + 35,
                y + 5
        );

        y += 22;

        // Итоговая траектория
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(x, y, x + 25, y);

        g2.setColor(Color.BLACK);
        g2.drawString(
                "Итоговое решение",
                x + 35,
                y + 5
        );

        y += 22;

        // Аналитическое решение
        g2.setColor(Color.BLACK);

        g2.setStroke(
                new BasicStroke(
                        2,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND,
                        0,
                        new float[]{7, 5},
                        0
                )
        );

        g2.drawLine(x, y, x + 25, y);

        g2.drawString(
                "Аналитическое решение",
                x + 35,
                y + 5
        );
    }
}
