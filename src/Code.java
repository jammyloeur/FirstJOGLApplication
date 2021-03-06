import java.nio.*;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.*;

import graphicslib3D.*;
import graphicslib3D.GLSLUtils.*;

public class Code extends JFrame implements GLEventListener {
    private int rendering_program;
    private int vao[] = new int[1];
    private GLCanvas myCanvas;
    private GLSLUtils util = new GLSLUtils();
    private float x = 0.0f; // location of triangle
    private float inc = 0.01f; // offset for moving the triangle

    public Code() {
        setTitle("Chapter2 - program1");
        setSize(600, 400);
        setLocation(200, 200);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        setVisible(true);
        FPSAnimator animator = new FPSAnimator(myCanvas, 50);
        animator.start();
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(rendering_program);
        float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f }; // clear the background to black, each time
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
        x += inc;
        if (x > 1.0f) inc = -0.01f;
        if (x < -1.0f) inc = 0.01f;
        int offset_loc = gl.glGetUniformLocation(rendering_program, "offset");
        gl.glProgramUniform1f(rendering_program, offset_loc, x);
        gl.glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    public static void main(String[ ] args) {
        new Code();
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        rendering_program = createShaderProgram();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        String vshaderSource[] = util.readShaderSource("src/vert.glsl");
        String fshaderSource[] = util.readShaderSource("src/frag.glsl");
        int lengths[];

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

        gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
        gl.glCompileShader(vShader);

        gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }
    public void dispose(GLAutoDrawable drawable) { }
}
