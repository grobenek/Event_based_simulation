package szathmary.peter.simulation;

import java.util.List;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousEmpiricRandomGenerator;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousExponentialRandomGenerator;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousTriangularRandomGenerator;
import szathmary.peter.randomgenerators.continuousgenerators.ContinuousUniformGenerator;
import szathmary.peter.randomgenerators.discretegenerators.DiscreteEmpiricRandomGenerator;
import szathmary.peter.randomgenerators.discretegenerators.DiscreteUniformRandomGenerator;
import szathmary.peter.randomgenerators.empiricnumbergenerator.EmpiricOption;

/** Created by petos on 23/03/2024. */
public class ElectroShopSimulation extends SimulationCore {
  private final ContinuousExponentialRandomGenerator timeBetweenCustomerArrivalsRandomGenerator =
      new ContinuousExponentialRandomGenerator(0.5);
  private final DiscreteUniformRandomGenerator customerTypeGenerator =
      new DiscreteUniformRandomGenerator(0, 101);
  private final ContinuousUniformGenerator ticketPrintingTimeRandomGenerator =
      new ContinuousUniformGenerator(30, 180);
  private final ContinuousUniformGenerator
      timeToFinishOrderForCasualAndContractCustomersRandomGenerator =
          new ContinuousUniformGenerator(60, 900);
  private final ContinuousTriangularRandomGenerator
      timeForFinishOrderForOnlineCustomerRandomGenerator =
          new ContinuousTriangularRandomGenerator(1, 2, 8);
  private final DiscreteUniformRandomGenerator orderSizeRandomGenerator =
      new DiscreteUniformRandomGenerator(0, 101);
  private final ContinuousUniformGenerator timeForTakeBigOrderRandomGenerator =
      new ContinuousUniformGenerator(30, 70);
  private final DiscreteUniformRandomGenerator paymentTypeRandomGenerator =
      new DiscreteUniformRandomGenerator(0, 101);
  private final DiscreteEmpiricRandomGenerator paymentTimeRandomGenerator =
      new DiscreteEmpiricRandomGenerator(
          List.of(new EmpiricOption<>(180, 481, 0.4), new EmpiricOption<>(180, 361, 0.6)));
  private final DiscreteUniformRandomGenerator typeOfOrderRandomGenerator =
      new DiscreteUniformRandomGenerator(0, 101);
  private final ContinuousEmpiricRandomGenerator easyOrderTimeRandomGenerator =
      new ContinuousEmpiricRandomGenerator(
          List.of(new EmpiricOption<>(2.0, 5.0, 0.6), new EmpiricOption<>(5.0, 9.0, 0.3)));
  private final ContinuousUniformGenerator mediumOrderTimeRandomGenerator =
      new ContinuousUniformGenerator(9, 11);
  private final ContinuousEmpiricRandomGenerator hardOrderTimeRandomGenerator =
      new ContinuousEmpiricRandomGenerator(
          List.of(
              new EmpiricOption<>(11.0, 12.0, 0.1),
              new EmpiricOption<>(12.0, 20.0, 0.6),
              new EmpiricOption<>(20.0, 25.0, 0.3)));

  public ElectroShopSimulation(long numberOfReplications, boolean verboseSimulation) {
    super(numberOfReplications, verboseSimulation);
  }

  @Override
  public void afterReplications() {}

  @Override
  public void afterReplication() {}

  @Override
  public void replication() {}

  @Override
  public void beforeReplication() {}

  @Override
  public void beforeReplications() {}

  public ContinuousExponentialRandomGenerator getTimeBetweenCustomerArrivalsRandomGenerator() {
    return timeBetweenCustomerArrivalsRandomGenerator;
  }

  public DiscreteUniformRandomGenerator getCustomerTypeGenerator() {
    return customerTypeGenerator;
  }

  public ContinuousUniformGenerator getTicketPrintingTimeRandomGenerator() {
    return ticketPrintingTimeRandomGenerator;
  }

  public ContinuousUniformGenerator
      getTimeToFinishOrderForCasualAndContractCustomersRandomGenerator() {
    return timeToFinishOrderForCasualAndContractCustomersRandomGenerator;
  }

  public ContinuousTriangularRandomGenerator
      getTimeForFinishOrderForOnlineCustomerRandomGenerator() {
    return timeForFinishOrderForOnlineCustomerRandomGenerator;
  }

  public DiscreteUniformRandomGenerator getOrderSizeRandomGenerator() {
    return orderSizeRandomGenerator;
  }

  public ContinuousUniformGenerator getTimeForTakeBigOrderRandomGenerator() {
    return timeForTakeBigOrderRandomGenerator;
  }

  public DiscreteUniformRandomGenerator getPaymentTypeRandomGenerator() {
    return paymentTypeRandomGenerator;
  }

  public DiscreteEmpiricRandomGenerator getPaymentTimeRandomGenerator() {
    return paymentTimeRandomGenerator;
  }

  public DiscreteUniformRandomGenerator getTypeOfOrderRandomGenerator() {
    return typeOfOrderRandomGenerator;
  }

  public ContinuousEmpiricRandomGenerator getEasyOrderTimeRandomGenerator() {
    return easyOrderTimeRandomGenerator;
  }

  public ContinuousUniformGenerator getMediumOrderTimeRandomGenerator() {
    return mediumOrderTimeRandomGenerator;
  }

  public ContinuousEmpiricRandomGenerator getHardOrderTimeRandomGenerator() {
    return hardOrderTimeRandomGenerator;
  }
}
