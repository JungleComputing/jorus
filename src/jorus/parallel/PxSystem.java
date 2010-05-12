/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra  (fjseins@cs.vu.nl)
 *
 */

package jorus.parallel;

import ibis.ipl.Ibis;
import ibis.ipl.IbisCapabilities;
import ibis.ipl.IbisFactory;
import ibis.ipl.IbisIdentifier;
import ibis.ipl.PortType;
import ibis.ipl.ReadMessage;
import ibis.ipl.ReceivePort;
import ibis.ipl.SendPort;
import ibis.ipl.WriteMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jorus.array.Array2d;
import jorus.array.Array2dDoubles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PxSystem {
	private static final Logger logger = LoggerFactory
			.getLogger(PxSystem.class);

	/** * Ibis Capabilities & PortTypes ******************************* */

	private static final boolean CONNECT_DYNAMIC = true;

	private static final PortType portType = new PortType(
			PortType.COMMUNICATION_RELIABLE, PortType.SERIALIZATION_DATA,
			PortType.RECEIVE_EXPLICIT, PortType.CONNECTION_ONE_TO_ONE);

	protected static final PortType sideChannelPortType = new PortType(
			PortType.COMMUNICATION_RELIABLE, PortType.SERIALIZATION_OBJECT,
			PortType.RECEIVE_AUTO_UPCALLS, PortType.CONNECTION_ONE_TO_MANY);

	private static final IbisCapabilities ibisCapabilities = new IbisCapabilities(
			IbisCapabilities.ELECTIONS_STRICT, IbisCapabilities.CLOSED_WORLD);

	private static final String COMM_ID = "px_comm";

	private final Ibis ibis;

	private final IbisIdentifier[] world;

	private final int nrCPUs;

	// private final int logCPUs;

	// private final int maxCPUs;

	private final int myCPU;
	private final boolean iAmRoot;

	private final SendPort[] sps;

	private final ReceivePort[] rps;

	private final Collectives<byte[]> byteCollectives;
	private final Collectives<short[]> shortCollectives;
	private final Collectives<int[]> intCollectives;
	private final Collectives<long[]> longCollectives;
	private final Collectives<float[]> floatCollectives;
	private final Collectives<double[]> doubleCollectives;

	private final Barrier<?> barrier;

	private static PxSystem system;

	private static long timeBarrier;
	private static long countBarrier;

	private static long timeReduceToRoot;
	private static long countReduceToRoot;
	private static long dataInReduceToRoot;
	private static long dataOutReduceToRoot;

	private static long timeReduceToAll;
	private static long countReduceToAll;
	private static long dataInReduceToAll;
	private static long dataOutReduceToAll;

	private static long timeScatter;
	private static long countScatter;
	private static long dataInScatter;
	private static long dataOutScatter;

	private static long timeGather;
	private static long countGather;
	private static long dataInGather;
	private static long dataOutGather;

	private static long timeBroadcast;
	private static long countBroadcast;
	private static long dataInBroadcast;
	private static long dataOutBroadcast;

	private static long timeBroadcastValue;
	private static long countBroadcastValue;

	private static long timeBorderExchange;
	private static long countBorderExchange;
	private static long dataInBorderExchange;
	private static long dataOutBorderExchange;

	public static PxSystem init(String name, String size) throws Exception {

		if (system != null) {
			throw new Exception("PxSystem already initialized");
		}

		system = new PxSystem(name, size);

		return system;
	}

	public static boolean initialized() {
		return (system != null);
	}

	public static PxSystem get() {
		return system;
	}

	private PxSystem(String name, String size) throws Exception {

		Properties props = new Properties();
		props.setProperty("ibis.pool.name", name);
		props.setProperty("ibis.pool.size", size);

		String internalImpl = System.getProperty("PxSystem.impl");

		if (internalImpl != null) {
			if (logger.isInfoEnabled()) {
				logger.info("PxSystem using implementation: " + internalImpl);
			}
			props.setProperty("ibis.implementation", internalImpl);
		}

		// Create Ibis & obtain parallel environment parameters (local)

		ibis = IbisFactory.createIbis(ibisCapabilities, props, true, null,
				portType, sideChannelPortType); // support the side channel
		nrCPUs = ibis.registry().getPoolSize();
		myCPU = (int) ibis.registry().getSequenceNumber("counter");

		iAmRoot = myCPU == 0;

		int tmpLog = (int) (Math.log((double) nrCPUs) / Math.log(2.0));
		int tmpMax = (int) Math.pow(2, tmpLog);

		if (tmpMax < nrCPUs) {
			tmpLog++;
			tmpMax *= 2;
		}

		// logCPUs = tmpLog;
		// maxCPUs = tmpMax;

		// Let each node elect itself as the Ibis with 'myCPU' as rank.
		// Then, obtain Ibis identifiers for all CPUs.

		ibis.registry().elect(Integer.toString(myCPU));
		world = new IbisIdentifier[nrCPUs];

		for (int i = 0; i < nrCPUs; i++) {
			String rank = Integer.toString(i);
			world[i] = ibis.registry().getElectionResult(rank);
		}

		// Initialize Send/ReceivePorts to/from all participants
		sps = new SendPort[nrCPUs];
		rps = new ReceivePort[nrCPUs];

		// Added -- J.
		//
		// Init all send and receive ports here. This will give
		// us an all-to-all setup which is more than we need and
		// doesn't scale. However, since the application does not
		// seem to scale anyway we don't really care about this.

		if (logger.isInfoEnabled()) {
			logger.info("I am " + myCPU + " of " + nrCPUs);
		}

		if (!CONNECT_DYNAMIC) {
			long time = System.currentTimeMillis();

			for (int i = 0; i < nrCPUs; i++) {
				if (i != myCPU) {
					rps[i] = ibis.createReceivePort(portType, COMM_ID + i);
					rps[i].enableConnections();
				}
			}

			if (iAmRoot) {

				// Connect to all
				for (int i = 1; i < nrCPUs; i++) {
					if (logger.isInfoEnabled()) {
						logger.info("Connecting to " + i);
					}

					// Connect to target
					sps[i] = ibis.createSendPort(portType);
					sps[i].connect(world[i], COMM_ID + myCPU);
				}

				// One by one send message to all nodes, to indicating that it
				// is
				// their turn....
				for (int i = 1; i < nrCPUs; i++) {
					WriteMessage wm = sps[i].newMessage();
					wm.writeInt(42);
					wm.finish();

					// Wait for the connection to come in from the current node.
					// This is the signal that the node is done...

					// Wait for connection from target
					if (logger.isInfoEnabled()) {
						logger.info("Waiting for connection from " + i);
					}

					while (rps[i].connectedTo().length == 0) {
						try {
							Thread.sleep(500);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}

			} else {

				// Wait for connection from all machines lower in rank
				for (int i = 0; i < myCPU; i++) {

					if (logger.isInfoEnabled()) {
						logger.info("Waiting for connection from " + i);
					}

					while (rps[i].connectedTo().length == 0) {
						try {
							Thread.sleep(500);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}

				// Wait for message from 0 that indicates that it is my turn
				ReadMessage r = rps[0].receive();
				int tmp = r.readInt();
				r.finish();

				// Connect to all expect 0 and myself
				for (int i = 1; i < nrCPUs; i++) {

					if (i != myCPU) {
						if (logger.isInfoEnabled()) {
							logger.info("Connecting to " + i);
						}

						// Connect to target
						sps[i] = ibis.createSendPort(portType);
						sps[i].connect(world[i], COMM_ID + myCPU);
					}
				}

				// Connect to 0 last
				if (logger.isInfoEnabled()) {
					logger.info("Connecting to 0");
				}

				sps[0] = ibis.createSendPort(portType);
				sps[0].connect(world[0], COMM_ID + myCPU);

				// Wait for connection from all machines higher in rank
				for (int i = myCPU + 1; i < nrCPUs; i++) {

					if (logger.isInfoEnabled()) {
						logger.info("Waiting for connection from " + i);
					}

					while (rps[i].connectedTo().length == 0) {
						try {
							Thread.sleep(500);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}

			if (logger.isInfoEnabled()) {
				long t2 = System.currentTimeMillis();
				logger.info("Connection setup took + " + (t2 - time));
			}

		}

		// Init collectives

		Properties p = System.getProperties();

		String type = p.getProperty("jorus.barrier", "Flat");
		String clazz = "jorus.parallel.collectives." + type + "Barrier";

		if (logger.isDebugEnabled()) {
			logger.debug("Loading " + type + " barrier");
		}

		barrier = (Barrier<?>) Collective.loadImplementation(clazz, this,
				byte[].class);

		byteCollectives = new Collectives<byte[]>(p, this, byte[].class);
		shortCollectives = new Collectives<short[]>(p, this, short[].class);
		intCollectives = new Collectives<int[]>(p, this, int[].class);
		longCollectives = new Collectives<long[]>(p, this, long[].class);
		floatCollectives = new Collectives<float[]>(p, this, float[].class);
		doubleCollectives = new Collectives<double[]>(p, this, double[].class);
	}

	/** * Public Methods ********************************************** */

	private double getThroughput(long data, long nanos) {

		double Mbits = (data * 8.0) / 1000000.0;
		double sec = nanos / (1000.0 * 1000.0 * 1000.0);

		return (Mbits / sec);
	}

	public void printStatistics() {
		long totalTime = timeBarrier /* + timeReduceValueToRoot0FT */
				+ timeReduceToRoot + timeReduceToAll + timeScatter + timeGather
				+ timeBroadcast + timeBroadcastValue + timeBorderExchange;

		long totalCount = countBarrier /* + countReduceValueToRoot0FT */
				+ countReduceToRoot + countReduceToAll + countScatter
				+ countGather + countBroadcast + countBroadcastValue
				+ countBorderExchange;

		if (totalCount > 0)
			System.out.printf("Total communication time %.2f usec, count %d\n",
					(totalTime / 1000.0), totalCount);
		if (countBarrier > 0)
			System.out.printf("            barrier time %.2f usec, count %d\n",
					(timeBarrier / 1000.0), countBarrier);
		if (countBroadcastValue > 0)
			System.out.printf("     broadcastValue time %.2f usec, count %d\n",
					(timeBroadcastValue / 1000.0), countBroadcastValue);
		// System.out.printf("          reduceV2R time %.2f usec, count %d\n",
		// (timeReduceValueToRoot0FT / 1000.0), countReduceValueToRoot0FT);
		if (countReduceToRoot > 0)
			System.out
					.printf(
							"       reduceToRoot time %.2f usec, count %d, dataIn %d bytes, dataOut %d bytes, TP %.2f Mbit/s\n",
							(timeReduceToRoot / 1000.0), countReduceToRoot,
							dataInReduceToRoot, dataOutReduceToRoot,
							getThroughput(dataInReduceToRoot
									+ dataOutReduceToRoot, timeReduceToRoot));

		if (countReduceToAll > 0)
			System.out
					.printf(
							"       reduceToAll time %.2f usec, count %d, dataIn %d bytes, dataOut %d bytes, TP %.2f Mbit/s\n",
							(timeReduceToAll / 1000.0), countReduceToAll,
							dataInReduceToAll, dataOutReduceToAll,
							getThroughput(dataInReduceToAll
									+ dataOutReduceToAll, timeReduceToAll));

		if (countScatter > 0)
			System.out
					.printf(
							"            scatter time %.2f usec, count %d, dataIn %d bytes, dataOut %d bytes, TP %.2f Mbit/s\n",
							(timeScatter / 1000.0), countScatter,
							dataInScatter, dataOutScatter,
							getThroughput(dataInScatter + dataOutScatter,
									timeScatter));

		if (countGather > 0)
			System.out
					.printf(
							"             gather time %.2f usec, count %d, dataIn %d bytes, dataOut %d bytes, TP %.2f Mbit/s\n",
							(timeGather / 1000.0), countGather, dataInGather,
							dataOutGather, getThroughput(dataInGather
									+ dataOutGather, timeGather));

		if (countBroadcast > 0)
			System.out
					.printf(
							"       broadcastSBT time %.2f usec, count %d, dataIn %d bytes, dataOut %d bytes, TP %.2f Mbit/s\n",
							(timeBroadcast / 1000.0), countBroadcast,
							dataInBroadcast, dataOutBroadcast, getThroughput(
									dataInBroadcast + dataOutBroadcast,
									timeBroadcast));

		if (countBorderExchange > 0)
			System.out
					.printf(
							"     borderExchange time %.2f usec, count %d, dataIn %d bytes, dataOut %d bytes, TP %.2f Mbit/s\n",
							(timeBorderExchange / 1000.0),
							countBorderExchange,
							dataInBorderExchange,
							dataOutBorderExchange,
							getThroughput(dataInBorderExchange
									+ dataOutBorderExchange, timeBorderExchange));

		timeBarrier = 0;
		// timeReduceValueToRoot0FT = 0;
		timeReduceToRoot = 0;
		timeReduceToAll = 0;
		timeScatter = 0;
		timeGather = 0;
		timeBroadcast = 0;
		timeBroadcastValue = 0;
		timeBorderExchange = 0;

		countBarrier = 0;
		// countReduceValueToRoot0FT = 0;
		countReduceToRoot = 0;
		countReduceToAll = 0;
		countScatter = 0;
		countGather = 0;
		countBroadcast = 0;
		countBroadcastValue = 0;
		countBorderExchange = 0;

		dataInReduceToRoot = 0;
		dataOutReduceToRoot = 0;

		dataInReduceToAll = 0;
		dataOutReduceToAll = 0;

		dataInScatter = 0;
		dataOutScatter = 0;

		dataInGather = 0;
		dataOutGather = 0;

		dataInBroadcast = 0;
		dataOutBroadcast = 0;

		dataInBorderExchange = 0;
		dataOutBorderExchange = 0;
	}

	public void exitParallelSystem() throws Exception {

		/*
		 * for (int i = 0; i < nrCPUs; i++) { if (sps[i] != null)
		 * sps[i].close(); if (rps[i] != null) rps[i].close(); }
		 */

		ibis.end();

	}

	public int myCPU() {
		return myCPU;
	}

	public int nrCPUs() {
		return nrCPUs;
	}

	public boolean isRoot() {
		return iAmRoot;
	}

	/*
	 * public Collectives<?> getCollective(Class<?> c) throws Exception {
	 * 
	 * // FIXME: ugly -- J if (!c.isArray()) { throw new
	 * Exception("Illegal Collective type: " + c.getName()); }
	 * 
	 * Class component = c.getComponentType();
	 * 
	 * if (component.equals(byte.class)) { return byteCollectives; } else if
	 * (component.equals(short.class)) { return shortCollectives; } else if
	 * (component.equals(int.class)) { return intCollectives; } else if
	 * (component.equals(long.class)) { return longCollectives; } else if
	 * (component.equals(float.class)) { return floatCollectives; } else if
	 * (component.equals(double.class)) { return doubleCollectives; } else {
	 * throw new Exception("Illegal Collectives type: " + c.getName()); } }
	 */

	public void barrier() throws IOException {
		if (nrCPUs == 1) {
			return;
		}

		long start = System.nanoTime();

		barrier.barrier();

		timeBarrier += System.nanoTime() - start;
		countBarrier++;
	}

	public void silentBarrier() throws IOException {
		if (nrCPUs == 1) {
			return;
		}

		barrier.barrier();
	}

	public <T> void reduceToRoot(T array, int extent, ReduceOp ReduceOperation)
			throws IOException {

		// if (nrCPUs == 1) {
		// // nothing to reduce any further, just create an alias
		// return;
		// }

		long start = System.nanoTime();
		if (array instanceof double[]) {
			doubleCollectives.reduceToRoot.reduceToRoot((double[]) array,
					extent, ReduceOperation);
		} else if (array instanceof float[]) {
			floatCollectives.reduceToRoot.reduceToRoot((float[]) array, extent,
					ReduceOperation);
		} else if (array instanceof int[]) {
			intCollectives.reduceToRoot.reduceToRoot((int[]) array, extent,
					ReduceOperation);
		} else if (array instanceof long[]) {
			longCollectives.reduceToRoot.reduceToRoot((long[]) array, extent,
					ReduceOperation);
		} else if (array instanceof short[]) {
			shortCollectives.reduceToRoot.reduceToRoot((short[]) array, extent,
					ReduceOperation);
		} else if (array instanceof byte[]) {
			byteCollectives.reduceToRoot.reduceToRoot((byte[]) array, extent,
					ReduceOperation);
		} else {
			// eeek! not supported
			throw new RuntimeException("Not implemented");
		}

		timeReduceToRoot += System.nanoTime() - start;
		countReduceToRoot++;
	}

	public <T> void reduceToRoot(Array2d<T> array) throws IOException {

		// if (nrCPUs == 1) {
		// // nothing to reduce any further, just create an alias
		// array.setData(array.getPartialWidth(), array.getPartialHeight(),
		// array.getPartialDataReadWrite(), Array2d.GLOB_VALID);
		// array.setReduceOperation(null);
		// array.setLocalState(PartialState.FULL);
		// return;
		// }

		reduceToRoot(array.getPartialDataReadWrite(), array.getExtent(), array
				.getReduceOperation());

		if (iAmRoot) {
			T globalData;
			if (!array.hasData()) {
				globalData = array.createDataArray(array.getPartialWidth()
						* array.getPartialHeight() * array.getExtent());
			} else {
				globalData = array.getDataReadWrite();
			}
			array.setData(array.getPartialWidth(), array.getPartialHeight(),
					globalData, Array2d.GLOBAL_VALID);
		} else {
			array.setGlobalState(Array2d.GLOBAL_VALID);
		}
		array.setReduceOperation(null);
		array.setLocalState(Array2d.LOCAL_NONE);
	}

	public <T> void reduceToAll(T array, int extent, ReduceOp ReduceOperation)
			throws Exception {

		// if (nrCPUs == 1) {
		// // nothing to reduce any further, just create an alias
		// return;
		// }

		long start = System.nanoTime();
		if (array instanceof double[]) {
			doubleCollectives.reduceToAll.reduceToAll((double[]) array, extent,
					ReduceOperation);
		} else if (array instanceof float[]) {
			floatCollectives.reduceToAll.reduceToAll((float[]) array, extent,
					ReduceOperation);
		} else if (array instanceof int[]) {
			intCollectives.reduceToAll.reduceToAll((int[]) array, extent,
					ReduceOperation);
		} else if (array instanceof long[]) {
			longCollectives.reduceToAll.reduceToAll((long[]) array, extent,
					ReduceOperation);
		} else if (array instanceof short[]) {
			shortCollectives.reduceToAll.reduceToAll((short[]) array, extent,
					ReduceOperation);
		} else if (array instanceof byte[]) {
			byteCollectives.reduceToAll.reduceToAll((byte[]) array, extent,
					ReduceOperation);
		} else {
			// eeek! not supported
			throw new RuntimeException("Not implemented");
		}

		timeReduceToAll += System.nanoTime() - start;
		countReduceToAll++;
	}

	public <T> void reduceToAll(Array2d<T> array) throws Exception {

		// if (nrCPUs == 1) {
		// array.setData(array.getPartialWidth(), array.getPartialHeight(),
		// array.getPartialDataReadWrite(), Array2d.GLOB_VALID);
		// array.setReduceOperation(null);
		// array.setLocalState(PartialState.FULL);
		// return;
		// }

		reduceToAll(array.getPartialDataReadWrite(), array.getExtent(), array
				.getReduceOperation());

		if (iAmRoot) {
			T globalData;
			if (!array.hasData()) {
				globalData = array.createDataArray(array.getPartialWidth()
						* array.getPartialHeight() * array.getExtent());
			} else {
				globalData = array.getDataReadWrite();
			}

			array.setData(array.getPartialWidth(), array.getPartialHeight(),
					globalData, Array2d.GLOBAL_VALID);
		} else {
			array.setGlobalState(Array2d.GLOBAL_VALID);
		}
		array.setReduceOperation(null);
		array.setLocalState(Array2d.LOCAL_FULL);
	}

	public void broadcastArray(double[] data) throws Exception {

		if (nrCPUs == 1) {
			return;
		}

		long start = System.nanoTime();

		doubleCollectives.broadcast.broadcast(data);

		timeBroadcast += System.nanoTime() - start;
		countBroadcast++;
	}

	public void broadcastArray(int[] data) throws Exception {

		if (nrCPUs == 1) {
			return;
		}

		long start = System.nanoTime();

		intCollectives.broadcast.broadcast(data);

		// Added -- J
		timeBroadcast += System.nanoTime() - start;
		countBroadcast++;
	}

	public <T> void broadcast(Array2d<T> a) throws Exception {
		// if (nrCPUs == 1) {
		// // On 1 CPU we simply create an alias to the same data
		// a.setPartialData(a.getWidth(), a.getHeight(), a.getDataReadWrite(),
		// PartialState.FULL);
		// return;
		// }

		long start = System.nanoTime();

		if (a instanceof Array2dDoubles) {
			broadcastDoubles((Array2dDoubles) a);
		} else {
			throw new RuntimeException("Not implemented");
		}

		// Added -- J
		timeBroadcast += System.nanoTime() - start;
		countBroadcast++;
	}

	private void broadcastDoubles(Array2dDoubles a) throws Exception {
		int extent = a.getExtent();
		int width = a.getWidth();
		int height = a.getHeight();
		int borderWidth = a.getBorderWidth();
		int borderHeight = a.getBorderHeight();

		int len = (width + borderWidth * 2) * (height + borderHeight * 2)
				* extent;

		double[] partialData = null;

		if (a.hasPartialData()) {

			partialData = a.getPartialDataReadWrite();

			if (partialData.length != len) {
				partialData = null;
			}
		}

		if (partialData == null) {
			partialData = new double[len];
			a.setPartialData(width, height, partialData, Array2d.LOCAL_FULL);
		} else {
			a.setLocalState(Array2d.LOCAL_FULL);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("PxSystem.broadcastDoubles:");
		}

		if (iAmRoot) {
			System.arraycopy(a.getDataReadOnly(), 0, partialData, 0, len);
			dataOutBroadcast += len * Double.SIZE / 8;
		} else {
			dataInBroadcast += len * Double.SIZE / 8;
		}

		doubleCollectives.broadcast.broadcast(partialData);

		if (logger.isDebugEnabled()) {
			logger.debug("PxSystem.scatterDoubles finished");
		}
	}

	public <T> void scatter(Array2d<T> a) throws Exception {

		// if (nrCPUs == 1) {
		// // On 1 CPU we simply create an alias to the same data
		// a.setPartialData(a.getWidth(), a.getHeight(), a.getDataReadWrite(),
		// PartialState.PARTIAL);
		// return;
		// }

		long start = System.nanoTime();

		if (a instanceof Array2dDoubles) {
			scatterDoubles((Array2dDoubles) a);
			// } else if (a instanceof Array2dBytes) {
			// scatterBytes((Array2dBytes) a);
		} else {
			throw new RuntimeException("Not implemented");
		}

		a.setLocalState(Array2d.LOCAL_PARTIAL);

		// Added -- J
		timeScatter += System.nanoTime() - start;
		countScatter++;

	}

	private void scatterDoubles(Array2dDoubles a) throws Exception {
		// Here we assume CPU 0 (root) to have a full & valid structure
		// which is scattered to the partial structs of all nodes. East
		// and west borders are also communicated (not north and south).
		if (nrCPUs == 1) {
			// On 1 CPU we simply create an alias to the same data
			double[] array = a.getDataReadWrite();

			a.setPartialData(a.getWidth(), a.getHeight(), Arrays.copyOf(array,
					array.length), Array2d.LOCAL_PARTIAL);
			// FIXME not cloning the data here leads to erroneous behaviour
			return;
		}

		int globH = a.getHeight();
		int extent = a.getExtent();
		int pWidth = a.getWidth();
		int pHeight = getPartHeight(globH, myCPU);
		int bWidth = a.getBorderWidth();
		int bHeight = a.getBorderHeight();

		int len = (pWidth + bWidth * 2) * (pHeight + bHeight * 2) * extent;
		int xSize = (pWidth + bWidth * 2) * extent;

		if (a.getLocalState() == Array2d.LOCAL_FULL) {
			// no need for communication, just cut the partial data out of the
			// full data
			double[] oldPartialData = a.getPartialDataReadWrite();
			double[] newPartialData = new double[len];
			int offset = xSize * (getLclStartY(globH, myCPU) + bHeight);

			System.arraycopy(oldPartialData, offset, newPartialData, 0, len);

			a.setPartialData(pWidth, pHeight, newPartialData,
					Array2d.LOCAL_PARTIAL);
			return;
		}

		double[] pData = null;

		if (a.hasPartialData()) {

			pData = a.getPartialDataReadWrite();

			if (pData.length != len) {
				pData = null;
			}
		}

		if (pData == null) {
			pData = new double[len];
			a.setPartialData(pWidth, pHeight, pData, Array2d.LOCAL_NONE);
		}

		int[] offsets = new int[nrCPUs];
		int[] sizes = new int[nrCPUs];

		int totalSize = 0;

		if (logger.isDebugEnabled()) {
			logger.debug("PxSystem.scatterDoubles:");
		}
		for (int partner = 0; partner < nrCPUs; partner++) {
			offsets[partner] = xSize * (getLclStartY(globH, partner) + bHeight);
			sizes[partner] = xSize * getPartHeight(globH, partner);
			totalSize += sizes[partner];
			if (logger.isDebugEnabled()) {
				logger.debug("CPU " + partner + ": offset = "
						+ offsets[partner] + ", size = " + sizes[partner]);
			}
		}

		int offset = xSize * bHeight;
		if (iAmRoot) {
			doubleCollectives.scatter.scatter(a.getDataReadOnly(), offsets,
					sizes, pData, offset, sizes[myCPU]);
			dataOutScatter += (totalSize - sizes[myCPU]) * Double.SIZE / 8;
		} else {
			// reading global data will give an error
			doubleCollectives.scatter.scatter(null, offsets, sizes, pData,
					offset, sizes[myCPU]);
			dataInScatter += sizes[myCPU] * Double.SIZE / 8;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("PxSystem.scatterDoubles finished");
		}
	}

	public <T> void gather(Array2d<T> a) throws IOException {

		// if (nrCPUs == 1) {
		// // On 1 CPU we simply create an alias to the same data
		// a.setData(a.getPartialWidth(), a.getPartialHeight(), a
		// .getPartialDataReadWrite(), Array2d.GLOB_VALID);
		// a.setPartialData(a.getWidth(), a.getHeight(), a.getDataReadWrite(),
		// PartialState.FULL);
		// return;
		// }

		long start = System.nanoTime();

		if (a instanceof Array2dDoubles) {
			gatherDoubles((Array2dDoubles) a);
		} else {
			throw new RuntimeException("Not implemented");
		}
		// Added -- J
		timeGather += System.nanoTime() - start;
		countGather++;

	}

	private void gatherDoubles(Array2dDoubles a) throws IOException {
		// Here we assume CPU 0 (root) to have a full & valid structure
		// which is scattered to the partial structs of all nodes. East
		// and west borders are also communicated (not north and south).

		int height = a.getHeight();
		int extent = a.getExtent();
		int width = a.getWidth();
		int bWidth = a.getBorderWidth();
		int bHeight = a.getBorderHeight();

		int len = (width + bWidth * 2) * (height + bHeight * 2) * extent;

		double[] data = null;

		if (iAmRoot) {
			// initialize the global data on the root
			if (a.hasData()) {

				data = a.getDataWriteOnly();

				if (data.length != len) {
					data = null;
				}
			}

			if (data == null) {
				data = new double[len];
				// a.setData(width, height, data, GlobalState.VALID);
				a.setData(width, height, data, Array2d.GLOBAL_VALID);
			} else {
				// a.setGlobalState(GlobalState.VALID);
				a.setGlobalState(Array2d.GLOBAL_VALID);
			}
		}

		if (a.getLocalState() == Array2d.LOCAL_FULL) {
			// we can just copy the local State at the root
			if (iAmRoot) {
				System.arraycopy(a.getPartialDataReadOnly(), 0, data, 0, len);
			}
			return;
		}

		int xSize = (width + bWidth * 2) * extent;

		int[] offsets = new int[nrCPUs];
		int[] sizes = new int[nrCPUs];
		int totalSize = 0;

		if (logger.isDebugEnabled()) {
			logger.debug("PxSystem.gatherDoubles:");
		}
		for (int partner = 0; partner < nrCPUs; partner++) {
			offsets[partner] = xSize
					* (getLclStartY(height, partner) + bHeight);
			sizes[partner] = xSize * getPartHeight(height, partner);
			totalSize += sizes[partner];
			if (logger.isDebugEnabled()) {
				logger.debug("CPU " + partner + ": offset = "
						+ offsets[partner] + ", size = " + sizes[partner]);
				if (data != null) {
					logger.debug("'data' size: " + data.length);
				}
			}
		}
		if (a.getPartialDataReadOnly() == null) {
			// FIXME bug??
			throw new Error("Panic!!! no partial Data");
		}

		int offset = bHeight * xSize;
		// int offset = 0;

		if (iAmRoot) {
			dataInGather += (totalSize - sizes[myCPU]) * Double.SIZE / 8;
		} else {
			dataOutGather += sizes[myCPU] * Double.SIZE / 8;
		}
		doubleCollectives.gather.gather(a.getPartialDataReadOnly(), offset,
				sizes[myCPU], data, offsets, sizes);

		// local state was partial, now it is invalid --> none
		a.setPartialData(0, 0, null, Array2d.LOCAL_NONE);
		// TODO or do we want to keep the local array and only set the state to
		// NONE
		if (logger.isDebugEnabled()) {
			logger.debug("PxSystem.gatherDoubles finished");
		}
	}

	public void borderExchange(double[] a, int width, int height, int off,
			int stride, int ySize) throws Exception {

		// barrier(); // FIXME temporary measurement barrier
		// Added -- J
		long start = System.nanoTime();

		// Border exchange in vertical direction (top <---> bottom)
		int prevCPU = myCPU - 1;
		int nextCPU = myCPU + 1;

		int xSize = width + stride;

		// System.out.println("Border exchange: " + xSize + "x" + ySize + " (" +
		// (xSize*ySize) + ")");

		final int borderSize = xSize * ySize;
		if ((myCPU & 1) == 0) {
			if (nextCPU < nrCPUs) {
				WriteMessage writeMessage = newMessage(nextCPU);
				 writeMessage.writeArray(a, off - stride / 2 + (height -
				 ySize)
				 * xSize, borderSize);
//				int from = off - stride / 2 + (height - ySize) * xSize;
//				writeMessage.writeArray(Arrays.copyOfRange(a, from, from
//						+ borderSize));
				writeMessage.finish();
				dataOutBorderExchange += borderSize * 8;

				ReadMessage readMessage = receive(nextCPU);
				readMessage.readArray(a, off - stride / 2 + height * xSize,
						borderSize);
				readMessage.finish();
				dataInBorderExchange += borderSize * 8;
			}
			if (prevCPU >= 0) {
				ReadMessage readMessage = receive(prevCPU);
				readMessage.readArray(a, off - stride / 2 - borderSize,
						borderSize);
				readMessage.finish();
				dataInBorderExchange += borderSize * 8;

				WriteMessage writeMessage = newMessage(prevCPU);
				writeMessage.writeArray(a, off - stride / 2, borderSize);
//				int from = off - stride / 2;
//				writeMessage.writeArray(Arrays.copyOfRange(a, from, from
//						+ borderSize));
				writeMessage.finish();
				dataOutBorderExchange += borderSize * 8;
			}
		} else {
			if (prevCPU >= 0) {
				ReadMessage readMessage = receive(prevCPU);
				readMessage.readArray(a, off - stride / 2 - borderSize,
						borderSize);
				readMessage.finish();
				dataInBorderExchange += borderSize * 8;

				WriteMessage writeMessage = newMessage(prevCPU);
				writeMessage.writeArray(a, off - stride / 2, borderSize);
//				int from = off - stride / 2;
//				writeMessage.writeArray(Arrays.copyOfRange(a, from, from
//						+ borderSize));
				writeMessage.finish();
				dataOutBorderExchange += borderSize * 8;
			}

			if (nextCPU < nrCPUs) {
				WriteMessage writeMessage = newMessage(nextCPU);
				writeMessage.writeArray(a, off - stride / 2 + (height - ySize)
						* xSize, borderSize);
//				int from = off - stride / 2 + (height - ySize) * xSize;
//				writeMessage.writeArray(Arrays.copyOfRange(a, from, from + borderSize));
				writeMessage.finish();
				dataOutBorderExchange += borderSize * 8;

				ReadMessage readMessage = receive(nextCPU);
				readMessage.readArray(a, off - stride / 2 + height * xSize,
						borderSize);
				readMessage.finish();
				dataInBorderExchange += borderSize * 8;
			}
		}
		// Added -- J
		timeBorderExchange += System.nanoTime() - start;
		countBorderExchange++;
	}

	// public static void borderExchange_Orig(double[] a, int width, int height,
	// int off, int stride, int ySize) throws Exception { // Added -- J
	// // long
	//
	// long start = System.nanoTime();
	//
	// // Border exchange in vertical direction (top <---> bottom)
	// int part1 = myCPU - 1;
	// int part2 = myCPU + 1;
	// int xSize = width + stride;
	//
	// // Send to first partner and receive from second partner
	//
	// if (part1 >= 0) {
	// // if (sps[part1] == null) {
	// // sps[part1] = ibis.createSendPort(portType);
	// // sps[part1].connect(world[part1], COMM_ID + myCPU);
	// // }
	// WriteMessage w = sps[part1].newMessage();
	// w.writeArray(a, off - stride / 2, xSize * ySize);
	// w.finish();
	// // dataOutBorderExchange += xSize * ySize * 8;
	// }
	//
	// if (part2 < PxSystem.nrCPUs()) {
	// // if (rps[part2] == null) {
	// // rps[part2] = ibis.createReceivePort(portType, COMM_ID + part2);
	// // rps[part2].enableConnections();
	// // }
	// ReadMessage r = rps[part2].receive();
	// r.readArray(a, off - stride / 2 + height * xSize, xSize * ySize);
	// r.finish();
	//
	// // dataInBorderExchange += xSize * ySize * 8;
	//
	// // Send to second partner and receive from first partner
	//
	// // if (sps[part2] == null) {
	// // sps[part2] = ibis.createSendPort(portType);
	// // sps[part2].connect(world[part2], COMM_ID + myCPU);
	// // }
	// WriteMessage w = sps[part2].newMessage();
	// w.writeArray(a, off - stride / 2 + (height - ySize) * xSize, xSize
	// * ySize);
	// w.finish();
	//
	// // dataOutBorderExchange += xSize * ySize * 8;
	//
	// }
	// if (part1 >= 0) {
	// // if (rps[part1] == null) {
	// // rps[part1] = ibis.createReceivePort(portType, COMM_ID + part1);
	// // rps[part1].enableConnections();
	// // }
	// ReadMessage r = rps[part1].receive();
	// r.readArray(a, 0, xSize * ySize);
	// r.finish();
	//
	// // dataInBorderExchange += xSize * ySize * 8;
	// }
	//
	// // Added -- J
	// timeBorderExchange += System.nanoTime() - start;
	// countBorderExchange++;
	// }

	public int getPartHeight(int height, int CPUnr) {
		int minLocalH = height / nrCPUs;
		int overflowH = height % nrCPUs;

		if (CPUnr < overflowH) {
			minLocalH++;
		}
		return minLocalH;
	}

	public int getLclStartY(int height, int CPUnr) {
		int minLocalH = height / nrCPUs;
		int overflowH = height % nrCPUs;

		if (CPUnr < overflowH) {
			return (CPUnr * (minLocalH + 1));
		} else {
			return (CPUnr * minLocalH + overflowH);
		}
	}

	public ReadMessage receive(int src) throws IOException {

		if (CONNECT_DYNAMIC) {
			if (rps[src] == null) {
				rps[src] = ibis.createReceivePort(portType, COMM_ID + src);
				rps[src].enableConnections();
			}
		}

		return rps[src].receive();
	}

	public WriteMessage newMessage(int dest) throws IOException {

		if (CONNECT_DYNAMIC) {
			if (sps[dest] == null) {
				// Connect to target

				sps[dest] = ibis.createSendPort(portType);

				boolean connected = false;

				while (!connected) {
					try {
						sps[dest].connect(world[dest], COMM_ID + myCPU);
						connected = true;
					} catch (Exception e) {
						System.err.println("Failed to connect to " + dest
								+ " will retry");
						try {
							Thread.sleep(100);
						} catch (Exception ex) {
							// ignore
						}
					}
				}
			}
		}

		return sps[dest].newMessage();
	}

	public void createSideChannel(SideChannel<?> sideChannel, String channelName)
			throws IOException {
		if (iAmRoot) {
			// This is the master node of the Jorus system
			SendPort sendPort = ibis.createSendPort(sideChannelPortType,
					channelName);
			if (logger.isInfoEnabled()) {
				logger.info("master: " + world[myCPU].name());
			}
			Map<IbisIdentifier, String> slaves = new HashMap<IbisIdentifier, String>();
			for (int i = 1; i < system.nrCPUs(); i++) {
				if (logger.isInfoEnabled()) {
					logger.info("slave: " + world[i].name());
				}
				slaves.put(world[i], channelName);
			}

			sendPort.connect(slaves, 5000, true);
			sideChannel.attachPort(sendPort);
		} else {
			// This is the slave node of the Jorus system
			if (logger.isInfoEnabled()) {
				logger.info("slave " + world[myCPU].name()
						+ " is ready for sideChannel");
			}
			ReceivePort receivePort = ibis.createReceivePort(
					sideChannelPortType, channelName, sideChannel);
			receivePort.enableConnections();
			receivePort.enableMessageUpcalls();
		}
	}

}
