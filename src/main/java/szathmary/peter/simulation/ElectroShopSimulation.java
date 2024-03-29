package szathmary.peter.simulation;

import java.util.*;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousEmpiricRandomGenerator;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousExponentialRandomGenerator;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousTriangularRandomGenerator;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousUniformGenerator;
import szathmary.peter.randomgenerators.discretegenerators.DiscreteEmpiricRandomGenerator;
import szathmary.peter.randomgenerators.discretegenerators.DiscreteUniformRandomGenerator;
import szathmary.peter.randomgenerators.empiricnumbergenerator.EmpiricOption;
import szathmary.peter.simulation.entity.CashRegister;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;

/** Created by petos on 23/03/2024.<br>
 * Simulation time is in <b>SECONDS</b>
 * */
public class ElectroShopSimulation extends SimulationCore {
  public static final double CLOSING_HOURS_OF_TICKET_MACHINE = 17.0 * 60 * 60;
  public static final int SERVICE_STATION_QUEUE_CAPACITY = 8;
  public static final double
      RATIO_OF_CASUAL_AND_CONTRACT_SERVICE_STATION_VS_ONLINE_SERVICE_STATIONS = 2.0 / 3.0;
  private final ContinuousExponentialRandomGenerator timeBetweenCustomerArrivalsRandomGenerator =
      new ContinuousExponentialRandomGenerator(30.0 / 60.0 / 60.0);
  private final DiscreteUniformRandomGenerator customerTypeGenerator =
      new DiscreteUniformRandomGenerator(0, 101);
  private final ContinuousUniformGenerator ticketPrintingTimeRandomGenerator =
      new ContinuousUniformGenerator(30, 180);
  private final ContinuousUniformGenerator
      timeToFinishOrderForCasualAndContractCustomersRandomGenerator =
          new ContinuousUniformGenerator(60, 900);
  private final ContinuousTriangularRandomGenerator
      timeForFinishOrderForOnlineCustomerRandomGenerator =
          new ContinuousTriangularRandomGenerator(1, 2, 8);
  private final DiscreteUniformRandomGenerator orderSizeRandomGenerator =
      new DiscreteUniformRandomGenerator(0, 101);
  private final ContinuousUniformGenerator timeForTakeBigOrderRandomGenerator =
      new ContinuousUniformGenerator(30, 70);
  private final DiscreteUniformRandomGenerator paymentTypeRandomGenerator =
      new DiscreteUniformRandomGenerator(0, 101);
  private final DiscreteEmpiricRandomGenerator paymentTimeRandomGenerator =
      new DiscreteEmpiricRandomGenerator(
          List.of(new EmpiricOption<>(180, 481, 0.4), new EmpiricOption<>(180, 361, 0.6)));
  private final DiscreteUniformRandomGenerator typeOfOrderRandomGenerator =
      new DiscreteUniformRandomGenerator(0, 101);
  private final ContinuousEmpiricRandomGenerator easyOrderTimeRandomGenerator =
      new ContinuousEmpiricRandomGenerator(
          List.of(
              new EmpiricOption<>(2.0 * 60, 5.0 * 60, 0.6),
              new EmpiricOption<>(5.0 * 60, 9.0 * 60, 0.3)));
  private final ContinuousUniformGenerator mediumOrderTimeRandomGenerator =
      new ContinuousUniformGenerator(9 * 60, 11 * 60);
  private final ContinuousEmpiricRandomGenerator hardOrderTimeRandomGenerator =
      new ContinuousEmpiricRandomGenerator(
          List.of(
              new EmpiricOption<>(11.0 * 60, 12.0 * 60, 0.1),
              new EmpiricOption<>(12.0 * 60, 20.0 * 60, 0.6),
              new EmpiricOption<>(20.0 * 60, 25.0 * 60, 0.3)));

  private final Queue<Customer> ticketMachineQueue;
  private final List<ServiceStation> serviceStations;
  private final List<CashRegister> cashRegisters;
  private final PriorityQueue<Customer> casualAndContractCustomerQueue;
  private final Queue<Customer> onlineCustomersQueue;
  private boolean isTicketMachineServingCustomer;
  private boolean isTicketMachineStopped;

  public ElectroShopSimulation(
      long numberOfReplications,
      int numberOfServiceStations,
      int numberOfCashRegisters,
      boolean verboseSimulation) {
    super(numberOfReplications, verboseSimulation);

    this.isTicketMachineServingCustomer = false;
    this.isTicketMachineStopped = false;

    this.ticketMachineQueue = new LinkedList<>();
    this.casualAndContractCustomerQueue = new PriorityQueue<>();
    this.onlineCustomersQueue = new LinkedList<>();

    this.serviceStations = new ArrayList<>(numberOfServiceStations);
    this.cashRegisters = new ArrayList<>(numberOfCashRegisters);

    // adding service stations
    initializeServingStations(numberOfServiceStations);

    // adding cash registers
    for (int i = 0; i < numberOfCashRegisters; i++) {
      cashRegisters.add(new CashRegister());
    }
  }

  private void initializeServingStations(int numberOfServiceStations) {
    int numberOfCasualAndContractServiceStations =
        (int)
            Math.ceil(
                RATIO_OF_CASUAL_AND_CONTRACT_SERVICE_STATION_VS_ONLINE_SERVICE_STATIONS
                    * numberOfServiceStations);

    for (int i = 0; i < numberOfServiceStations; i++) {
      serviceStations.add(new ServiceStation(i < numberOfCasualAndContractServiceStations));
    }
  }

  @Override
  public void afterReplications() {}

  @Override
  public void afterReplication() {}

  @Override
  public void replication() {}

  @Override
  public void beforeReplication() {}

  @Override
  public void beforeReplications() {}

