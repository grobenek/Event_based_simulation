package szathmary.peter.simulation;

import java.util.*;
import java.util.stream.IntStream;
import szathmary.peter.event.InitialEvent;
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
import szathmary.peter.statistic.DiscreteStatistic;
import szathmary.peter.statistic.Statistic;

/**
 * Created by petos on 23/03/2024.<br>
 * Simulation time is in <b>SECONDS</b>
 */
public class ElectroShopSimulation extends SimulationCore {
  public static final double CLOSING_HOURS_OF_TICKET_MACHINE = 8 * 60 * 60;
  public static final int SERVICE_STATION_QUEUE_CAPACITY = 8;
  public static final double
      RATIO_OF_CASUAL_AND_CONTRACT_SERVICE_STATION_VS_ONLINE_SERVICE_STATIONS = 2.0 / 3.0;
  private List<Customer> allCustomerList;
  private final int numberOfServiceStations;
  private final int numberOfCashRegisters;
  private final ContinuousExponentialRandomGenerator timeBetweenCustomerArrivalsRandomGenerator =
      new ContinuousExponentialRandomGenerator(1.0 / (30.0 / 60.0 / 60.0));
  private final ContinuousUniformGenerator customerTypeGenerator =
      new ContinuousUniformGenerator(0, 1);
  private final ContinuousUniformGenerator ticketPrintingTimeRandomGenerator =
      new ContinuousUniformGenerator(30, 180);
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
  private final Statistic timeInTicketQueueSummary;
  private final Statistic timeInTicketQueueReplications;
  private Queue<Customer> ticketMachineQueue;
  private List<ServiceStation> serviceStations;
  private List<CashRegister> cashRegisters;
  private PriorityQueue<Customer> casualAndContractCustomerQueue;
  private Queue<Customer> onlineCustomersQueue;
  private boolean isTicketMachineServingCustomer;
  private boolean isTicketMachineStopped;

  public ElectroShopSimulation(
      long numberOfReplications,
      int numberOfServiceStations,
      int numberOfCashRegisters,
      boolean verboseSimulation) {
    super(numberOfReplications, verboseSimulation);

    this.numberOfCashRegisters = numberOfCashRegisters;
    this.numberOfServiceStations = numberOfServiceStations;

    initializeVariables();

    this.timeInSystemStatisticReplications = new DiscreteStatistic();
    this.timeInSystemStatisticSummary = new DiscreteStatistic();

    this.timeInTicketQueueReplications = new DiscreteStatistic();
    this.timeInTicketQueueSummary = new DiscreteStatistic();
  }

  private void initializeVariables() {
    this.allCustomerList = new ArrayList<>();

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
  public void afterReplications() {
    System.out.println(timeInSystemStatisticSummary);
    System.out.println(timeInTicketQueueSummary);
  }

  @Override
  public void afterReplication() {
    timeInSystemStatisticSummary.addObservation(timeInSystemStatisticReplications.getMean());
    timeInTicketQueueSummary.addObservation(timeInTicketQueueReplications.getMean());
  }

  @Override
  public void replication() {
    while (!isEventCalendarEmpty()) {
      simulateEvent();
    }
  }

  @Override
  public void beforeReplication() {
    initializeVariables();

    timeInSystemStatisticReplications.clear();
    timeInTicketQueueReplications.clear();
    addStartingEvent();
  }

  private void addStartingEvent() {
    addEvent(new InitialEvent(getCurrentTime()));
  }

  @Override
  public void beforeReplications() {}

  public boolean isServiceQueueFull() {
    return onlineCustomersQueue.size() + casualAndContractCustomerQueue.size() == 8;
  }

  public boolean isAtLeastOneOnlineServiceFree() {
    Optional<ServiceStation> freeServingStation =
        serviceStations.stream()
            .filter(station -> station.isServingOnlineCustomers() && (!station.isServing()))
            .findFirst();

    return freeServingStation.isPresent();
  }

  public boolean isAtLeastOneCausualAndContractServiceFree() {
    Optional<ServiceStation> freeServingStation =
        serviceStations
            .stream() // TODO toto potom refactornut na boolean parameter ktory zistuje ci ma byt
            // online ci nie
            .filter(station -> (!station.isServingOnlineCustomers()) && (!station.isServing()))
            .findFirst();

    return freeServingStation.isPresent();
  }

  public ServiceStation getFreeCasualAndContractServiceStation() {
    Optional<ServiceStation> freeServingStation =
        serviceStations
            .stream() // TODO toto potom refactornut na boolean parameter ktory zistuje ci ma byt
            // online ci nie
            .filter(station -> (!station.isServingOnlineCustomers()) && (!station.isServing()))
            .findFirst();

    return freeServingStation.orElseThrow(
        () ->
            new IllegalStateException(
                "Cannot get free casual and contract station, because non is free!"));
  }

  public ServiceStation getFreeOnlineServiceStation() {
    Optional<ServiceStation> freeServingStation =
        serviceStations
            .stream() // TODO toto potom refactornut na boolean parameter ktory zistuje ci ma byt
            // online ci nie
            .filter(station -> (station.isServingOnlineCustomers()) && (!station.isServing()))
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
    return this;
  }

  public void addCustomerToTicketQueue(Customer customer) {
    customer.setTimeOfEnteringTicketQueue(getCurrentTime());
    allCustomerList.add(customer);

    ticketMachineQueue.add(customer);
  }

  public Customer removeCustomerFromTicketQueue() {
    if (ticketMachineQueue.isEmpty()) {
      throw new IllegalStateException(
          "Cannot remove customer from ticket machine queue, because queue is empty!");
    }

    Customer removedCustomer = ticketMachineQueue.poll();
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

  public ContinuousUniformGenerator getEmptyCashRegistersChoosingRandomGenerator() {
    return emptyCashRegistersChoosingRandomGenerator;
  }

  public ContinuousUniformGenerator getEqualQueueCashRegistersChoosingRandomgenerator() {
    return equalQueueCashRegistersChoosingRandomgenerator;
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

    if (minValuesIndeces.length == 1) {
      return cashRegisters.get(minValuesIndeces[0]);
    } else {
      return cashRegisters.get(
          generateCashRegisterFromAvailableOptions(
              equalQueueCashRegistersChoosingRandomgenerator.sample(), minValuesIndeces.length));
    }
  }

  private int generateCashRegisterFromAvailableOptions(double sample, int numberOfCashRegisters) {
    double cummulatedProbability = 0.0;
    int selectedFreeCashRegister = -1;
    for (int i = 0; i < numberOfCashRegisters; i++) {
      cummulatedProbability += 1.0 / numberOfCashRegisters;

      if (sample <= cummulatedProbability) {
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

  public Statistic getTimeInTicketQueueReplications() {
    return timeInTicketQueueReplications;
  }
}
