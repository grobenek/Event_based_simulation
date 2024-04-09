package szathmary.peter.simulation;

import java.util.*;
import java.util.stream.IntStream;
import szathmary.peter.event.InitialEvent;
import szathmary.peter.mvc.model.SimulationOverview;
import szathmary.peter.mvc.observable.IObserver;
import szathmary.peter.mvc.observable.IReplicationObservable;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousEmpiricRandomGenerator;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousExponentialRandomGenerator;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousTriangularRandomGenerator;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousUniformGenerator;
import szathmary.peter.randomgenerators.discretegenerators.DiscreteEmpiricRandomGenerator;
import szathmary.peter.randomgenerators.empiricnumbergenerator.EmpiricOption;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.customer.CustomerType;
import szathmary.peter.simulation.entity.employee.Employee;
import szathmary.peter.simulation.entity.employee.EmployeeStatus;
import szathmary.peter.statistic.ContinuousStatistic;
import szathmary.peter.statistic.DiscreteStatistic;
import szathmary.peter.statistic.Statistic;

/**
 * Created by petos on 23/03/2024.<br>
 * Simulation time is in <b>SECONDS</b>
 */
public class ElectroShopSimulation extends SimulationCore implements IReplicationObservable {
  public static final double CLOSING_HOURS_OF_TICKET_MACHINE = 8 * 60 * 60;
  public static final int SERVICE_STATION_QUEUE_CAPACITY = 9;
  public static final double
      ONLINE_SERVICE_STATIONS_VS_RATIO_OF_CASUAL_AND_CONTRACT_SERVICE_STATION = 1.0 / 3.0;
  private final List<IObserver> observers;
  private final int numberOfServiceStations;
  private final int numberOfCashRegisters;
  private final ContinuousExponentialRandomGenerator timeBetweenCustomerArrivalsRandomGenerator =
      new ContinuousExponentialRandomGenerator(1.0 / 120.0);
  private final ContinuousUniformGenerator customerTypeGenerator =
      new ContinuousUniformGenerator(0, 1);
  private final ContinuousUniformGenerator ticketPrintingTimeRandomGenerator =
      new ContinuousUniformGenerator(30, 120);
  private final ContinuousUniformGenerator
      timeToFinishOrderForCasualAndContractCustomersRandomGenerator =
          new ContinuousUniformGenerator(60, 900);
  private final ContinuousTriangularRandomGenerator
      timeForFinishOrderForOnlineCustomerRandomGenerator =
          new ContinuousTriangularRandomGenerator(60, 120, 480);
  private final ContinuousUniformGenerator orderSizeRandomGenerator =
      new ContinuousUniformGenerator(0, 1);
  private final ContinuousUniformGenerator timeForTakeBigOrderRandomGenerator =
      new ContinuousUniformGenerator(30, 70);
  private final DiscreteEmpiricRandomGenerator paymentTimeRandomGenerator =
      new DiscreteEmpiricRandomGenerator(
          List.of(new EmpiricOption<>(180, 480, 0.4), new EmpiricOption<>(180, 360, 0.6)));
  private final ContinuousUniformGenerator typeOfOrderRandomGenerator =
      new ContinuousUniformGenerator(0, 1);
  private final ContinuousEmpiricRandomGenerator easyOrderTimeRandomGenerator =
      new ContinuousEmpiricRandomGenerator(
          List.of(
              new EmpiricOption<>(2.0 * 60, 5.0 * 60, 0.6),
              new EmpiricOption<>(5.0 * 60, 9.0 * 60, 0.4)));
  private final ContinuousUniformGenerator mediumOrderTimeRandomGenerator =
      new ContinuousUniformGenerator(9 * 60, 11 * 60);
  private final ContinuousEmpiricRandomGenerator hardOrderTimeRandomGenerator =
      new ContinuousEmpiricRandomGenerator(
          List.of(
              new EmpiricOption<>(11.0 * 60, 12.0 * 60, 0.1),
              new EmpiricOption<>(12.0 * 60, 20.0 * 60, 0.6),
              new EmpiricOption<>(20.0 * 60, 25.0 * 60, 0.3)));
  private final ContinuousUniformGenerator emptyCashRegistersChoosingRandomGenerator =
      new ContinuousUniformGenerator(0, 1);
  private final ContinuousUniformGenerator equalQueueCashRegistersChoosingRandomgenerator =
      new ContinuousUniformGenerator(0, 1);
  private final Statistic timeInSystemStatisticReplications;
  private final Statistic timeInSystemStatisticSummary;
  private final Statistic timeInTicketQueueStatisticSummary;
  private final Statistic timeInTicketQueueStatisticReplications;
  private final Statistic ticketQueueLengthStatisticSummary;
  private final Statistic ticketQueueLengthStatisticReplication;
  private final Statistic lastCustomerTimeLeftStatisticSummary;
  private final Statistic cashRegistersWorkloadStatisticSummary;
  private final Statistic serviceStationsWorkloadStatisticSummary;
  private final Statistic ticketMachineWorkloadSummary;
  private final Statistic ticketMachineWorkloadReplication;
  private final Statistic customersServedStatisticSummary;
  private double lastCustomerLeavingTime;
  private List<Customer> allCustomerList;
  private Queue<Customer> ticketMachineQueue;
  private List<ServiceStation> serviceStations;
  private List<CashRegister> cashRegisters;
  private PriorityQueue<Customer> casualAndContractCustomerQueue;
  private Queue<Customer> onlineCustomersQueue;
  private boolean isTicketMachineServingCustomer;
  private boolean isTicketMachineStopped;
  private List<Employee> allEmployeesList;
  private int countOfServedCustomers;

