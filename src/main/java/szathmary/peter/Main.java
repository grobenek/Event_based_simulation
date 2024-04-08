package szathmary.peter;

import szathmary.peter.mvc.controller.IController;
import szathmary.peter.mvc.controller.SimulationController;
import szathmary.peter.mvc.model.IModel;
import szathmary.peter.mvc.model.SimulationModel;
import szathmary.peter.mvc.view.IMainWindow;
import szathmary.peter.mvc.view.MainWindow;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.util.TimeFormatter;

public class Main {
  public static void main(String[] args) {
//    ElectroShopSimulation electroShopSimulation = new ElectroShopSimulation(25_000, 13, 4, false);
//    electroShopSimulation.startSimulation();

        IModel model = new SimulationModel();
        IController controller = new SimulationController(model);
        IMainWindow mainWindow = new MainWindow(controller);
  }
}
