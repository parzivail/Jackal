package com.parzivail.jackal.util.gltk;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.EnumSet;


/**
 * Simple port of OpenTK's GL wrapper for LWJGL because I like OpenTK's methodology better
 * Created by colby on 9/13/2017.
 */
public class GL
{
	public static void Vertex2(float x, float y)
	{
		GL11.glVertex2f(x, y);
	}

	public static void Vertex2(double x, double y)
	{
		GL11.glVertex2d(x, y);
	}

	public static void Vertex2(Vector2f v)
	{
		Vertex2(v.x, v.y);
	}

	public static void Vertex3(float x, float y, float z)
	{
		GL11.glVertex3f(x, y, z);
	}

	public static void Vertex3(double x, double y, double z)
	{
		GL11.glVertex3d(x, y, z);
	}

	public static void Vertex3(Vector3f v)
	{
		Vertex3(v.x, v.y, v.z);
	}

	public static void TexCoord2(float x, float y)
	{
		GL11.glTexCoord2f(x, y);
	}

	public static void TexCoord2(double x, double y)
	{
		GL11.glTexCoord2d(x, y);
	}

	public static void TexCoord2(Vector2f v)
	{
		TexCoord2(v.x, v.y);
	}

	public static void Normal3(float x, float y, float z)
	{
		GL11.glNormal3f(x, y, z);
	}

	public static void Normal3(double x, double y, double z)
	{
		GL11.glNormal3d(x, y, z);
	}

	public static void Normal3(Vector3f v)
	{
		Normal3(v.x, v.y, v.z);
	}

	public static void Translate(float x, float y, float z)
	{
		GL11.glTranslatef(x, y, z);
	}

	public static void Translate(double x, double y, double z)
	{
		GL11.glTranslated(x, y, z);
	}

	public static void Translate(Vector3f v)
	{
		Translate(v.x, v.y, v.z);
	}

	public static void Rotate(float a, float x, float y, float z)
	{
		GL11.glRotatef(a, x, y, z);
	}

	public static void Rotate(double a, double x, double y, double z)
	{
		GL11.glRotated(a, x, y, z);
	}

	public static void Scale(float x, float y, float z)
	{
		GL11.glScalef(x, y, z);
	}

	public static void Scale(double x, double y, double z)
	{
		GL11.glScaled(x, y, z);
	}

	public static void Scale(Vector3f v)
	{
		Scale(v.x, v.y, v.z);
	}

	public static void Scale(float xyz)
	{
		GL11.glScalef(xyz, xyz, xyz);
	}

	public static void Scale(double xyz)
	{
		GL11.glScaled(xyz, xyz, xyz);
	}

	public static void Enable(EnableCap cap)
	{
		GL11.glEnable(cap.getGlValue());
	}

	public static void Disable(EnableCap cap)
	{
		GL11.glDisable(cap.getGlValue());
	}

	public static void PushAttrib(EnumSet<AttribMask> masks)
	{
		GL11.glPushAttrib(AttribMask.encode(masks));
	}

	public static void PushAttrib(AttribMask mask)
	{
		GL11.glPushAttrib(mask.getGlValue());
	}

	public static void PopAttrib()
	{
		GL11.glPopAttrib();
	}

	public static void PushMatrix()
	{
		GL11.glPushMatrix();
	}

	public static void PopMatrix()
	{
		GL11.glPopMatrix();
	}

	public static void NewList(int list, ListMode mode)
	{
		GL11.glNewList(list, mode.getGlValue());
	}

	public static void EndList()
	{
		GL11.glEndList();
	}

	public static void CallList(int list)
	{
		GL11.glCallList(list);
	}

	public static int GenLists(int range)
	{
		return GL11.glGenLists(range);
	}

	public static void PolygonMode(MaterialFace face, PolygonMode mode)
	{
		GL11.glPolygonMode(face.getGlValue(), mode.getGlValue());
	}

	public static void Begin(PrimitiveType mode)
	{
		GL11.glBegin(mode.getGlValue());
	}

	public static void End()
	{
		GL11.glEnd();
	}
}
