package szathmary.peter.statistic;

/** Created by petos on 20/03/2024. */
public class DiscreteStatistic extends Statistic {

  public DiscreteStatistic(String name) {
    super(name);
  }

  @Override
  public void addObservation(double observation) {
    updateCount();
    updateSums(observation);
    updateMax(observation);
    updateMin(observation);
    updatesampleStandardDeviation();
    updateMean(observation);
  }

  @Override
  public void addObservation(double observation, double time) {
    throw new UnsupportedOperationException(
        "Cannot add observation with time in DiscreteStatistic!");
  }

  @Override
  protected void updateMean(double observation) {
    mean = getSum() / getCoutnOfObservations();
  }

  @Override
  protected void updateMean(double observation, double timestamp) {
    throw new UnsupportedOperationException("Cannot update observation with time in DiscreteStatistic!");
  }
}
