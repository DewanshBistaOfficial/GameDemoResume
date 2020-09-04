package com.hero.game;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input.Keys;

/**
 * Main Game Object
 * Contains global game variables
 * @author Dewansh
 */
public class HeroGame extends Game {
	public final static float SCALE = 16;
	public final static float INV_SCALE = 1.f/SCALE;
	public final static float VP_WIDTH = 15*SCALE;
	public final static float VP_HEIGHT = 20*SCALE;
	//(1440 x 1080 or 1.333 to 1)

	//we will use 16px/unit in world

		//Total Virtual Pixels
		//1920 x 1080 -Screen Space
		//1440 x 1080 (90 x 67.5 * 16) -Game Space
		//Also known as 1.33 to 1

		//For Battle Screen
		//What I actually need is 20 to 15 Squares or ( 320 x 240 )
		//So I have to up-scale that to 1440 x 1080 or 4.5 magnification
		// 480 x 1080 -Ad Space

		BattleScreen battleScreen;
		
		//Navigation System
		public final int BACK = Keys.B;
		public final int FORWARD = Keys.A;
		public final int RIGHT = Keys.RIGHT;
		public final int LEFT = Keys.LEFT;
		public final int UP = Keys.UP;
		public final int DOWN = Keys.DOWN;

		//Main Units
		public PlayerUnit hero, ally1, ally2, ally3;
		public PlayerUnit[] mainUnit = {hero, ally1, ally2, ally3};
		public Random rand;
		
		//Campaign Progression
		public int campaignProgress;
		
		//Needed for Assets
		static Assets asset;
		
	@Override
	public void create () {
		//Loads Up All Assets in Game
		asset = new Assets(this);
		asset.load();
		
		//Used for procedural generation
		rand = new Random();
		String gen = Integer.toString(rand.nextInt(1000));
		
		//Saves the player's information in a csv file
		asset.writePlayer(new PlayerUnit(asset.createPlayerMapImage(gen), "Darius", gen, "Hero", 3, 1, 35, 5, 6), "Hero");
		gen = Integer.toString(rand.nextInt(1000));
		asset.writePlayer(new PlayerUnit(asset.createPlayerMapImage(gen), "Jerome", gen, "Ally1", 4, 1, 20, 8, 1), "Ally1");
		gen = Integer.toString(rand.nextInt(1000));
		asset.writePlayer(new PlayerUnit(asset.createPlayerMapImage(gen), "Luz", gen, "Ally2", 5, 2, 15, 6, 3), "Ally2");

		//Used to Construct Player
		hero = asset.constructPlayer("Hero");
		ally1 = asset.constructPlayer("Ally1");
		ally2 = asset.constructPlayer("Ally2");
		
		//Sets Screen
		setScreen(new MainScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	public void update() {

	}

	@Override
	public void dispose () {
		asset.dispose();
	}
}
