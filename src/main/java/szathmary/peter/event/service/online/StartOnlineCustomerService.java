package szathmary.peter.event.service.online;

import szathmary.peter.event.Event;
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

    ServiceStation freeServiceStation = electroShopSimulation.getFreeServiceStation(true);

    freeServiceStation.setServing(true);
    freeServiceStation.setCurrentServedCustomer(servedCustomer);
    freeServiceStation.getEmployee().setStatus(EmployeeStatus.SERVING);

    OrderSize orderSize =
        generateOrderSize(electroShopSimulation.getOrderSizeRandomGenerator().sample());

    servedCustomer.setOrderSize(orderSize);
    servedCustomer.setServiceStationThatServedCustomer(freeServiceStation);
    servedCustomer.setTimeOfStartOfService(getTimestamp());

    electroShopSimulation.addEvent(
        new EndOfOnlineServiceEvent(getTimestamp(), servedCustomer, freeServiceStation));
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
    return String.format(
        "Starting online service of customer at %s!",
        TimeFormatter.getFormattedTime(getTimestamp()));
  }
}
