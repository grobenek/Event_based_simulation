package szathmary.peter.event.service.online;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;
import szathmary.peter.simulation.entity.order.OrderSize;
import szathmary.peter.simulation.entity.order.OrderType;

/** Created by petos on 30/03/2024. */
public class StartOnlineCustomerService extends Event {
  private final Customer servedCustomer;

  public StartOnlineCustomerService(Double timestamp, Customer customerToServe) {
    super(timestamp);

    CustomerType customerType = customerToServe.getCustomerType();
    if (customerType != CustomerType.ONLINE) {
      throw new IllegalArgumentException(
          String.format(
              "Cannot serve %s customer at online service station at %f!",
              customerType, getTimestamp()));
    }

    this.servedCustomer = customerToServe;
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    ServiceStation freeServiceStation = electroShopSimulation.getFreeOnlineServiceStation();

    freeServiceStation.setServing(true);
    freeServiceStation.setCurrentServedCustomer(servedCustomer);

    // getting type of order

    double generatedValueOfOrderType =
        electroShopSimulation.getTypeOfOrderRandomGenerator().sample();
    OrderType orderType = generateOrderType(generatedValueOfOrderType);

    double timeOfEndOfService;

    switch (orderType) {
      case EASY ->
          timeOfEndOfService = electroShopSimulation.getEasyOrderTimeRandomGenerator().sample();

      case MEDIUM ->
          timeOfEndOfService = electroShopSimulation.getMediumOrderTimeRandomGenerator().sample();

      case HARD ->
          timeOfEndOfService = electroShopSimulation.getHardOrderTimeRandomGenerator().sample();

      default ->
          throw new IllegalStateException(
              String.format("Unknown %s order type selected!", orderType));
    }

    OrderSize orderSize =
        generateOrderSize(electroShopSimulation.getOrderSizeRandomGenerator().sample());

    servedCustomer.setOrderSize(orderSize);
    servedCustomer.setServiceStationThatServedCustomer(freeServiceStation);

    electroShopSimulation.addEvent(
        new EndOfOnlineServiceEvent(
            getTimestamp() + timeOfEndOfService, servedCustomer, freeServiceStation));
  }

  private OrderType generateOrderType(double sample) {
    double cummulatedProbability = 0.0;
    OrderType selectedOrderType = null;
    for (int i = 0; i < OrderType.values().length; i++) {
      OrderType orderType = OrderType.values()[i];

      cummulatedProbability += orderType.getProbability();

      if (sample <= cummulatedProbability) {
        selectedOrderType = orderType;
        break;
      }
    }

    if (selectedOrderType == null) {
      throw new IllegalStateException("No order type was selected!");
    }
    return selectedOrderType;
  }

  private OrderSize generateOrderSize(double sample) {
    double cummulatedProbability = 0.0;
    OrderSize selectedOrderSize = null;
    for (int i = 0; i < OrderSize.values().length; i++) {
      OrderSize orderSize = OrderSize.values()[i];

      cummulatedProbability += orderSize.getProbability();

      if (sample <= cummulatedProbability) {
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
  public String getEventDescription() {
    return String.format("Starting online service of customer at %f!", getTimestamp());
  }
}
