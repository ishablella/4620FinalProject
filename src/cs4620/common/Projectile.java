package cs4620.common;
import egl.math.Vector3;

public class Projectile {
	private boolean isActive = false;
	private Vector3 dir = new Vector3(0, 0, 0);
	private Vector3 origin = new Vector3(0,0,0);
	private String link = null;
	private int life = 0;
	private int maxlife = 100;
	public boolean isActive() {
		return isActive;
	}
	public void activate(Vector3 Dir){
		dir = Dir;
		isActive = true;
		life = 0;
	}
	public void kill(){
		isActive = false;
	}
	public Vector3 getDirection() {
		return dir.clone();
	}
	public void setDirection(Vector3 Dir){
		dir = Dir;
	}
	public void age(){
		life +=1;
		if (life > maxlife){
			kill();
		}
	}
	public int getAge(){
		return life;
	}
	public String getLink(){
		return link;
	}
	public Projectile(String Link){
		link = Link;
	}
}