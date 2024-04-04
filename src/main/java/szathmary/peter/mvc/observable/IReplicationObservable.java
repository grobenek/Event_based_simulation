package szathmary.peter.mvc.observable;

import szathmary.peter.mvc.model.SimulationOverview;

public interface IReplicationObservable extends IObservable {
  SimulationOverview getSimulationOverview();
}
