package jorus.parallel.collectives;

import java.io.IOException;

import jorus.parallel.AllGather;
import jorus.parallel.PxSystem;

public class RingAllGather<T> extends AllGather<T>{

	public RingAllGather(PxSystem system, Class<T> c) throws Exception {
		super(system, c);
	}

	@Override
	public void allGather(T out, int offset, int length,
			T in, int[] offsets, int[] sizes) throws IOException {
		final int source = (rank + 1) % size; // receive from next node
		final int destination = (rank + size - 1) % size; // send to previous node
		
		int sendBuffer = rank; // send my data first
		int receiveBuffer = (sendBuffer + 1) % size; // receive data from next rank
		
		System.arraycopy(out, offset, in, offsets[rank], length);
		
		if(rank % 1 == 0) {
			// send first
			for(int i = 0; i < size - 1; i++) {
				comm.send(destination, in, offsets[sendBuffer], sizes[sendBuffer]);
				comm.receive(source, in, offsets[receiveBuffer], sizes[receiveBuffer]);
				
				sendBuffer = receiveBuffer; // next round, send what we received here
				receiveBuffer = (receiveBuffer + 1) % size; // next round, receive data of next node
			}
		} else {
			// receive first
			for(int i = 0; i < size - 1; i++) {
				comm.receive(source, in, offsets[receiveBuffer], sizes[receiveBuffer]);
				comm.send(destination, in, offsets[sendBuffer], sizes[sendBuffer]);
				
				sendBuffer = receiveBuffer; // next round, send what we received here
				receiveBuffer = (receiveBuffer + 1) % size; // next round, receive data of next node
			}
		}
		
	
		
	}

}
