
package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class SimulationUI {

    private JFrame frame;
    private JButton startButton;
    private GraphPanel graphPanel;
    private JTextArea resultArea;

    public void start() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {

        frame = new JFrame("Лабораторная работа № 2");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Информация о расчёте
        JPanel infoPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        JLabel infoLabel = new JLabel(
                "Метод стрельбы. Начальное приближение alpha = "
                        + RootFinder.INITIAL_ALPHA + " рад."
        );

        infoLabel.setFont(
                new Font("Times New Roman", Font.PLAIN, 14)
        );

        infoPanel.add(infoLabel);

        // График
        graphPanel = new GraphPanel();

        // Область результатов
        resultArea = new JTextArea();

        resultArea.setEditable(false);

        resultArea.setFont(
                new Font("Monospaced", Font.PLAIN, 12)
        );

        resultArea.setText(
                "Нажмите кнопку для запуска расчёта."
        );

        JScrollPane scrollPane = new JScrollPane(resultArea);

        scrollPane.setPreferredSize(
                new Dimension(350, 0)
        );

        // Кнопка запуска
        JPanel controlPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        startButton = new JButton("Запустить расчёт");

        startButton.setFont(
                new Font("Times New Roman", Font.PLAIN, 16)
        );

        controlPanel.add(startButton);

        startButton.addActionListener(e -> {

            try {

                // Запускаем расчёт
                graphPanel.calculateTrajectories(
                        RootFinder.INITIAL_ALPHA
                );

                // Выводим результаты
                showResults();

                startButton.setText("Пересчитать");

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Ошибка расчёта: " + ex.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Размещение компонентов
        frame.add(infoPanel, BorderLayout.NORTH);
        frame.add(graphPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.EAST);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setSize(1200, 750);

        frame.setMinimumSize(
                new Dimension(850, 550)
        );

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    // Вывод результатов поиска
    private void showResults() {

        RootFinder rootFinder = graphPanel.getRootFinder();

        List<Trajectory> trajectories =
                rootFinder.getTrajectories();

        Trajectory finalTrajectory =
                rootFinder.getFinalTrajectory();

        StringBuilder text = new StringBuilder();

        text.append("РЕЗУЛЬТАТЫ ПОИСКА\n\n");

        text.append("Начальное приближение: ")
                .append(RootFinder.INITIAL_ALPHA)
                .append("\n\n");

        text.append("№ | Тип траектории | alpha | F(alpha)\n");

        text.append(
                "----------------------------------------------------------\n"
        );

        for (int i = 0; i < trajectories.size(); i++) {

            Trajectory trajectory = trajectories.get(i);

            // Определяем тип траектории
            String type;

            if (trajectory == finalTrajectory && i == 0) {

                type = "Начальная/итоговая";

            } else if (trajectory == finalTrajectory) {

                type = "Итоговая";

            } else if (i == 0) {

                type = "Начальная";

            } else {

                type = "Промежуточная";
            }

            double alpha = trajectory.getAlpha();

            double f = rootFinder.calculateF(trajectory);

            text.append(
                    String.format(
                            Locale.US,
                            "%d | %-20s | %.8f | %.8e%n",
                            i + 1,
                            type,
                            alpha,
                            f
                    )
            );
        }

        text.append(
                "\n----------------------------------------------------------\n"
        );

        text.append(
                String.format(
                        Locale.US,
                        "Найденный alpha: %.10f%n",
                        rootFinder.getSolutionAlpha()
                )
        );

        text.append(
                String.format(
                        Locale.US,
                        "Невязка: %.10e%n",
                        rootFinder.calculateF(finalTrajectory)
                )
        );

        text.append(
                "Количество итераций дихотомии: "
                        + rootFinder.getIterationCount()
                        + "\n"
        );

        text.append(
                "Количество рассчитанных траекторий: "
                        + trajectories.size()
                        + "\n"
        );

        text.append(
                "\nАналитическое решение:\n"
        );

        text.append("y(x) = x * sin(x)\n");

        text.append("alpha* = 0\n");

        resultArea.setText(text.toString());

        resultArea.setCaretPosition(0);
    }
}
