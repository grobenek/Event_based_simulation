package szathmary.peter.event.cashregister;

import szathmary.peter.event.Event;
import szathmary.peter.event.service.casualandcontract.RemoveCustomerFromCasualAndContractQueueEvent;
import szathmary.peter.event.service.online.RemoveCustomerFromOnlineQueueEvent;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;
import szathmary.peter.simulation.entity.employee.EmployeeStatus;
import szathmary.peter.simulation.entity.order.OrderSize;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 31/03/2024. */
public class EndOfCashRegisterServiceEvent extends Event {
  private final Customer servedCustomer;
  private final CashRegister currentCashRegister;

  public EndOfCashRegisterServiceEvent(
      Double timestamp, Customer servedCustomer, CashRegister currentCashRegister) {
    super(timestamp);

    this.servedCustomer = servedCustomer;
    this.currentCashRegister = currentCashRegister;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    double timeForGettingBigOrder = 0.0;
    if (servedCustomer.getOrderSize() == OrderSize.BIG) {
      servedCustomer.getServiceStationThatServedCustomer().setServing(false);
      servedCustomer.getServiceStationThatServedCustomer().setCurrentServedCustomer(null);

      timeForGettingBigOrder +=
          electroShopSimulation.getTimeForTakeBigOrderRandomGenerator().sample();

      // service of next customer is being planned
      if (servedCustomer.getCustomerType() != CustomerType.ONLINE) {
        if (!electroShopSimulation.isCasualContractCustomerQueueEmpty()) {
          electroShopSimulation.addEvent(
              new RemoveCustomerFromCasualAndContractQueueEvent(getTimestamp()));
        }
      } else {
        if (!electroShopSimulation.isOnlineCustomerQueueEmpty()) {
          electroShopSimulation.addEvent(new RemoveCustomerFromOnlineQueueEvent(getTimestamp()));
        }
      }
    }

    if (!currentCashRegister.isQueueEmpty()) {
      simulationCore.addEvent(
          new RemoveCustomerFromCashRegisterQueue(getTimestamp(), currentCashRegister));
    }

    double timeOfLeavingSystem = getTimestamp() + timeForGettingBigOrder;
    servedCustomer.setTimeOfLeavingSystem(timeOfLeavingSystem);
    electroShopSimulation.setLastCustomerLeavingTime(timeOfLeavingSystem);

    updateStatistics(electroShopSimulation);

    currentCashRegister.getEmployee().setStatus(EmployeeStatus.IDLE);
    currentCashRegister.setServing(false);
    currentCashRegister.setCurrentServedCustomer(null);
  }

  private void updateStatistics(ElectroShopSimulation electroShopSimulation) {
    electroShopSimulation
        .getTimeInSystemStatisticReplications()
        .addObservation(
            servedCustomer.getTimeOfLeavingSystem() - servedCustomer.getTimeOfArrival());

    if (servedCustomer.getTimeOfLeavingTicketQueue() < ElectroShopSimulation.CLOSING_HOURS_OF_TICKET_MACHINE) {
      electroShopSimulation
          .getTimeInTicketQueueStatisticReplications()
          .addObservation(
              servedCustomer.getTimeOfLeavingTicketQueue()
                  - servedCustomer.getTimeOfEnteringTicketQueue());
    }
  }

  @Override
  public String getEventDescription() {
    return String.format(
        "%s customer has payed and is leaving system at %s!",
        servedCustomer.getCustomerType(), TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
