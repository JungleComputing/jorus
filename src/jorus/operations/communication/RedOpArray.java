/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.communication;


public abstract class RedOpArray<T>
{
	public abstract void doIt(T target, T src);
	public abstract void doItRange(T target, T src, int startIndex, int length);
}
