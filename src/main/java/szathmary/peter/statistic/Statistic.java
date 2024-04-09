package szathmary.peter.statistic;

import szathmary.peter.util.TimeFormatter;

/** Created by petos on 19/03/2024. */
public abstract class Statistic {
  private static final double Z_SCORE_FOR_95_PERCENT_CONFIDENCE_INTERVAL = 1.96;
  private final String name;
  private final boolean formatAsTime;
  protected double mean = Double.NEGATIVE_INFINITY;
  private long count = 0;
  private double min = Double.POSITIVE_INFINITY;
  private double max = Double.NEGATIVE_INFINITY;
  private double sum = 0.0;
  private double sumOfSquaredObservation = 0.0;
  private double sampleStandardDeviation = Double.NEGATIVE_INFINITY;

  public Statistic(String name, boolean formatAsDigitalTime) {
    this.name = name;
    this.formatAsTime = formatAsDigitalTime;
  }

  public abstract void addObservation(double observation);

  public abstract void addObservation(double observation, double time);

  protected void updateCount() {
    count++;
  }

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

  protected void updateSums(double observation) {
    sum = sum + observation;
    sumOfSquaredObservation = sumOfSquaredObservation + Math.pow(observation, 2);
  }

  protected void updatesampleStandardDeviation() {
    sampleStandardDeviation =
        Math.sqrt((sumOfSquaredObservation - (Math.pow(sum, 2) / count)) / (count - 1));
  }

  protected abstract void updateMean(double observation);

  protected abstract void updateMean(double observation, double timestamp);

  public abstract double getMean();
  ;

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  public long getCoutnOfObservations() {
    return count;
  }

  public double getSum() {
    return sum;
  }

  public double[] getConfidenceInterval(double zAlphaScore) {
    if (Double.isInfinite(mean) || getCoutnOfObservations() < 30) {
      return new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
    }

    double value = (sampleStandardDeviation * zAlphaScore) / Math.sqrt(count);
    double upperInterval = mean + value;
    double lowerInterval = mean - value;

    return new double[] {lowerInterval, upperInterval};
  }

  public void clear() {
    min = Double.POSITIVE_INFINITY;
    max = Double.NEGATIVE_INFINITY;
    mean = Double.NEGATIVE_INFINITY;
    sum = 0.0;
    count = 0;
  }

  @Override
  public String toString() {
    double[] confidenceInterval = getConfidenceInterval(Z_SCORE_FOR_95_PERCENT_CONFIDENCE_INTERVAL);

    if (formatAsTime) {
      return "Statistic: %s {\n, count = %d\n, min = %s\n, max = %s\n, sum = %.02f seconds\n, mean = %s\n, Sample standard deviation = %.2f seconds\n, 95%% confidence interval = [%s, %s]\n}\n"
          .formatted(
              name,
              getCoutnOfObservations(),
              TimeFormatter.getFormattedTime(getMin()),
              TimeFormatter.getFormattedTime(getMax()),
              getSum(),
              TimeFormatter.getFormattedTime(getMean()),
              sampleStandardDeviation,
              TimeFormatter.getFormattedTime(confidenceInterval[0]),
              TimeFormatter.getFormattedTime(confidenceInterval[1]));
    } else {
      return "Statistic: %s {\n, count = %d\n, min = %.2f\n, max = %.2f\n, sum = %.2f\n, mean = %.2f\n, Sample standard deviation = %.2f\n, 95%% confidence interval = [%.2f, %.2f]\n}\n"
          .formatted(
              name,
              getCoutnOfObservations(),
              getMin(),
              getMax(),
              getSum(),
              getMean(),
              sampleStandardDeviation,
              confidenceInterval[0],
              confidenceInterval[1]);
    }
  }
}
