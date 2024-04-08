package szathmary.peter.event.service.online;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 30/03/2024. */
public class RemoveCustomerFromOnlineQueueEvent extends Event {
  public RemoveCustomerFromOnlineQueueEvent(Double timestamp) {
    super(timestamp);
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    if (electroShopSimulation.isOnlineCustomerQueueEmpty()) {
      throw new IllegalStateException(
          "Cannot remove customer from online queue, because it is empty!");
    }

    Customer customerToServe = electroShopSimulation.removeCustomerFromOnlineCustomerQueue();
    customerToServe.setTimeOfEnteringServiceQueue(getTimestamp());

    electroShopSimulation.setTicketMachineStopped(electroShopSimulation.isServiceQueueFull());

    ServiceStation freeServiceStation = electroShopSimulation.getFreeServiceStation(true);

    electroShopSimulation.addEvent(new StartOnlineCustomerService(getTimestamp(), customerToServe, freeServiceStation));
  }

  @Override
  public String getEventDescription() {
    return String.format(
        "Removing online customer from online customer queue at %s",
        TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
