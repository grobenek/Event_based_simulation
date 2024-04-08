package szathmary.peter.event.service;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.order.OrderSize;

/** Created by petos on 06/04/2024. */
public class StartGettingBigOrderEvent extends Event {
  private final Customer customer;

  public StartGettingBigOrderEvent(Double timestamp, Customer customer) {
    super(timestamp);

    if (customer.getOrderSize() != OrderSize.BIG) {
      throw new IllegalStateException(
          String.format(
              "Cannot start getting big order event for customer with %s order size!",
              customer.getOrderSize()));
    }

    this.customer = customer;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    double timeForGettingBigOrder =
        electroShopSimulation.getTimeForTakeBigOrderRandomGenerator().sample();

    electroShopSimulation.addEvent(
        new EndOfGettingBigOrder(getTimestamp() + timeForGettingBigOrder, customer));
  }

  @Override
  public String getEventDescription() {
    return String.format("Customer has started getting big order at %f", getTimestamp());
  }
}
