package szathmary.peter.statistic;

import szathmary.peter.simulation.ElectroShopSimulation;

/** Created by petos on 20/03/2024. */
public class ContinuousStatistic extends Statistic {
  private double weightedSum;
  private double timestampSum;

  public ContinuousStatistic(String name) {
    super(name);
    this.weightedSum = 0;
    this.timestampSum = 0;
  }

  @Override
  public void addObservation(double observation) {
    throw new UnsupportedOperationException(
        "Cannot add observation without time in ContinuousStatistic!");
  }

  @Override
  public void addObservation(double observation, double timestamp) {
    if (timestamp >= ElectroShopSimulation.CLOSING_HOURS_OF_TICKET_MACHINE) {
      return;
    }

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

  protected void updateMean(double observation, double timestamp) {
    weightedSum += observation * timestamp;
    timestampSum += timestamp;

    mean = weightedSum / timestampSum;
  }

  @Override
  public void clear() {
    super.clear();
  }
}
