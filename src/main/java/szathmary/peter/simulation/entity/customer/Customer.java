package szathmary.peter.simulation.entity.customer;

/** Created by petos on 21/03/2024. */
public final class Customer {
  private CustomerType customerType;
  private double timeOfArrival;
  private double timeOfEnteringTicketQueue;
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
}
