package com.baiju.demo.actors;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.baiju.demo.core.Properties;
import com.baiju.demo.messages.Request;
import com.sun.xml.internal.ws.api.pipe.NextAction;

import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;
import akka.actor.UntypedActor;

/**
 * A request agent that generates random requests containing an action (like,unlike) on a resource (picture)
 * @author bdevani
 *
 */

@Slf4j
public class RequestAgent extends UntypedActor {

	final Random random = new Random();
	int lastPicId;
	String name;
	int id;
	int minSleep; //millis
	int maxSleep; //millis
	
	
	public RequestAgent(String name,int minSleep,int maxSleep, int id) {
		this.minSleep = minSleep;
		this.maxSleep = maxSleep;
		this.name = name;
		this.id = id;
		this.lastPicId = -1; // indicates no value
	}

	@Override
	public void preStart() {
		getContext()
				.system()
				.scheduler()
				.scheduleOnce(Duration.create(500, TimeUnit.MILLISECONDS),
						getSelf(), "tick", getContext().dispatcher(), null);
	}

	// override postRestart so we don't call preStart and schedule a new message
	@Override
	public void postRestart(Throwable reason) {
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message.equals("tick")) {
			// send another periodic tick after the specified delay
			getContext()
					.system()
					.scheduler()
					.scheduleOnce(Duration.create(randomInt(minSleep,maxSleep), TimeUnit.MILLISECONDS),
							getSelf(), "tick", getContext().dispatcher(), null);
			onReceiveTick();
		} else {
			unhandled(message);
		}

	}

	private void onReceiveTick() {
		// generate and publish request
		val request = generateRequest();
		this.lastPicId = request.getPictureId();
		publish(request);
	}
	
	/**
	 * A random actio on a resource
	 * @return
	 */
	private Request generateRequest() {
		String action = Properties.actions[random.nextInt(2)];
		int picId = randomInt(0,Properties.totalPics);
		return new Request(action,picId,this.name,this.id,DateTime.now());
	}

	/**
	 * Send to action router
	 * @param request
	 */
	private void publish(Request request) {
		log.debug("request sent");
		context().actorSelection("/user/router")
				.tell(request, getSelf());
		;
	}

	/**
	 * Return a random number in range {@code [min, max]}
	 */
	protected int randomInt(int min, int max) {
		return min + random.nextInt(max - min) + 1;
	}

}
