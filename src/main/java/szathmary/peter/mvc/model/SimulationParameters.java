package szathmary.peter.mvc.model;

public record SimulationParameters(
    long numberOfReplications,
    int numberOfServiceStations,
    int numberOfCashRegisters,
    boolean verboseSimulation) {
  @Override
  public String toString() {
    return "SimulationParameters{"
        + "numberOfReplications="
        + numberOfReplications
        + ", numberOfServiceStations="
        + numberOfServiceStations
        + ", numberOfCashRegisters="
        + numberOfCashRegisters
        + ", verboseSimulation="
        + verboseSimulation
        + '}';
  }
}
