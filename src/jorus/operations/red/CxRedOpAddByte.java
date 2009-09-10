/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.red;

import java.lang.Integer;

import jorus.operations.CxRedOp;


public class CxRedOpAddByte extends CxRedOp<Byte>
{
	@Override
	public Byte doIt(Byte val1, Byte val2)
	{
		Integer iVal = new Integer(val1.intValue() + val2.intValue());
		return iVal.byteValue();
	}
}
