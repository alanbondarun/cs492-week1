package com.example.alanb.cs492week1;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * Created by alanb on 12/28/2015.
 */

/* an Omok board */
public class OmokBoard
{
    private final static String TAG = "OmokBoard";

    private int m_rows, m_cols;
    private float m_scaleFactor;

    // coordinates and draw list for the board
    private ArrayList<Float> m_coordList;
    private ArrayList<Short> m_drawList;

    // program used for OpenGL drawing
    private int m_program;

    // buffer objects
    private FloatBuffer m_vertexBuffer;
    private ShortBuffer m_drawListBuffer;

    // number of coordinates per vertex in the array
    static final int COORDS_PER_VERTEX = 3;

    // the width of the line!
    private final float LINE_WIDTH = 0.01f;

    // threshold for the touch event
    private final double THRESHOLD_TOUCH;

    // list of Omokobject's
    private ArrayList<OmokObject> listOmokObject = new ArrayList<>();

    // lock for listOmokObject
    private final Object lockListOmokObject = new Object();

    private boolean isBlackTurn = true;

    public OmokBoard(int program, float ScaleFactor, int rows, int cols)
    {
        m_program = program;
        m_scaleFactor = ScaleFactor;
        m_rows = rows;
        m_cols = cols;

        int maxXCoord = (rows - 1) / 2;
        int maxYCoord = (cols - 1) / 2;

        m_coordList = new ArrayList<>();
        m_drawList = new ArrayList<>();

        THRESHOLD_TOUCH = ScaleFactor * 0.4;

        // construct the board (vertical line)
        for (int x = -maxXCoord; x <= maxXCoord; x++)
        {
            short lineOffset = (short)(m_coordList.size() / COORDS_PER_VERTEX);

            m_coordList.add(x*m_scaleFactor + LINE_WIDTH);
            m_coordList.add(maxYCoord*m_scaleFactor);
            m_coordList.add(0f);
            m_coordList.add(x*m_scaleFactor - LINE_WIDTH);
            m_coordList.add(maxYCoord*m_scaleFactor);
            m_coordList.add(0f);
            m_coordList.add(x*m_scaleFactor - LINE_WIDTH);
            m_coordList.add(-maxYCoord*m_scaleFactor);
            m_coordList.add(0f);
            m_coordList.add(x*m_scaleFactor + LINE_WIDTH);
            m_coordList.add(-maxYCoord*m_scaleFactor);
            m_coordList.add(0f);

            m_drawList.add((short)(lineOffset + 0));
            m_drawList.add((short)(lineOffset + 1));
            m_drawList.add((short)(lineOffset + 3));
            m_drawList.add((short)(lineOffset + 1));
            m_drawList.add((short)(lineOffset + 2));
            m_drawList.add((short)(lineOffset + 3));
        }

        // construct the board (horizontal line)
        for (int y = -maxYCoord; y<=maxYCoord; y++)
        {
            short lineOffset = (short)(m_coordList.size() / COORDS_PER_VERTEX);

            m_coordList.add(maxXCoord*m_scaleFactor);
            m_coordList.add(y*m_scaleFactor + LINE_WIDTH);
            m_coordList.add(0f);
            m_coordList.add(-maxXCoord*m_scaleFactor);
            m_coordList.add(y*m_scaleFactor + LINE_WIDTH);
            m_coordList.add(0f);
            m_coordList.add(-maxXCoord*m_scaleFactor);
            m_coordList.add(y*m_scaleFactor - LINE_WIDTH);
            m_coordList.add(0f);
            m_coordList.add(maxXCoord*m_scaleFactor);
            m_coordList.add(y*m_scaleFactor - LINE_WIDTH);
            m_coordList.add(0f);

            m_drawList.add((short)(lineOffset + 0));
            m_drawList.add((short)(lineOffset + 1));
            m_drawList.add((short)(lineOffset + 3));
            m_drawList.add((short)(lineOffset + 1));
            m_drawList.add((short)(lineOffset + 2));
            m_drawList.add((short)(lineOffset + 3));
        }

        // initialize vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(m_coordList.size() * Float.SIZE);
        bb.order(ByteOrder.nativeOrder());
        m_vertexBuffer = bb.asFloatBuffer();
        for (float coord: m_coordList)
        {
            m_vertexBuffer.put(coord);
        }
        m_vertexBuffer.position(0);

        // initialize draw list buffer
        ByteBuffer bb2 = ByteBuffer.allocateDirect(m_drawList.size() * Short.SIZE);
        bb2.order(ByteOrder.nativeOrder());
        m_drawListBuffer = bb2.asShortBuffer();
        for (short drawOffset: m_drawList)
        {
            m_drawListBuffer.put(drawOffset);
        }
        m_drawListBuffer.position(0);

        // register the program
        m_program = program;

        init();
    }

    // initialize the board
    public void init()
    {
        // clear the list of objects
        synchronized (lockListOmokObject)
        {
            listOmokObject.clear();
        }
        isBlackTurn = true;
    }

    // draw this board.
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
        GLES20.glUniform4f(colorHandle, 9f / 16f, 6f / 16f, 0.0f, 1.0f);

        // get the handle to shape's transformation matrix
        int MVPMatrixHandle = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");

