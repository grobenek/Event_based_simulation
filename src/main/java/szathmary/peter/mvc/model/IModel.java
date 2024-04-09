package szathmary.peter.mvc.model;

import szathmary.peter.mvc.observable.IObserver;
import szathmary.peter.mvc.observable.IReplicationObservable;

public interface IModel extends IReplicationObservable, IObserver {
  void startSimulation();

  void setParameters(SimulationParameters simulationParameters);

  void stopSimulation();

  void setVerboseSimulation(boolean verbose);

  void changeSimulationSpeed(int secondsInOneTick);
}
