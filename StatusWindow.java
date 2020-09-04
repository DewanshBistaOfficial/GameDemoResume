package com.hero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Screen that shows the status of a unit
 * @author Dewansh
 */
public class StatusWindow implements Screen {
	private HeroGame game;
	SpriteBatch batch;
	OrthographicCamera camera;
	Unit unit;
	int currentUnits, totalUnits;
	private StretchViewport viewport;

	/**
	 * Constructs the status Window for a unit
	 * @param unit, screen shows status for this unit
	 */
	public StatusWindow(Unit unit, HeroGame game, int currentUnits, int totalUnits) {
		this.game = game;
		this.unit = unit;
		camera = new OrthographicCamera();
		batch = new SpriteBatch();
		this.currentUnits = currentUnits;
		this.totalUnits = totalUnits;
		viewport = new StretchViewport(HeroGame.VP_WIDTH, HeroGame.VP_HEIGHT, camera);
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 1);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
	    batch.begin();
	    UIDraw(batch);
		batch.end();
	    logic();
	}

	/**
	 * Exits the status window
	 */
	public void logic() {
		if(Gdx.input.isKeyJustPressed(game.BACK)) {
			game.setScreen(game.battleScreen);
			game.battleScreen.show();
			dispose();
		}
	}

	/**
	 * Draws the status window
	 */
	public void UIDraw(SpriteBatch batch){
		batch.draw(Assets.statBack, 0, 0, 16*15, 16*20);
		Assets.font.draw(batch, unit.name, 16, 16*13);
		Assets.font.draw(batch, "Damage: " + unit.damage, 16, 16*8);
		Assets.font.draw(batch, "Health: " + unit.currentHealth + " / " + unit.maxHealth, 16, 16*7);
		Assets.font.draw(batch, "Movement: " + (int)unit.movement, 16, 16*6);
		Assets.font.draw(batch, "Attack Range: " + (int)unit.attackRange, 16, 16*5);
		Assets.font.draw(batch, "Defense: " + (int)unit.defense, 16, 16*4);
		Assets.font.draw(batch, "Current Units: " + currentUnits, 16, 16*3);
		Assets.font.draw(batch, "Total Units: " + totalUnits, 16, 16*2);

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

}
