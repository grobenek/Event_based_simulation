package szathmary.peter.simulation.entity.employee;

/** Created by petos on 29/03/2024. */
public class Employee {
    private EmployeeStatus status;
    private final EmployeeType employeeType;

    public Employee(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public Employee setStatus(EmployeeStatus status) {
        this.status = status;
        return this;
    }
}
