package szathmary.peter.mvc.view.tablemodel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import szathmary.peter.simulation.entity.ServiceStation;

/** Created by petos on 01/04/2024. */
public class ServiceStationTableModel extends AbstractTableModel {
  private static final String[] COLUMN_NAMES = {"Name", "Work load"};
  private List<ServiceStation> serviceStationList;

  public ServiceStationTableModel(int serviceStationsQueueLength) {
    this.serviceStationList = new ArrayList<>();
  }

  public ServiceStationTableModel setServiceStations(List<ServiceStation> serviceStationList) {
    this.serviceStationList = serviceStationList;
    fireTableDataChanged();
    return this;
  }

  @Override
  public int getRowCount() {
    return serviceStationList.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    ServiceStation serviceStation = serviceStationList.get(rowIndex);

    switch (columnIndex) {
      case 0 -> {
        return serviceStation.getName();
      }
      case 1 -> {
        return String.format("%.02f", serviceStation.getWorkloadStatistics().getMean() * 100);
      }
      default -> throw new IllegalStateException();
    }
  }

  @Override
  public String getColumnName(int column) {
    return COLUMN_NAMES[column];
  }
}
