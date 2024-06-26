package szathmary.peter.event.service.casualandcontract;

import szathmary.peter.event.Event;
import szathmary.peter.event.ticketmachine.StartGettingTicketEvent;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;
import szathmary.peter.simulation.entity.employee.EmployeeStatus;
import szathmary.peter.simulation.entity.order.OrderSize;
import szathmary.peter.simulation.entity.order.OrderType;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 30/03/2024. */
public class StartOfCasualAndContractCustomerServiceEvent extends Event {
  private final Customer servedCustomer;
  private final ServiceStation serviceStation;

  public StartOfCasualAndContractCustomerServiceEvent(
      Double timestamp, ServiceStation serviceStation, Customer customer) {
    super(timestamp);
    this.serviceStation = serviceStation;
    this.servedCustomer = customer;
  }

  private static OrderType generateOrderType(double generatedRandomValue) {
    double cummulatedProbability = 0.0;
    OrderType selectedOrderType = null;
    for (int i = 0; i < OrderType.values().length; i++) {
      OrderType orderType = OrderType.values()[i];

      cummulatedProbability += orderType.getProbability();

      if (generatedRandomValue < cummulatedProbability) {
        selectedOrderType = orderType;
        break;
      }
    }

    if (selectedOrderType == null) {
      throw new IllegalStateException("No order type was selected!");
    }
    return selectedOrderType;
  }

  private static OrderSize generateOrderSize(double generatedRandomValue) {
    double cummulatedProbability = 0.0;
    OrderSize selectedOrderSize = null;
    for (int i = 0; i < OrderSize.values().length; i++) {
      OrderSize orderSize = OrderSize.values()[i];

      cummulatedProbability += orderSize.getProbability();

      if (generatedRandomValue < cummulatedProbability) {
        selectedOrderSize = orderSize;
        break;
      }
    }

    if (selectedOrderSize == null) {
      throw new IllegalStateException("No order size was selected!");
    }
    return selectedOrderSize;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    CustomerType customerType = servedCustomer.getCustomerType();
    if (customerType != CustomerType.CASUAL && customerType != CustomerType.CONTRACT) {
      throw new IllegalStateException(
          String.format(
              "Cannot serve %s customer at casual and contract service station!", customerType));
    }

    electroShopSimulation.setTicketMachineStopped(electroShopSimulation.isServiceQueueFull());

    if ((!electroShopSimulation.isTicketMachineStopped())
        && (!electroShopSimulation.isTicketQueueEmpty())
        && (!electroShopSimulation.isTicketMachineServingCustomer())) {
      electroShopSimulation.addEvent(new StartGettingTicketEvent(getTimestamp()));
    }

    if (serviceStation.isServing()) {
      throw new IllegalStateException(
          String.format(
              "Cannot start service in casual and contract service station, because it is already serving at %f!",
              getTimestamp()));
    }

    serviceStation.setServing(true, getTimestamp());
    serviceStation.setCurrentServedCustomer(servedCustomer);
    serviceStation.getEmployee().setStatus(EmployeeStatus.SERVING);

    servedCustomer.setTimeOfStartOfService(getTimestamp());

    // getting type of order
    double generatedValueOfOrderType =
        electroShopSimulation.getTypeOfOrderRandomGenerator().sample();
    OrderType orderType = generateOrderType(generatedValueOfOrderType);

    double lengthOfService;

    switch (orderType) {
      case EASY ->
          lengthOfService = electroShopSimulation.getEasyOrderTimeRandomGenerator().sample();

      case MEDIUM ->
          lengthOfService = electroShopSimulation.getMediumOrderTimeRandomGenerator().sample();

      case HARD ->
          lengthOfService = electroShopSimulation.getHardOrderTimeRandomGenerator().sample();

      default ->
          throw new IllegalStateException(
              String.format(
                  "Unknown %s order type selected at %s!", orderType, getClass().getName()));
    }

    lengthOfService +=
        electroShopSimulation
            .getTimeToFinishOrderForCasualAndContractCustomersRandomGenerator()
            .sample();

    OrderSize orderSize =
        generateOrderSize(electroShopSimulation.getOrderSizeRandomGenerator().sample());

    servedCustomer.setOrderSize(orderSize);
    servedCustomer.setServiceStationThatServedCustomer(serviceStation);

    electroShopSimulation.addEvent(
        new EndOfCasualAndContractServiceEvent(
            getTimestamp() + lengthOfService, servedCustomer, serviceStation));
  }

  @Override
  public String getEventDescription() {
    return String.format(
        "Serving %s customer at casual and contract service station at %s",
        servedCustomer.getCustomerType(), TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
