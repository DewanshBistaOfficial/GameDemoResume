package com.hero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ActionMenu {
	
	//Used for determining player selection
	static int selectionNumber = 1;

	/**
	 * Code for drawing the action menu
	 */
	public void drawMenu(SpriteBatch batch, OrthographicCamera camera) {
		
			//Draw the background menu
			batch.draw(Assets.butBack, camera.position.x + 18, camera.position.y + 32);
			Assets.font.draw(batch, "ATTACK", camera.position.x + 22, camera.position.y + 80);
			Assets.font.draw(batch, "STATUS", camera.position.x + 22, camera.position.y + 64);
			Assets.font.draw(batch, "REST",  camera.position.x + 22, camera.position.y + 48);

			//Changes font color based on player selection
			switch(selectionNumber) {
			case 1:
				Assets.menuFont.draw(batch, "ATTACK", camera.position.x + 22, camera.position.y + 80);
			break;
			case 2:
				Assets.menuFont.draw(batch, "STATUS", camera.position.x + 22, camera.position.y + 64);
			break;
			case 3:
				Assets.menuFont.draw(batch, "REST", camera.position.x + 22, camera.position.y + 48);
			break;
			}
		}

	/**
	 * Allows for the player to select a menu item
	 * @param game
	 */
	public void navigateMenu(HeroGame game) {
		if(Gdx.input.isKeyJustPressed(game.UP)) {
			selectionNumber -=1;
		}

		if(Gdx.input.isKeyJustPressed(game.DOWN)) {
			selectionNumber +=1;
		}
		if(selectionNumber > 3) {
			selectionNumber = 1;
		}

		if(selectionNumber < 1) {
			selectionNumber = 3;
		}
	}
}
