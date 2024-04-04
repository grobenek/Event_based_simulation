package szathmary.peter.event.ticketmachine;

import szathmary.peter.event.Event;
import szathmary.peter.event.service.casualandcontract.RemoveCustomerFromCasualAndContractQueueEvent;
import szathmary.peter.event.service.online.RemoveCustomerFromOnlineQueueEvent;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 29/03/2024. */
public class EndOfGettingTicketEvent extends Event {
  private final Customer servedCustomer;

  public EndOfGettingTicketEvent(Double timestamp, Customer serverCustomer) {
    super(timestamp);

    this.servedCustomer = serverCustomer;
  }

  private static CustomerType generateCustomerType(double generatedRandomValue) {
    double cummulatedProbability = 0.0;
    CustomerType selectedCustomerType = null;
    for (int i = 0; i < CustomerType.values().length; i++) {
      CustomerType customerType = CustomerType.values()[i];

      cummulatedProbability += customerType.getTypeProbability();

      if (generatedRandomValue <= cummulatedProbability) {
        selectedCustomerType = customerType;
        break;
      }
    }

    if (selectedCustomerType == null) {
      throw new IllegalStateException("No customer type was selected!");
    }
    return selectedCustomerType;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    servedCustomer.setTimeOfGettingTicket(getTimestamp());

    electroShopSimulation.setTicketMachineServingCustomer(false);

    // setting customer type
    double generatedRandomValue = electroShopSimulation.getCustomerTypeGenerator().sample();

    CustomerType selectedCustomerType = generateCustomerType(generatedRandomValue);

    servedCustomer.setCustomerType(selectedCustomerType);

    // putting customer in the right queue
    if (servedCustomer.getCustomerType() == CustomerType.ONLINE) {
      electroShopSimulation.addCustomerToOnlineCustomerQueue(servedCustomer);
    } else {
      electroShopSimulation.addCustomerToCasualAndContractCustomerQueue(servedCustomer);
    }

    if (electroShopSimulation.isServiceQueueFull()) {
      electroShopSimulation.setTicketMachineStopped(true);
    }

    switch (selectedCustomerType) {
      case CASUAL, CONTRACT -> {
        if (electroShopSimulation.isAtLeastOneServiceFree(false)
            && !electroShopSimulation.isCasualContractCustomerQueueEmpty()) {
          electroShopSimulation.addEvent(
              new RemoveCustomerFromCasualAndContractQueueEvent(getTimestamp()));
        }
      }
      case ONLINE -> {
        if (electroShopSimulation.isAtLeastOneServiceFree(true)
            && !electroShopSimulation.isOnlineCustomerQueueEmpty()) {
          electroShopSimulation.addEvent(new RemoveCustomerFromOnlineQueueEvent(getTimestamp()));
        }
      }
      default ->
          throw new IllegalStateException(
              String.format(
                  "Unknown customer type %s was selected at %s!",
                  selectedCustomerType, getClass().getName()));
    }

    if ((!electroShopSimulation.isTicketMachineStopped())
        && (!electroShopSimulation.isTicketMachineServingCustomer())
        && (!electroShopSimulation.isTicketQueueEmpty())) {
      electroShopSimulation.addEvent(new RemoveCustomerFromTicketQueue(getTimestamp()));
    }
  }

  @Override
  public String getEventDescription() {
    return String.format("Customer stopped getting ticket at %s", TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
