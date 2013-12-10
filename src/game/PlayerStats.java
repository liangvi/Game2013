package game;

public class PlayerStats{
	public static final int MAX_HEALTH=10;
	
	private int health=MAX_HEALTH,honor=8;
	private PlayerMotion motion;
	
	public PlayerStats(PlayerMotion m){
		motion=m;
		motion.setStats(this);
	}
	
	public boolean alive(){return health>0;}
	
	public void changeHealth(int i){
		health+=i;
		if(health<=0){
			health=0;
			motion.setMobile(false);
		}
	}
	public void changeHonor(int i){
		honor+=i;
		if(honor<=0){
			motion.setMobile(false);
		}
	}
	public String healthString(){
		return health>0?("Health: "+health+"/"+MAX_HEALTH):"You died";
	}
	public String honorString(){
		String s="Honor: "+honor;
		if(honor<1)s+=" - can't move anymore";
		return s;
	}
}
