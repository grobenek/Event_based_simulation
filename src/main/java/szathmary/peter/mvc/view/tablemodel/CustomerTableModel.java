package szathmary.peter.mvc.view.tablemodel;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 01/04/2024. */
public class CustomerTableModel extends AbstractTableModel {
  private static final String[] COLUMN_NAMES = {
    "Customer type",
    "Order size",
    "Time of arrival",
    "Time of entering ticket queue",
    "Time of leaving ticket queue",
    "Time of getting ticket",
    "Time of entering service queue",
    "Time of start of service",
    "Time of entering checkout queue",
    "Time of start of checkout service",
    "Time of leaving system"
  };
  private List<Customer> customerList;

  public CustomerTableModel(List<Customer> customerList) {
    this.customerList = customerList;
  }

  public CustomerTableModel setCustomerList(List<Customer> customerList) {
    this.customerList = customerList;
    fireTableDataChanged();
    return this;
  }

  @Override
  public int getRowCount() {
    return customerList.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public synchronized Object getValueAt(int rowIndex, int columnIndex) {
    if (customerList.isEmpty()) {
      return null;
    }

    Customer customer = customerList.get(rowIndex);

    switch (columnIndex) {
      case 0 -> {
        return customer.getCustomerType();
      }
      case 1 -> {
        return customer.getOrderSize();
      }
      case 2 -> {
        return TimeFormatter.getFormattedTime(customer.getTimeOfArrival());
      }
      case 3 -> {
        return TimeFormatter.getFormattedTime(customer.getTimeOfEnteringTicketQueue());
      }
      case 4 -> {
        return TimeFormatter.getFormattedTime(customer.getTimeOfLeavingTicketQueue());
      }
      case 5 -> {
        return TimeFormatter.getFormattedTime(customer.getTimeOfGettingTicket());
      }
      case 6 -> {
        return TimeFormatter.getFormattedTime(customer.getTimeOfEnteringServiceQueue());
      }
      case 7 -> {
        return TimeFormatter.getFormattedTime(customer.getTimeOfStartOfService());
      }
      case 8 -> {
        return TimeFormatter.getFormattedTime(customer.getTimeOfEnteringCheckoutQueue());
      }
      case 9 -> {
        return TimeFormatter.getFormattedTime(customer.getTimeOfStartCheckoutService());
      }
      case 10 -> {
        return TimeFormatter.getFormattedTime(customer.getTimeOfLeavingSystem());
      }
      default -> throw new IllegalStateException("Getting unknown column information!");
    }
  }

  @Override
  public String getColumnName(int column) {
    return COLUMN_NAMES[column];
  }
}