        // pass the transformation matrix to the shader
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, VPMatrix, 0);

        // draw this shape
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, m_drawList.size(), GLES20.GL_UNSIGNED_SHORT, m_drawListBuffer);

        // disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);

        // draw the OmokObject
        synchronized (lockListOmokObject)
        {
            for (OmokObject object: listOmokObject)
            {
                object.draw(VPMatrix);
            }
        }
    }

    public void addObject(float[] modelCoord)
    {
        float XCoord = modelCoord[0] / m_scaleFactor;
        float YCoord = modelCoord[1] / m_scaleFactor;

        int maxXCoord = (m_rows - 1) / 2;
        int maxYCoord = (m_cols - 1) / 2;
        int xpos = -maxXCoord, ypos = -maxYCoord;

        for (; xpos <= maxXCoord; xpos++)
        {
            for (ypos = -maxYCoord; ypos <= maxYCoord; ypos++)
            {
                double dist = Math.pow(XCoord - xpos, 2) + Math.pow(YCoord - ypos, 2);
                if (dist < Math.pow(THRESHOLD_TOUCH, 2))
                {
                    break;
                }
            }
            if (ypos <= maxYCoord)
            {
                break;
            }
        }

        if (xpos <= maxXCoord)
        {
            boolean samePosition = false;
            synchronized (lockListOmokObject)
            {
                for (OmokObject object: listOmokObject)
                {
                    if (object.getXPos() == xpos && object.getYPos() == ypos)
                    {
                        samePosition = true;
                        break;
                    }
                }
            }
            if (!samePosition)
            {
                synchronized (lockListOmokObject)
                {
                    listOmokObject.add(new OmokObject(m_program, m_scaleFactor, xpos, ypos, isBlackTurn));
                }
                isBlackTurn = !isBlackTurn;
            }
        }
    }

    public enum GameState
    {
        GAME_UNFINISHED,
        GAME_BLACK_WON,
        GAME_WHITE_WON,
        GAME_STALEMATE,
    }

    public GameState isGameFinished() {
        int[][] boardState = new int[m_rows][m_cols];
        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[0].length; j++) {
                boardState[i][j] = 0;
            }
        }

        int minXCoord = -(m_rows - 1) / 2;
        int minYCoord = -(m_cols - 1) / 2;

        synchronized (lockListOmokObject) {
            for (OmokObject object : listOmokObject) {
                if (object.isBlack()) {
                    boardState[object.getXPos() - minXCoord][object.getYPos() - minYCoord] = 1;
                } else {
                    boardState[object.getXPos() - minXCoord][object.getYPos() - minYCoord] = 2;
                }
            }
        }

        // vertical
        GameState gameState = GameState.GAME_UNFINISHED;
        for (int i = 0; i < boardState.length - 4; i++) {
            for (int j = 0; j < boardState[0].length; j++) {
                boolean isSame = true;
                for (int k = 0; k < 4; k++) {
                    if (boardState[i + k][j] != boardState[i + k + 1][j]) {
                        isSame = false;
                        break;
                    }
                }
                if (isSame) {
                    if (boardState[i][j] == 1) {
                        gameState = GameState.GAME_BLACK_WON;
                        break;
                    } else if (boardState[i][j] == 2) {
                        gameState = GameState.GAME_WHITE_WON;
                        break;
                    }
                }
            }
            if (gameState != GameState.GAME_UNFINISHED) {
                break;
            }
        }
        if (gameState != GameState.GAME_UNFINISHED) {
            return gameState;
        }

        // horizontal
        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[0].length - 4; j++) {
                boolean isSame = true;
                for (int k = 0; k < 4; k++) {
                    if (boardState[i][j + k] != boardState[i][j + k + 1]) {
                        isSame = false;
                        break;
                    }
                }
                if (isSame) {
                    if (boardState[i][j] == 1) {
                        gameState = GameState.GAME_BLACK_WON;
                        break;
                    } else if (boardState[i][j] == 2) {
                        gameState = GameState.GAME_WHITE_WON;
                        break;
                    }
                }
            }
            if (gameState != GameState.GAME_UNFINISHED) {
                break;
            }
        }
        if (gameState != GameState.GAME_UNFINISHED) {
            return gameState;
        }

        // right-upward diagonal
        for (int i = 0; i < boardState.length - 4; i++) {
            for (int j = 0; j < boardState[0].length - 4; j++) {
                boolean isSame = true;
                for (int k = 0; k < 4; k++) {
                    if (boardState[i + k][j + k] != boardState[i + k + 1][j + k + 1]) {
                        isSame = false;
                        break;
                    }
                }
                if (isSame) {
                    if (boardState[i][j] == 1) {
                        gameState = GameState.GAME_BLACK_WON;
                        break;
                    } else if (boardState[i][j] == 2) {
                        gameState = GameState.GAME_WHITE_WON;
                        break;
                    }
                }
            }
            if (gameState != GameState.GAME_UNFINISHED) {
                break;
            }
        }
        if (gameState != GameState.GAME_UNFINISHED) {
            return gameState;
        }

        // left-upward diagonal
        for (int i = 4; i < boardState.length; i++) {
            for (int j = 0; j < boardState[0].length - 4; j++) {
                boolean isSame = true;
                for (int k = 0; k < 4; k++) {
                    if (boardState[i - k][j + k] != boardState[i - k - 1][j + k + 1]) {
                        isSame = false;
                        break;
                    }
                }
                if (isSame) {
                    if (boardState[i][j] == 1) {
                        gameState = GameState.GAME_BLACK_WON;
                        break;
                    } else if (boardState[i][j] == 2) {
                        gameState = GameState.GAME_WHITE_WON;
                        break;
                    }
                }
            }
            if (gameState != GameState.GAME_UNFINISHED) {
                break;
            }
        }
        if (gameState != GameState.GAME_UNFINISHED) {
            return gameState;
        }

        gameState = GameState.GAME_STALEMATE;
        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[0].length; j++) {
                if (boardState[i][j] == 0) {
                    gameState = GameState.GAME_UNFINISHED;
                }
            }
        }

        return gameState;
    }
}
