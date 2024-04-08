package szathmary.peter.event.ticketmachine;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 29/03/2024. */
public class RemoveCustomerFromTicketQueue extends Event {
  public RemoveCustomerFromTicketQueue(Double timestamp) {
    super(timestamp);
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    if (electroShopSimulation.isTicketMachineServingCustomer()) {
      throw new IllegalStateException("Cannot remove customer from ticket queue, becasue ticket queue is serving customer!");
    }

    if (electroShopSimulation.isTicketQueueEmpty()) {
      throw new IllegalStateException(
          String.format(
              "Cannot remove customer from ticket machine queue, because it is empty at %f",
              getTimestamp()));
    }

    if (getTimestamp() >= ElectroShopSimulation.CLOSING_HOURS_OF_TICKET_MACHINE) {
      removeCustomersAfterClosingHours(electroShopSimulation);
      return; // TODO teoreticky tu chyba
    }

    Customer removedCustomerFromTicketMachineQueue =
        electroShopSimulation.removeCustomerFromTicketQueue();

    electroShopSimulation.addEvent(
        new StartGettingTicketEvent(getTimestamp(), removedCustomerFromTicketMachineQueue));
  }

  private void removeCustomersAfterClosingHours(ElectroShopSimulation electroShopSimulation) {
    while (!electroShopSimulation.isTicketQueueEmpty()) {
      Customer removedCustomer = electroShopSimulation.removeCustomerFromTicketQueue();
      removedCustomer.setTimeOfLeavingSystem(ElectroShopSimulation.CLOSING_HOURS_OF_TICKET_MACHINE);
    }
    electroShopSimulation
        .getTicketQueueLengthStatisticReplication()
        .addObservation(0.0, ElectroShopSimulation.CLOSING_HOURS_OF_TICKET_MACHINE);
  }

  @Override
  public String getEventDescription() {
    return String.format(
        "Customer is removed from ticket machine queue at %s!",
        TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
