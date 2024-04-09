package szathmary.peter.simulation;

import java.util.PriorityQueue;
import szathmary.peter.event.Event;
import szathmary.peter.event.SleepEvent;

public abstract class SimulationCore {
  protected final PriorityQueue<Event> eventCalendar;
  private final long numberOfReplications;
  private volatile boolean verboseSimulation;
  private volatile boolean isRunning = true;
  private volatile boolean isStopped = false;
  private double currentTime;
  private volatile double sleepEventInterval = 0.01;
  private volatile long sleepEventDuration = 10;
  private long currentReplication = 1;

  public SimulationCore(long numberOfReplications, boolean verboseSimulation) {
    this.numberOfReplications = numberOfReplications;
    this.eventCalendar = new PriorityQueue<>();

    this.verboseSimulation = verboseSimulation;
    this.currentTime = 0;
  }

  public void startSimulation() {
    if (isStopped) {
      setStopped(false);
    } else {
      beforeReplications();
      for (int i = 0; i < numberOfReplications; i++) {
        if (!isRunning) {
          break;
        }
        beforeReplication();
        replication();
        afterReplication();
        currentReplication++;
      }
      afterReplications();
    }
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

  public SimulationCore setVerboseSimulation(boolean verboseSimulation) {
    this.verboseSimulation = verboseSimulation;

    if (verboseSimulation) {
      addEvent(new SleepEvent(getCurrentTime()));
    }

    return this;
  }

  public SimulationCore setStopped(boolean stopped) {
    isStopped = stopped;
    return this;
  }

  protected boolean getIsStopped() {
    return isStopped;
  }

  protected long getCurrentReplication() {
    return currentReplication;
  }

  public abstract void afterReplications();

  public abstract void afterReplication();

  public abstract void replication();

  public abstract void beforeReplication();

  public abstract void beforeReplications();
}
