Sample script (without imports and privatizing some details)

@ScriptManifest(author = "Khiz", description = "CTrainer", name = "CombatTrainer", category = Category.MAGIC, version = 1)
public class CombatTrainer extends AbstractScript{

	State state;
	NPC npc;
	
	Area lSpawn = new Area(new Tile(3217, 3222), new Tile(3225, 3215));
	Area ratArea = new Area(new Tile(3190, 3212), new Tile(3201, 3199));
	
	Tile[] pathToRats = {new Tile(3210, 3210), new Tile(3199, 3215), new Tile(3196, 3203)};
	
	@Override //Infinite loop
	public int onLoop() {
		
		//Determined by which state gets returned by getState() then do that case.
		switch(getState()) {
		case WALKINGTORATS:
			log("Walking to Rats");
			
			if(!getTabs().isOpen(Tab.INVENTORY)) {
				getTabs().open(Tab.INVENTORY);
			}
			
			if(getTabs().isOpen(Tab.INVENTORY)) {
				if(getInventory().contains("Wooden shield")) {
					getInventory().interact("Wooden shield", "Wield");
					sleep(randomNum(1000, 1500));
					getInventory().interact("Bronze sword", "Wield");
					sleep(randomNum(1000, 1500));
				}
			}
			
			for(int i = 0; i < pathToRats.length; i++) {
				while(!getLocalPlayer().getTile().equals(pathToRats[i])) {
					if(!getLocalPlayer().isMoving()) {
						getWalking().walk(pathToRats[i]);
						sleep(randomNum(1000, 2000));
					}
				}
			}
			break;
		case LOOKING4RATS:
			log("Looking for Rat");
			npc = getNpcs().closest(rat -> rat != null && rat.getName().contentEquals("Giant rat")
					&& !rat.isInCombat() && ratArea.contains(rat));
			if(npc != null) {
				if(!npc.isOnScreen()) {
					getCamera().keyboardRotateToEntity(npc);
					sleep(randomNum(500, 1000));
				}
				
				npc.interact("Attack");
				sleep(randomNum(1000, 2000));
			}
			break;
		
		case FIGHTING:
			log("Fighting");
			if(npc != null) {
				if(!npc.exists() || npc.getHealthPercent() == 0) {
					npc = null;
				}
			}
		
			
			break;
		default: log("an error has occurred");
		}
		return 0;
	}
	
	//State names
	private enum State{
		FIGHTING, WALKINGTORATS, LOOKING4RATS, BREAK
	}
	
	//Checks if a certain condition is met, then return that state.
	private State getState() {
		if(lumbyspawn.contains(getLocalPlayer())) {
			state = State.WALKINGTORATS;
		}else if(ratArea.contains(getLocalPlayer()) && !getLocalPlayer().isInCombat()) {
			state = State.LOOKING4RATS;
		}else if(ratArea.contains(getLocalPlayer()) && getLocalPlayer().isInCombat()) {
			state = State.FIGHTING;
		}
		return state;
	}
	
	//When script start load this.
	public void onStart() {
		log("Bot Started");
	}
	
	//When script ends do this.
	public void onExit() {
		log("Bot Ended");
	}
	
	public int randomNum(int i, int k) {
		int num = (int)(Math.random() * (k - i + 1)) + i;
		return num;
	}

}
