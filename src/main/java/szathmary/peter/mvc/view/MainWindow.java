package szathmary.peter.mvc.view;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
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
  private JPanel mainSimulationPanel;
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
  private JButton startCorrelationButton;
  private JTabbedPane tabPane;
  private JPanel mainWindowPanel;
  private JPanel correlationPanel;
  private ChartPanel chartPanel;
  private JTextField numberOfCashRegistersStartTextField;
  private JTextField numberOfCashRegistersEndTextField;
  private SwingWorker<Void, Void> simulationWorker;
  private CustomerTableModel customerTableModel;
  private EmployeeTableModel employeeTableModel;
  private ServiceStationTableModel serviceStationsTableModel;
  private CashRegisterTableModel cashRegistersTableModel;
  private DefaultCategoryDataset barChartDataset;
  private JFreeChart correlationBarChart;

  public MainWindow(IController controller) {
    this.controller = controller;
    this.controller.attach(this);

    setContentPane(
        mainWindowPanel); // TODO spravit graf - potom vytazenost raodv aj vsetkeho + interval
    // spolahlivosti
    setTitle("Event oriented simulation");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1600, 1000);
    setLocationRelativeTo(null);
    setVisible(true);

    // disable automatic scrolling text are when it is updated
    DefaultCaret replicationStatisticTextAreaCaret =
        (DefaultCaret) replicationStatisticTextArea.getCaret();
    replicationStatisticTextAreaCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

    DefaultCaret summaryStatisticTextAreaCaret = (DefaultCaret) summaryStatisticTextArea.getCaret();
    summaryStatisticTextAreaCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

    verboseCheckBox.addActionListener(e -> setInspectReplication(verboseCheckBox.isSelected()));

    startSimulationButton.addActionListener(
        e -> {
          startSimulation();
          setParametersButton.setEnabled(false);
          startSimulationButton.setEnabled(false);
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
          startSimulationButton.setEnabled(true);
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

    startCorrelationButton.addActionListener(
        e -> {
          SwingUtilities.invokeLater(
              () -> {
                startPrintingCorrelationChart();
                startCorrelationButton.setEnabled(false);
              });
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

    if (verboseCheckBox.isSelected()) {
      replicationStatisticTextArea.setEnabled(true);
      replicationStatisticTextArea.setText(getReplicationStatisticsToString(simulationOverview));
    } else {
      replicationStatisticTextArea.setEnabled(false);
    }
    summaryStatisticTextArea.setText(getSummaryStatisticsToString(simulationOverview));

    List<Customer> customers = simulationOverview.customerList();
    customerTableModel.setCustomerList(customers);

    List<Employee> employees = simulationOverview.employeeList();
    employeeTableModel.setEmployees(employees);

    List<CashRegister> cashRegisters = simulationOverview.cashRegisters();
    cashRegistersTableModel.setCashRegisters(cashRegisters);

    int serviceStationsQueueLength = simulationOverview.serviceStationsQueueLength();
    serviceStationsTableModel.setServiceStationsQueueLength(serviceStationsQueueLength);
  }

  private String getReplicationStatisticsToString(SimulationOverview simulationOverview) {

    return simulationOverview.timeInSystemStatisticReplications()
        + "\n"
        + simulationOverview.ticketQueueLengthStatisticReplication()
        + "\n"
        + simulationOverview.timeInTicketQueueStatisticReplications();
  }

  private String getSummaryStatisticsToString(SimulationOverview simulationOverview) {

    return simulationOverview.timeInSystemStatisticSummary()
        + "\n"
        + simulationOverview.ticketQueueLengthStatisticSummary()
        + "\n"
        + simulationOverview.timeInTicketQueueStatisticSummary()
        + "\n"
        + simulationOverview.lastCustomerTimeLeftStatisticSummary();
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
  public void setInspectReplication(boolean verbose) {
    controller.setInspectReplication(verbose);
  }

  private void startPrintingCorrelationChart() {
    tabPane.setEnabled(false);

    int numberOfCashRegistersToSimulate =
        Integer.parseInt(numberOfCashRegistersEndTextField.getText())
            - Integer.parseInt(numberOfCashRegistersStartTextField.getText());

    Semaphore semaphore = new Semaphore(1);

    for (int i = 1; i <= numberOfCashRegistersToSimulate + 1; i++) {
        int finalI = i;

      SwingWorker<SimulationOverview, Void> worker =
          new SwingWorker<>() {
            @Override
            protected SimulationOverview doInBackground() {
              try {
                // Acquire the permit before starting the simulation
                semaphore.acquire();
                controller.setParameters(
                    30_000, //TODO prist na to AKO ZACHOVAT PORADIE - potom spravit interval spolahlivosti, vyrazenie, ako vyratat integral a evenry pozriet - teams
                    Integer.parseInt(numberOfServiceStationsTextField.getText()),
                    finalI,
                    false);
                controller.startSimulation();
                return controller.getSimulationOverview();
              } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
              } finally {
                // Release the permit after the simulation finishes
                semaphore.release();
              }
            }

            @Override
            protected void done() {
              super.done();
              try {
                // Wait for this simulation to finish
                get();
                // Add result of statistics to chart
                barChartDataset.addValue(
                    controller
                        .getSimulationOverview()
                        .timeInTicketQueueStatisticSummary()
                        .getMean(),
                    finalI + ". cash registers",
                    String.valueOf(finalI));
              } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
              }
            }
          };

      worker.execute();
    }

    tabPane.setEnabled(true);
    startCorrelationButton.setEnabled(true);
  }

  private void createUIComponents() {
    SwingUtilities.invokeLater(
        () -> {
          // tables
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

          // chart
          barChartDataset = new DefaultCategoryDataset();

          correlationBarChart =
              ChartFactory.createBarChart(
                  "Correlation",
                  "Avarege ticket queue length",
                  "Number of cash registers",
                  barChartDataset,
                  PlotOrientation.VERTICAL,
                  true,
                  true,
                  false);

          chartPanel = new ChartPanel(correlationBarChart);
          chartPanel.setMouseZoomable(false);
        });
  }
}
