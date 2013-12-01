//Power-Ups are used immediately upon
//being picked up by the player

package inventory;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public interface PowerUp {

	public void draw(GL2 gl, GLU glu);

	public void use();

	public boolean grabbed();

	public String getType();

}