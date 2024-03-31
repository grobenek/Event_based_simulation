package szathmary.peter.event.cashregister;

import szathmary.peter.event.Event;
import szathmary.peter.event.service.casualandcontract.RemoveCustomerFromCasualAndContractQueueEvent;
import szathmary.peter.event.service.online.RemoveCustomerFromOnlineQueueEvent;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;
import szathmary.peter.simulation.entity.order.OrderSize;

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

    // TODO nastavit boolean a nastavit cas zakaznikovi

    double timeForGettingBigOrder = 0.0;
    if (servedCustomer.getOrderSize() == OrderSize.BIG) {
      servedCustomer.getServiceStationThatServedCustomer().setServing(false);
      servedCustomer.getServiceStationThatServedCustomer().setCurrentServedCustomer(null);

      timeForGettingBigOrder += electroShopSimulation.getTimeForTakeBigOrderRandomGenerator().sample();

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

    // TODO naplanovat dalsiehho ak tak

    if (!currentCashRegister.isQueueEmpty()) {
      simulationCore.addEvent(
          new RemoveCustomerFromCashRegisterQueue(getTimestamp(), currentCashRegister));
    }

    servedCustomer.setTimeOfLeavingSystem(getTimestamp() + timeForGettingBigOrder);

    electroShopSimulation.getTimeInSystemStatisticReplications().addObservation(servedCustomer.getTimeOfLeavingSystem() - servedCustomer.getTimeOfArrival());
    electroShopSimulation.getTimeInTicketQueueReplications().addObservation(servedCustomer.getTimeOfLeavingTicketQueue() - servedCustomer.getTimeOfEnteringTicketQueue());
  }

  @Override
  public String getEventDescription() {
    return String.format(
        "%s customer has payed and is leaving system at %f!",
        servedCustomer.getCustomerType(), getTimestamp());
  }
}
