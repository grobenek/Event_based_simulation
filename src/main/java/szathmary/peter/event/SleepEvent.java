package szathmary.peter.event;

import szathmary.peter.simulation.SimulationCore;

/** Created by petos on 23/03/2024. */
public class SleepEvent extends Event {

  public SleepEvent(Double timestamp) {
    super(timestamp);
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    if (!simulationCore.isVerbose()) {
      return;
    }

    try {
      Thread.sleep(simulationCore.getSleepEventDuration());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    if (simulationCore.isEventCalendarEmpty()) {
      return;
    }

    if (simulationCore.isVerbose()) {
      simulationCore.addEvent(
          new SleepEvent(getTimestamp() + simulationCore.getSleepEventInterval()));
    }
  }

  @Override
  public String getEventDescription() {
    return "";
  }
}
