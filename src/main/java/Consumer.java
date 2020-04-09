import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class Consumer extends AbstractLoggingActor {
	private String consumerName;
	private long timeConsume;
	private boolean isConsuming = false;
	private static int count = 0;
	
	public Consumer(String name, long timeConsume)
	{
		this.timeConsume = timeConsume;
		this.consumerName = name;
		isConsuming = false;
//		count = 0;
	}
	
	public static Props props(String name, long timeConsume)
	{
		return Props.create(Consumer.class, name, timeConsume);
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(ItemReady.class, this::onItemReady)
				.match(ConsumeItem.class, this::onConsumeItem)
				.match(GetReport.class, this::onGetReport)
				.build();
	}
	
	public static class ConsumeItem
	{
		private final String itemName;
		
		public ConsumeItem(String itemName)
		{
			this.itemName = itemName;
		}
	}
	
	public void onConsumeItem(ConsumeItem msg)
	{		
		System.out.println("Start consume");
		isConsuming = true;
		
		long startTime = System.currentTimeMillis();
		
		while (((System.currentTimeMillis() - startTime)/ 1_000) < timeConsume)
		{
			//waiting for consuming
		}
		
		System.out.println("\t" + consumerName + " - Consumed item " + msg.itemName);
		count++;
		isConsuming = false;
		sender().tell(new Machine.ReadyForNextItem(), self());
	}
	
	public static class ItemReady
	{
		
	}
	
	private void onItemReady(ItemReady msg)
	{
		if (!isConsuming)
		{
//			context().parent().tell(new Machine.ReadyToConsume(), self());
			sender().tell(new Machine.ReadyToConsume(), self());
		}
		else
		{
//			self().forward(new Consumer.ItemReady(), getContext());
			sender().tell(new Machine.BusyConsuming(), self());
			
		}
	}
	
	public static class GetReport
	{
		
	}
	
	private void onGetReport(GetReport msg)
	{
		System.out.println(consumerName + " consumed " + count);
		sender().tell(new Machine.ReadyToStop(), self());
	}
	
}
