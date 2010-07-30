/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.communication;


public class RedOpAddFloat extends ReduceToRoot<Float>
{
	public Float doIt(Float val1, Float val2)
	{
		return val1 + val2;
	}
}
