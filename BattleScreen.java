package com.hero.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class BattleScreen implements Screen {
	//Scene Elements
	private HeroGame game;
	SpriteBatch batch;
	OrthographicCamera camera;
	private StretchViewport viewport;
	OrthogonalTiledMapRenderer rend;
	Cursor cursor;
	ActionMenu actMenu;

	//Unit Logic
	static int totalUnits, currentUnits;
	PlayerUnit selectedUnit;
	EnemyUnit selectedEnemy;
	private int enemyCounter;
	ArrayList<PlayerUnit> playerUnits;
	ArrayList<EnemyUnit> enemyUnits;
	Random rand;
	float timeSinceFrame = 0;
	//Slash Animation
	boolean drawSlash = false;
	
	//Map Info
	float mapWidth, mapHeight;
	int turnNumber = 1;
	float camX, camY;

	//Control Flow
	State currentState;

	
	enum State {
		// Player Turn
		PAN, LOCKED, MENU, ATTACKMENU, ATTACKANIM,

		// Enemy (AI) Turn
		SELECTENEMY, ENEMYMOVE, ENEMYATTACK,
	}

	public BattleScreen(HeroGame game) {
		//Init Logic
		currentState = State.PAN;
		enemyCounter = 0;
		playerUnits = new ArrayList<PlayerUnit>();
		enemyUnits = new ArrayList<EnemyUnit>();
		
		//Needed Inits
		rand = new Random();
		actMenu = new ActionMenu();
		camera = new OrthographicCamera();
		viewport = new StretchViewport(HeroGame.VP_WIDTH, HeroGame.VP_HEIGHT, camera);
		batch = new SpriteBatch();
		this.game = game;

		//Init Map
		mapWidth = (float) Assets.map.getProperties().get("width", Integer.class);
		mapHeight = (float) Assets.map.getProperties().get("height", Integer.class);
		rend = new OrthogonalTiledMapRenderer(Assets.map, batch);
		
		//Create player cursor
		cursor = new Cursor(game, Assets.cursor, 15, 15);
		
		//Spawn Units
		spawnMain();
		spawnEnemies(7);
	}

	@Override
	public void show() {
		camera.position.set(camX, camY, 0);
		camera.update();
	}

	/**
	 * Battle Screen main loop
	 */
	@Override
	public void render(float delta) {
		//Camera and Graphics
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		rend.setView(camera);
		rend.render();
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		camera.position.set(cursor.getX() * HeroGame.SCALE + HeroGame.SCALE / 2, cursor.getY() * HeroGame.SCALE + HeroGame.SCALE / 2, 0);
		
		//Main Logic Loop
		update(delta);
		
		//Update and close
		camera.update();
		batch.end();
	}

	/**
	 * Spawns the main (persistent) character units
	 */
	public void spawnMain() {
		game.hero.xPos = 20;
		game.hero.yPos = 20;
		playerUnits.add(game.hero);
		
		game.ally1.xPos = 20;
		game.ally1.yPos = 21;
		playerUnits.add(game.ally1);
		
		game.ally2.xPos = 19;
		game.ally2.yPos = 19;
		playerUnits.add(game.ally2);
		
		currentUnits +=3;
		totalUnits +=3;
	}
	
	/**
	 * Spawns enemy units randomly and adds them to the main list of enemies
	 * @param numSpawn, the number to spawn
	 */
	public void spawnEnemies(int numSpawn) {
		for (int i = 0; i < numSpawn; i++) {
			EnemyUnit enemy = new EnemyUnit(Assets.enemy, "Enemy " + i, rand.nextInt(30), rand.nextInt(30), 2 + rand.nextInt(3), 1 + rand.nextInt(1), 8 + rand.nextInt(15), 1 + rand.nextInt(4), rand.nextInt(2));
			enemyUnits.add(enemy);
		}
	}

	/**
	 * Checks if any players or enemies are dead, if so removes them
	 */
	public void deathCheck() {
		for (int i = 0; i < playerUnits.size(); i++) {
			if (playerUnits.get(i).currentHealth <= 0) {
				playerUnits.remove(playerUnits.get(i));
				totalUnits -= 1;
				currentUnits -= 1;
			}
		}
		
		//Check enemies
		for (int i = 0; i < enemyUnits.size(); i++) {
			if (enemyUnits.get(i).currentHealth <= 0) {
				enemyUnits.remove(enemyUnits.get(i));
			}
		}
		
		//Resets Map
		if (totalUnits <= 0 || enemyUnits.size() <= 0) {
			game.setScreen(new MainScreen(game));
		}
		
	}

	/**
	 * Updates the game logic based on a state machine
	 */
	public void update(float delta) {
		switch (currentState) {
		case PAN:
			panState();
			deathCheck();
			break;

		case LOCKED:
			selectedPlayerState();
			break;

		case ATTACKMENU:
			playerAttackState();
			break;

		case MENU:
			drawAll();
			menuState();
			break;

		case ATTACKANIM:
			attackAnimationState(delta);
			break;

		case SELECTENEMY:
			enemySelectState();
			break;

		case ENEMYMOVE:
			enemyMoveState(delta);
			break;

		case ENEMYATTACK:
			enemyAttackState(delta);
			break;

		default:
			System.out.println("Error");
			break;
		}
		
		//Different draw order in menu state to deal with clipping
		if(currentState != State.MENU) {
			drawAll();
		}
	}

	public void panState() {
		// State Logic
		cursor.update(mapWidth, mapHeight);

		// Changes State if a player is selected
		if (Gdx.input.isKeyJustPressed(game.FORWARD)) {
			for (PlayerUnit player : playerUnits) {
				if (cursor.canSelect(player)) {
					selectedUnit = player;
					currentState = State.LOCKED;
					PlayerUnit.origX = selectedUnit.getX();
					PlayerUnit.origY = selectedUnit.getY();

					selectedUnit.isSelectable = false;
				}
			}
			
			//Allows for the player to see enemy status when clicking on them
			for (EnemyUnit enemy : enemyUnits) {
				if (cursor.canSelect(enemy)) {
					game.setScreen(new StatusWindow(enemy, game, currentUnits, totalUnits));
				}
			}
		}

		// Changes State if all players have moved
		if (currentUnits == 0) {
			turnNumber++;
			currentUnits = totalUnits;
			for (int i = 0; i < playerUnits.size(); i++) {
				playerUnits.get(i).isSelectable = true;
				playerUnits.get(i).mapImage.setColor(Color.WHITE);
			}
			currentState = State.SELECTENEMY;
		}
	}

	public void selectedPlayerState() {
		// State Logic (Controls a player unit)
		cursor.updateMove(mapWidth, mapHeight, selectedUnit, playerUnits, enemyUnits);
		selectedUnit.drawMovement(batch);

		// Change State if the Menu option is selected
		if (Gdx.input.isKeyJustPressed(game.FORWARD)) {
			selectedUnit.setX(cursor.getX());
			selectedUnit.setY(cursor.getY());
			currentState = State.MENU;
		}

		// Changes State back to Pan (Basically an undo button)
		if (Gdx.input.isKeyJustPressed(game.BACK)) {
			selectedUnit.isSelectable = true;
			currentState = State.PAN;
		}
	}

	public void menuState() {
		// State Logic (Controls Action Menu)
		actMenu.navigateMenu(game);
		actMenu.drawMenu(batch, camera);

		// Change State if a menu option is selected
		if (Gdx.input.isKeyJustPressed(game.FORWARD)) {
			switch (ActionMenu.selectionNumber) {
			case 1:
				currentState = State.ATTACKMENU;
				break;
			case 2:
				game.setScreen(new StatusWindow(selectedUnit, game, currentUnits, totalUnits));
				break;
			case 3:
				selectedUnit.isSelectable = false;
				currentUnits -= 1;
				selectedUnit.mapImage.setColor(Color.LIGHT_GRAY);
				currentState = State.PAN;
				break;
			}
		}

		//Undo Button
		if (Gdx.input.isKeyJustPressed(game.BACK)) {
			selectedUnit.setX(PlayerUnit.origX);
			selectedUnit.setY(PlayerUnit.origY);
			cursor.setX(PlayerUnit.origX);
			cursor.setY(PlayerUnit.origY);
			currentState = State.LOCKED;
		}
	}

	public void playerAttackState() {
		// State Logic (Attack Logic)
		cursor.updateAttack(mapWidth, mapHeight, selectedUnit);
		selectedUnit.drawAttack(batch);
		drawFightMenu();

		// Change State if the player chooses to attack an enemy
		if (Gdx.input.isKeyJustPressed(game.FORWARD)) {
			for (EnemyUnit enemy : enemyUnits) {
				if (cursor.canSelect(enemy)) {
					selectedUnit.attackEnemy(enemy);
					currentUnits -= 1;
					
					//Pass information and change state when attacking
					Assets.slash.setX(enemy.xPos*HeroGame.SCALE - HeroGame.SCALE/8);
					Assets.slash.setY(enemy.yPos*HeroGame.SCALE);
					drawSlash = true;
					currentState = State.ATTACKANIM;
				}
			}
		}

		//Undo Button
		if (Gdx.input.isKeyJustPressed(game.BACK)) {
			currentState = State.MENU;
		}
	}
	
	/**
	 * Selects an enemy unit for the AI to move
	 */
	public void enemySelectState() {
		// If Enemies have moved go to player turn
		if (enemyCounter >= enemyUnits.size()) {
			enemyCounter = 0;
			currentState = State.PAN;
			
			//Fixes camera pan issues
			cursor.setX(playerUnits.get(0).xPos);
			cursor.setY(playerUnits.get(0).yPos);
			
		} else {
			//Moves a selected enemy unit (AI)
			selectedEnemy = enemyUnits.get(enemyCounter);
			currentState = State.ENEMYMOVE;
		}
	}

	//Handles the AI Movement logic 
	public void enemyMoveState(float deltaTime) {
		//Gets the movement path and sets up movement animation
		if(selectedEnemy.frameNumber < 0) {
			selectedEnemy.move(playerUnits, enemyUnits, mapWidth, mapHeight);
		}

		//Changes how fast the animation plays
		timeSinceFrame += deltaTime;
		//.03 seems good
		if(timeSinceFrame > .03f) {

			//Walks the unit through animation states until the unit reaches the target and then attacks
			if(selectedEnemy.frameNumber > 0) {
					selectedEnemy.xPos = selectedEnemy.movementPath.get(selectedEnemy.frameNumber).xPos;
					selectedEnemy.yPos = selectedEnemy.movementPath.get(selectedEnemy.frameNumber).yPos;	
					cursor.setX(selectedEnemy.xPos);
					cursor.setY(selectedEnemy.yPos);
			}
			
			//End of movement
			if(selectedEnemy.frameNumber == 0) {
				
				//Check if can attack
				boolean attacked = false;
				for (float i = -selectedEnemy.attackRange; i <= selectedEnemy.attackRange; i++) {
					for (float j = -(selectedEnemy.attackRange - Math.abs(i)); j <= (selectedEnemy.attackRange - Math.abs(i)); j++) {
						
						//If can attack, attack and end turn
						if (selectedEnemy.getX() + i == selectedEnemy.target.getX() && selectedEnemy.target.getY() + j == selectedEnemy.getY()) {
							selectedEnemy.attack(selectedEnemy.target);
							
							//Pass information and change state when attacking
							Assets.slash.setX(selectedEnemy.target.xPos*HeroGame.SCALE - HeroGame.SCALE/8);
							Assets.slash.setY(selectedEnemy.target.yPos*HeroGame.SCALE);
							drawSlash = true;
							currentState = State.ENEMYATTACK;
							attacked = true;
						}
					}
				}
				
				//If can't attack end turn
				if(attacked != true) {
					enemyCounter++;
					currentState = State.SELECTENEMY;
				}
			}
			
			//Resets frame timer, and moves to next frame
			selectedEnemy.frameNumber -= 1;
			timeSinceFrame = 0f;
		} 
	}
	
	//prints attack Animation and checks for deaths
	public void enemyAttackState(float delta) {
		timeSinceFrame += delta;
		
		//Animation finishes
		if(timeSinceFrame > .4f) {
			//Stop drawing and reset logic
			timeSinceFrame = 0;
			drawSlash = false;
			enemyCounter++;
			deathCheck();
			
			//Change state
			currentState = State.SELECTENEMY;
		}
	}

	//Handles Attack Animation
	public void attackAnimationState(float delta) {
		timeSinceFrame += delta;
		
		//Animation finishes
		if(timeSinceFrame > .4f) {
			//Stop drawing and reset logic
			timeSinceFrame = 0;
			drawSlash = false;
			deathCheck();
			
			//Change state
			currentState = State.PAN;
		}
	}
	
	/**
	 * Draws a small menu when
	 * the player chooses to attack an enemy unit
	 */
	public void drawFightMenu() {
		for (EnemyUnit enemy : enemyUnits) {
			if (cursor.canSelect(enemy)) {
				batch.draw(Assets.butBack, camera.position.x + 18, camera.position.y + 32, 64, 96);
				Assets.font.draw(batch, selectedUnit.name, camera.position.x + 24, camera.position.y + 96);
				Assets.font.draw(batch, "Damage: " + selectedUnit.damage, camera.position.x + 24, camera.position.y + 80);
				Assets.font.draw(batch, enemy.name, camera.position.x + 24, camera.position.y + 64);
				Assets.font.draw(batch, "Health: " + enemy.currentHealth, camera.position.x + 24, camera.position.y + 48);
			}
		}
	}
	
	/**
	 * Draws the player units, enemies and cursor and UI Essentially anything that
	 * can "move" alongside the camera
	 */
	public void drawAll() {
		// Players and enemies
		for (int i = 0; i < enemyUnits.size(); i++) {
			enemyUnits.get(i).draw(batch, HeroGame.SCALE);
		}
		
		for (int i = 0; i < playerUnits.size(); i++) {
			playerUnits.get(i).draw(batch, HeroGame.SCALE);
		}
		
		//Draw slash
		if(drawSlash == true) {
			Assets.slash.draw(batch);
		}
		
		cursor.draw(batch, HeroGame.SCALE);

		// UI
		Assets.font.draw(batch, "TURN: " + turnNumber, camera.position.x - 96, camera.position.y + 144);
		Assets.font.draw(batch, "UNITS LEFT: " + currentUnits + " / " + totalUnits, camera.position.x - 96, camera.position.y + 128);
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
		camX = camera.position.x;
		camY = camera.position.y;
	}

	@Override
	public void dispose() {
		batch.dispose();
		rend.dispose();
	}
}
