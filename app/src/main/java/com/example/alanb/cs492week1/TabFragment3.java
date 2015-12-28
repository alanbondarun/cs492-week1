package com.example.alanb.cs492week1;

/**
 * Created by shinjaemin on 2015. 12. 23..
 */
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eaglesakura.view.GLTextureView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TabFragment3 extends Fragment implements GLTextureView.Renderer
{
    private GLTextureView glTextureView;

    // projection & view matrix for OpenGL
    private final float[] m_projectionMatrix = new float[16];
    private final float[] m_viewMatrix = new float[16];
    private final float[] m_VPMatrix = new float[16];

    // width & height of the screen
    private float m_width = 0;
    private float m_height = 0;

    // line width
    private final float lineWidth = 2.0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // glTextureView setup
        glTextureView = new GLTextureView(getContext());
        glTextureView.setVersion(GLTextureView.GLESVersion.OpenGLES20);
        glTextureView.setRenderer(this);

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
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        // set the projection matrix
        float ratio = (float) width / height;
        Matrix.frustumM(m_projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 512);

        m_width = width;
        m_height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        /*
        // set the view matrix
        Matrix.setLookAtM(m_viewMatrix, 0, 0, 0, -3, 0, 0, 0, 0, 1.0f, 0);

        // calculate the VP matrix from view & projection matrices
        Matrix.multiplyMM(m_VPMatrix, 0, m_projectionMatrix, 0, m_viewMatrix, 0);
*/
    }

    @Override
    public void onSurfaceDestroyed(GL10 gl) {

    }
}