package szathmary.peter.event.service.online;

import szathmary.peter.event.Event;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;
import szathmary.peter.simulation.entity.employee.EmployeeStatus;
import szathmary.peter.simulation.entity.order.OrderSize;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 30/03/2024. */
public class StartOnlineCustomerService extends Event {
  private final Customer servedCustomer;
  private final ServiceStation serviceStation;

  public StartOnlineCustomerService(Double timestamp, Customer customerToServe, ServiceStation serviceStation) {
    super(timestamp);

    if (serviceStation.isServing()) {
      throw new IllegalStateException(String.format("Cannot start service in online service station, because it is already serving at %f!", getTimestamp()));
    }

    this.serviceStation = serviceStation;

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

//    ServiceStation freeServiceStation = electroShopSimulation.getFreeServiceStation(true);

    serviceStation.setServing(true, getTimestamp());
    serviceStation.setCurrentServedCustomer(servedCustomer);
    serviceStation.getEmployee().setStatus(EmployeeStatus.SERVING);

    OrderSize orderSize =
        generateOrderSize(electroShopSimulation.getOrderSizeRandomGenerator().sample());

    servedCustomer.setOrderSize(orderSize);
    servedCustomer.setServiceStationThatServedCustomer(serviceStation);
    servedCustomer.setTimeOfStartOfService(getTimestamp());

    double endOfOnlineServiceTime =
        getTimestamp()
            + electroShopSimulation
                .getTimeForFinishOrderForOnlineCustomerRandomGenerator()
                .sample();

    electroShopSimulation.addEvent(
        new EndOfOnlineServiceEvent(endOfOnlineServiceTime, servedCustomer, serviceStation));
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
