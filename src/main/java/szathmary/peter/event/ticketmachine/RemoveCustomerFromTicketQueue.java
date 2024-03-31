package szathmary.peter.event.ticketmachine;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.customer.Customer;

/** Created by petos on 29/03/2024. */
public class RemoveCustomerFromTicketQueue extends Event {
  public RemoveCustomerFromTicketQueue(Double timestamp) {
    super(timestamp);
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    if (electroShopSimulation.isTicketQueueEmpty()) {
      throw new IllegalStateException(String.format("Cannot remove customer from ticket machine queue, because it is empty at %f", getTimestamp()));
    }

    Customer removedCustomerFromTicketMachineQueue = electroShopSimulation.removeCustomerFromTicketQueue();

    electroShopSimulation.addEvent(new StartGettingTicketEvent(getTimestamp(), removedCustomerFromTicketMachineQueue));

  }

  @Override
  public String getEventDescription() {
    return String.format("Customer is removed from ticket machine queue at %f!", getTimestamp());
  }
}
