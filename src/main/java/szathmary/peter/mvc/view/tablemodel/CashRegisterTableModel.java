package szathmary.peter.mvc.view.tablemodel;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import szathmary.peter.simulation.entity.cashregister.CashRegister;

/** Created by petos on 01/04/2024. */
public class CashRegisterTableModel extends AbstractTableModel {
  private final String[] COLUMN_NAMES = {"Name", "Queue length", "Average workload"};
  private List<CashRegister> cashRegisters;

  public CashRegisterTableModel(List<CashRegister> cashRegisters) {
    this.cashRegisters = cashRegisters;
  }

  public CashRegisterTableModel setCashRegisters(List<CashRegister> cashRegisters) {
    this.cashRegisters = cashRegisters;
    fireTableDataChanged();
    return this;
  }

  @Override
  public int getRowCount() {
    return cashRegisters.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    CashRegister cashRegister = cashRegisters.get(rowIndex);

    switch (columnIndex) {
      case 0 -> {
        return cashRegister.getName();
      }
      case 1 -> {
        return cashRegister.getQueueLength();
      }
      case 2 -> {
        return String.format("%.2f", cashRegister.getAverageWorkloadOfCashRegister().getMean() * 100);
      }
      default -> throw new IllegalStateException("Selecting unknown column!");
    }
  }

  @Override
  public String getColumnName(int column) {
    return COLUMN_NAMES[column];
  }
}
