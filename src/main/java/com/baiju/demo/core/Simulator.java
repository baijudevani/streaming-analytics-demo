package com.baiju.demo.core;

import java.util.stream.IntStream;

import com.baiju.demo.actors.AnalyzerAgent;
import com.baiju.demo.actors.RequestAgent;
import com.baiju.demo.actors.Router;
import com.demo.baiju.messages.Request;
import com.espertech.esper.client.Configuration;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

@Slf4j
public class Simulator {

	private final ActorSystem actorSystem = ActorSystem.create();
	private static final Configuration cepConfig = new Configuration();

	@SneakyThrows
	public void simulate() {
		log.info("starting simulation");
		configureCEP();
		createRequestAgents(cepConfig);
		createRouter(cepConfig);
		Thread.sleep(120000);
		log.info("shutting simulation");
		actorSystem.shutdown();
	}

	private void createRequestAgents(Configuration cepConfig) {

		String name;
		Props props;
		for (int i = 0; i < Properties.numRequestors; i++) {
			name = "requestor" + i;
			props = Props.create(RequestAgent.class, name,
					Properties.minSleepTime, Properties.maxSleepTime, i);
			actorSystem.actorOf(props, name);
		}
	}

	private void createRouter(Configuration cepConfig) {
		String name = "router";
		Props props = Props.create(Router.class, cepConfig);
		actorSystem.actorOf(props, name);
	}

	private void configureCEP() {
		cepConfig.addEventType("Request", Request.class.getName());
	}

}
