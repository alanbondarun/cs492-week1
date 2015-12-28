package com.example.alanb.cs492week1;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.io.StringBufferInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Created by alanb on 12/28/2015.
 */
public class OmokObject {
    private static final String TAG = "OmokObject";

    // buffer objects
    private FloatBuffer m_vertexBuffer;
    private ShortBuffer m_drawListBuffer;

    // number of coordinates per vertex in the array
    static final int COORDS_PER_VERTEX = 3;

    static final float m_coordArray[] = {
             0.4f,  0.4f, 0.0f,
            -0.4f,  0.4f, 0.0f,
            -0.4f, -0.4f, 0.0f,
             0.4f, -0.4f, 0.0f,
    };
    private short m_drawOrderArray[] = { 0, 1, 3, 1, 2, 3 };

    // an OpenGL ES program
    private int m_program;

    // scaleFactor and position information
    private float m_scaleFactor;
    private int m_xPos;
    private int m_yPos;

    // true if this object is black
    boolean m_isBlack;

    // initialization of the object
    public OmokObject(int program, float scaleFactor, int xpos, int ypos, boolean isBlack)
    {
        m_scaleFactor = scaleFactor;
        m_xPos = xpos;
        m_yPos = ypos;
        m_isBlack = isBlack;

        // initialize vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(m_coordArray.length * Float.SIZE);
        bb.order(ByteOrder.nativeOrder());
        m_vertexBuffer = bb.asFloatBuffer();
        m_vertexBuffer.put(m_coordArray);
        m_vertexBuffer.position(0);

        // initialize draw list buffer
        ByteBuffer bb2 = ByteBuffer.allocateDirect(m_drawOrderArray.length * Short.SIZE);
        bb2.order(ByteOrder.nativeOrder());
        m_drawListBuffer = bb2.asShortBuffer();
        m_drawListBuffer.put(m_drawOrderArray);
        m_drawListBuffer.position(0);

        // register the program
        m_program = program;
    }

    public int getXPos()
    {
        return m_xPos;
    }

    public int getYPos()
    {
        return m_yPos;
    }

    public boolean isBlack()
    {
        return m_isBlack;
    }

    public void draw(float[] VPMatrix)
    {
        // use the created program in the OpenGL ES
        GLES20.glUseProgram(m_program);

        // get handle to vertex shader's vPosition
        int positionHandle = GLES20.glGetAttribLocation(m_program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        // prepare the coordinates for this shape
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, m_vertexBuffer);

        // get handle to fragment shader's vColor
        int colorHandle = GLES20.glGetUniformLocation(m_program, "vColor");

        // set the color
        if (m_isBlack)
        {
            GLES20.glUniform4f(colorHandle, 0.05f, 0.05f, 0.05f, 1.0f);
        }
        else
        {
            GLES20.glUniform4f(colorHandle, 0.9f, 0.9f, 0.9f, 1.0f);
        }

        // construct the model matrix
        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, m_xPos, m_yPos, 0);
        Matrix.scaleM(modelMatrix, 0, m_scaleFactor, m_scaleFactor, 1);

        // construct the MVP matrix
        float[] MVPMatrix = new float[16];
        Matrix.multiplyMM(MVPMatrix, 0, VPMatrix, 0, modelMatrix, 0);

        // get the handle to shape's transformation matrix
        int MVPMatrixHandle = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");

        // pass the transformation matrix to the shader
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, MVPMatrix, 0);

        // draw this shape
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, m_drawOrderArray.length, GLES20.GL_UNSIGNED_SHORT, m_drawListBuffer);

        // disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
