package szathmary.peter.randomgenerators.discretegenerators;

import java.util.List;
import szathmary.peter.randomgenerators.RandomGenerator;
import szathmary.peter.randomgenerators.empiricnumbergenerator.EmpiricNumberGenerator;
import szathmary.peter.randomgenerators.empiricnumbergenerator.EmpiricOption;

/** Created by petos on 23/03/2024. */
public class DiscreteEmpiricRandomGenerator extends EmpiricNumberGenerator<Integer> {
  public DiscreteEmpiricRandomGenerator(long seed, List<EmpiricOption<Integer>> parameters) {
    super(seed, parameters);
  }

  public DiscreteEmpiricRandomGenerator(List<EmpiricOption<Integer>> parameters) {
    super(parameters);
  }

  @Override
  protected void initializeParameterGenerators() {
    parameters.forEach(
        parameter ->
            parametersGenerators.add(
                new DiscreteUniformRandomGenerator(parameter.min(), parameter.max())));
  }

  @Override
  public Integer sample() {
    double generatedProbabilityOfOption = randomGenerator.nextDouble();
    double comulativeProbability = 0;
    int selectedParameterIndex = -1;

    for (int i = 0; i < parameters.size(); i++) {
      EmpiricOption<Integer> parameter = parameters.get(i);
      comulativeProbability += parameter.probability();

      if (generatedProbabilityOfOption <= comulativeProbability) {
        selectedParameterIndex = i;
        break;
      }
    }

    RandomGenerator<Integer> generatorOfParameter =
        parametersGenerators.get(selectedParameterIndex);
    return generatorOfParameter.sample();
  }
}
