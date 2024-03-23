package szathmary.peter.statistic;

/** Created by petos on 20/03/2024. */
public class DiscreteStatistic extends Statistic {

  @Override
  public void addObservation(double observation) {
    updateSum(observation);
    updateMax(observation);
    updateMin(observation);

    observations.add(observation);
  }

  @Override
  public void addObservation(double observation, double time) {
    throw new UnsupportedOperationException(
        "Cannot add observation with time in DiscreteStatistic!");
  }

  @Override
  public double getMean() {
    return getSum() / getCoutnOfObservations();
  }
}
