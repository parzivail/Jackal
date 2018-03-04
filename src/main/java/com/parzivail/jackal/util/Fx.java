package com.parzivail.jackal.util;

import com.parzivail.jackal.util.gltk.GL;
import com.parzivail.jackal.util.gltk.PrimitiveType;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by colby on 9/13/2017.
 */
public class Fx
{
	public static class Util
	{
		public static int GetRgb(int r, int g, int b)
		{
			int rgb = r;
			rgb = (rgb << 8) + g;
			rgb = (rgb << 8) + b;
			return rgb;
		}

		public static int GetRgba(int r, int g, int b, int a)
		{
			int rgba = a;
			rgba = (rgba << 8) + r;
			rgba = (rgba << 8) + g;
			rgba = (rgba << 8) + b;
			return rgba;
		}

		public static double Lerp(double a, double b, double f)
		{
			return (1 - f) * a + f * b;
		}

		public static Vector2f Lerp(Vector2f a, Vector2f b, float f)
		{
			double x = Lerp(a.x, b.x, f);
			double y = Lerp(a.y, b.y, f);
			return new Vector2f((float)x, (float)y);
		}

		public static float Hz(float hz)
		{
			return MathHelper.sin((float)((System.currentTimeMillis() % (long)(1000 / hz)) / (1000 / hz) * Math.PI));
		}

		public static float HzPercent(float hz)
		{
			return (System.currentTimeMillis() % (long)(1000 / hz)) / (1000 / hz);
		}

		//		public static Vector3f Vector3f(Vec3 startPos)
		//		{
		//			return new Vector3f((float)startPos.xCoord, (float)startPos.yCoord, (float)startPos.zCoord);
		//		}
	}

	public static class D2
	{
	        /*
	            Public Methods
             */

		public static void DrawLine(float x1, float y1, float x2, float y2)
		{
			GL.Begin(PrimitiveType.LineStrip);
			GL.Vertex2(x1, y1);
			GL.Vertex2(x2, y2);
			GL.End();
		}

		public static void DrawLine(Vector2f a, Vector2f b)
		{
			DrawLine(a.x, a.y, b.x, b.y);
		}

		public static void DrawWireRectangle(float x, float y, float w, float h)
		{
			Rectangle(x, y, w, h, PrimitiveType.LineLoop);
		}

		public static void DrawSolidRectangle(float x, float y, float w, float h)
		{
			Rectangle(x, y, w, h, PrimitiveType.Quads);
		}

		public static void DrawWireCircle(float x, float y, float radius)
		{
			Circle(x, y, radius, PrimitiveType.LineLoop);
		}

		public static void DrawSolidCircle(float x, float y, float radius)
		{
			Circle(x, y, radius, PrimitiveType.TriangleFan);
		}

		public static void DrawWirePieSlice(float x, float y, float radius, float percent)
		{
			Pie(x, y, radius, percent, PrimitiveType.LineLoop);
		}

		public static void DrawSolidPieSlice(float x, float y, float radius, float percent)
		{
			Pie(x, y, radius, percent, PrimitiveType.TriangleFan);
		}

		public static void DrawWireTriangle(float x, float y, float sideLen)
		{
			Triangle(x, y, sideLen, PrimitiveType.LineLoop);
		}

		public static void DrawSolidTriangle(float x, float y, float sideLen)
		{
			Triangle(x, y, sideLen, PrimitiveType.TriangleFan);
		}

            /*
                Private Methods
             */

		static void Rectangle(float x, float y, float w, float h, PrimitiveType mode)
		{
			GL.Begin(mode);
			GL.Vertex3(x, y, 0);
			GL.Vertex3(x, y + h, 0);
			GL.Vertex3(x + w, y + h, 0);
			GL.Vertex3(x + w, y, 0);
			GL.End();
		}

		static void Circle(float x, float y, float radius, PrimitiveType mode)
		{
			GL.Begin(mode);
			for (int i = 0; i <= 360; i++)
			{
				float nx = MathHelper.sin(i * 3.141526f / 180) * radius;
				float ny = MathHelper.cos(i * 3.141526f / 180) * radius;
				GL.Vertex2(nx + x, ny + y);
			}
			GL.End();
		}

