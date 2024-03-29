package szathmary.peter.simulation.entity.customer;

/** Created by petos on 21/03/2024. */
public final class Customer implements Comparable<Customer> {
  private CustomerType customerType;
  private double timeOfArrival;
  private double timeOfEnteringTicketQueue;
  private double timeOfGettingTicket;
  private double timeOfEnteringServiceQueue;
  private double timeOfStartOfService;
  private double timeOfEnteringCheckoutQueue;
  private double timeOfStartCheckoutService;
  private double timeOfLeavingSystem;

  public Customer() {}

  public double getTimeOfArrival() {
    return timeOfArrival;
  }

  public Customer setTimeOfArrival(double timeOfArrival) {
    this.timeOfArrival = timeOfArrival;
    return this;
  }

  public double getTimeOfEnteringTicketQueue() {
    return timeOfEnteringTicketQueue;
  }

  public Customer setTimeOfEnteringTicketQueue(double timeOfEnteringTicketQueue) {
    this.timeOfEnteringTicketQueue = timeOfEnteringTicketQueue;
    return this;
  }

  public double getTimeOfGettingTicket() {
    return timeOfGettingTicket;
  }

  public Customer setTimeOfGettingTicket(double timeOfGettingTicket) {
    this.timeOfGettingTicket = timeOfGettingTicket;
    return this;
  }

  public double getTimeOfEnteringServiceQueue() {
    return timeOfEnteringServiceQueue;
  }

  public Customer setTimeOfEnteringServiceQueue(double timeOfEnteringServiceQueue) {
    this.timeOfEnteringServiceQueue = timeOfEnteringServiceQueue;
    return this;
  }

  public double getTimeOfStartOfService() {
    return timeOfStartOfService;
  }

  public Customer setTimeOfStartOfService(double timeOfStartOfService) {
    this.timeOfStartOfService = timeOfStartOfService;
    return this;
  }

  public double getTimeOfEnteringCheckoutQueue() {
    return timeOfEnteringCheckoutQueue;
  }

  public Customer setTimeOfEnteringCheckoutQueue(double timeOfEnteringCheckoutQueue) {
    this.timeOfEnteringCheckoutQueue = timeOfEnteringCheckoutQueue;
    return this;
  }

  public double getTimeOfStartCheckoutService() {
    return timeOfStartCheckoutService;
  }

  public Customer setTimeOfStartCheckoutService(double timeOfStartCheckoutService) {
    this.timeOfStartCheckoutService = timeOfStartCheckoutService;
    return this;
  }

  public double getTimeOfLeavingSystem() {
    return timeOfLeavingSystem;
  }

  public Customer setTimeOfLeavingSystem(double timeOfLeavingSystem) {
    this.timeOfLeavingSystem = timeOfLeavingSystem;
    return this;
  }

  public CustomerType getCustomerType() {
    return customerType;
  }

  public Customer setCustomerType(CustomerType customerType) {
    this.customerType = customerType;
    return this;
  }

  /**
   * If customer types are equal, customers are compared by their ticker time, else Contract
   * customer is always bigger than other types
   *
   * @param other the object to be compared.
   * @return 1 if current is bigger, 0 if same and -1 if current is lower
   */
  @Override
  public int compareTo(Customer other) {
    if (this.customerType == other.getCustomerType()) {
      return Double.compare(getTimeOfGettingTicket(), other.getTimeOfGettingTicket());
    }

    if (customerType == CustomerType.CONTRACT) {
      return 1;
    } else {
      return -1;
    }
  }
}