  public boolean isTicketMachineStopped() {
    return isTicketMachineStopped;
  }

  public ElectroShopSimulation setTicketMachineStopped(boolean ticketMachineStopped) {
    isTicketMachineStopped = ticketMachineStopped;
    return this;
  }

  public boolean isTicketMachineServingCustomer() {
    return isTicketMachineServingCustomer;
  }

  public ElectroShopSimulation setTicketMachineServingCustomer(boolean ticketMachineServingCustomer) {
    isTicketMachineServingCustomer = ticketMachineServingCustomer;
    return this;
  }

  public void addCustomerToTicketQueue(Customer customer) {
    ticketMachineQueue.add(customer);
  }

  public Customer removeCustomerFromTicketQueue() {
    if (ticketMachineQueue.isEmpty()) {
      throw new IllegalStateException("Cannot remove customer from ticket machine queue, because queue is empty!");
    }

    return ticketMachineQueue.poll();
  }

  public boolean isTicketQueueEmpty() {
    return ticketMachineQueue.isEmpty();
  }

  public void addCustomerToCasualAndContractCustomerQueue(Customer customer) {
    if (customer.getCustomerType() != CustomerType.CASUAL
        && customer.getCustomerType() != CustomerType.CONTRACT) {
      throw new IllegalArgumentException(
          String.format(
              "Cannot add customer with type %s to queue for casual and contract customers!",
              customer.getCustomerType()));
    }

    if (casualAndContractCustomerQueue.size() == SERVICE_STATION_QUEUE_CAPACITY) {
      throw new IllegalStateException(
          String.format(
              "Cannot add another customer to queue for casual and contract customers, current capacity is %d",
              casualAndContractCustomerQueue.size()));
    }

    casualAndContractCustomerQueue.add(customer);
  }

  public Customer removeCustomerFromCasualAndContractCustomerQueue() {
    if (casualAndContractCustomerQueue.isEmpty()) {
      throw new IllegalStateException(
          "Cannot remove customer from queue for casual and contract customers, because it is empty!");
    }

    return casualAndContractCustomerQueue.poll();
  }

  public void addCustomerToOnlineCustomerQueue(Customer customer) {
    if (customer.getCustomerType() != CustomerType.ONLINE) {
      throw new IllegalArgumentException(
          String.format(
              "Cannot add customer with type %s to queue for online customers!",
              customer.getCustomerType()));
    }

    if (onlineCustomersQueue.size() == SERVICE_STATION_QUEUE_CAPACITY) {
      throw new IllegalStateException(
          String.format(
              "Cannot add another customer to queue for online customers, current capacity is %d",
              onlineCustomersQueue.size()));
    }

    onlineCustomersQueue.add(customer);
  }

  public Customer removeCustomerFromOnlineCustomerQueue() {
    if (onlineCustomersQueue.isEmpty()) {
      throw new IllegalStateException(
          "Cannot remove customer from queue for online customers, because it is empty!");
    }

    return onlineCustomersQueue.poll();
  }

  public ServiceStation getServiceStation(int index) {
    if (index < 0 || index > serviceStations.size()) {
      throw new IllegalArgumentException(
          String.format(
              "Cannot get service station at index %d, there are only %d service stations!",
              index, serviceStations.size()));
    }

    return serviceStations.get(index);
  }

  public CashRegister getCashRegister(int index) {
    if (index < 0 || index > cashRegisters.size()) {
      throw new IllegalArgumentException(
          String.format(
              "Cannot get cash register at index %d, there are only %d cash registers!",
              index, cashRegisters.size()));
    }

    return cashRegisters.get(index);
  }

  public List<ServiceStation> getServiceStations() {
    return serviceStations;
  }

  public List<CashRegister> getCashRegisters() {
    return cashRegisters;
  }

  public ContinuousExponentialRandomGenerator getTimeBetweenCustomerArrivalsRandomGenerator() {
    return timeBetweenCustomerArrivalsRandomGenerator;
  }

  public DiscreteUniformRandomGenerator getCustomerTypeGenerator() {
    return customerTypeGenerator;
  }

  public ContinuousUniformGenerator getTicketPrintingTimeRandomGenerator() {
    return ticketPrintingTimeRandomGenerator;
  }

  public ContinuousUniformGenerator
      getTimeToFinishOrderForCasualAndContractCustomersRandomGenerator() {
    return timeToFinishOrderForCasualAndContractCustomersRandomGenerator;
  }

  public ContinuousTriangularRandomGenerator
      getTimeForFinishOrderForOnlineCustomerRandomGenerator() {
    return timeForFinishOrderForOnlineCustomerRandomGenerator;
  }

  public DiscreteUniformRandomGenerator getOrderSizeRandomGenerator() {
    return orderSizeRandomGenerator;
  }

  public ContinuousUniformGenerator getTimeForTakeBigOrderRandomGenerator() {
    return timeForTakeBigOrderRandomGenerator;
  }

  public DiscreteUniformRandomGenerator getPaymentTypeRandomGenerator() {
    return paymentTypeRandomGenerator;
  }

  public DiscreteEmpiricRandomGenerator getPaymentTimeRandomGenerator() {
    return paymentTimeRandomGenerator;
  }

  public DiscreteUniformRandomGenerator getTypeOfOrderRandomGenerator() {
    return typeOfOrderRandomGenerator;
  }

  public ContinuousEmpiricRandomGenerator getEasyOrderTimeRandomGenerator() {
    return easyOrderTimeRandomGenerator;
  }

  public ContinuousUniformGenerator getMediumOrderTimeRandomGenerator() {
    return mediumOrderTimeRandomGenerator;
  }

  public ContinuousEmpiricRandomGenerator getHardOrderTimeRandomGenerator() {
    return hardOrderTimeRandomGenerator;
  }
}
