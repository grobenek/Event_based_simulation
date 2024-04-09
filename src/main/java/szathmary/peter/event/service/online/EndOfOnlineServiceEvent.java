package szathmary.peter.event.service.online;

import szathmary.peter.event.Event;
import szathmary.peter.event.cashregister.StartCashRegisterServiceEvent;
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

    currentServiceStation.getEmployee().setStatus(EmployeeStatus.IDLE);

    if ((!cashRegisterToPutCustomerIn.isServing())
        && (!cashRegisterToPutCustomerIn.isQueueEmpty())) {
      electroShopSimulation.addEvent(
          new StartCashRegisterServiceEvent(getTimestamp(), cashRegisterToPutCustomerIn));
    }

    if (servedCustomer.getOrderSize() == OrderSize.SMALL) {
      currentServiceStation.setServing(false, getTimestamp());
      currentServiceStation.setCurrentServedCustomer(null);

      // service of next customer is being planned
      if (!electroShopSimulation.isOnlineCustomerQueueEmpty()) {
        electroShopSimulation.addEvent(new StartOnlineCustomerService(getTimestamp()));
      }
    }
  }

  @Override
  public String getEventDescription() {
    return String.format(
        "Ending online customer service at %s!", TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
