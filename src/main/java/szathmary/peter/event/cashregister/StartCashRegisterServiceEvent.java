package szathmary.peter.event.cashregister;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;

/** Created by petos on 31/03/2024. */
public class StartCashRegisterServiceEvent extends Event {
  private final Customer servedCustomer;
  private final CashRegister currentCashRegister;

  public StartCashRegisterServiceEvent(
      Double timestamp, Customer removedCustomerFromQueue, CashRegister currentCashRegister) {
    super(timestamp);

    this.servedCustomer = removedCustomerFromQueue;
    this.currentCashRegister = currentCashRegister;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    double timeOfEndOfService =
        getTimestamp() + electroShopSimulation.getPaymentTimeRandomGenerator().sample();

    electroShopSimulation.addEvent(
        new EndOfCashRegisterServiceEvent(timeOfEndOfService, servedCustomer, currentCashRegister));
  }

  @Override
  public String getEventDescription() {
    return String.format("%s customer is start paying at cash register at %f", servedCustomer.getCustomerType(), getTimestamp());
  }
}
