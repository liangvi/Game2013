package Enemies;

//import java.awt.List;
import java.util.*;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;


public class MoveSwarm {
	private List<BasicBat> swarm;

	public MoveSwarm(GL2 gl, GLU glu) {
		swarm = new ArrayList<BasicBat>();
		
		for (int i = 0; i < 50; i++) {
			int j = (int) (Math.random()*20) - 10;
			int k = (int) (Math.random()*20) - 10;
			swarm.add(new BasicBat(gl, glu, 10+j, 50+k));
		}
	}

	public MoveSwarm(List<BasicBat> swarm){
		this.swarm = swarm;
	}

	public void draw(GL2 gl, GLU glu) {
		
		for (BasicBat b: swarm) {
			b.draw2(gl, glu);
			if (b.isDead())
				swarm.remove(b);
			else {
				b.setX(b.getX()+b.getDX());
				b.setZ(b.getZ()+b.getDZ());
			}
		}
		
		for (BasicBat b: swarm) {
			for (BasicBat t: swarm) {
				if(b != t) {
					//calc distances
					float xdist = t.getX()-b.getX();
					float zdist = t.getZ()-b.getZ();

					//calc force
					float distancesq = (xdist*xdist + zdist*zdist);
					float force = (float) (.04/distancesq);

					//update velocity vectors
					b.setDX(b.getDX()+(force*xdist));
					b.setDZ(b.getDZ()+(force*zdist));
				}
				float hypotenuse = (float) Math.sqrt((float) (b.getDX()*b.getDX() + b.getDZ()*b.getDZ()));
				b.setDirection((float) Math.toDegrees(Math.acos(b.getDX()/hypotenuse)));
				
			}
		}
	}
}