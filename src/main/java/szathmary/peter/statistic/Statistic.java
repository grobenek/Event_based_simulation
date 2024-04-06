package szathmary.peter.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Created by petos on 19/03/2024. */
public abstract class Statistic {
  private static final double T_ALPHA_SCORE_FOR_95_PERCENT_CONFIDENCE_INTERVAL =
      1.96; // TODO porozmyslat ci tu nechat studentovo skore ci nie
  private static final double Z_SCORE_FOR_95_PERCENT_CONFIDENCE_INTERVAL = 1.96;
  private final String name;
  private long count = 0;
  protected double mean = Double.NEGATIVE_INFINITY;
  private double min = Double.POSITIVE_INFINITY;
  private double max = Double.NEGATIVE_INFINITY;
  private double sum = 0.0;
  private double sumOfSquaredObservation = 0.0;
  private double sampleStandardDeviation = Double.NEGATIVE_INFINITY;

  public Statistic(String name) {
    this.name = name;
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
        Math.sqrt(
            (sumOfSquaredObservation - (Math.pow(sum, 2) / count))
                / (count - 1));
  }

  protected abstract void updateMean(double observation);

  protected abstract void updateMean(double observation, double timestamp);

  public double getMean() {
    return mean;
  }
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

  public double[] get95PercentConfidenceInterval() {
    double score =
        count <= 30
            ? T_ALPHA_SCORE_FOR_95_PERCENT_CONFIDENCE_INTERVAL
            : Z_SCORE_FOR_95_PERCENT_CONFIDENCE_INTERVAL;
    double upperInterval =
        mean + ((sampleStandardDeviation * score) / Math.sqrt(count));
    double lowerInterval =
        mean - ((sampleStandardDeviation * score) / Math.sqrt(count));

    return new double[] {lowerInterval, upperInterval};
  }

  public void clear() {
    min = Double.POSITIVE_INFINITY;
    max = Double.NEGATIVE_INFINITY;
    sum = 0.0;
    count = 0;
  }

  @Override
  public String toString() {
    return "Statistic: "
        + name
        + " {"
        + "\n, count = "
        + count
        + "\n, min = "
        + min
        + "\n, max = "
        + max
        + "\n, sum = "
        + sum
        + "\n, mean = "
        + mean
        + "\n, Sample standard deviation = "
        + sampleStandardDeviation
        + "\n, 95% confidence interval = "
        + Arrays.toString(get95PercentConfidenceInterval())
        + "\n}\n";
  }
}
