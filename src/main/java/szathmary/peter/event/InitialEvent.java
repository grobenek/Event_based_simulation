package szathmary.peter.event;

import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;

/** Created by petos on 30/03/2024. */
public class InitialEvent extends Event {
  public InitialEvent(Double timestamp) {
    super(timestamp);
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);
    Event customerArrivalEvent =
        new CustomerArrivalInSystemEvent(
            getTimestamp()
                + electroShopSimulation.getTimeBetweenCustomerArrivalsRandomGenerator().sample());

    simulationCore.addEvent(customerArrivalEvent);

    if (simulationCore.isVerbose()) {
      Event sleepEvent = new SleepEvent(getTimestamp() + simulationCore.getSleepEventInterval());
      simulationCore.addEvent(sleepEvent);
    }
  }

  @Override
  public String getEventDescription() {
    return "Starting simulation";
  }
}
