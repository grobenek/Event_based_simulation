package szathmary.peter.simulation.entity.employee;

/** Created by petos on 29/03/2024. */
public class Employee {
    private final EmployeeType employeeType;
    private EmployeeStatus status;

    public Employee(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public Employee setStatus(EmployeeStatus status) {
        this.status = status;
        return this;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }
}
