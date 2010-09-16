package test;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.Imaging4j;
import ibis.imaging4j.test.ImageViewer;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import jorus.array.Array2dFloat;
import jorus.array.Array2dScalarFloat;
import jorus.parallel.PxSystem;
import jorus.pixel.PixelFloat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jorus2DFloat {

	private static final int ITER = 10; // number of iterations

	private static final int MIN_THETA = 0;
	private static final int MAX_THETA = 180;
	private static final int STEP_THETA = 1; // 5; // 15 for minimal measurement

	private static final float MIN_SX = 1; // 1.0 for minimal measurement
	private static final float MAX_SX = 4; // 5.0 for minimal measurement
	private static final float STEP_SX = 1;// 4; // 1; // 2.0 for minimal
	// measurement

	private static final float MIN_SY = 3; // 3.0 for minimal measurement
	private static final float MAX_SY = 9; // 11.0 for minimal measurement
	private static final float STEP_SY = 2;// 9; //2; // 4.0 for minimal
	// measurement

	private static final boolean Fixed = true; // Fixed MAX_SX: YES/NO

	private static float Max_sx(float sy) {
		return (Fixed ? MAX_SX : (sy * 0.75f));
	}

	private static float Step_sx(float sx) {
		return (Fixed ? STEP_SX : (sx / 2));
	}

	private static final Logger logger = LoggerFactory
			.getLogger(Jorus2DFloat.class);

	private final PxSystem px;
	private final boolean master;

	private boolean ended = false;
	File file;

	private Jorus2DFloat(String poolName, String poolSize, String fileName)
			throws Exception {
		file = new File(fileName);

		if (Integer.parseInt(poolSize) > 1) {
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

	Array2dScalarFloat conv2D(Array2dScalarFloat source) throws Exception {
		float sx, sy;
		Array2dScalarFloat resultImage = new Array2dScalarFloat(source
				.getWidth(), source.getHeight(), 0, 0, true);
		/*** Loop over entire orientation scale-space ***/

		for (int theta = MIN_THETA; theta < MAX_THETA; theta += STEP_THETA) {
			Array2dScalarFloat filtIm1 = null;
			Array2dScalarFloat filtIm2 = null;

			for (sy = MIN_SY; sy < MAX_SY; sy += STEP_SY) {
				for (sx = MIN_SX; sx < Max_sx(sy); sx += Step_sx(sx)) {
					if (sx != sy) {
						filtIm1 = source.convGauss1x2d(sx, sy, -theta, 2, 3);
						filtIm2 = source.convGauss1x2d(sx, sy, -theta, 0, 3);

						//						
						Array2dScalarFloat contrastIm = filtIm1.posDiv(filtIm2,
								true);
						// Array2dScalarFloat contrastIm = filtIm1.div(filtIm2,
						// true);
						// Array2dScalarFloat contrastIm =
						// filtIm1.posDiv(filtIm2, true);

						// Array2dScalarFloat contrastIm = filtIm1;

						contrastIm = contrastIm.mulVal(new PixelFloat(sx * sy),
								true);

						resultImage = resultImage.max(contrastIm, true);
					}
				}
			}
			// resultImage.getData();
			// logger.debug("conv2D: theta = " + theta + " finished");
		}
		resultImage.createGlobalImage();
		return resultImage;
	}

	private Array2dScalarFloat singleRun(Array2dScalarFloat array, boolean master, String name) {

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
		Array2dScalarFloat result = null;
		try {
			result = conv2D(array);
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

//			Array2dScalarFloat viewImage = result;// .clone(0,0);
//			try {
//				viewImage(viewImage.getData(), viewImage.getWidth(), viewImage
//						.getHeight(), name);
//			} catch (Exception e) {
//
//				// e.printStackTrace();
//			}
		}
		return result;
	}

	private void run() {
		Array2dScalarFloat array = null;
		try {
			Image srcImage = Imaging4j.load(file);
			Image convertedImage;
			convertedImage = Imaging4j.convert(srcImage, Format.ARGB32);
			convertedImage = Imaging4j.convert(convertedImage,
					Format.TGFLOATARGB);
			convertedImage = Imaging4j.convert(convertedImage,
					Format.TGFLOATGREY);
			FloatBuffer buf = convertedImage.getData().asFloatBuffer();
			float[] data;
			if (buf.hasArray()) {
				data = buf.array();
			} else {
				data = new float[buf.capacity()];
				buf.clear();
				buf.get(data);
			}

			array = new Array2dScalarFloat(convertedImage.getWidth(),
					convertedImage.getHeight(), data, false);

			if (master) {
				try {
				viewImage(array.getData(), array.getWidth() + 2
						* array.getBorderWidth(), array.getHeight() + 2
						* array.getBorderHeight(), "Import");
				} catch (Exception e) {
					//ignore
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		Array2dScalarFloat result = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < ITER; i++) {
			// System.gc();
			result = singleRun(array, master, "Jorus2DFloat " + i);
			if (px != null) {
				px.printStatistics();
			}
		}
		long totalTime = System.currentTimeMillis() - start;
		System.err.println("Total execution time: " + totalTime + "ms");
		if (master) {
			try {
				viewImage(result.getData(), result.getWidth() + 2
						* result.getBorderWidth(), result.getHeight() + 2
						* result.getBorderHeight(), "Jorus2DFloat");
			} catch (Exception e) {
				//ignore
			}

			try {
				saveImage(result.getData(), result.getWidth() + 2
						* result.getBorderWidth(), result.getHeight() + 2
						* result.getBorderHeight(), "Jorus2DFloat");
			} catch (Exception e) {
				// ignore
//				e.printStackTrace();
			}
		}
	}

	private static void viewImage(float[] image, int width, int height,
			String text) throws Exception {
		ibis.imaging4j.Image outputImage;
		ByteBuffer buf = ByteBuffer.allocate(image.length * Float.SIZE / 8);
		buf.asFloatBuffer().put(image);
		outputImage = new ibis.imaging4j.Image(Format.TGFLOATGREY, width,
				height, buf.array());

		// outputImage = Imaging4j.convert(Imaging4j.convert(outputImage,
		// Format.TGFLOATARGB), Format.ARGB32);
		outputImage = Imaging4j.convert(Imaging4j.convert(outputImage,
				Format.GREY), Format.ARGB32);
		ImageViewer viewer = new ImageViewer(outputImage.getWidth(),
				outputImage.getHeight());
		viewer.setImage(outputImage, text);
	}

	private static void saveImage(float[] image, int width, int height,
			String filename) throws Exception {
		ibis.imaging4j.Image outputImage;
		ibis.imaging4j.Image outputJpeg;

		ByteBuffer buf = ByteBuffer.allocate(image.length * Float.SIZE / 8);
		buf.asFloatBuffer().put(image);
		outputImage = new ibis.imaging4j.Image(Format.TGFLOATGREY, width,
				height, buf.array());
		outputJpeg = Imaging4j.convert(Imaging4j.convert(Imaging4j.convert(
				outputImage, Format.GREY), Format.ARGB32), Format.RGB24);
		File file = new File(filename + ".jpg");
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		Imaging4j.save(outputJpeg, file);
	}

	static class ShutDown extends Thread {
		final Jorus2DFloat server;

		ShutDown(Jorus2DFloat server) {
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

		Jorus2DFloat server = null;
		try {
			server = new Jorus2DFloat(poolName, poolSize, fileName);
			// Install a shutdown hook that terminates Ibis.
			Runtime.getRuntime().addShutdownHook(new ShutDown(server));

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
