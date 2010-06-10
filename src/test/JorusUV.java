package test;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.Imaging4j;
import ibis.imaging4j.test.ImageViewer;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import jorus.array.Array2dDoubles;
import jorus.array.Array2dScalarDouble;
import jorus.parallel.PxSystem;
import jorus.pixel.PixelDouble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JorusUV {

	private static final int ITER = 10; // number of iterations

	private static final int MIN_THETA = 0;
	private static final int MAX_THETA = 180;
	private static final int STEP_THETA = 5; // 15 for minimal measurement

	private static final double MIN_SX = 1; // 1.0 for minimal measurement
	private static final double MAX_SX = 4; // 5.0 for minimal measurement
	private static final double STEP_SX = 1; // 2.0 for minimal measurement

	private static final double MIN_SY = 3; // 3.0 for minimal measurement
	private static final double MAX_SY = 9; // 11.0 for minimal measurement
	private static final double STEP_SY = 2; // 4.0 for minimal measurement

	private static final boolean Fixed = true; // Fixed MAX_SX: YES/NO

	private static double Max_sx(double sy) {
		return (Fixed ? MAX_SX : (sy * 0.75));
	}

	private static double Step_sx(double sx) {
		return (Fixed ? STEP_SX : (sx / 2));
	}

	private static final Logger logger = LoggerFactory.getLogger(JorusUV.class);

	private final PxSystem px;
	private final boolean master;

	private boolean ended = false;
	File file;

	private JorusUV(String poolName, String poolSize, String fileName)
			throws Exception {
		file = new File(fileName);

		if (Integer.parseInt(poolSize) > 0) {
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
				logger.info("Local PxSystem initialized.");
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
		if (px != null) {
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

	Array2dScalarDouble convUV(Array2dScalarDouble source) throws Exception {
		double sx, sy;
		Array2dScalarDouble resultImage = new Array2dScalarDouble(source
				.getWidth(), source.getHeight(), 0, 0, true);
		/*** Loop over entire orientation scale-space ***/

		for (int theta = MIN_THETA; theta < MAX_THETA; theta += STEP_THETA) {
			Array2dScalarDouble filtIm1 = null;
			Array2dScalarDouble filtIm2 = null;

			double thetaRad = radians(theta);
			for (sy = MIN_SY; sy < MAX_SY; sy += STEP_SY) {
				for (sx = MIN_SX; sx < Max_sx(sy); sx += Step_sx(sx)) {
					if (sx != sy) {
						// filtIm1 = source.gaussDerivativeRot(thetaRad, sx, sy,
						// 2, 0); // FIXME no rotation!
						// filtIm2 = source.gaussDerivativeRot(thetaRad, sx, sy,
						// 0, 0); // FIXME no rotation!
						filtIm1 = (Array2dScalarDouble) source
								.convGaussAnisotropic2d(sx, 2, 3, sy, 0, 3,
										thetaRad, false);
						filtIm2 = (Array2dScalarDouble) source
								.convGaussAnisotropic2d(sx, 0, 3, sy, 0, 3,
										thetaRad, false);
						Array2dScalarDouble contrastIm = (Array2dScalarDouble) filtIm1
								.negDiv(filtIm2, true);
						contrastIm = (Array2dScalarDouble) contrastIm
								.mulVal(new PixelDouble(
										new double[] { sx * sy }), true);

						resultImage = (Array2dScalarDouble) resultImage.max(
								contrastIm, true);
					}
				}
			}
			// resultImage.getData();
			// logger.debug("convUV: theta = " + theta + " finished");
		}
		resultImage.createGlobalImage();
		return resultImage;
	}

	private void singleRun(Array2dScalarDouble array, boolean master,
			String name) {

		long computing = 0;
		long end = 0;

		byte[] pixels;

		computing = System.currentTimeMillis();

		// FIXME prevent unnecessary scatters:
		if (px != null) {
			try {
				px.scatter(array);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		// FIXME do line detection here
		Array2dScalarDouble result = null;
		try {
			result = convUV(array);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		end = System.currentTimeMillis();

		if (master) {
			// logger.debug("Computation done");
			System.out.println("Computation done");
			// FIXME create output here

			// logger.info("Processing took: " + (end - computing));
			System.out.println("Processing took: " + (end - computing) + " ms");

			Array2dScalarDouble viewImage = result;// .clone(0,0);
			try {
//				viewImage(viewImage.getData(), viewImage.getWidth(),
//						viewImage.getHeight(), name);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	private void run() {
		Array2dScalarDouble array = null;
		try {
			Image srcImage = Imaging4j.load(file);
			Image convertedImage;
			convertedImage = Imaging4j.convert(srcImage, Format.ARGB32);
			convertedImage = Imaging4j.convert(convertedImage,
					Format.TGDOUBLEARGB);
			convertedImage = Imaging4j.convert(convertedImage,
					Format.TGDOUBLEGREY);
			DoubleBuffer buf = convertedImage.getData().asDoubleBuffer();
			double[] data;
			if (buf.hasArray()) {
				data = buf.array();
			} else {
				data = new double[buf.capacity()];
				buf.clear();
				buf.get(data);
			}

			array = new Array2dScalarDouble(convertedImage.getWidth(),
					convertedImage.getHeight(), data, false);

			// if (master) {
			// viewImage(array.getDataReadOnly(), array.getWidth() + 2
			// * array.getBorderWidth(), array.getHeight() + 2
			// * array.getBorderHeight(), "Import");
			// }
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		long start = System.currentTimeMillis();
		for (int i = 0; i < ITER; i++) {
			singleRun(array, master, "JorusUV " + i);
			System.err.println("run " + i + ": double[] #" + Array2dDoubles.getAndResetcreateCounter());
			if (px != null) {
				px.printStatistics();
			}
			System.gc();
		}
		long totalTime = System.currentTimeMillis() - start;
		System.err.println("Total execution time: " + totalTime + "ms");

	}

	private static void viewImage(double[] image, int width, int height,
			String text) throws Exception {
		ibis.imaging4j.Image outputImage;
		ByteBuffer buf = ByteBuffer.allocate(image.length * Double.SIZE / 8);
		buf.asDoubleBuffer().put(image);
		outputImage = new ibis.imaging4j.Image(Format.TGDOUBLEGREY, width,
				height, buf.array());

		// outputImage = Imaging4j.convert(Imaging4j.convert(outputImage,
		// Format.TGDOUBLEARGB), Format.ARGB32);
		outputImage = Imaging4j.convert(Imaging4j.convert(outputImage,
				Format.GREY), Format.ARGB32);
		ImageViewer viewer = new ImageViewer(outputImage.getWidth(),
				outputImage.getHeight());
		viewer.setImage(outputImage, text);
	}

	static class ShutDown extends Thread {
		final JorusUV server;

		ShutDown(JorusUV server) {
			this.server = server;
		}

		public void run() {
			server.end();
		}
	};

	public static void main(String[] args) {
		String poolName = null;
		String poolSize = null;

		if (args.length == 0) {
			poolName = System.getProperty("ibis.deploy.job.id", null);
			poolSize = System.getProperty("ibis.deploy.job.size", null);
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

		JorusUV server = null;
		try {
			server = new JorusUV(poolName, poolSize, fileName);
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
	}

}
