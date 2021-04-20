package collisions;


import static com.almasb.fxgl.dsl.FXGL.*;

import java.io.IOException;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

import application.TheLastManStandingApp;

import static com.almasb.fxgl.dsl.FXGL.getGameTimer;

import components.PlayerComponent;
import components.TextureComponent;
import components.ComponentUtils;
import controller.ScoreControllerImpl;
import model.TLMSType;
import model.score.JsonScore;
import javafx.util.Duration;
import model.PlayerTexture;

/**
 * Manages collisions between players and zombies
 */
public class PlayerZombieCollision extends CollisionHandler{

	public PlayerZombieCollision(TLMSType player, TLMSType zombie) {
		super(player, zombie);
	}

	@Override
	public void onCollisionBegin(Entity player, Entity zombie) {
		
		player.getComponent(ComponentUtils.HEALTH_COMPONENT).damage(zombie.getComponent(ComponentUtils.DAMAGING_COMPONENT).getDamage());
		
		PlayerTexture playerTexture = new PlayerTexture();
		
		if(player.getComponent(PlayerComponent.class).isRed()) {
			getGameTimer().runOnceAfter(() -> {
				player.removeComponent(TextureComponent.class);  
				player.addComponent(new TextureComponent(playerTexture.getTextureBlue().getTextureMap()));		
			}, Duration.seconds(0.8));
		}
    	
    	player.getComponent(PlayerComponent.class).attacked();
    	zombie.getComponent(ComponentUtils.TEXTURE_COMPONENT).setAttacking(true);
    	
    	inc("playerLife", -((double)(zombie.getComponent(ComponentUtils.DAMAGING_COMPONENT).getDamage())) / 10);
		
		System.out.println("Il player ha vita: " + player.getComponent(ComponentUtils.HEALTH_COMPONENT).getValue());
			
	
		if(player.getComponent(ComponentUtils.PLAYER_COMPONENT).isDead()) {
			getGameTimer().runOnceAfter(() -> {			
			    	player.removeFromWorld();
					System.out.println("Hai perso!");
					try {
						new ScoreControllerImpl().updateScore(
								new JsonScore.Builder()
								    .nameFromPath(ScoreControllerImpl.FILE_NAME_USER)
								    .score(getWorldProperties().intProperty("score").get())
								    .build()
					    );
					} catch (IOException e) {
						e.printStackTrace();
					}
					gameOver();		
	    	}, Duration.seconds(1.7));
		}
	}
	
	private void gameOver() {
        getDialogService().showMessageBox("Game Over. Press OK to exit", getGameController()::exit);
    }
}
