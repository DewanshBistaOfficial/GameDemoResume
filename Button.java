package com.hero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

/**
 * A class that represents a template for making buttons
 * @author Dewansh Bista
 *
 */
public class Button extends Sprite {

	public Button(Sprite sprite, float x, float y) {
		super(sprite);
		setX(x);
		setY(y);
	}

	@Override
	public void draw(Batch batch) {
		super.draw(batch);
	}

	public boolean click(Camera cam) {
		if (Gdx.input.isTouched()) {
			int x1 = Gdx.input.getX();
			int y1 = Gdx.input.getY();
			Vector3 input = new Vector3(x1, y1, 0);
			cam.unproject(input);
			if (getBoundingRectangle().contains(input.x, input.y)) {
				return true;
			}
		}
		return false;
	}
}


class startButton extends Button {

	public startButton(Sprite sprite, float x, float y) {
		super(sprite, x, y);
	}

	public void draw(Batch batch, BitmapFont font) {
		super.draw(batch);
		font.draw(batch, "Start", getX() + 10, getY() + 15);
	}

	//TODO Update to a menu stage (to start a run)
	/**
	 * Goes straight to fight screen when button is clicked
	 */
	public void update(HeroGame game, Screen menu, Camera cam) {
		if (click(cam)) {
			game.battleScreen = new BattleScreen(game);
			game.setScreen(game.battleScreen);
			menu.dispose();
		}
	}
}

//TODO Continues a run
class continueButton extends Button {

	public continueButton(Sprite sprite, float x, float y) {
		super(sprite, x, y);
	}

	public void draw(Batch batch, BitmapFont font) {
		super.draw(batch);
		font.draw(batch, "Resume", getX() + 10, getY() + 15);
	}

	public void update(HeroGame game, Screen menu, Camera cam) {
		if (click(cam)) {
		}
	}
}