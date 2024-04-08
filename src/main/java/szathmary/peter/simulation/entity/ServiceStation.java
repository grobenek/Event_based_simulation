package szathmary.peter.simulation.entity;

import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.employee.Employee;
import szathmary.peter.simulation.entity.employee.EmployeeType;
import szathmary.peter.statistic.ContinuousStatistic;
import szathmary.peter.statistic.Statistic;

/** Created by petos on 29/03/2024. */
public class ServiceStation {
  private final Employee employee;
  private final boolean isServingOnlineCustomers;
  private final ContinuousStatistic workloadStatistics;
  private final String name;
  private boolean isServing;
  private Customer currentServedCustomer;

  public ServiceStation(boolean isServingOnlineCustomers, String name) {
    this.employee = new Employee(EmployeeType.SERVICE_STATION);
    this.isServing = false;
    this.currentServedCustomer = null;
    this.isServingOnlineCustomers = isServingOnlineCustomers;
    this.workloadStatistics = new ContinuousStatistic("Service station work load", false);
    this.name = name;
  }

  public Employee getEmployee() {
    return employee;
  }

  public boolean isServing() {
    return isServing;
  }

  public ServiceStation setServing(boolean serving, double time) {
    isServing = serving;
    workloadStatistics.addObservation(serving ? 1 : 0, time);
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

  public Statistic getWorkloadStatistics() {
    return workloadStatistics;
  }

  public String getName() {
    return name;
  }
}