  public ElectroShopSimulation(
      long numberOfReplications,
      int numberOfServiceStations,
      int numberOfCashRegisters,
      boolean verboseSimulation) {
    super(numberOfReplications, verboseSimulation);

    this.observers = new ArrayList<>();

    this.numberOfCashRegisters = numberOfCashRegisters;
    this.numberOfServiceStations = numberOfServiceStations;

    initializeVariables();

    this.timeInSystemStatisticReplications =
        new DiscreteStatistic("Time in system in minutes - replication", false);
    this.timeInSystemStatisticSummary =
        new DiscreteStatistic("Time in system in minutes - summary", false);

    this.timeInTicketQueueStatisticReplications =
        new DiscreteStatistic("Time in ticket queue in minutes - replication", false);
    this.timeInTicketQueueStatisticSummary =
        new DiscreteStatistic("Time in ticket queue in minutes - summary", false);

    this.ticketQueueLengthStatisticReplication =
        new ContinuousStatistic("Ticket queue length - replication", false);
    this.ticketQueueLengthStatisticSummary =
        new DiscreteStatistic("Ticket queue length - summary", false);

    this.lastCustomerTimeLeftStatisticSummary =
        new DiscreteStatistic("Leaving time - summary", true);

    this.cashRegistersWorkloadStatisticSummary =
        new DiscreteStatistic("Cash registers workload - summary", false);
    this.serviceStationsWorkloadStatisticSummary =
        new DiscreteStatistic("Service stations workload - summary", false);

    this.ticketMachineWorkloadSummary =
        new DiscreteStatistic("Ticket machine workload - summary", false);
    this.ticketMachineWorkloadReplication =
        new ContinuousStatistic("Ticket machine workload - replication", false);

    this.customersServedStatisticSummary =
        new DiscreteStatistic("Customers served - summary", false);
  }

  private void initializeVariables() {
    this.allCustomerList = new ArrayList<>();
    this.allEmployeesList = new ArrayList<>();

    this.lastCustomerLeavingTime = Double.NaN;

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
      CashRegister cashRegister = new CashRegister(Integer.toString(i + 1));
      cashRegisters.add(cashRegister);
      cashRegister.getEmployee().setStatus(EmployeeStatus.IDLE);
      addEmployee(cashRegister.getEmployee());
    }

