package com.hero.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class QuestScreen implements Screen {
	private HeroGame game;
	private SpriteBatch batch;
	private startButton start;
	private continueButton cont;
	private OrthographicCamera camera;
	private StretchViewport viewport;

	public QuestScreen(HeroGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		camera = new OrthographicCamera();
		batch = new SpriteBatch();
		start = new startButton(Assets.butBack2, 50, 100);
		cont = new continueButton(Assets.butBack2, 50, 50);
		viewport = new StretchViewport(HeroGame.VP_WIDTH, HeroGame.VP_HEIGHT, camera);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 1);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    batch.setProjectionMatrix(camera.combined);
	    batch.begin();
	    uiDraw(batch);
		batch.end();
		start.update(game, this, camera);
	}


	public void uiDraw(SpriteBatch batch){
		batch.draw(Assets.statBack, 0, 0);
		start.draw(batch, Assets.font);
		cont.draw(batch, Assets.font);
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
