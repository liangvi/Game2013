package game;

public interface PlayerMotionWatcher {
	public void playerMoved(float x, float y, float z, float angle); // angle in degrees
}