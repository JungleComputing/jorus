/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.pixel;
 
 public abstract class Pixel<T> {
	/*** Private Properties *******************************************/

	protected int index;
	protected int extent;
	protected T data;

	/*** Public Methods ***********************************************/
	
	protected Pixel(int extent, T array) {
		this.extent = extent;
		data = array;
	}
	
	public Pixel(int extent) {
		index = 0;
		this.extent = extent;
		data = createDataArray(extent);
	}

	public int getExtent() {
		return extent;
	}

	public T getValue() {
		return data; 
	}
	
	protected abstract T createDataArray(int size);
}