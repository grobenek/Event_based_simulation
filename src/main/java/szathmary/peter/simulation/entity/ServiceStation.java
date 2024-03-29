package szathmary.peter.simulation.entity;

import java.util.PriorityQueue;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.employee.Employee;

/** Created by petos on 29/03/2024. */
public class ServiceStation {
  private final Employee employee;
  private boolean isServing;
  private Customer currentServedCustomer;
  private final boolean isServingOnlineCustomers;

  public ServiceStation(boolean isServingOnlineCustomers) {
    this.employee = new Employee();
    this.isServing = false;
    this.currentServedCustomer = null;
    this.isServingOnlineCustomers = isServingOnlineCustomers;
  }

  public Employee getEmployee() {
    return employee;
  }

  public boolean isServing() {
    return isServing;
  }

  public ServiceStation setServing(boolean serving) {
    isServing = serving;
    return this;
  }

  public Customer getCurrentServedCustomer() {
    return currentServedCustomer;
  }

  public ServiceStation setCurrentServedCustomer(Customer currentServedCustomer) {
    this.currentServedCustomer = currentServedCustomer;
    return this;
  }

  public boolean isServingOnlineCustomers() {
    return isServingOnlineCustomers;
  }
}