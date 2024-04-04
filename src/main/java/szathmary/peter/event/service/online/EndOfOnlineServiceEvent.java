package szathmary.peter.event.service.online;

import szathmary.peter.event.Event;
import szathmary.peter.event.cashregister.RemoveCustomerFromCashRegisterQueue;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.employee.EmployeeStatus;
import szathmary.peter.simulation.entity.order.OrderSize;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 31/03/2024. */
public class EndOfOnlineServiceEvent extends Event {

  private final Customer servedCustomer;
  private final ServiceStation currentServiceStation;

  public EndOfOnlineServiceEvent(
      Double timestamp, Customer servedCustomer, ServiceStation freeServiceStation) {
    super(timestamp);

    this.servedCustomer = servedCustomer;
    this.currentServiceStation = freeServiceStation;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    CashRegister cashRegisterToPutCustomerIn = electroShopSimulation.getEligebleCashRegister();
    servedCustomer.setTimeOfEnteringCheckoutQueue(getTimestamp());
    cashRegisterToPutCustomerIn.addCustomerToQueue(servedCustomer);

    if (servedCustomer.getOrderSize() == OrderSize.SMALL) {
      currentServiceStation.setServing(false);
      currentServiceStation.setCurrentServedCustomer(null);

      // service of next customer is being planned
      if (!electroShopSimulation.isOnlineCustomerQueueEmpty()) {
        electroShopSimulation.addEvent(new RemoveCustomerFromOnlineQueueEvent(getTimestamp()));
      }
    }

    currentServiceStation.getEmployee().setStatus(EmployeeStatus.IDLE);

    electroShopSimulation.addEvent(
        new RemoveCustomerFromCashRegisterQueue(getTimestamp(), cashRegisterToPutCustomerIn));
  }

  @Override
  public String getEventDescription() {
    return String.format("Ending online customer service at %s!", TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
