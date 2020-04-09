import java.util.ArrayList;
import java.util.LinkedList;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class CheckPrimes extends AbstractLoggingActor {
//		private int startNum; 
	private LinkedList<Integer> numArr;

	public CheckPrimes(LinkedList<Integer> numArr) {
		this.numArr = numArr;
	}

	public static Props props(LinkedList<Integer> newArr) {
		return Props.create(CheckPrimes.class, () -> new CheckPrimes(newArr));
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Start.class, this::onProcess).match(Stop.class, this::onStop).build();
	}

	public static class Start {
		int startNum;

		public Start(int startNum) {
			this.startNum = startNum;
		}
	}

	private void onProcess(Start msg) {
		int prime = msg.startNum;
		System.out.println(prime);
		for (int i = 0; i < numArr.size(); i++) {
			if (i % prime == 0) {
				numArr.remove(i);
			}
		}

//		int newPrime = prime;
//
//		for (int i = prime + 1; i < numArr.length; i++) {
//
//			if (numArr[i] == true) {
//				newPrime = i;
//				break;
//			}
//		}
//		int newPrime = numArr.get(0);
		if (numArr.size() != 0) {
			int newPrime = numArr.get(0);
			ActorRef nextPrime = context().actorOf(CheckPrimes.props(numArr));
			nextPrime.tell(new CheckPrimes.Start(newPrime), self());
		} else {
			self().tell(new CheckPrimes.Stop(), self());
		}
	}

	public static class Stop {

	}

	private void onStop(Stop msg) {
		getContext().parent().tell(new CheckPrimes.Stop(), self());
		getContext().stop(self());
	}
}
