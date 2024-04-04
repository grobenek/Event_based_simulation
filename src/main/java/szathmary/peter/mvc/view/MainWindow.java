package szathmary.peter.mvc.view;

import java.util.List;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import szathmary.peter.mvc.controller.IController;
import szathmary.peter.mvc.model.SimulationOverview;
import szathmary.peter.mvc.observable.IObservable;
import szathmary.peter.mvc.observable.IReplicationObservable;
import szathmary.peter.mvc.view.tablemodel.CashRegisterTableModel;
import szathmary.peter.mvc.view.tablemodel.CustomerTableModel;
import szathmary.peter.mvc.view.tablemodel.EmployeeTableModel;
import szathmary.peter.mvc.view.tablemodel.ServiceStationTableModel;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.employee.Employee;
import szathmary.peter.util.TimeFormatter;

/** Created by petos on 31/03/2024. */
public class MainWindow extends JFrame implements IMainWindow {
  private final IController controller;
  private JPanel mainPanel;
  private JPanel overviewPanel;
  private JPanel settingsPanel;
  private JPanel verbosePanel;
  private JPanel timerPanel;
  private JPanel controllingPanel;
  private JPanel customersPanel;
  private JPanel employeesPanel;
  private JPanel stationsPanel;
  private JPanel parametersPanel;
  private JCheckBox verboseCheckBox;
  private JTextPane timeTextPane;
  private JSlider timeSlider;
  private JTextField numberOfServiceStationsTextField;
  private JTextField numberOfCashRegistersTextField;
  private JTextField numberOfReplicationsTextField;
  private JButton startSimulationButton;
  private JButton stopSimulationButton;
  private JButton setParametersButton;
  private JTable customerTable;
  private JTable employeesTable;
  private JTable serviceStationsTable;
  private JTable cashRegistersTable;
  private JScrollPane terminalScrollPane;
  private JTextArea replicationStatisticTextArea;
  private JTextArea summaryStatisticTextArea;
    private JScrollPane replicationStatisticsScrollPane;
    private JScrollPane summaryStatisticsScrollPane;
    private SwingWorker<Void, Void> simulationWorker;
  private CustomerTableModel customerTableModel;
  private EmployeeTableModel employeeTableModel;
  private ServiceStationTableModel serviceStationsTableModel;
  private CashRegisterTableModel cashRegistersTableModel;

  public MainWindow(IController controller) {
    this.controller = controller;
    this.controller.attach(this);

    setContentPane(mainPanel);
    setTitle("Event oriented simulation");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1600, 1000);
    setLocationRelativeTo(null);
    setVisible(true);

    DefaultCaret replicationStatisticTextAreaCaret = (DefaultCaret)replicationStatisticTextArea.getCaret();
    replicationStatisticTextAreaCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

    DefaultCaret summaryStatisticTextAreaCaret = (DefaultCaret)summaryStatisticTextArea.getCaret();
    summaryStatisticTextAreaCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

    verboseCheckBox.addActionListener(e -> setVerboseSimulation(verboseCheckBox.isSelected()));

    startSimulationButton.addActionListener(
        e -> {
          startSimulation();
          setParametersButton.setEnabled(false);
          numberOfCashRegistersTextField.setEnabled(false);
          numberOfReplicationsTextField.setEnabled(false);
          numberOfServiceStationsTextField.setEnabled(false);

          stopSimulationButton.setEnabled(true);
          timeSlider.setEnabled(true);
        });

    stopSimulationButton.addActionListener(
        e -> {
          stopSimulation();
          stopSimulationButton.setEnabled(false);

          setParametersButton.setEnabled(true);
          numberOfCashRegistersTextField.setEnabled(true);
          numberOfReplicationsTextField.setEnabled(true);
          numberOfServiceStationsTextField.setEnabled(true);
        });

