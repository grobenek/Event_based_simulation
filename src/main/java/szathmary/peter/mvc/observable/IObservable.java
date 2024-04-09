package szathmary.peter.mvc.observable;

public interface IObservable {
  void attach(IObserver observer);

  void detach(IObserver observer);

  void sendNotifications();
}
