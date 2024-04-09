package szathmary.peter.statistic;

import java.util.ArrayList;
import java.util.List;

/** Created by petos on 20/03/2024. */
public class ContinuousStatistic extends Statistic {
  private final List<Double> observations;
  private final List<Double> timestamps;

  public ContinuousStatistic(String name, boolean formatAsTime) {
    super(name, formatAsTime);

    this.observations = new ArrayList<>();
    this.timestamps = new ArrayList<>();
  }

  @Override
  public void addObservation(double observation) {
    throw new UnsupportedOperationException(
        "Cannot add observation without time in ContinuousStatistic!");
  }

  @Override
  public void addObservation(double observation, double timestamp) {
    observations.add(observation);
    timestamps.add(timestamp);

    updateCount();
    updateSums(observation);
    updateMin(observation);
    updateMax(observation);
    updatesampleStandardDeviation();
    updateMean(observation, timestamp);
  }

  @Override
  protected void updateMean(double observation) {
    throw new UnsupportedOperationException(
        "Cannot update mean without time in ContinuousStatistic!");
  }

  protected void updateMean(double observation, double timestamp) {}

  @Override
  public double getMean() {
    if (timestamps.size() == 1 || timestamps.isEmpty()) {
      return 0.0;
    }

    double sum = 0.0;

    for (int i = 0; i < timestamps.size() - 1; i++) {
      double current = timestamps.get(i);
      double next = timestamps.get(i + 1);

      sum += (next - current) * observations.get(i);
    }

    mean = sum / timestamps.getLast();
    return mean;
  }

  @Override
  public void clear() {
    super.clear();

    observations.clear();
    timestamps.clear();
  }
}
