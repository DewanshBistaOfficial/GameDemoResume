package com.hero.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

	/**
	 * A class that represents the cursor 
	 * with which the player interacts with the game
	 * @author Dewansh
	 */
	public class Cursor{
		Sprite image;
		float xPos, yPos;
		private HeroGame game;

		public Cursor(HeroGame game, Sprite sprite, float xPos, float yPos){
			image = sprite;
			setX(xPos);
			setY(yPos);
			this.game = game;
		}

	//Various Getters and Setters
		public float getX() {
			return xPos;
		}
		public void setX(float xPos) {
			this.xPos = xPos;
		}
		public float getY() {
			return yPos;
		}
		public void setY(float yPos) {
			this.yPos = yPos;
		}

		/**
		 * Draw the cursor scaled by the game UNIT
		 * @param UNIT, game unit to scale by
		 */
		public void draw(SpriteBatch batch, float UNIT) {
			image.setX(xPos*UNIT);
			image.setY(yPos*UNIT);
			image.draw(batch);
		}


		/**
		 * Updates the cursor
		 */
		public void update(float xBound, float yBound) {
				if(Gdx.input.isKeyJustPressed(game.RIGHT)) {
					if(getX() + 1 < xBound) {
						setX(getX() + 1);
				}}
				
				if(Gdx.input.isKeyJustPressed(game.LEFT)) {
					if(getX() - 1 >= 0) {
					setX(getX() - 1);
				}}

				if(Gdx.input.isKeyJustPressed(game.UP)) {
					if(getY() + 1 < yBound*1) {
						setY(getY() + 1);
				}}

				if(Gdx.input.isKeyJustPressed(game.DOWN)) {
					if(getY() - 1 >= 0) {
					setY(getY() - 1);
				}}
		}

		/**
		 * Updates the cursor when a player is attacking
		 */
		public void updateAttack(float xBound, float yBound, PlayerUnit player) {
				if(Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
					if(getX() + 1 <= player.getX() + player.attackRange - Math.abs((getY() - player.getY()))) {
					if(getX() + 1 < xBound) {
						setX(getX() + 1);
				}}}

					if(Gdx.input.isKeyJustPressed(Keys.LEFT)) {
						if(getX() - 1 >= player.getX() - player.attackRange + Math.abs((getY() - player.getY()))) {
						if(getX() - 1 >= 0) {
							setX(getX() - 1);
				}}}

					if(Gdx.input.isKeyJustPressed(Keys.UP)) {
						if(getY() + 1 <= player.getY() + player.attackRange - Math.abs((getX() - player.getX()))) {
						if(getY() + 1 < yBound) {
							setY(getY() + 1);
				}}}

					if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
						if(getY() - 1 >= player.getY() - player.attackRange + Math.abs((getX() - player.getX()))) {
						if(getY() - 1 >= 0) {
						setY(getY() - 1);
				}}}
	}

		/**
		 * Checks for collisions on the X axis
		 */
		public boolean collisionX(float futureMove, ArrayList<? extends Unit> units) {
			for(int i = 0; i < units.size(); i++) {
				if(futureMove == units.get(i).getX() && getY() == units.get(i).getY()) {
				return false;
				}
			}
			return true;
		}

		/**
		 * Checks for collisions on the Y axis
		 */
		public boolean collisionY(float futureMove, ArrayList<? extends Unit> units) {
			for(int i = 0; i < units.size(); i++) {
				if(futureMove == units.get(i).getY() && getX() == units.get(i).getX()) {
				return false;
				}
			}
			return true;
		}

	
		/**
		 * Performs both a movement and collision check
		 * @param UNIT unit in pixels of map
		 * @param playerUnits, entities to collide with
		 * @return true if unit can move false otherwise
		 */
		public boolean checkRight(float xBound, PlayerUnit unit, ArrayList<PlayerUnit> playerUnits, ArrayList<EnemyUnit> enemyUnits) {
			boolean canRight = false;
			if(getX() + 1 <= unit.getX() + unit.movement - Math.abs((getY() - unit.getY())) && getX() + 1 < xBound) {
				canRight = true;
		}
			//Collision
			if(!(collisionX(getX() + 1, playerUnits) && collisionX(getX() + 1, enemyUnits))) {
				canRight = false;
			}

			return canRight;
		}

		/**
		 * Performs both a movement and collision check
		 * @param UNIT unit in pixels of map
		 * @param playerUnits, entities to collide with
		 * @return true if unit can move false otherwise
		 */
		public boolean checkLeft(PlayerUnit unit, ArrayList<PlayerUnit> playerUnits, ArrayList<EnemyUnit> enemyUnits) {
			boolean canLeft = false;
			if(getX() - 1 >= unit.getX() - unit.movement + Math.abs((getY() - unit.getY())) && (getX() - 1 >= 0)) {
				canLeft = true;
		}
			//Collision
			if(!(collisionX(getX() - 1, playerUnits) && collisionX(getX() - 1, enemyUnits))) {
				canLeft = false;
			}

			return canLeft;
		}

		/**
		 * Performs both a movement and collision check
		 * @param UNIT unit in pixels of map
		 * @param yBound map bound
		 * @param playerUnits, entities to collide with
		 * @return true if unit can move false otherwise
		 */
		public boolean checkUp(float yBound, PlayerUnit unit, ArrayList<PlayerUnit> playerUnits, ArrayList<EnemyUnit> enemyUnits) {
			boolean canUp = false;
			if(getY() + 1 <= (unit.getY() + unit.movement) - Math.abs((getX() - unit.getX())) && getY() + 1 < yBound) {
					canUp = true;
		}

			//Collision
			if(!(collisionY(getY() + 1, playerUnits) && collisionY(getY() + 1, enemyUnits))) {
				canUp = false;
			}

			return canUp;
		}

		/**
		 * Performs both a movement and collision check
		 * @param UNIT unit in pixels of map
		 * @param playerUnits, entities to collide with
		 * @return true if unit can move false otherwise
		 */
		public boolean checkDown(PlayerUnit unit, ArrayList<PlayerUnit> playerUnits, ArrayList<EnemyUnit> enemyUnits) {
			boolean canDown = false;
			if(getY() - 1 >= unit.getY() - unit.movement + Math.abs((getX() - unit.getX())) && (getY() - 1 >= 0)) {
				canDown = true;
		}
			//Collision
			if(!(collisionY(getY() - 1, playerUnits) && collisionY(getY() - 1, enemyUnits))) {
				canDown = false;
			}

		return canDown;
		}

		/**
		 * Updates the cursor when a player is moving
		 * @param UNIT
		 * @param xBound
		 * @param yBound
		 * @param player
		 * @param playerUnits
		 */
		public void updateMove(float xBound, float yBound, PlayerUnit player, ArrayList<PlayerUnit> playerUnits, ArrayList<EnemyUnit> enemyUnits) {
			//Makes it so that the player can move to itself
			@SuppressWarnings("unchecked")
			ArrayList<PlayerUnit> otherPlayers = (ArrayList<PlayerUnit>) playerUnits.clone();
			otherPlayers.remove(player);
			
				if(Gdx.input.isKeyJustPressed(Keys.RIGHT) && checkRight(xBound, player, otherPlayers, enemyUnits)) {
					setX(getX() + 1);
				}

				if(Gdx.input.isKeyJustPressed(Keys.LEFT) && checkLeft(player, otherPlayers, enemyUnits)){
					setX(getX() - 1);
				}

				if(Gdx.input.isKeyJustPressed(Keys.UP) && checkUp(yBound, player, otherPlayers, enemyUnits)) {
					setY(getY() + 1);
				}

				if(Gdx.input.isKeyJustPressed(Keys.DOWN) && checkDown(player, otherPlayers, enemyUnits)) {
					setY(getY() - 1);
				}
	}

		/**
		 * Checks if the unit is under the cursor and is selectable
		 * @param unit
		 * @return
		 */
		public boolean canSelect(PlayerUnit unit) {
			if(getX() == unit.getX() && getY() == unit.getY() && unit.isSelectable) {
				return true;
			}
			return false;
		}

		/**
		 * Checks if the unit is under the cursor
		 * @param unit
		 * @return
		 */
		public boolean canSelect(EnemyUnit unit) {
			if(getX() == unit.getX() && getY() == unit.getY()) {
				return true;
			}
			return false;
		}

	}
