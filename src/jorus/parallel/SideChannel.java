/**
 * 
 */
package jorus.parallel;

import ibis.ipl.MessageUpcall;
import ibis.ipl.ReadMessage;
import ibis.ipl.SendPort;
import ibis.ipl.WriteMessage;

import java.io.IOException;

/**
 * @author Timo van Kessel
 * 
 */
public abstract class SideChannel<T> implements MessageUpcall {

	private SendPort sendPort;
	private boolean dummy;
	
	protected SideChannel() {
		sendPort = null;
		dummy = false;
	}

	void makeDummy() {
		dummy = true;
	}
	
	protected void attachPort(SendPort sendPort) {
		this.sendPort = sendPort;
	}

	public void send(T message) throws IOException {
		if(dummy) {
			return;
		}
		
		if(sendPort == null) {
			throw new IOException("SideChannel not connected to a sendPort");
		}
		
		long start = System.nanoTime();
		WriteMessage writeMessage = sendPort.newMessage();
		writeMessage.writeObject(message);
		writeMessage.finish();
		synchronized(this) {
			PxSystem.timeSideChannel += System.nanoTime() - start;
			PxSystem.dataOutSideChannel += writeMessage.bytesWritten();
			PxSystem.countSideChannel++;
		}	
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void upcall(ReadMessage message) throws IOException,
			ClassNotFoundException {
		long start = System.nanoTime();
		T payload = (T) message.readObject();
		message.finish();
		synchronized(this) {
			PxSystem.timeSideChannel += System.nanoTime() - start;
			PxSystem.dataInSideChannel += message.size();
			PxSystem.countSideChannel++;
		}
		upcall(payload);
	}

	public abstract void upcall(T message) throws IOException;

}
