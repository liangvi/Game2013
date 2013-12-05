package game;
// Fiona Tamburini, and the CS 333 class
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JTextField;

import Multiplayer.*;

import com.jogamp.opengl.util.FPSAnimator;

public class BatsEverywhere implements GLEventListener
{
    private JTextField statusLine = new JTextField(10); // for misc messages at bottom of window
    private int framesDrawn=0;
    private GLU glu = new GLU();
    private Town town;
    private long runtime = 0;
    private PlayerMotion playerMotion = new PlayerMotion();
    private PlayerLogger logger = new PlayerLogger();
    private Player player;
    
    //For multiplayer
    private static Map<Integer, Player> playerMap;
    private static Semaphore isUpdate;

    public synchronized static Map<Integer, Player> getPlayers() {
		return playerMap;
    }
    
    public synchronized static Semaphore getSem() {
    	return isUpdate;
    }
    
    public void init(GLAutoDrawable drawable) {
      //drawable.setGL(new DebugGL2(drawable.getGL().getGL2())); // to do error check upon every GL call.  Slow but useful.
      //drawable.setGL(new TraceGL2(drawable.getGL().getGL2(), System.out)); // to trace every call.  Less useful.
        GL2 gl = drawable.getGL().getGL2();
        statusLine.setEditable(false);
        gl.setSwapInterval(1); // for animation synchronized to refresh rate
        gl.glClearColor(.7f,.7f,1f,0f); // background
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE); // or GL_MODULATE
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST); // or GL_FASTEST
        
        gl.glEnable(GL2.GL_DEPTH_TEST);
        
        town = new Town(gl, glu);
        try {
			player = new Player(glu, playerMotion);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("reshaping to " + width + "x" + height);

        GL2 gl = drawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(50, 1, .5, 1000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();     
    }

    public void display(GLAutoDrawable drawable) {
        long startTime = System.currentTimeMillis();
        GL2 gl  = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        playerMotion.setLookAt(gl, glu);   
                
        // draw town
        town.draw(gl, glu, playerMotion.getEyeX(), playerMotion.getEyeY(), playerMotion.getEyeZ());
        
        // draw player
        player.draw(gl, glu); 
     //   System.out.println("Trying to acquire");
        if(BatsEverywhere.isUpdate.tryAcquire()) {
        	for (Integer i : BatsEverywhere.getPlayers().keySet()) {
        		System.out.println("Drawing Player " + i);
        		BatsEverywhere.getPlayers().get(i).draw(gl, glu);
        	}
        	try {
				BatsEverywhere.getSem().acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        // check for errors, at least once per frame
        int error = gl.glGetError();
        if (error != GL2.GL_NO_ERROR) {
            System.out.println("OpenGL Error: " + glu.gluErrorString(error));
            System.exit(1);
        }

        long endTime = System.currentTimeMillis();
        runtime += endTime-startTime;
        ++framesDrawn;
        if (framesDrawn%60==0) {
            statusLine.setText("Frames drawn: "  +  framesDrawn +
                    "   Time per frame: " + runtime/60/1000f);
            runtime = 0;
        }
    }

    public void dispose(GLAutoDrawable drawable) { /* not needed */ }

    public static void main(String[] args) {
    	 GLProfile.initSingleton();
         System.setProperty("sun.awt.noerasebackground", "true"); // sometimes necessary to avoid erasing over the finished window

         JFrame frame = new JFrame("Too Many Bats");
         GLCanvas canvas = new GLCanvas();
         canvas.setPreferredSize(new Dimension(500,500));
         isUpdate = new Semaphore(0);
         playerMap = new ConcurrentHashMap<Integer, Player>();
         BatsEverywhere renderer = new BatsEverywhere();
         
         canvas.addGLEventListener(renderer);

         frame.setLayout(new BorderLayout());
         frame.add(renderer.statusLine, BorderLayout.SOUTH);
         frame.add(canvas, BorderLayout.CENTER);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.pack(); // make just big enough to hold objects inside
         frame.setVisible(true);
         canvas.addKeyListener(renderer.playerMotion);
         canvas.requestFocusInWindow();
         
         FPSAnimator animator = new FPSAnimator(canvas, 60);
         animator.start();
    }

}
