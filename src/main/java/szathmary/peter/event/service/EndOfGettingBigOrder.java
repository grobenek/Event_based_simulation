package szathmary.peter.event.service;

import szathmary.peter.event.Event;
import szathmary.peter.event.service.casualandcontract.StartOfCasualAndContractCustomerServiceEvent;
import szathmary.peter.event.service.online.StartOnlineCustomerService;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;
import szathmary.peter.simulation.entity.order.OrderSize;

/** Created by petos on 06/04/2024. */
public class EndOfGettingBigOrder extends Event {
  private final Customer customer;

  public EndOfGettingBigOrder(double timestamp, Customer customer) {
    super(timestamp);

    if (customer.getOrderSize() != OrderSize.BIG) {
      throw new IllegalStateException(
          String.format(
              "Cannot end getting big order event for customer with %s order size!",
              customer.getOrderSize()));
    }

    this.customer = customer;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    customer.getServiceStationThatServedCustomer().setServing(false, getTimestamp()); //TODO mozno sa nezaratava do statistiky ako ze je serving?
    customer.getServiceStationThatServedCustomer().setCurrentServedCustomer(null);

    customer.setTimeOfLeavingSystem(getTimestamp());
    electroShopSimulation.setLastCustomerLeavingTime(getTimestamp());
    electroShopSimulation.increaseCountOfServedCustomers();
    updateStatistics(electroShopSimulation);

    // service of next customer is being planned
    if (customer.getCustomerType() != CustomerType.ONLINE) {
      if (!electroShopSimulation.isCasualContractCustomerQueueEmpty()) {
        electroShopSimulation.addEvent(
            new StartOfCasualAndContractCustomerServiceEvent(getTimestamp()));
      }
    } else {
      if (!electroShopSimulation.isOnlineCustomerQueueEmpty()) {
        electroShopSimulation.addEvent(new StartOnlineCustomerService(getTimestamp()));
      }
    }
  }

  private void updateStatistics(ElectroShopSimulation electroShopSimulation) {
    electroShopSimulation
        .getTimeInSystemStatisticReplications()
        .addObservation(customer.getTimeOfLeavingSystem() - customer.getTimeOfArrival());

    if (customer.getTimeOfLeavingTicketQueue()
        < ElectroShopSimulation.CLOSING_HOURS_OF_TICKET_MACHINE) {
      electroShopSimulation
          .getTimeInTicketQueueStatisticReplications()
          .addObservation(
              customer.getTimeOfLeavingTicketQueue() - customer.getTimeOfEnteringTicketQueue());
    }
  }

  @Override
  public String getEventDescription() {
    return String.format("End of getting big order at %f", getTimestamp());
  }
}
