package com.baiju.demo.core;

import com.espertech.esper.client.EPStatement;

/**
 * Application properties. 
 * TODO put this in a properties config file. Inject
 * into classes.
 * 
 * @author bdevani
 *
 */
public class Properties {

	public static final int numAnalyzers = 10;
	public static final int numRequestors = 1000;
	public static final int totalPics = 10;
	public static final int minSleepTime = 100; // milliseconds
	public static final int maxSleepTime = 1000; // milliseconds
	public static final String[] actions = { "like", "unlike" };

	// EPL expressions
	public static final String CREATE_CONTEXT = "CREATE CONTEXT "
			+ "SegmentedByUser " + "PARTITION BY " + "userId " + "FROM "
			+ "Request";

	public static final String VIEWS_BY_USERS = "SELECT userId,pictureId,count(*) as totalViews "
			+ "FROM Request.win:time(10 sec) " + "GROUP BY userId,pictureId";

	public static final String USER_CLICK_PATH = "SELECT * FROM Request.win:time(30 sec) "
			+ "MATCH_RECOGNIZE ( "
				+ "PARTITION BY userId "
				+ "MEASURES A.pictureId as picIdStart,B.pictureId as picIdMid,C.pictureId as picIdEnd "
				+ "PATTERN (A B C) "
				+ "DEFINE C as C.pictureId=3"
				+ ")";

	// EPStatement statement = cep.getEPAdministrator().createEPL(
	// "CONTEXT SegmentedByUser " +
	// "select * from pattern [every a=Request(pictureId=1) -> b=Request(pictureId=3).win:time(10 sec)]"
	// );

}
