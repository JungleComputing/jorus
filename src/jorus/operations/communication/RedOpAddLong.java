/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.communication;


public class RedOpAddLong extends RedOp<Long>
{
	public Long doIt(Long val1, Long val2)
	{
		return val1 + val2;
	}
}
