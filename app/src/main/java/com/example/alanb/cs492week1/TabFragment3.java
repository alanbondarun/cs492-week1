package com.example.alanb.cs492week1;

/**
 * Created by shinjaemin on 2015. 12. 23..
 */
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.eaglesakura.view.GLTextureView;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TabFragment3 extends Fragment
        implements GLTextureView.Renderer, ViewTouchEventListener
{
    private final static String TAG = "TabFragment3";

    private GLTextureView glTextureView;

    // projection & view matrix for OpenGL
    private final float[] m_projectionMatrix = new float[16];
    private final float[] m_viewMatrix = new float[16];
    private final float[] m_VPMatrix = new float[16];

    // width & height of the screen
    private float m_width = 0;
    private float m_height = 0;

    // an OmokBoard
    private OmokBoard m_board;

    // hard-coded vertex & fragment shader
    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "   gl_FragColor = vColor;" +
                    "}";

    // OpenGL shader program
    private int m_program;

    private final int PIXEL_SPACE_SIZE = 75;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // glTextureView setup
        glTextureView = new GLTextureView(getContext());
        glTextureView.setVersion(GLTextureView.GLESVersion.OpenGLES20);
        glTextureView.setRenderingThreadType(GLTextureView.RenderingThreadType.BackgroundThread);
        glTextureView.setRenderer(this);
        glTextureView.setViewTouchEventListener(this);

        return glTextureView;
    }

    @Override
    public void onPause()
    {
        glTextureView.onPause();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        glTextureView.onResume();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // set the background color to white
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // load vertex & fragment shader to the OpenGL context
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // create an empty OpenGL ES program
        m_program = GLES20.glCreateProgram();

        // add the vertex & fragment shader to the program, and create executables
        GLES20.glAttachShader(m_program, vertexShader);
        GLES20.glAttachShader(m_program, fragmentShader);
        GLES20.glLinkProgram(m_program);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        // set the projection matrix
        float ratio = (float) width / height;
        Matrix.frustumM(m_projectionMatrix, 0, ratio, -ratio, -1, 1, 1, 512);

        // set the view matrix
        Matrix.setLookAtM(m_viewMatrix, 0, 0, 0, -5, 0, 0, 0, 0, 1.0f, 0);

        // calculate the VP matrix from view & projection matrices
        Matrix.multiplyMM(m_VPMatrix, 0, m_projectionMatrix, 0, m_viewMatrix, 0);

        // save the current width and height
        m_width = width;
        m_height = height;

        float[] spaceCoord = new float[4];
        convertToModelCoord(width/2 + PIXEL_SPACE_SIZE, height/2 + PIXEL_SPACE_SIZE, spaceCoord);
        float[] endCoord = new float[4];
        convertToModelCoord(width, height, endCoord);
        m_board = new OmokBoard(m_program, spaceCoord[0],
                Math.round((float)(Math.floor(endCoord[0]/spaceCoord[0] - 0.5))) * 2 + 1,
                Math.round((float)(Math.floor(endCoord[1]/spaceCoord[1] - 0.5))) * 2 + 1);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        // clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        m_board.draw(m_VPMatrix);
    }

    @Override
    public void onSurfaceDestroyed(GL10 gl) {

    }

    // load the shader from the shader code
    private static int loadShader(int type, String shaderCode)
    {
        // create a shader based on the given TYPE
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader, and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        IntBuffer intBuffer = IntBuffer.allocate(1);
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, intBuffer);
        //Log.d(TAG, "shader code compilation, result=" + intBuffer.get(0));

        return shader;
    }

    // convert viewport coordinate -> model coordinate
    public void convertToModelCoord(float x, float y, float[] modelCoord)
    {
        float viewport_x = (x - m_width/2.0f) / (m_width/2.0f);
        float viewport_y = (y - m_height/2.0f) / (m_height/2.0f);

        float[] MVector = {1.0f, -1.0f, 0, 1};
        float[] VPVector = new float[4];
        Matrix.multiplyMV(VPVector, 0, m_VPMatrix, 0, MVector, 0);

        float XScaleFactor = VPVector[3] / VPVector[0];
        float YScaleFactor = VPVector[3] / VPVector[1];
        float ZScaleFactor = VPVector[3] / VPVector[2];

        modelCoord[0] = viewport_x * XScaleFactor;
        modelCoord[1] = viewport_y * YScaleFactor;
        modelCoord[2] = 1.0f * ZScaleFactor;            // not verified...
        modelCoord[3] = 1;
    }

    @Override
    public void onViewTouched(MotionEvent e)
    {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                float[] modelCoord = new float[4];
                convertToModelCoord(x, y, modelCoord);
                m_board.addObject(modelCoord);
                break;
        }
    }
}