import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Driver {

	public static void main(String[] args) {
		int maxItems = 10;
		int noProducers = 2;
		int limitProduceItem = 100;
		int noConsumers = 5;
		long timeConsumeItem = 1; //1 second
		
		long startTime = System.currentTimeMillis();
		
        ActorSystem system = ActorSystem.create("akka");
        final ActorRef machine = system.actorOf(Machine.props(maxItems, noProducers, limitProduceItem, noConsumers, timeConsumeItem), "machine");
        machine.tell(new Machine.StartMachine(), ActorRef.noSender());
        
        while(!machine.isTerminated())
        {
        	
        }
        
        system.terminate();
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("Run time: " + (endTime - startTime) / 1_000.0 + " seconds.");
	}

}
