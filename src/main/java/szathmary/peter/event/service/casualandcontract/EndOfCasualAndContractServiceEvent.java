package szathmary.peter.event.service.casualandcontract;

import szathmary.peter.event.Event;
import szathmary.peter.event.cashregister.RemoveCustomerFromCashRegisterQueue;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;
import szathmary.peter.simulation.entity.order.OrderSize;

/** Created by petos on 31/03/2024. */
public class EndOfCasualAndContractServiceEvent extends Event {
  private final Customer servedCustomer;
  private final ServiceStation currentServiceStation;

  public EndOfCasualAndContractServiceEvent(
      Double timestamp, Customer customerToBeServed, ServiceStation serviceStation) {
    super(timestamp);

    CustomerType customerType = customerToBeServed.getCustomerType();
    if (customerType != CustomerType.CASUAL && customerType != CustomerType.CONTRACT) {
      throw new IllegalStateException(
          String.format(
              "Cannot end service of %s customer at casual and contract service!", customerType));
    }

    this.servedCustomer = customerToBeServed;
    this.currentServiceStation = serviceStation;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    // TODO upravit service booleans + spravit pokladne
    CashRegister cashRegisterToPutCustomerIn = electroShopSimulation.getEligebleCashRegister();
    servedCustomer.setTimeOfEnteringCheckoutQueue(getTimestamp());
    cashRegisterToPutCustomerIn.addCustomerToQueue(servedCustomer);

    if (servedCustomer.getOrderSize() == OrderSize.SMALL) {
      currentServiceStation.setServing(false);
      currentServiceStation.setCurrentServedCustomer(null);

      // service of next customer is being planned
    if (!electroShopSimulation.isCasualContractCustomerQueueEmpty()) {
      electroShopSimulation.addEvent(
          new RemoveCustomerFromCasualAndContractQueueEvent(getTimestamp()));
    }
    }

    electroShopSimulation.addEvent(
        new RemoveCustomerFromCashRegisterQueue(getTimestamp(), cashRegisterToPutCustomerIn));

  }

  @Override
  public String getEventDescription() {
    return String.format(
        "%s customer has been stopped served at casual and contract service station at %f",
        servedCustomer.getCustomerType(), getTimestamp());
  }
}
