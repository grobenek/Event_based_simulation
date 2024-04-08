package szathmary.peter.mvc.model;

import java.util.List;
import szathmary.peter.simulation.entity.ServiceStation;
import szathmary.peter.simulation.entity.cashregister.CashRegister;
import szathmary.peter.simulation.entity.customer.Customer;
import szathmary.peter.simulation.entity.employee.Employee;
import szathmary.peter.statistic.Statistic;

/** Created by petos on 31/03/2024. */
public record SimulationOverview(
    int serviceStationsQueueLength,
    long currentReplication,
    double currentTime,
    List<Customer> customerList,
    List<Employee> employeeList,
    List<ServiceStation> serviceStations,
    List<CashRegister> cashRegisters,
    Statistic timeInSystemStatisticReplications,
    Statistic timeInSystemStatisticSummary,
    Statistic timeInTicketQueueStatisticSummary,
    Statistic timeInTicketQueueStatisticReplications,
    Statistic ticketQueueLengthStatisticSummary,
    Statistic ticketQueueLengthStatisticReplication,
    Statistic lastCustomerTimeLeftStatisticSummary,
    Statistic serviceStationWorkloadStatisticSummary,
    Statistic cashRegisterWorkloadStatisticSummary,
    Statistic ticketMachineWorkloadSummary,
    Statistic customersServedStatisticSummary) {}
