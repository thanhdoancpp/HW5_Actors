import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class DriverPrime {
	public static void main(String[] args) {
		int maxNum = 10;
//		boolean num[] = new boolean[maxNum];
		int countPrint = 0;
		
//		Arrays.fill(num, Boolean.TRUE);
		
		LinkedList<Integer> num = new LinkedList<Integer>(); 
		
		for (int i = 2; i < maxNum; i++) {
			num.add(i);
		}
		
		long startTime = System.currentTimeMillis();
		
		ActorSystem system = ActorSystem.create("Sample");
		final ActorRef start = system.actorOf(CheckPrimes.props(num), "start");
		start.tell(new CheckPrimes.Start(2), ActorRef.noSender());
		
		while(!start.isTerminated())
        {
        	
        }
        
        system.terminate();
        
        for(int i = 0; i < num.size(); i++)
        {
//        	if(num.get(i) == true)
//        	{
        		System.out.print(i + " ");
        		countPrint++;
//        	}

			if (countPrint == 10) {
				System.out.println();
				countPrint = 0;
			}
        }
		System.out.println();
		
        long endTime = System.currentTimeMillis();
        
        System.out.println("Run time: " + (endTime -startTime) / 1_000.0 + " seconds.");
	}
}
