package szathmary.peter.mvc.controller;

import szathmary.peter.mvc.observable.IObserver;
import szathmary.peter.mvc.observable.IReplicationObservable;

public interface IController extends IReplicationObservable, IObserver {
  void startSimulation();

  void setParameters(
      long numberOfReplications,
      int numberOfServiceStations,
      int numberOfCashRegisters,
      boolean verboseSimulation);

  void stopSimulation();

  void setVerboseSimulation(boolean verbose);

  void changeSimulationSpeed(int value);
}
