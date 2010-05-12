/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.communication;


public class RedOpAddInt extends RedOp<Integer>
{
	public Integer doIt(Integer val1, Integer val2)
	{
		return val1 + val2;
	}
}