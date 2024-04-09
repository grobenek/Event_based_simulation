package szathmary.peter.mvc.view.tablemodel;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import szathmary.peter.simulation.entity.employee.Employee;

/** Created by petos on 01/04/2024. */
public class EmployeeTableModel extends AbstractTableModel {
  private static final String[] COLUMN_NAMES = {"Type", "Status"};
  private List<Employee> employees;

  public EmployeeTableModel(List<Employee> employees) {
    this.employees = employees;
  }

  public EmployeeTableModel setEmployees(List<Employee> employees) {
    this.employees = employees;
    fireTableDataChanged();
    return this;
  }

  @Override
  public int getRowCount() {
    return employees.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (employees.isEmpty()) {
      return null;
    }

    Employee employee = employees.get(rowIndex);

    switch (columnIndex) {
      case 0 -> {
        return employee.getEmployeeType();
      }
      case 1 -> {
        return employee.getStatus();
      }
      default -> throw new IllegalStateException("Unknown column selected!");
    }
  }

  @Override
  public String getColumnName(int column) {
    return COLUMN_NAMES[column];
  }
}
