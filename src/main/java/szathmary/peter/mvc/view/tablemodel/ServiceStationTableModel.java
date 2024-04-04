package szathmary.peter.mvc.view.tablemodel;

import javax.swing.table.AbstractTableModel;

/** Created by petos on 01/04/2024. */
public class ServiceStationTableModel extends AbstractTableModel {
  private static final String[] COLUMN_NAMES = {"Queue length"}; // TODO toto teoreticky netreba
  private int serviceStationsQueueLength = 0;

  public ServiceStationTableModel(int serviceStationsQueueLength) {
    this.serviceStationsQueueLength = serviceStationsQueueLength;
  }

  public ServiceStationTableModel setServiceStationsQueueLength(int serviceStationsQueueLength) {
    this.serviceStationsQueueLength = serviceStationsQueueLength;
    fireTableDataChanged();
    return this;
  }

  @Override
  public int getRowCount() {
    return 1;
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return serviceStationsQueueLength;
  }

  @Override
  public String getColumnName(int column) {
    return COLUMN_NAMES[column];
  }
}
