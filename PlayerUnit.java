package com.hero.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

	/**
	 * Class that represents units under the player's control
	 * @author Dewansh
	 */
	public class PlayerUnit extends Unit{
		final String ID, MAPID; 
		static float origX, origY;
		boolean isSelectable = true;
		//static Texture headArt;

		/**
		 * Full Constructor
		 */
		public PlayerUnit(Sprite mapImage, String name, String MAPID, String ID, float xPos, float yPos, int movement, int attackRange, int maxHealth, int damage, int defense){
			super(mapImage, Assets.greenBar, name, xPos, yPos, movement, attackRange, maxHealth, damage, defense);
			//Unique Fields
			this.ID = ID;
			this.MAPID = MAPID;
		}
		
		/**
		 * Constructor to be used for loading and saving
		 */
		public PlayerUnit(Sprite mapImage, String name, String MAPID, String ID, int movement, int attackRange, int maxHealth, int damage, int defense){
			super(mapImage, Assets.greenBar, name, 0, 0, movement, attackRange, maxHealth, damage, defense);
			
			//Unique Fields
			this.ID = ID;
			this.MAPID = MAPID;
		}

		//TODO Try to account for terrain geometry (Particularly non-passable terrain)
		/**
		 * Draws a bunch of blue tiles showing the units movement range
		 */
		public void drawMovement(Batch batch) {
			for(int i = -movement; i<= movement; i++) {
				for(int j = -(movement-Math.abs(i)); j<= movement-Math.abs(i); j++) {
					batch.draw(Assets.blue, 16*(getX()+i),  16*(getY()+j));
				}
			}
		}

		/**
		 * Draws the tiles that this unit can attack
		 * @param UNIT
		 * @param batch
		 */
		public void drawAttack(Batch batch){
				for(int i = -attackRange; i<= attackRange; i++) {
					for(int j = -(attackRange-Math.abs(i)); j<= (attackRange-Math.abs(i)); j++) {
						batch.draw(Assets.red, 16*(getX()+i),  16*(getY()+j));
					}
				}
		}

		/**
		 * Chooses a enemy unit to attack and attacks them
		 * @param enemyUnits, the list of enemies to choose from
		 */
		public void attackEnemy(EnemyUnit enemy) {
				attack(enemy);
				mapImage.setColor(Color.LIGHT_GRAY);
		}
	}