    setParametersButton.addActionListener(
        e -> {
          setParameters();
          startSimulationButton.setEnabled(true);
          stopSimulationButton.setEnabled(true);

          setParametersButton.setEnabled(false);
          numberOfCashRegistersTextField.setEnabled(false);
          numberOfReplicationsTextField.setEnabled(false);
          numberOfServiceStationsTextField.setEnabled(false);
        });

    timeSlider.addChangeListener(
        ce -> {
          controller.changeSimulationSpeed(timeSlider.getValue());
        });
  }

  @Override
  public void update(IObservable observable) {
    if (!(observable instanceof IReplicationObservable replicationObservable)) {
      return;
    }

    SwingUtilities.invokeLater(
        () -> {
          SimulationOverview simulationOverview = replicationObservable.getSimulationOverview();

          updateUIComponents(simulationOverview);
        });
  }

  private void updateUIComponents(SimulationOverview simulationOverview) {
    timeTextPane.setText(
        String.format(
            "Current replication: %d\nTIME: %s",
            simulationOverview.currentReplication(),
            TimeFormatter.getFormattedTime(simulationOverview.currentTime())));

    // TODO toto je narocne na vykon - UPRAVIT - POTOM SPRAVIT STATISTIKY VSETKY - POTOM GRAF
    // ZAVISLOSTI a UI DONE. Este orezat vsetky hodnoty a POTOM SKONTROLOVAT SIMULACIU
    if (verboseCheckBox.isSelected()) {
      replicationStatisticTextArea.setEnabled(true);
      replicationStatisticTextArea.setText(simulationOverview.replicationsStatistics().toString());
    } else {
      replicationStatisticTextArea.setEnabled(false);
    }
    summaryStatisticTextArea.setText(simulationOverview.summaryStatitics().toString());

    List<Customer> customers = simulationOverview.customerList();
    customerTableModel.setCustomerList(customers);

    List<Employee> employees = simulationOverview.employeeList();
    employeeTableModel.setEmployees(employees);

    List<CashRegister> cashRegisters = simulationOverview.cashRegisters();
    cashRegistersTableModel.setCashRegisters(cashRegisters);

    int serviceStationsQueueLength = simulationOverview.serviceStationsQueueLength();
    serviceStationsTableModel.setServiceStationsQueueLength(serviceStationsQueueLength);
  }

  @Override
  public void startSimulation() {
    simulationWorker =
        new SwingWorker<>() {
          @Override
          protected Void doInBackground() {
            controller.startSimulation();
            return null;
          }
        };

    simulationWorker.execute();

    //      simulationWorker = new Thread(controller::startSimulation);
    //    simulationWorker.start();
  }

  @Override
  public void setParameters() {
    controller.setParameters(
        Long.parseLong(numberOfReplicationsTextField.getText()),
        Integer.parseInt(numberOfServiceStationsTextField.getText()),
        Integer.parseInt(numberOfCashRegistersTextField.getText()),
        verboseCheckBox.isSelected());
  }

  @Override
  public void stopSimulation() {
    if (simulationWorker == null) {
      return;
    }

    controller.stopSimulation();
  }

  @Override
  public void setVerboseSimulation(boolean verbose) {
    controller.setVerboseSimulation(verbose);
  }

  private void createUIComponents() {
    customerTable = new JTable();
    customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    customerTableModel = new CustomerTableModel(List.of());
    customerTable.setModel(customerTableModel);

    employeesTable = new JTable();
    employeesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    employeeTableModel = new EmployeeTableModel(List.of());
    employeesTable.setModel(employeeTableModel);

    serviceStationsTable = new JTable();
    serviceStationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    serviceStationsTableModel = new ServiceStationTableModel(0);
    serviceStationsTable.setModel(serviceStationsTableModel);

    cashRegistersTable = new JTable();
    cashRegistersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    cashRegistersTableModel = new CashRegisterTableModel(List.of());
    cashRegistersTable.setModel(cashRegistersTableModel);
  }
}
