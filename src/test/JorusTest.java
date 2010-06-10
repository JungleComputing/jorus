package test;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.Imaging4j;
import ibis.imaging4j.test.ImageViewer;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jorus.array.Array2dScalarDouble;
import jorus.array.Gaussian1d;
import jorus.parallel.PxSystem;
import jorus.pixel.Pixel;
import jorus.pixel.PixelDouble;
import jorus.weibull.CxWeibull;

public class JorusTest {

	private static final Logger logger = LoggerFactory
			.getLogger(JorusTest.class);

	private final PxSystem px;
	private final boolean master;

	private boolean ended = false;
	File file;

	private JorusTest(String poolName, String poolSize, String fileName)
			throws Exception {
		file = new File(fileName);

		if (Integer.parseInt(poolSize) > 1) {
			// if (Integer.parseInt(poolSize) > 1 || true) {
			logger.info("Initializing PxSystem.");
			try {
				px = PxSystem.init(poolName, poolSize);
			} catch (Exception e) {
				logger.error("Could not initialize Parallel system", e);
				throw e;
			}

			logger.debug("nrCPUs = " + px.nrCPUs());
			logger.debug("myCPU = " + px.myCPU());

			master = px.isRoot();

			// Node 0 needs to provide an Ibis to contact the outside world.
			if (master) {
				logger.info("Local PxSystem initalized.");
			}

			// do initialisation now instead of after the first request is
			// received.
			logger.info("Rank " + px.myCPU() + " of " + px.nrCPUs()
					+ " Initialization done");
		} else {
			px = null;
			master = true;
		}
	}

	private synchronized void setEnded() {
		ended = true;
	}

	private synchronized boolean hasEnded() {
		return ended;
	}

	void end() {
		logger.info("Ending server");
		setEnded();
		if (PxSystem.initialized()) {
			try {
				px.exitParallelSystem();
			} catch (Exception e) {
				System.err.println("error on exiting parallel system");
				e.printStackTrace(System.err);
			}
		}
	}

	private double radians(double angle) {
		return (angle / 180.) * Math.PI;
	}

	private void run() {
		Array2dScalarDouble array = null;
		try {
			PixelDouble pixel = new PixelDouble(1);
			array = new Array2dScalarDouble(128, 128, 0, 0, true);
			double[] pixelData = pixel.getValue();
			// pixelData[0] = 0.5;
			// array.setVal(pixel, true);

			pixelData[0] = 1;
//			array.setSingleValue(pixel, 16, 16, true);
			for (int i = 32; i < 96; i++) {
				for (int j = 32; j < 96; j++) {
					array.setSingleValue(pixel, i, j, true);
				}
			}
			/*
			 * for (int i = 100; i < 150; i++) { for (int j = 100; j < 150; j++)
			 * { array.setSingleValue(pixel, i, j, true); } }
			 */

			if (master) {
				try {
					viewImage(array.getData(), array.getWidth(), array
							.getHeight(), "1");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// array = array.gaussDerivativeRot(0, 2, 2, 0, 0);
		// array = (Array2dScalarDouble) array.gauss(4, 11);
//		 array = (Array2dScalarDouble) array.convGauss2d(4, 3, 11, 4, 3, 11);
//		array = (Array2dScalarDouble) array.gaussDerivative2d(11, 0, 0, 3);
//		array = (Array2dScalarDouble) array.convGauss2d(5, 0, 3, 11, 0, 3);
//		array = (Array2dScalarDouble) array.convolutionRotated1d(Gaussian1d.create(5, 0, 0.995, 25, 25), -0.25* Math.PI);
		array = (Array2dScalarDouble) array.convGaussAnisotropic2d(2, 0, 3, 11, 0, 3, 0.25 * Math.PI, true);
		array = (Array2dScalarDouble) array.clone(0, 0); // remove borders

		if (PxSystem.initialized()) {
			try {
				px.gather(array);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// array = (Array2dScalarDouble) array.clone(0, 0);
		if (master) {
			try {
				viewImage(array.getData(), array.getWidth(), array
						.getHeight(), "2");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static void viewImage(double[] image, int width, int height,
			String text) throws Exception {
		ibis.imaging4j.Image outputImage;
		ByteBuffer buf = ByteBuffer.allocate(image.length * Double.SIZE / 8);
		buf.asDoubleBuffer().put(image);
		outputImage = new ibis.imaging4j.Image(Format.TGDOUBLEGREY, width,
				height, buf.array());

		outputImage = Imaging4j.convert(Imaging4j.convert(outputImage,
				Format.TGDOUBLEARGB), Format.ARGB32);
		ImageViewer viewer;
		if (outputImage.getWidth() < 128) {
			viewer = new ImageViewer(128, outputImage.getHeight());
		} else {
			viewer = new ImageViewer(outputImage.getWidth(), outputImage
					.getHeight());
		}
		viewer.setImage(outputImage, text);
	}

	static class ShutDown extends Thread {
		final JorusTest server;

		ShutDown(JorusTest server) {
			this.server = server;
		}

		public void run() {
			server.end();
		}
	};

	public static void main(String[] args) {
		// Controller controller = Controller.getController();
		String poolName = null;
		String poolSize = null;

		if (args.length == 0) {
			poolName = System.getProperty("ibis.deploy.job.id", "jorus");
			poolSize = System.getProperty("ibis.deploy.job.size", "1");
		} else if (args.length >= 2) {
			poolName = args[0];
			poolSize = args[1];
		}

		String fileName;
		if (args.length < 3) {
			fileName = "images/celegcolor.jpg";
		} else {
			fileName = args[2];
		}

		logger.info("Image processing server starting in pool \"" + poolName
				+ "\" of size " + poolSize);

		if (poolName == null || poolSize == null) {
			System.err
					.println("USAGE: Server poolname poolsize OR set ibis.deploy.job.id and ibis.deploy.job.size properties");
			System.exit(1);
		}

		JorusTest server = null;
		try {
			server = new JorusTest(poolName, poolSize, fileName);
			// Install a shutdown hook that terminates Ibis.
			// Runtime.getRuntime().addShutdownHook(new ShutDown(server));

			server.run();
		} catch (Throwable e) {
			System.err.println("Server died unexpectedly!");
			e.printStackTrace(System.err);
		}

		try {
			if (server != null) {
				server.end();
			}
		} catch (Exception e) {
			// Nothing we can do now...
		}
		// controller.quit();
	}

}
