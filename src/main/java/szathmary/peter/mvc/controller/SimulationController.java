package szathmary.peter.mvc.controller;

import java.util.ArrayList;
import java.util.List;
import szathmary.peter.mvc.model.IModel;
import szathmary.peter.mvc.model.SimulationOverview;
import szathmary.peter.mvc.model.SimulationParameters;
import szathmary.peter.mvc.observable.IObservable;
import szathmary.peter.mvc.observable.IObserver;
import szathmary.peter.mvc.observable.IReplicationObservable;

/** Created by petos on 01/04/2024. */
public class SimulationController implements IController {
  private final IModel model;
  private final List<IObserver> observerList;
  private SimulationOverview simulationOverview;

  public SimulationController(IModel model) {
    this.model = model;
    this.model.attach(this);

    this.observerList = new ArrayList<>();
  }

  @Override
  public void startSimulation() {
    model.startSimulation();
  }

  @Override
  public void setParameters(
      long numberOfReplications,
      int numberOfServiceStations,
      int numberOfCashRegisters,
      boolean verboseSimulation) {
    model.setParameters(
        new SimulationParameters(
            numberOfReplications,
            numberOfServiceStations,
            numberOfCashRegisters,
            verboseSimulation));
  }

  @Override
  public void stopSimulation() {
    model.stopSimulation();
  }

  @Override
  public void setVerboseSimulation(boolean verbose) {
    model.setVerboseSimulation(verbose);
  }

  @Override
  public void changeSimulationSpeed(int secondsInOneTick) {
    System.out.println(secondsInOneTick);
    if (secondsInOneTick == 0) {
      secondsInOneTick = 1;
    }

    model.changeSimulationSpeed(secondsInOneTick);
  }

  @Override
  public void attach(IObserver observer) {
    observerList.add(observer);
  }

  @Override
  public void detach(IObserver observer) {
    observerList.remove(observer);
  }

  @Override
  public void sendNotifications() {
    for (IObserver observer : observerList) {
      observer.update(this);
    }
  }

  @Override
  public void update(IObservable observable) {
    if (!(observable instanceof IReplicationObservable replicationObservable)) {
      return;
    }

    simulationOverview = replicationObservable.getSimulationOverview();

    sendNotifications();
  }

  @Override
  public SimulationOverview getSimulationOverview() {
    return simulationOverview;
  }
}
