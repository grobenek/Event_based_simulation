package szathmary.peter.event.ticketmachine;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 29/03/2024. */
public class StartGettingTicketEvent extends Event {

  public StartGettingTicketEvent(Double timestamp) {
    super(timestamp);
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    if (getTimestamp() >= ElectroShopSimulation.CLOSING_HOURS_OF_TICKET_MACHINE) {
      removeCustomersAfterClosingHours(electroShopSimulation);
      return;
    }

    Customer servedCustomer = electroShopSimulation.removeCustomerFromTicketQueue();

    electroShopSimulation.setTicketMachineServingCustomer(true);

    double timeOfEndOfServingCustomer =
        getTimestamp() + electroShopSimulation.getTicketPrintingTimeRandomGenerator().sample();

    Event endOfGettingTicketEvent =
        new EndOfGettingTicketEvent(timeOfEndOfServingCustomer, servedCustomer);

    electroShopSimulation.addEvent(endOfGettingTicketEvent);
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
        "Customer is getting ticket at %s", TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
