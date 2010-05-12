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
	
	protected SideChannel() {
		sendPort = null;
	}

	protected void attachPort(SendPort sendPort) {
		this.sendPort = sendPort;
	}

	public void send(T message) throws IOException {
		if(sendPort == null) {
			throw new IOException("SideChannel not connected to a sendPort");
		}
		WriteMessage writeMessage = sendPort.newMessage();
		writeMessage.writeObject(message);
		writeMessage.finish();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void upcall(ReadMessage message) throws IOException,
			ClassNotFoundException {
		T payload = (T) message.readObject();
		message.finish();
		upcall(payload);
	}

	public abstract void upcall(T message) throws IOException;

}
