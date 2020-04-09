import java.util.LinkedList;
import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class Machine extends AbstractLoggingActor {
	private int maxItems;
	private int noProducers;
	private int noConsumers;
	private long timeConsumeItem;
	private int limitProduceItem;
	private LinkedList<ActorRef> producers = new LinkedList<ActorRef>();
	private LinkedList<ActorRef> consumers = new LinkedList<ActorRef>();
	private static LinkedList<String> listItems = new LinkedList<String>();

	public static class StartMachine {
	}

	public static class HasItemReady {
		private String itemName;

		public HasItemReady(String itemName) {
			this.itemName = itemName;
		}
	}

	public static class OutOfItem {

	}

	public static class ReadyToConsume {

	}

	public static Props props(int maxItems, int noProducers, int limitProduceItem, int noConsumers,
			long timeConsumeItem) {
		return Props.create(Machine.class,
				() -> new Machine(maxItems, noProducers, limitProduceItem, noConsumers, timeConsumeItem));
	}

	public Machine(int maxItems, int noProducers, int limitProduceItem, int noConsumers, long timeConsumeItem) {
		this.maxItems = maxItems;
		this.noProducers = noProducers;
		this.noConsumers = noConsumers;
		this.timeConsumeItem = timeConsumeItem;
		this.limitProduceItem = limitProduceItem;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(StartMachine.class, this::onStartMachine)
				.match(HasItemReady.class, this::onHasItemReady).match(ReadyToConsume.class, this::onReadyToConsume)
				.match(BusyConsuming.class, this::onBusyConsuming)
				.match(ReadyForNextItem.class, this::onReadyForNextItem).match(OutOfItem.class, this::onOutOfItem)
				.match(ReadyToStop.class, this::onReadyToStop).build();
	}

	private void onStartMachine(StartMachine msg) {
//		log().info("Starting the machine...");
		System.out.println("Starting the machine...");

		for (int i = 1; i <= noProducers; i++) {
			producers.add(getContext().actorOf(Producer.props("Producer " + i, limitProduceItem), "producer" + i));
		}

		for (int i = 1; i <= noConsumers; i++) {

			consumers.add(getContext().actorOf(Consumer.props("Consumer " + i, timeConsumeItem), "consumer" + i));
		}

		callRequestItem();
	}

	private void onHasItemReady(HasItemReady msg) {
		System.out.println("Machine received an item from " + sender());
		if (listItems.size() < maxItems) {
			listItems.add(msg.itemName);
			System.out.println("Added item " + msg.itemName);
			sender().tell(new Producer.AddedItem(), self());

			callItemReady();
		}
	}

	private void onReadyToConsume(ReadyToConsume msg) {
		if (listItems.size() != 0) {
			String item = listItems.remove(0);
			sender().tell(new Consumer.ConsumeItem(item), self());

		} 
//		else {
//			callRequestItem();
//		}
	}

	private void onOutOfItem(OutOfItem msg) {
		getContext().stop(sender());
		while (!sender().isTerminated()) {

		}
		producers.remove(sender());
		System.out.println("Stopping " + sender());
//		System.out.println(producers.size());
	}

	public static class BusyConsuming {

	}

	private void onBusyConsuming(BusyConsuming msg) {
		if (listItems.size() > 0) {
			sender().tell(new Consumer.ItemReady(), self());
		}
	}

	public static class ReadyForNextItem {

	}

	private void onReadyForNextItem(ReadyForNextItem msg) {
		if (listItems.size() != 0) {
			sender().tell(new Consumer.ItemReady(), self());
		} 
		else {
			if (producers.size() == 0) {
				sender().tell(new Consumer.GetReport(), self());
			} 
			else {
				callRequestItem();
			}
		}
	}

	private void callItemReady() {
		for (ActorRef consumer : consumers) {
			{
				consumer.tell(new Consumer.ItemReady(), self());
			}
		}
	}

	private void callRequestItem() {
		for (ActorRef producer : producers) {
			producer.tell(new Producer.RequestItem(), self());
		}
	}

	public static class ReadyToStop {

	}

	private void onReadyToStop(ReadyToStop msg) {
		getContext().stop(sender());

		while (!sender().isTerminated()) {

		}
		consumers.remove(sender());
		System.out.println("Stopping " + sender());

		if (consumers.size() == 0) {
			System.out.println("Stopping " + self());
			getContext().stop(self());
		}
	}
}
