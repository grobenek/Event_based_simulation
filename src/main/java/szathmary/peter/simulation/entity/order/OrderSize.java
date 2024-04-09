package szathmary.peter.simulation.entity.order;

public enum OrderSize {
  SMALL(0.4),
  BIG(0.6);

  private final double probability;

  OrderSize(double probability) {
    this.probability = probability;
  }

  public double getProbability() {
    return probability;
  }
}
