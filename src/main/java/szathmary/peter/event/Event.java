package szathmary.peter.event;

import szathmary.peter.simulation.SimulationCore;

public abstract class Event implements Comparable<Event> {
  private final Double timestamp;

  public Event(Double timestamp) {
    this.timestamp = timestamp;
  }

  public abstract void execute(SimulationCore simulationCore);

  public Double getTimestamp() {
    return timestamp;
  }

  @Override
  public int compareTo(Event other) {
    if (this.timestamp.equals(other.timestamp)) {
      return 0;
    }

    if (other.timestamp.equals(0.0)) {
      return 1;
    }

    return Double.compare(this.timestamp, other.timestamp);
  }

  public abstract String getEventDescription();
}