    this.countOfServedCustomers = 0;
  }

  private void initializeServingStations(int numberOfServiceStations) {
    int numberOfOnlineServiceStations =
        (int)
            Math.floor(
                ONLINE_SERVICE_STATIONS_VS_RATIO_OF_CASUAL_AND_CONTRACT_SERVICE_STATION
                    * numberOfServiceStations);

    for (int i = 0; i < numberOfServiceStations; i++) {
      ServiceStation serviceStation =
          new ServiceStation(i < numberOfOnlineServiceStations, String.valueOf(i + 1));
      serviceStations.add(serviceStation);

      serviceStation.getEmployee().setStatus(EmployeeStatus.IDLE);
      addEmployee(serviceStation.getEmployee());
    }
  }

  @Override
  public void afterReplications() {
    System.out.println(timeInSystemStatisticSummary);
    System.out.println(timeInTicketQueueStatisticSummary);
    System.out.println(ticketQueueLengthStatisticSummary);
    System.out.println(lastCustomerTimeLeftStatisticSummary);
    System.out.println(serviceStationsWorkloadStatisticSummary);
    System.out.println(cashRegistersWorkloadStatisticSummary);
    System.out.println(ticketMachineWorkloadSummary);
    System.out.println(customersServedStatisticSummary);
  }

  @Override
  public void afterReplication() {
    timeInSystemStatisticSummary.addObservation(timeInSystemStatisticReplications.getMean());
    timeInTicketQueueStatisticSummary.addObservation(
        timeInTicketQueueStatisticReplications.getMean());
    ticketQueueLengthStatisticSummary.addObservation(
        ticketQueueLengthStatisticReplication.getMean());
    lastCustomerTimeLeftStatisticSummary.addObservation(lastCustomerLeavingTime);

    for (ServiceStation serviceStation : serviceStations) {
      serviceStationsWorkloadStatisticSummary.addObservation(
          serviceStation.getWorkloadStatistics().getMean());
    }

    for (CashRegister cashRegister : cashRegisters) {
      cashRegistersWorkloadStatisticSummary.addObservation(
          cashRegister.getAverageWorkloadOfCashRegister().getMean());
    }

    ticketMachineWorkloadSummary.addObservation(ticketMachineWorkloadReplication.getMean());
    customersServedStatisticSummary.addObservation(countOfServedCustomers);

    sendNotifications();
  }

  @Override
  public void replication() {
    while (!isEventCalendarEmpty()) {
      if (getIsStopped()) {
        continue;
      }
      simulateEvent();
      if (isVerbose()) {
        sendNotifications();
      }
    }
  }

  @Override
  public void beforeReplication() {
    allEmployeesList.clear();
    allCustomerList.clear();
    resetCurrentTime();
    countOfServedCustomers = 0;
    lastCustomerLeavingTime = 0.0;

    initializeVariables();

    timeInSystemStatisticReplications.clear();
    timeInTicketQueueStatisticReplications.clear();
    ticketQueueLengthStatisticReplication.clear();
    ticketMachineWorkloadReplication.clear();
    addStartingEvent();
  }

  private void addStartingEvent() {
    addEvent(new InitialEvent(getCurrentTime()));
  }

  @Override
  public void beforeReplications() {
    cashRegistersWorkloadStatisticSummary.clear();
    serviceStationsWorkloadStatisticSummary.clear();
    ticketQueueLengthStatisticSummary.clear();
    lastCustomerTimeLeftStatisticSummary.clear();
    timeInSystemStatisticSummary.clear();
    timeInTicketQueueStatisticSummary.clear();
    ticketMachineWorkloadSummary.clear();
    customersServedStatisticSummary.clear();
  }

  public boolean isServiceQueueFull() {
    return onlineCustomersQueue.size() + casualAndContractCustomerQueue.size()
        >= SERVICE_STATION_QUEUE_CAPACITY;
  }

  public boolean isAtLeastOneServiceFree(boolean isOnlineService) {
    Optional<ServiceStation> freeServingStation =
        serviceStations.stream()
            .filter(
                station ->
                    (station.isServingOnlineCustomers() == isOnlineService)
                        && (!station.isServing()))
            .findFirst();

    return freeServingStation.isPresent();
  }

  public ServiceStation getFreeServiceStation(boolean isOnlineServiceStation) {
    Optional<ServiceStation> freeServingStation =
        serviceStations.stream()
            .filter(
                station ->
                    (station.isServingOnlineCustomers() == isOnlineServiceStation)
                        && (!station.isServing()))
            .findFirst();

    return freeServingStation.orElseThrow(
        () -> new IllegalStateException("Cannot get online station, because non is free!"));
  }

  public boolean isOnlineCustomerQueueEmpty() {
    return onlineCustomersQueue.isEmpty();
  }

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

  public ElectroShopSimulation setTicketMachineServingCustomer(
      boolean ticketMachineServingCustomer) {
    isTicketMachineServingCustomer = ticketMachineServingCustomer;
    ticketMachineWorkloadReplication.addObservation(
        ticketMachineServingCustomer ? 1 : 0, getCurrentTime());
    return this;
  }

  public void addCustomerToTicketQueue(Customer customer) {
    customer.setTimeOfEnteringTicketQueue(getCurrentTime());
    allCustomerList.add(customer);

    ticketMachineQueue.add(customer);

    if (getCurrentTime() < CLOSING_HOURS_OF_TICKET_MACHINE) {
      ticketQueueLengthStatisticReplication.addObservation(
          ticketMachineQueue.size(), getCurrentTime());
    }
  }

  public Customer removeCustomerFromTicketQueue() {
    if (ticketMachineQueue.isEmpty()) {
      throw new IllegalStateException(
          "Cannot remove customer from ticket machine queue, because queue is empty!");
    }

    Customer removedCustomer = ticketMachineQueue.poll();

    if (getCurrentTime() < CLOSING_HOURS_OF_TICKET_MACHINE) {
      ticketQueueLengthStatisticReplication.addObservation(
          ticketMachineQueue.size(), getCurrentTime());
    }

    removedCustomer.setTimeOfLeavingTicketQueue(getCurrentTime());
    return removedCustomer;
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

    if (isServiceQueueFull()) {
      throw new IllegalStateException(
          String.format(
              "Cannot add another customer to queue for casual and contract customers, current capacity is %d",
              casualAndContractCustomerQueue.size()));
    }

    customer.setTimeOfEnteringServiceQueue(getCurrentTime());

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

    if (isServiceQueueFull()) {
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

  public ContinuousExponentialRandomGenerator getTimeBetweenCustomerArrivalsRandomGenerator() {
    return timeBetweenCustomerArrivalsRandomGenerator;
  }

  public ContinuousUniformGenerator getCustomerTypeGenerator() {
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

  public ContinuousUniformGenerator getOrderSizeRandomGenerator() {
    return orderSizeRandomGenerator;
  }

  public ContinuousUniformGenerator getTimeForTakeBigOrderRandomGenerator() {
    return timeForTakeBigOrderRandomGenerator;
  }

  public DiscreteEmpiricRandomGenerator getPaymentTimeRandomGenerator() {
    return paymentTimeRandomGenerator;
  }

  public ContinuousUniformGenerator getTypeOfOrderRandomGenerator() {
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

  public boolean isCasualContractCustomerQueueEmpty() {
    return casualAndContractCustomerQueue.isEmpty();
  }

  public double getLastCustomerLeavingTime() {
    return lastCustomerLeavingTime;
  }

  public ElectroShopSimulation setLastCustomerLeavingTime(double lastCustomerLeavingTime) {
    this.lastCustomerLeavingTime = lastCustomerLeavingTime;
    return this;
  }

  public CashRegister getEligebleCashRegister() {
    // check how many cash registers are not serving anyone
    List<CashRegister> freeCashRegisters =
        cashRegisters.stream().filter(cashRegister -> !cashRegister.isServing()).toList();

    if (!freeCashRegisters.isEmpty()) {
      return freeCashRegisters.get(
          generateCashRegisterFromAvailableOptions(
              emptyCashRegistersChoosingRandomGenerator.sample(), freeCashRegisters.size()));
    }

    // no cash register is free, checking queues length

    int[] cashRegistersQueueLength =
        cashRegisters.stream().mapToInt(CashRegister::getQueueLength).toArray();

    // cheking if there are multiple minimum lengths of queues
    int minValue =
        Arrays.stream(cashRegistersQueueLength)
            .min()
            .orElseThrow(
                () -> new IllegalStateException("No cash register with minimal value was found!"));

    int[] minValuesIndeces =
        IntStream.range(0, cashRegistersQueueLength.length)
            .filter(index -> cashRegistersQueueLength[index] == minValue)
            .toArray();

    return cashRegisters.get(
        generateCashRegisterFromAvailableOptions(
            equalQueueCashRegistersChoosingRandomgenerator.sample(), minValuesIndeces.length));
  }

  private int generateCashRegisterFromAvailableOptions(double sample, int numberOfCashRegisters) {
    double cummulatedProbability = 0.0;
    int selectedFreeCashRegister = -1;
    for (int i = 0; i < numberOfCashRegisters; i++) {
      cummulatedProbability += 1.0 / numberOfCashRegisters;

      if (sample < cummulatedProbability) {
        selectedFreeCashRegister = i;
        break;
      }
    }

    if (selectedFreeCashRegister == -1) {
      throw new IllegalStateException("No cash register was selected!");
    }
    return selectedFreeCashRegister;
  }

  public Statistic getTimeInSystemStatisticReplications() {
    return timeInSystemStatisticReplications;
  }

  public Statistic getTimeInTicketQueueStatisticReplications() {
    return timeInTicketQueueStatisticReplications;
  }

  public Statistic getTicketQueueLengthStatisticSummary() {
    return ticketQueueLengthStatisticSummary;
  }

  public Statistic getTicketQueueLengthStatisticReplication() {
    return ticketQueueLengthStatisticReplication;
  }

  public Statistic getLastCustomerTimeLeftStatisticSummary() {
    return lastCustomerTimeLeftStatisticSummary;
  }

  public void addEmployee(Employee employee) {
    allEmployeesList.add(employee);
  }

  @Override
  public SimulationOverview getSimulationOverview() {
    return new SimulationOverview(
        casualAndContractCustomerQueue.size() + onlineCustomersQueue.size(),
        getCurrentReplication(),
        getCurrentTime(),
        allCustomerList,
        allEmployeesList,
        serviceStations,
        cashRegisters,
        timeInSystemStatisticReplications,
        timeInSystemStatisticSummary,
        timeInTicketQueueStatisticSummary,
        timeInTicketQueueStatisticReplications,
        ticketQueueLengthStatisticSummary,
        ticketQueueLengthStatisticReplication,
        lastCustomerTimeLeftStatisticSummary,
        serviceStationsWorkloadStatisticSummary,
        cashRegistersWorkloadStatisticSummary,
        ticketMachineWorkloadSummary,
        customersServedStatisticSummary);
  }

  public void increaseCountOfServedCustomers() {
    countOfServedCustomers++;
  }

  @Override
  public void attach(IObserver observer) {
    observers.add(observer);
  }

  @Override
  public void detach(IObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void sendNotifications() {
    for (IObserver observer : observers) {
      observer.update(this);
    }
  }
}
