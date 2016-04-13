package com.baiju.demo;

import org.apache.log4j.BasicConfigurator;

import com.baiju.demo.core.Simulator;

/**
 * Main method for demo
 * @author bdevani
 *
 */
public class DemoMain {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Simulator simulator = new Simulator();
		simulator.simulate();
	}
		
}
