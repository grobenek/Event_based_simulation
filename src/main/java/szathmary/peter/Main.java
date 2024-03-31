package szathmary.peter;

import szathmary.peter.simulation.ElectroShopSimulation;

public class Main {
  public static void main(String[] args) {
    ElectroShopSimulation electroShopSimulation = new ElectroShopSimulation(25_000, 15, 6, false);
    electroShopSimulation.startSimulation();
  }
}
