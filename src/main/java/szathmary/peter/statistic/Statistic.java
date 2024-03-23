package szathmary.peter.statistic;

import java.util.ArrayList;
import java.util.List;

/** Created by petos on 19/03/2024. */
public abstract class Statistic {
  protected final List<Double> observations;
  private double min = Double.POSITIVE_INFINITY;
  private double max = Double.NEGATIVE_INFINITY;
  private double sum = 0.0;

  public Statistic() {
    this.observations = new ArrayList<>();
  }

  public abstract void addObservation(double observation);

  public abstract void addObservation(double observation, double time);

  protected void updateMax(double observation) {
    if (!(observation > getMax())) {
      return;
    }
    max = observation;
  }

  protected void updateMin(double observation) {
    if (!(observation < getMin())) {
      return;
    }
    min = observation;
  }

  protected void updateSum(double observation) {
    sum = getSum() + observation;
  }

  public abstract double getMean();

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  public int getCoutnOfObservations() {
    return observations.size();
  }

  public double getSum() {
    return sum;
  }

  public void clear() {
    min = Double.POSITIVE_INFINITY;
    max = Double.NEGATIVE_INFINITY;
    sum = 0.0;

    observations.clear();
  }

  @Override
  public String toString() {
    return "Statistic{"
        + "\n, count="
        + observations.size()
        + "\n, min="
        + min
        + "\n, max="
        + max
        + "\n, sum="
        + sum
        + "\n, mean= "
        + getMean()
        + "\n}\n";
  }
}
