/**
 * 
 */
package jorus.array;


public class TransformationMatrix {
//	public static Matrix translate2d(double x, double y) {
//		Matrix m = new Matrix(3, 3);
//		m.set(0, 0, 1);
//		m.set(1, 1, 1);
//		m.set(2, 2, 1);
//		m.set(0, 2, x);
//		m.set(1, 2, y);
//		return m;
//	}
//
//	public static Matrix scale2d(double sx, double sy) {
//		Matrix m = new Matrix(3, 3);
//
//		m.set(0, 0, sx);
//		m.set(1, 1, sy);
//		m.set(2, 2, 1);
//		return m;
//	}
//
//	public static Matrix rotate2d(double alpha) {
//		Matrix m = new Matrix(3, 3);
//
//		m.set(0, 0, Math.cos(alpha));
//		m.set(0, 1, -Math.sin(alpha));
//		m.set(1, 0, Math.sin(alpha));
//		m.set(1, 1, Math.cos(alpha));
//		m.set(2, 2, 1);
//
//		return m;
//	}
//
//	public static Matrix rotate2dDeg(double alpha) {
//		return rotate2d(Math.PI * alpha / 180.0);
//	}
//
//	public static Matrix reflect2d(boolean doX, boolean doY) {
//		double rx = (doX) ? -1 : 1;
//		double ry = (doY) ? -1 : 1;
//		return scale2d(rx, ry);
//	}
//
//	public static Matrix shear2d(double sx, double sy) {
//		Matrix m = new Matrix(3, 3);
//
//		m.set(0, 0, 1);
//		m.set(0, 1, sx);
//		m.set(1, 0, sy);
//		m.set(1, 1, 1);
//		m.set(2, 2, 1);
//
//		return m;
//	}
}