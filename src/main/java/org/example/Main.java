
package org.example;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            SimulationUI simulationUI = new SimulationUI();

            simulationUI.start();

        });
    }
}
