package szathmary.peter.event.ticketmachine;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 29/03/2024. */
public class StartGettingTicketEvent extends Event {
  private final Customer servedCustomer;

  public StartGettingTicketEvent(Double timestamp, Customer removedCustomerFromTicketMachineQueue) {
    super(timestamp);

    this.servedCustomer = removedCustomerFromTicketMachineQueue;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    electroShopSimulation.setTicketMachineServingCustomer(true);

    double timeOfEndOfServingCustomer =
        getTimestamp() + electroShopSimulation.getTicketPrintingTimeRandomGenerator().sample();

    Event endOfGettingTicketEvent =
        new EndOfGettingTicketEvent(timeOfEndOfServingCustomer, servedCustomer);

    electroShopSimulation.addEvent(endOfGettingTicketEvent);
  }

  @Override
  public String getEventDescription() {
    return String.format("Customer is getting ticket at %s", TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