		static void Triangle(float x, float y, float sideLen, PrimitiveType mode)
		{
			GL.Begin(mode);
			GL.Vertex2(x, y - sideLen / 2);
			GL.Vertex2(x - sideLen / 2, y + sideLen / 2);
			GL.Vertex2(x + sideLen / 2, y + sideLen / 2);
			GL.End();
		}

		static void Pie(float x, float y, float radius, float percent, PrimitiveType mode)
		{
			GL.Begin(mode);
			GL.Vertex2(x, y);
			for (int i = 0; i <= 360 * percent; i++)
			{
				float nx = MathHelper.sin(i * 3.141526f / 180) * radius;
				float ny = MathHelper.cos(i * 3.141526f / 180) * radius;
				GL.Vertex2(nx + x, ny + y);
			}
			GL.End();
		}

		public static void CentripetalCatmullRomTo(Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3, float numSamplePoints)
		{
			CentripetalCatmullRomTo(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, numSamplePoints);
		}

		public static void CentripetalCatmullRomTo(float p0X, float p0Y, float p1X, float p1Y, float p2X, float p2Y, float p3X, float p3Y, float numSamplePoints)
		{
			GL.Begin(PrimitiveType.LineStrip);
			for (int i = 0; i <= numSamplePoints; i++)
				GL.Vertex2(EvalCentripetalCatmullRom(p0X, p0Y, p1X, p1Y, p2X, p2Y, p3X, p3Y, i / numSamplePoints));
			GL.End();
		}

		public static void CentripetalCatmullRomToVertexOnly(Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3, float numSamplePoints)
		{
			CentripetalCatmullRomToVertexOnly(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, numSamplePoints);
		}

		public static void CentripetalCatmullRomToVertexOnly(float p0X, float p0Y, float p1X, float p1Y, float p2X, float p2Y, float p3X, float p3Y, float numSamplePoints)
		{
			for (int i = 0; i <= numSamplePoints; i++)
				GL.Vertex2(EvalCentripetalCatmullRom(p0X, p0Y, p1X, p1Y, p2X, p2Y, p3X, p3Y, i / numSamplePoints));
		}

		public static Vector2f EvalCentripetalCatmullRom(Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3, float percentageAcross)
		{
			return EvalCentripetalCatmullRom(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, percentageAcross);
		}

		public static Vector2f EvalCentripetalCatmullRom(float p0X, float p0Y, float p1X, float p1Y, float p2X, float p2Y, float p3X, float p3Y, float percentageAcross)
		{
			//We use 0.25 as our power because of the alpha value of 0.5 of centripetal catmull rom splines
			//============================1st Stage==================================//
			double dt0 = Math.pow((p1X - p0X) * (p1X - p0X) + (p1Y - p0Y) * (p1Y - p0Y), 0.25);
			double dt1 = Math.pow((p2X - p1X) * (p2X - p1X) + (p2Y - p1Y) * (p2Y - p1Y), 0.25);
			double dt2 = Math.pow((p3X - p2X) * (p3X - p2X) + (p3Y - p2Y) * (p3Y - p2Y), 0.25);
			//=============================2nd Stage==================================//
			double t1X = ((p1X - p0X) / dt0 - (p2X - p0X) / (dt0 + dt1) + (p2X - p1X) / dt1) * dt1;
			double t1Y = ((p1Y - p0Y) / dt0 - (p2Y - p0Y) / (dt0 + dt1) + (p2Y - p1Y) / dt1) * dt1;
			double t2X = ((p2X - p1X) / dt1 - (p3X - p1X) / (dt1 + dt2) + (p3X - p2X) / dt2) * dt1;
			double t2Y = ((p2Y - p1Y) / dt1 - (p3Y - p1Y) / (dt1 + dt2) + (p3Y - p2Y) / dt2) * dt1;
			//=============================3rd Stage=================================//
			double c0X = p1X;
			double c0Y = p1Y;
			double c1X = t1X;
			double c1Y = t1Y;
			double c2X = -3 * p1X + 3 * p2X - 2 * t1X - t2X;
			double c2Y = -3 * p1Y + 3 * p2Y - 2 * t1Y - t2Y;
			double c3X = 2 * p1X - 2 * p2X + t1X + t2X;
			double c3Y = 2 * p1Y - 2 * p2Y + t1Y + t2Y;
			//==============================4th Stage================================//
			//Now to evalute it with our parameterization t, we start at 1 as we are already at 0,and we need
			//to make sure we have i=numSample points so we cn get the last point
			double t = percentageAcross;
			double t2 = t * t;
			double t3 = t2 * t;
			return new Vector2f((float)(c0X + c1X * t + c2X * t2 + c3X * t3), (float)(c0Y + c1Y * t + c2Y * t2 + c3Y * t3));
		}
	}

