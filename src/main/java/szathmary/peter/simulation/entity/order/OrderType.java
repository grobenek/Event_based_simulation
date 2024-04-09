package szathmary.peter.simulation.entity.order;

public enum OrderType {
  EASY(0.3),
  MEDIUM(0.4),
  HARD(0.3);

  private final double probability;

  OrderType(double probability) {
    this.probability = probability;
  }

  public double getProbability() {
    return probability;
  }
}
