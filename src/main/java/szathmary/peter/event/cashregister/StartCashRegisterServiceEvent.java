package szathmary.peter.event.cashregister;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.employee.EmployeeStatus;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 31/03/2024. */
public class StartCashRegisterServiceEvent extends Event {
  private final CashRegister currentCashRegister;
  private Customer servedCustomer;

  public StartCashRegisterServiceEvent(Double timestamp, CashRegister currentCashRegister) {
    super(timestamp);

    this.currentCashRegister = currentCashRegister;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    if (currentCashRegister.isServing()) {
      throw new IllegalStateException(
          "Cannot remove customer from cash register queue, becasue owning cash register is serving!");
    }

    servedCustomer = currentCashRegister.removeCustomerFromQueue();

    servedCustomer.setTimeOfStartCheckoutService(getTimestamp());

    currentCashRegister.getEmployee().setStatus(EmployeeStatus.SERVING);
    currentCashRegister.setServing(true, getTimestamp());
    currentCashRegister.setCurrentServedCustomer(servedCustomer);

    double timeOfEndOfService =
        getTimestamp() + electroShopSimulation.getPaymentTimeRandomGenerator().sample();

    electroShopSimulation.addEvent(
        new EndOfCashRegisterServiceEvent(timeOfEndOfService, servedCustomer, currentCashRegister));
  }

  @Override
  public String getEventDescription() {
    return String.format(
        "%s customer is start paying at cash register at %s",
        servedCustomer.getCustomerType(), TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