	public static class D3
	{
		private static double[][] _vertsBox = new double[8][3];

		private static double[][] _normalsBox = {
				{ -1.0, 0.0, 0.0 },
				{ 0.0, 1.0, 0.0 },
				{ 1.0, 0.0, 0.0 },
				{ 0.0, -1.0, 0.0 },
				{ 0.0, 0.0, 1.0 },
				{ 0.0, 0.0, -1.0 }
		};

		private static int[][] _facesBox = {
				{ 0, 1, 2, 3 }, { 3, 2, 6, 7 }, { 7, 6, 5, 4 }, { 4, 5, 1, 0 }, { 5, 6, 2, 1 }, { 7, 4, 0, 3 }
		};

		private static double[][] _dodec = new double[20][3];

		static
		{
			// cube
			_vertsBox[0][0] = _vertsBox[1][0] = _vertsBox[2][0] = _vertsBox[3][0] = -0.5f;
			_vertsBox[4][0] = _vertsBox[5][0] = _vertsBox[6][0] = _vertsBox[7][0] = 0.5f;
			_vertsBox[0][1] = _vertsBox[1][1] = _vertsBox[4][1] = _vertsBox[5][1] = -0.5f;
			_vertsBox[2][1] = _vertsBox[3][1] = _vertsBox[6][1] = _vertsBox[7][1] = 0.5f;
			_vertsBox[0][2] = _vertsBox[3][2] = _vertsBox[4][2] = _vertsBox[7][2] = -0.5f;
			_vertsBox[1][2] = _vertsBox[2][2] = _vertsBox[5][2] = _vertsBox[6][2] = 0.5f;

			// dodec
			double alpha;
			double beta;
			//alpha = sqrt(2.0 / (3.0 + sqrt(5.0)));
			//beta = 1.0 + sqrt(6.0 / (3.0 + sqrt(5.0)) - 2.0 + 2.0 * sqrt(2.0 / (3.0 + sqrt(5.0))));
			alpha = 0.618033989;
			beta = 1.618033989;
			_dodec[0][0] = -alpha;
			_dodec[0][1] = 0;
			_dodec[0][2] = beta;
			_dodec[1][0] = alpha;
			_dodec[1][1] = 0;
			_dodec[1][2] = beta;
			_dodec[2][0] = -1;
			_dodec[2][1] = -1;
			_dodec[2][2] = -1;
			_dodec[3][0] = -1;
			_dodec[3][1] = -1;
			_dodec[3][2] = 1;
			_dodec[4][0] = -1;
			_dodec[4][1] = 1;
			_dodec[4][2] = -1;
			_dodec[5][0] = -1;
			_dodec[5][1] = 1;
			_dodec[5][2] = 1;
			_dodec[6][0] = 1;
			_dodec[6][1] = -1;
			_dodec[6][2] = -1;
			_dodec[7][0] = 1;
			_dodec[7][1] = -1;
			_dodec[7][2] = 1;
			_dodec[8][0] = 1;
			_dodec[8][1] = 1;
			_dodec[8][2] = -1;
			_dodec[9][0] = 1;
			_dodec[9][1] = 1;
			_dodec[9][2] = 1;
			_dodec[10][0] = beta;
			_dodec[10][1] = alpha;
			_dodec[10][2] = 0;
			_dodec[11][0] = beta;
			_dodec[11][1] = -alpha;
			_dodec[11][2] = 0;
			_dodec[12][0] = -beta;
			_dodec[12][1] = alpha;
			_dodec[12][2] = 0;
			_dodec[13][0] = -beta;
			_dodec[13][1] = -alpha;
			_dodec[13][2] = 0;
			_dodec[14][0] = -alpha;
			_dodec[14][1] = 0;
			_dodec[14][2] = -beta;
			_dodec[15][0] = alpha;
			_dodec[15][1] = 0;
			_dodec[15][2] = -beta;
			_dodec[16][0] = 0;
			_dodec[16][1] = beta;
			_dodec[16][2] = alpha;
			_dodec[17][0] = 0;
			_dodec[17][1] = beta;
			_dodec[17][2] = -alpha;
			_dodec[18][0] = 0;
			_dodec[18][1] = -beta;
			_dodec[18][2] = alpha;
			_dodec[19][0] = 0;
			_dodec[19][1] = -beta;
			_dodec[19][2] = -alpha;
		}

