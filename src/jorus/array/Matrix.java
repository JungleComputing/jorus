package jorus.array;

public class Matrix implements Cloneable {

	/*** Compare Horus - HxBasis/Main/HxMatrix.c ***/
	private static final double MATRIX_EPS = 1e-12;

	private final int rows, columns;
	private final double[][] matrix;

	public Matrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		matrix = new double[rows][columns];
	}

	public Matrix(double[][] matrix) {
		this.matrix = matrix;
		rows = matrix.length;
		columns = matrix[0].length;
	}

	public void printMatrix() {
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				System.out.printf("%.2f, ", matrix[y][x]);
			}
			System.out.println();
		}
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return the columns
	 */
	public int getColumns() {
		return columns;
	}
	
	public double[][] getData() {
		return matrix;
	}

	public static Matrix translate2d(double x, double y) {
		double[][] data = new double[][] { { 1, 0, x }, { 0, 1, y },
				{ 0, 0, 1 } };
		return new Matrix(data);
	}

	 public static Matrix scale2d(double sx, double sy) {
		double[][] data = new double[][] { { sx, 0, 0 }, { 0, sy, 0 },
				{ 0, 0, 1 } };
		return new Matrix(data);
	}

	 public static Matrix rotate2d(double alpha) {
		double cosA = Math.cos(alpha);
		double sinA = Math.sin(alpha);

		double[][] data = new double[][] { { cosA, -sinA, 0 },
				{ sinA, cosA, 0 }, { 0, 0, 1 } };
		return new Matrix(data);
	}

	 public static Matrix rotate2dDeg(double alpha) {
		return rotate2d(Math.PI * alpha / 180.0);
	}

	 public static Matrix reflect2d(boolean doX, boolean doY) {
		double rx = doX ? -1 : 1;
		double ry = doY ? -1 : 1;

		double[][] data = new double[][] { { rx, 0, 0 }, { 0, ry, 0 },
				{ 0, 0, 1 } };
		return new Matrix(data);
	}

	 public static Matrix shear2d(double sx, double sy) {
		double[][] data = new double[][] { { 1, sx, 0 }, { sy, 1, 0 },
				{ 0, 0, 1 } };
		return new Matrix(data);
	}

	public static Matrix transpose(Matrix matrix) {
		Matrix result = new Matrix(matrix.columns, matrix.rows);
		for (int i = 0; i < matrix.columns; i++) {
			for (int j = 0; j < matrix.rows; j++) {
				result.matrix[j][i] = matrix.matrix[i][j];
			}
		}
		return result;
	}

	public void inverse() throws Exception {
		if (rows != columns) {
			throw new Exception("Inverse: matrix is not square!");
		}

		double wmax, wmin;

		double[][] v = new double[rows][rows];
		double[] w = new double[rows];
		double[][] u = cloneData();
		matrix_svdcmp(u, w, v);

		wmax = 0.0;
		for (int j = 0; j < rows; j++) {
			if (w[j] > wmax) {
				wmax = w[j];
			}
		}
		wmin = wmax * MATRIX_EPS;

		for (int k = 0; k < rows; k++) {
			if (w[k] < wmin) {
				w[k] = 0.0;
			} else {
				w[k] = 1.0 / w[k];
			}
		}

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < rows; j++) {
				matrix[i][j] = 0.0;
				for (int k = 0; k < rows; k++) {
					matrix[i][j] += v[i][k] * w[k] * u[j][k];
				}
			}
		}
	}

	@Override
	public Matrix clone() {
		return new Matrix(cloneData());
	}

	private double[][] cloneData() {
		double[][] newMatrix = matrix.clone();
		for (int row = 0; row < rows; row++) {
			newMatrix[row] = matrix[row].clone();
		}
		return newMatrix;
	}

	public static Matrix multMM(Matrix a, Matrix b) throws Exception {
		double[][] m;

		if (a.columns != b.rows) {
			throw new Exception("nonconformant HxMatrix * HxMatrix operands.");
		}

		m = new double[a.rows][b.columns];
		for (int i = 0; i < a.rows; i++) {
			for (int j = 0; j < b.columns; j++) {
				double sum = 0;
				for (int k = 0; k < a.columns; k++) {
					sum += a.matrix[i][k] * b.matrix[k][j];
				}
				m[i][j] = sum;
			}
		}
		return new Matrix(m);
	}

	public static double[] multMV(Matrix matrix, double[] vector)
			throws Exception {
		if (vector.length != matrix.columns) {
			throw new Exception("nonconformant HxMatrix * HxVector operands.");
		}

		double[] result = new double[matrix.rows];
		for (int i = 0; i < matrix.rows; i++) {
			double sum = 0;
			for (int j = 0; j < matrix.columns; j++) {
				sum += matrix.matrix[i][j] * vector[j];
			}
			result[i] = sum;
		}
		return result;
	}

	public static double[] multVM(double[] vector, Matrix matrix)
			throws Exception {
		if (vector.length != matrix.rows) {
			throw new Exception("nonconformant HxVector * HxMatrix operands.");
		}

		double[] result = new double[matrix.columns];
		for (int i = 0; i < matrix.columns; i++) {
			double sum = 0;
			for (int j = 0; j < matrix.rows; j++) {
				sum += vector[j] * matrix.matrix[j][i];
			}
			result[i] = sum;
		}
		return result;
	}

	/*
	 * Given a matrix a[m][n], this routine computes its singular value
	 * decomposition, A = U*W*V^{T}. The matrix U replaces a on output. The
	 * diagonal matrix of singular values W is output as a vector w[n]. The
	 * matrix V (not the transpose V^{T}) is output as v[n][n]. m must be
	 * greater or equal to n; if it is smaller, then a should be filled up to
	 * square with zero rows.
	 */
	private static void matrix_svdcmp(double[][] a_u, double[] w, double[][] v)
			throws Exception {
		//FIXME very ugly C-function ported to Java. Needs cleanup
		int m = a_u.length;
		int n = a_u[0].length;
		int i, its, j, jj, k, l = 0, nm = 0;
		double c, f, h, s, x, y, z;
		double anorm = 0.0, g = 0.0, scale = 0.0;
		double[] rv1 = new double[n];
	
		if (m < n) {
			throw new Exception(
					"SVDCMP: Augment matrix A with extra rows of zeros.");
		}
	
		/* Householder reduction to bidiagonal form. */
		for (i = 0; i < n; i++) {
			l = i + 1;
			rv1[i] = scale * g;
			g = s = scale = 0.0;
			if (i < m) {
				for (k = i; k < m; k++)
					scale += Math.abs(a_u[k][i]);
				if (scale != 0.) {
					for (k = i; k < m; k++) {
						a_u[k][i] /= scale;
						s += a_u[k][i] * a_u[k][i];
					}
					;
					f = a_u[i][i];
					g = -sign(Math.sqrt(s), f);
					h = f * g - s;
					a_u[i][i] = f - g;
					if (i != n - 1) {
						for (j = l; j < n; j++) {
							for (s = 0.0, k = i; k < m; k++)
								s += a_u[k][i] * a_u[k][j];
							f = s / h;
							for (k = i; k < m; k++)
								a_u[k][j] += f * a_u[k][i];
						}
						;
					}
					;
					for (k = i; k < m; k++)
						a_u[k][i] *= scale;
				}
				;
			}
			;
			w[i] = scale * g;
			g = s = scale = 0.0;
			if (i < m && i != n - 1) {
				for (k = l; k < n; k++)
					scale += Math.abs(a_u[i][k]);
				if (scale != 0.) {
					for (k = l; k < n; k++) {
						a_u[i][k] /= scale;
						s += a_u[i][k] * a_u[i][k];
					}
					;
					f = a_u[i][l];
					g = -sign(Math.sqrt(s), f);
					h = f * g - s;
					a_u[i][l] = f - g;
					for (k = l; k < n; k++)
						rv1[k] = a_u[i][k] / h;
					if (i != m - 1) {
						for (j = l; j < m; j++) {
							for (s = 0.0, k = l; k < n; k++)
								s += a_u[j][k] * a_u[i][k];
							for (k = l; k < n; k++)
								a_u[j][k] += s * rv1[k];
						}
						;
					}
					;
					for (k = l; k < n; k++)
						a_u[i][k] *= scale;
				}
				;
			}
			;
			anorm = Math.max(anorm, (Math.abs(w[i]) + Math.abs(rv1[i])));
		}
		;
		/* Accumulation of right-hand transformations. */
		for (i = n - 1; 0 <= i; i--) {
			if (i < n - 1) {
				if (g != 0.) {
					for (j = l; j < n; j++)
						v[j][i] = (a_u[i][j] / a_u[i][l]) / g;
					/* Double division to avoid possible underflow: */
					for (j = l; j < n; j++) {
						for (s = 0.0, k = l; k < n; k++)
							s += a_u[i][k] * v[k][j];
						for (k = l; k < n; k++)
							v[k][j] += s * v[k][i];
					}
				}
				for (j = l; j < n; j++)
					v[i][j] = v[j][i] = 0.0;
			}
			v[i][i] = 1.0;
			g = rv1[i];
			l = i;
		}
		/* Accumulation of left-hand transformations. */
		for (i = n - 1; 0 <= i; i--) {
			l = i + 1;
			g = w[i];
			if (i < n - 1)
				for (j = l; j < n; j++)
					a_u[i][j] = 0.0;
			if (g != 0.) {
				g = 1.0 / g;
				if (i != n - 1) {
					for (j = l; j < n; j++) {
						for (s = 0.0, k = l; k < m; k++)
							s += a_u[k][i] * a_u[k][j];
						f = (s / a_u[i][i]) * g;
						for (k = i; k < m; k++)
							a_u[k][j] += f * a_u[k][i];
					}
				}
				for (j = i; j < m; j++)
					a_u[j][i] *= g;
			} else
				for (j = i; j < m; j++)
					a_u[j][i] = 0.0;
			++a_u[i][i];
		}
		/* Diagonalization of the bidiagonal form. */
		for (k = n - 1; 0 <= k; k--) /* Loop over singular values. */
		{
			for (its = 0; its < 30; its++) /* Loop over allowed iterations. */
			{
				boolean flag = true;
				for (l = k; 0 <= l; l--) /* Test for splitting: */
				{
					nm = l - 1; /* Note that rv1[0] is always zero. */
					if (Math.abs(rv1[l]) + anorm == anorm) {
						flag = false;
						break;
					}
					if (Math.abs(w[nm]) + anorm == anorm)
						break;
				}
				if (flag) {
					c = 0.0; /* Cancellation of rv1[l], if l>0: */
					s = 1.0;
					for (i = l; i <= k; i++) {
						f = s * rv1[i];
						if (Math.abs(f) + anorm != anorm) {
							g = w[i];
							h = pythagoras(f, g);
							w[i] = h;
							h = 1.0 / h;
							c = g * h;
							s = (-f * h);
							for (j = 0; j < m; j++) {
								y = a_u[j][nm];
								z = a_u[j][i];
								a_u[j][nm] = y * c + z * s;
								a_u[j][i] = z * c - y * s;
							}
						}
					}
				}
				z = w[k];
				if (l == k) /* Convergence. */
				{
					if (z < 0.0) /* Singular value is made non-negative. */
					{
						w[k] = -z;
						for (j = 0; j < n; j++)
							v[j][k] = (-v[j][k]);
					}
					;
					break;
				}
				;
				if (its == 29) {
					throw new Exception("No convergence in 30 SVDCMP iterations.");
				}
				x = w[l]; /* Shift from bottom 2-by-2 minor. */
				nm = k - 1;
				y = w[nm];
				g = rv1[nm];
				h = rv1[k];
				f = ((y - z) * (y + z) + (g - h) * (g + h)) / (2.0 * h * y);
				g = pythagoras(f, 1.0);
				f = ((x - z) * (x + z) + h * ((y / (f + sign(g, f))) - h)) / x;
	
				/* Next QR transformation: */
	
				c = s = 1.0;
				for (j = l; j <= nm; j++) {
					i = j + 1;
					g = rv1[i];
					y = w[i];
					h = s * g;
					g = c * g;
					z = pythagoras(f, h);
					rv1[j] = z;
					c = f / z;
					s = h / z;
					f = x * c + g * s;
					g = g * c - x * s;
					h = y * s;
					y = y * c;
					for (jj = 0; jj < n; jj++) {
						x = v[jj][j];
						z = v[jj][i];
						v[jj][j] = x * c + z * s;
						v[jj][i] = z * c - x * s;
					}
					z = pythagoras(f, h);
					w[j] = z; /* Rotation can be arbitrary if z = 0. */
					if (z != 0.) {
						z = 1.0 / z;
						c = f * z;
						s = h * z;
					}
					f = (c * g) + (s * y);
					x = (c * y) - (s * g);
					for (jj = 0; jj < m; jj++) {
						y = a_u[jj][j];
						z = a_u[jj][i];
						a_u[jj][j] = y * c + z * s;
						a_u[jj][i] = z * c - y * s;
					}
				}
				rv1[l] = 0.0;
				rv1[k] = f;
				w[k] = x;
			}
		}
	}

	private static double sign(double a, double b) {
		return b < 0.0 ? -Math.abs(a) : Math.abs(a);
	}

	private static double pythagoras(double a, double b) {
		double at = Math.abs(a);
		double bt = Math.abs(b);
		double ct;
		if (at > bt) {
			ct = bt / at;
			return at * Math.sqrt(1. + ct * ct);
		} else if (bt > 0.) {
			ct = at / bt;
			return bt * Math.sqrt(1. + ct * ct);
		} else {
			return 0;
		}
	}
}
