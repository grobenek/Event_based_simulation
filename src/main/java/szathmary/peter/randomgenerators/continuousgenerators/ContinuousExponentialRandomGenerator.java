package szathmary.peter.randomgenerators.continuousgenerators;

import szathmary.peter.randomgenerators.RandomGenerator;

/** Created by petos on 19/03/2024. */
public class ContinuousExponentialRandomGenerator extends RandomGenerator<Double> {
  private final double lambda;

  public ContinuousExponentialRandomGenerator(long seed, double lambda) {
    super(seed);
    this.lambda = lambda;
  }

  public ContinuousExponentialRandomGenerator(double lambda) {
    this.lambda = lambda;
  }

  @Override
  public Double sample() {
    return Math.log(1 - randomGenerator.nextDouble()) / (-lambda);
  }
}
