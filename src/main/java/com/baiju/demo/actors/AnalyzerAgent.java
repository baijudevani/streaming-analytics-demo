package com.baiju.demo.actors;

import lombok.val;
import lombok.extern.log4j.Log4j;
import akka.actor.UntypedActor;

import com.baiju.demo.core.Properties;
import com.baiju.demo.messages.Request;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Analyzer agents. Akka agents with embedded complex event processor. Also acts
 * as an event listener for CEP events. Events are processed as seen (processed
 * time).
 * 
 * @author bdevani
 *
 */

@Log4j
public class AnalyzerAgent extends UntypedActor implements UpdateListener {
	// CEP config
	private final Configuration cepConfig;
	private EPServiceProvider cep;
	private EPRuntime cepRuntime;

	private String name;

	public AnalyzerAgent(Configuration cepConfig, String name) {
		this.cepConfig = cepConfig;
		this.name = name;
	}

	@Override
	public void preStart() {
		// initiatize the CEP runtime engine.
		cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
		cepRuntime = cep.getEPRuntime();
		registerEPL();
	}

	/**
	 * register CEP expressions from properties.
	 */
	private void registerEPL() {
		log.info("agent " + name + " registering EPL");
		// create a per user stream
		cep.getEPAdministrator().createEPL(Properties.CREATE_CONTEXT);
		// Lists route to a picture3 action i.e. path analysis
		EPStatement statementClickPath = cep.getEPAdministrator().createEPL(
				Properties.USER_CLICK_PATH);
		// total views by user in the last 10 seconds.
		EPStatement statementViewsPerUser = cep.getEPAdministrator().createEPL(
				Properties.VIEWS_BY_USERS);
		statementClickPath.addListener(this);
		statementViewsPerUser.addListener(this);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Request) {
			log.debug("message received " + ((Request) message).toString());
			// send to CEP runtime.
			cepRuntime.sendEvent((Request) message);
		} else {
			unhandled(message);
		}

	}
	
	/**
	 * Event update listener. Simply logs data for now.
	 */
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (val newEvent : newEvents) {
			log.info(name + "-" + newEvent.getUnderlying());
		}
	}

}
