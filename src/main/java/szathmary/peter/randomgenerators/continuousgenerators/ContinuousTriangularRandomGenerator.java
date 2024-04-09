package szathmary.peter.randomgenerators.continuousgenerators;

import szathmary.peter.randomgenerators.RandomGenerator;

/** Created by petos on 21/03/2024. */
public class ContinuousTriangularRandomGenerator extends RandomGenerator<Double> {
  private final double min;
  private final double modus;
  private final double max;

  public ContinuousTriangularRandomGenerator(long seed, double min, double modus, double max) {
    super(seed);

    checkParameters(min, modus, max);

    this.min = min;
    this.modus = modus;
    this.max = max;
  }

  public ContinuousTriangularRandomGenerator(double min, double modus, double max) {
    checkParameters(min, modus, max);

    this.min = min;
    this.modus = modus;
    this.max = max;
  }

  private void checkParameters(double min, double modus, double max) {
    if (min < 0) {
      throw new IllegalArgumentException("Minimum must be positive value!");
    }

    if (max < 0) {
      throw new IllegalArgumentException("Maximum must be positive value!");
    }

    if (modus < min || modus > max) {
      throw new IllegalArgumentException("Modus must be between min and max!");
    }
  }

  @Override
  public Double sample() {
    double randomNumber = randomGenerator.nextDouble();
    double limit = (modus - min) / (max - min);

    if (limit > randomNumber) {
      return min + Math.sqrt(randomNumber * (max - min) * (modus - min));
    } else {
      return max - Math.sqrt((1 - randomNumber) * (max - min) * (max - modus));
    }
  }
}
