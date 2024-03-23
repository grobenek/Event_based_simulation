package szathmary.peter.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/** Created by petos on 20/03/2024. */
public class ContinuousStatistic extends Statistic {
  private final List<Double> timestampsOfObservations;

  public ContinuousStatistic() {
    super();
    this.timestampsOfObservations = new ArrayList<>(100);
  }

  @Override
  public void addObservation(double observation) {
    throw new UnsupportedOperationException(
        "Cannot add observation without time in ContinuousStatistic!");
  }

  @Override
  public void addObservation(double observation, double time) {
    updateSum(observation);
    updateMin(observation);
    updateMax(observation);

    observations.add(observation);
    timestampsOfObservations.add(time);
  }

  @Override
  public double getMean() {
    if (observations.isEmpty()) {
      throw new IllegalStateException("Cannot calculate mean from empty observations!");
    }

    double weightedSum =
        IntStream.range(0, timestampsOfObservations.size())
            .mapToDouble(index -> observations.get(index) * timestampsOfObservations.get(index))
            .sum();

    double timestampSum = timestampsOfObservations.stream().mapToDouble(time -> time).sum();

    return weightedSum / timestampSum;
  }

  @Override
  public void clear() {
    super.clear();

    timestampsOfObservations.clear();
  }
}
