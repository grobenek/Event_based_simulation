package szathmary.peter.mvc.view;

import szathmary.peter.mvc.observable.IObserver;

public interface IMainWindow extends IObserver {
  void startSimulation();

  void setParameters();

  void stopSimulation();

  void setVerboseSimulation(boolean verbose);
}
