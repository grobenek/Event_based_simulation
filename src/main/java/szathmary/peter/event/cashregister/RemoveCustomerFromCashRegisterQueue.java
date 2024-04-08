package szathmary.peter.event.cashregister;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 31/03/2024. */
public class RemoveCustomerFromCashRegisterQueue extends Event {
  private final CashRegister cashRegisterOwningQueue;

  public RemoveCustomerFromCashRegisterQueue(
      Double timestamp, CashRegister cashRegisterToPutCustomerIn) {
    super(timestamp);

    this.cashRegisterOwningQueue = cashRegisterToPutCustomerIn;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    if (cashRegisterOwningQueue.isServing()) {
      throw new IllegalStateException("Cannot remove customer from cash register queue, becasue owning cash register is serving!");
    }

    Customer removedCustomerFromQueue = cashRegisterOwningQueue.removeCustomerFromQueue();

    cashRegisterOwningQueue.setServing(true, getTimestamp());
    cashRegisterOwningQueue.setCurrentServedCustomer(removedCustomerFromQueue);

    electroShopSimulation.addEvent(
        new StartCashRegisterServiceEvent(
            getTimestamp(), removedCustomerFromQueue, cashRegisterOwningQueue));
  }

  @Override
  public String getEventDescription() {
    return String.format(
        "Removing customer from cash register queue at %s!",
        TimeFormatter.getFormattedTime(getTimestamp()));
  }
}