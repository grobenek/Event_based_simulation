package szathmary.peter.event.service.casualandcontract;

import szathmary.peter.event.Event;
import szathmary.peter.event.ticketmachine.RemoveCustomerFromTicketQueue;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 30/03/2024. */
public class RemoveCustomerFromCasualAndContractQueueEvent extends Event {
  public RemoveCustomerFromCasualAndContractQueueEvent(Double timestamp) {
    super(timestamp);
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    if (electroShopSimulation.isCasualContractCustomerQueueEmpty()) {
      throw new IllegalStateException(
          "Cannot remove customer from casual and contract queue, because it is empty!");
    }

    Customer customerToServe =
        electroShopSimulation.removeCustomerFromCasualAndContractCustomerQueue();

    electroShopSimulation.setTicketMachineStopped(electroShopSimulation.isServiceQueueFull());

    ServiceStation freeServiceStation = electroShopSimulation.getFreeServiceStation(false);

    electroShopSimulation.addEvent(
        new StartOfCasualAndContractCustomerServiceEvent(getTimestamp(), customerToServe, freeServiceStation));
  }

  @Override
  public String getEventDescription() {
    return String.format("Removing customer from casual and contract queue at %s", TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
