package com.baiju.demo.messages;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Request {
	
	private String action;
	private int pictureId;
	private String userName;
	private int userId;
	private DateTime date;
	

}
