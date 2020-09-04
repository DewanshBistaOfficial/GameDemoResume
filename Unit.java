package com.hero.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Unit template for other classes
 * @author Dewansh
 */
public class Unit {
	//Stats
	int maxHealth, currentHealth, damage, attackRange, movement, defense;
	float xPos, yPos;
	Sprite mapImage;
	Sprite bar;
	
	//TODO add features
	Sprite headImage;
	String name;

	public Unit(Sprite mapImage, Sprite bar, String name, float xPos, float yPos, int movement, int attackRange, int maxHealth, int damage, int defense){
		setX(xPos);
		setY(yPos);
		this.movement = movement;
		this.attackRange = attackRange;
		this.maxHealth = maxHealth;
		this.currentHealth = maxHealth;
		this.damage = damage;
		this.mapImage = mapImage;
		this.defense = defense;
		this.name = name;
		this.bar = bar;
	}

	/**
	 * Draws the unit's map image and it's health bar
	 */
	public void draw(SpriteBatch batch, float UNIT) {
		mapImage.setX(xPos*UNIT);
		mapImage.setY(yPos*UNIT);
		mapImage.draw(batch);
		
		bar.setScale((float)currentHealth/maxHealth *(float)3/4, (float)3/4);
		bar.setX(xPos*UNIT - UNIT/4);
		bar.setY(yPos*UNIT + UNIT - UNIT/2 + UNIT/4);
		bar.draw(batch);
			}

	public float getX() {
		return xPos;
	}

	public void setX(float x) {
		xPos = x;
	}

	public float getY() {
		return yPos;
	}

	public void setY(float y) {
		yPos = y;
	}

	/**
	 * Has this unit attack another unit
	 * @param unit, the unit (in pixels) to move
	 */
	public void attack(Unit enemy) {
		enemy.currentHealth -= this.damage;
	}

	/**
	 * Move the unit right a unit
	 * @param unit, the unit (in pixels) to move
	 */
	public void moveRight() {
			setX(getX() + 1);
	}

	/**
	 * Move the unit left a unit
	 * @param unit, the unit (in pixels) to move
	 */
	public void moveLeft() {
			setX(getX() - 1);
	}

	/**
	 * Move the unit up a unit
	 * @param unit, the unit (in pixels) to move
	 */
	public void moveUp() {
			setY(getY() + 1);
	}

	/**
	 * Move the unit down a unit
	 * @param unit, the unit (in pixels) to move
	 */
	public void moveDown() {
			setY(getY() - 1);
	}
}
