package szathmary.peter.simulation.entity.customer;

public enum CustomerType {
  CASUAL(0.5),
  CONTRACT(0.15),
  ONLINE(0.35);

  private final double typePercantage;

  CustomerType(double typePercantage) {
    this.typePercantage = typePercantage;
  }

  public double getTypeProbability() {
    return typePercantage;
  }
}
