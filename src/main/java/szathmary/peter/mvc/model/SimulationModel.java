package szathmary.peter.mvc.model;

import java.util.ArrayList;
import java.util.List;
import szathmary.peter.mvc.observable.IObservable;
import szathmary.peter.mvc.observable.IObserver;
import szathmary.peter.mvc.observable.IReplicationObservable;
import szathmary.peter.simulation.ElectroShopSimulation;

/** Created by petos on 01/04/2024. */
public class SimulationModel implements IModel {
  private final List<IObserver> observerList;
  private SimulationOverview simulationOverview;
  private ElectroShopSimulation electroShopSimulation;

  public SimulationModel() {
    this.observerList = new ArrayList<>();
  }

  @Override
  public void startSimulation() {
    electroShopSimulation.startSimulation();
  }

  @Override
  public void setParameters(SimulationParameters simulationParameters) {
    if (electroShopSimulation != null) {
      electroShopSimulation.setRunning(false);
    }

    this.electroShopSimulation =
        new ElectroShopSimulation(
            simulationParameters.numberOfReplications(),
            simulationParameters.numberOfServiceStations(),
            simulationParameters.numberOfCashRegisters(),
            simulationParameters.verboseSimulation());

    electroShopSimulation.attach(this);
  }

  @Override
  public void stopSimulation() {
    electroShopSimulation.setStopped(true);
  }

  @Override
  public void setVerboseSimulation(boolean verbose) {
    electroShopSimulation.setVerboseSimulation(verbose);
  }

  @Override
  public void changeSimulationSpeed(int secondsInOneTick) {
    electroShopSimulation.setSleepEventInterval(secondsInOneTick / 1000.0);
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
