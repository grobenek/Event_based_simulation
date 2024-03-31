package szathmary.peter.event;

import szathmary.peter.event.ticketmachine.RemoveCustomerFromTicketQueue;
import szathmary.peter.simulation.ElectroShopSimulation;
import szathmary.peter.simulation.SimulationCore;
import szathmary.peter.simulation.entity.customer.Customer;

/** Created by petos on 29/03/2024. */
public class CustomerArrivalInSystemEvent extends Event {
  public CustomerArrivalInSystemEvent(Double timestamp) {
    super(timestamp);
  }

  @Override
  public void execute(SimulationCore simulationCore) {
    ElectroShopSimulation electroShopSimulation = ((ElectroShopSimulation) simulationCore);

    double timeOfNextArrival = getTimestamp() + electroShopSimulation.getTimeBetweenCustomerArrivalsRandomGenerator().sample();

    // adding new customer arrival event
    if (timeOfNextArrival <= ElectroShopSimulation.CLOSING_HOURS_OF_TICKET_MACHINE) { //TODO chyba je, ze nepridavam zakaznikov po 17tej, ale mali by asi cakat v rade, ak tam su a ratat to do statistik.. Dalej, ak je vela zakaznikov po 17tej UZ v rade, stale ich pustim, aj ked by som nemal!!!!
      electroShopSimulation.addEvent(new CustomerArrivalInSystemEvent(timeOfNextArrival));
    }
    Customer arrivedCustomer = new Customer();
    arrivedCustomer.setTimeOfArrival(getTimestamp());

    electroShopSimulation.addCustomerToTicketQueue(arrivedCustomer);

    // if the ticket machine is giving ticket and is empty, add event to remove customer from the queue
    if ((!electroShopSimulation.isTicketMachineStopped()) && (!electroShopSimulation.isTicketMachineServingCustomer())) {
      electroShopSimulation.addEvent(new RemoveCustomerFromTicketQueue(getTimestamp()));
    }
  }

  @Override
  public String getEventDescription() {
    return String.format("Customer has arrived to the system at %f", getTimestamp());
  }
}