            /*
                Public Methods
             */

		public static void DrawLine(double x1, double y1, double z1, double x2, double y2, double z2)
		{
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d(x1, y1, z1);
			GL11.glVertex3d(x2, y2, z2);
			GL11.glEnd();
		}

		public static void DrawWireBox()
		{
			Box(GL11.GL_LINE_LOOP);
		}

		public static void DrawSolidBox()
		{
			Box(GL11.GL_QUADS);
		}

		public static void DrawWireTorus(double innerRadius, double outerRadius, int nsides, int rings)
		{
			GL11.glPushAttrib(GL11.GL_POLYGON_BIT);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			Doughnut(innerRadius, outerRadius, nsides, rings, GL11.GL_LINE_STRIP);
			GL11.glPopAttrib();
		}

		public static void DrawSolidTorus(double innerRadius, double outerRadius, int nsides, int rings)
		{
			Doughnut(innerRadius, outerRadius, nsides, rings, GL11.GL_QUAD_STRIP);
		}

            /*
                Private Methods
             */

		private static void Box(int type)
		{
			for (int i = 5; i >= 0; i--)
			{
				GL11.glBegin(type);
				GL11.glNormal3d(_normalsBox[i][0], _normalsBox[i][1], _normalsBox[i][2]);
				GL11.glVertex3d(_vertsBox[_facesBox[i][0]][0], _vertsBox[_facesBox[i][0]][1], _vertsBox[_facesBox[i][0]][2]);
				GL11.glVertex3d(_vertsBox[_facesBox[i][1]][0], _vertsBox[_facesBox[i][1]][1], _vertsBox[_facesBox[i][1]][2]);
				GL11.glVertex3d(_vertsBox[_facesBox[i][2]][0], _vertsBox[_facesBox[i][2]][1], _vertsBox[_facesBox[i][2]][2]);
				GL11.glVertex3d(_vertsBox[_facesBox[i][3]][0], _vertsBox[_facesBox[i][3]][1], _vertsBox[_facesBox[i][3]][2]);
				GL11.glEnd();
			}
		}

		private static void Doughnut(double r, double rOuter, int nsides, int rings, int type)
		{
			int i, j;
			double theta, phi, theta1;
			double cosTheta, sinTheta;
			double cosTheta1, sinTheta1;
			double ringDelta, sideDelta;

			ringDelta = 2.0 * Math.PI / rings;
			sideDelta = 2.0 * Math.PI / nsides;

			theta = 0.0;
			cosTheta = 1.0;
			sinTheta = 0.0;
			for (i = rings - 1; i >= 0; i--)
			{
				theta1 = theta + ringDelta;
				cosTheta1 = MathHelper.cos((float)theta1);
				sinTheta1 = MathHelper.sin((float)theta1);
				GL11.glBegin(type);
				phi = 0.0;
				for (j = nsides; j >= 0; j--)
				{
					double cosPhi, sinPhi, dist;

					phi += sideDelta;
					cosPhi = MathHelper.cos((float)phi);
					sinPhi = MathHelper.sin((float)phi);
					dist = rOuter + r * cosPhi;

					GL11.glNormal3d(cosTheta1 * cosPhi, -sinTheta1 * cosPhi, sinPhi);
					GL11.glVertex3d(cosTheta1 * dist, -sinTheta1 * dist, r * sinPhi);
					GL11.glNormal3d(cosTheta * cosPhi, -sinTheta * cosPhi, sinPhi);
					GL11.glVertex3d(cosTheta * dist, -sinTheta * dist, r * sinPhi);
				}
				GL11.glEnd();
				theta = theta1;
				cosTheta = cosTheta1;
				sinTheta = sinTheta1;
			}
		}
	}
}
