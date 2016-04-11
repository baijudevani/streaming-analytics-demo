package com.baiju.demo.actors;

import lombok.extern.slf4j.Slf4j;

import com.baiju.demo.core.Properties;
import com.demo.baiju.messages.Request;
import com.espertech.esper.client.Configuration;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Routes requests from request agents to AnalyzerAgents. The routing is based
 * on a simple hashing function that ensures event stream of a user is seen by
 * the same worker agent.
 * 
 * @author bdevani
 *
 */
@Slf4j
public class Router extends UntypedActor {

	// analyzer actors
	// TODO analyzer agents are children of router. This would have actor
	// selection implications & it also seems more logical for them to belong to
	// simulation. Change it!
	private ActorRef[] analyzers;
	private Configuration cepConfig;

	public Router(Configuration cepConfig) {
		this.cepConfig = cepConfig;
	}

	@Override
	public void preStart() {
		log.info("preparing analyzerAgents");
		createAnalyzerAgents();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Request) {
			getAnalyzer(((Request) message).getUserId()).tell(
					(Request) message, self());
		} else {
			unhandled(message);
		}

	}

	private ActorRef getAnalyzer(int id) {
		// TODO check for corner cases
		int hashKey = Properties.numRequestors / Properties.numAnalyzers;
		int actorIndex = id / hashKey;
		log.debug("id = " + id + " hashkey = " + hashKey + " actor index = "
				+ actorIndex);
		// TODO better exception handling e.g. indexBound errors
		return analyzers[actorIndex];
	}

	private void createAnalyzerAgents() {
		String name;
		Props props;
		analyzers = new ActorRef[Properties.numAnalyzers];
		for (int i = 0; i < Properties.numAnalyzers; i++) {
			name = "agent" + i;
			props = Props.create(AnalyzerAgent.class, cepConfig, name);
			analyzers[i] = getContext().actorOf(props, name);
		}
	}

}
