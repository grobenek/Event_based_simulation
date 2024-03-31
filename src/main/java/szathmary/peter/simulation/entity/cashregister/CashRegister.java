package szathmary.peter.simulation.entity.cashregister;

import java.util.LinkedList;
import java.util.Queue;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.employee.Employee;

/** Created by petos on 29/03/2024. */
public class CashRegister {
  private final Queue<Customer> cashRegisterQueue;
  private final Employee employee;
  private boolean isServing;
  private Customer currentServedCustomer;

  public CashRegister() {
    this.cashRegisterQueue = new LinkedList<>();
    this.employee = new Employee();
    this.isServing = false;
    this.currentServedCustomer = null;
  }

  public Employee getEmployee() {
    return employee;
  }

  public int getCustomersInQueue() {
    return cashRegisterQueue.size();
  }

  public void addCustomerToQueue(Customer customer) {
    cashRegisterQueue.add(customer);
  }

  public Customer removeCustomerFromQueue() {
    return cashRegisterQueue.poll();
  }

  public boolean isServing() {
    return isServing;
  }

  public CashRegister setServing(boolean serving) {
    isServing = serving;
    return this;
  }

  public Customer getCurrentServedCustomer() {
    return currentServedCustomer;
  }

  public int getQueueLength() {
    return cashRegisterQueue.size();
  }

  public CashRegister setCurrentServedCustomer(Customer currentServedCustomer) {
    this.currentServedCustomer = currentServedCustomer;
    return this;
  }

  public boolean isQueueEmpty() {
    return cashRegisterQueue.isEmpty();
  }
}
