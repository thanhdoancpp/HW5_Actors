import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

public class Producer extends AbstractLoggingActor {
	private String producerName;
	private int limitItems;
	private int count = 1;
	private boolean isAvailable = true;
	
	public Producer(String name, int limitItems)
	{
		this.producerName = name;
		this.limitItems = limitItems;
		count = 1;
		isAvailable = true;
	}
	
	public static Props props(String name, int limitItems)
	{
		return Props.create(Producer.class, name, limitItems);
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(RequestItem.class, this::onRequestItem)
				.match(AddedItem.class, this::onAddedItem)
				.build();
	}
	
	public static class RequestItem
	{
	}
	
	public void onRequestItem(RequestItem msg)
	{	
		if(isAvailable)
		{
			if(count <= limitItems)
			{
				sender().tell(new Machine.HasItemReady(producerName + " - " + count), self());
				isAvailable = false;
			}
			else
			{
				sender().tell(new Machine.OutOfItem(), self());
			}
		}
	}
	
	public static class AddedItem
	{
		
	}
	
	public void onAddedItem(AddedItem msg)
	{
		count++;
		if(count <= limitItems)
		{
			sender().tell(new Machine.ReadyForNextItem(), self());
			isAvailable = true;
		}
		else
		{
			sender().tell(new Machine.OutOfItem(), self());
		}
	}
}
