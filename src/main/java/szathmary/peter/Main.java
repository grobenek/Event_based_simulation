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
    ElectroShopSimulation electroShopSimulation = new ElectroShopSimulation(100_000, 13, 4, false);
    electroShopSimulation.startSimulation();

//        IModel model = new SimulationModel();
//        IController controller = new SimulationController(model);
//        IMainWindow mainWindow = new MainWindow(controller);

    // generator testing

    //    File file = new File("./exponential.csv");
    //            ContinuousEmpiricRandomGenerator continuousEmpiricRandomGenerator =
    //                new ContinuousEmpiricRandomGenerator(
    //                    List.of(
    //                        new EmpiricOption<>(3.0, 5.0, 0.4),
    //                        new EmpiricOption<>(8.0, 12.0, 0.2),
    //                        new EmpiricOption<>(20.0, 22.0, 0.4)));
    //
    //        ContinuousTriangularRandomGenerator continuousTrinagularGenerator =
    //            new ContinuousTriangularRandomGenerator(10, 15, 30);
    //
    //    ContinuousExponentialRandomGenerator continuousExponentialRandomGenerator =
    //        new ContinuousExponentialRandomGenerator(1.0/120.0);
    //
    //    try {
    //      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new
    // FileOutputStream(file));
    //      for (int i = 0; i < 100000000; i++) {
    //        outputStreamWriter.write(continuousExponentialRandomGenerator.sample() + "\n");
    //      }
    //
    //    } catch (IOException e) {
    //      throw new RuntimeException(e);
    //    }
  }
}
