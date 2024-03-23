package szathmary.peter.simulation;

import java.util.PriorityQueue;
import szathmary.peter.event.Event;

public abstract class SimulationCore {
  protected final PriorityQueue<Event> eventCalendar;
  private final long numberOfReplications;
  private final boolean verboseSimulation;
  private volatile boolean isRunning = true;
  private double currentTime;
  private volatile double sleepEventInterval = 100;
  private volatile long sleepEventDuration = 1000;

  public SimulationCore(long numberOfReplications, boolean verboseSimulation) {
    this.numberOfReplications = numberOfReplications;
    this.eventCalendar = new PriorityQueue<>();

    this.verboseSimulation = verboseSimulation;
    this.currentTime = 0;
  }

  public void startSimulation() {
    beforeReplications();
    for (int i = 0; i < numberOfReplications; i++) {
      if (!isRunning) {
        break;
      }
      beforeReplication();
      replication();
      afterReplication();
    }
    afterReplications();
  }

  protected void simulateEvent() {
    Event currentEvent = eventCalendar.poll();

    if (currentEvent == null) {
      throw new IllegalStateException("Event calendar is empty!");
    }

    double timeOfEvent = currentEvent.getTimestamp();

    if (timeOfEvent < currentTime) {
      throw new IllegalStateException(
          String.format(
              "Time going backwards! Current time: %f, new event time: %f",
              currentTime, timeOfEvent));
    }

    currentTime = timeOfEvent;

    if (isVerbose()) {
      System.out.println(currentEvent.getEventDescription());
    }

    currentEvent.execute(this);
  }

  protected void resetCurrentTime() {
    currentTime = 0;
  }

  public void addEvent(Event event) {
    eventCalendar.add(event);
  }

  public boolean isEventCalendarEmpty() {
    return eventCalendar.isEmpty();
  }

  public void setRunning(boolean running) {
    isRunning = running;
  }

  public double getCurrentTime() {
    return currentTime;
  }

  public boolean isVerbose() {
    return verboseSimulation;
  }

  public double getSleepEventInterval() {
    return sleepEventInterval;
  }

  public SimulationCore setSleepEventInterval(double sleepEventInterval) {
    this.sleepEventInterval = sleepEventInterval;
    return this;
  }

  public long getSleepEventDuration() {
    return sleepEventDuration;
  }

  public SimulationCore setSleepEventDuration(long sleepEventDuration) {
    this.sleepEventDuration = sleepEventDuration;
    return this;
  }

  public abstract void afterReplications();

  public abstract void afterReplication();

  public abstract void replication();

  public abstract void beforeReplication();

  public abstract void beforeReplications();
}
