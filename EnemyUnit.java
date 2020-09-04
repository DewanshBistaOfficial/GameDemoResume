package com.hero.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * The class representing enemies
 * (Contains various drawing and AI functions)
 * @author Dewansh
 */
public class EnemyUnit extends Unit {
	static int currentUnits;
	static int totalUnits;
	
	List<Cell> movementPath;
	
	int frameNumber = -1;
	PlayerUnit target;
	
	Cell thisCell;
	Cell targetCell;

	public EnemyUnit(Sprite mapImage, String name, float xPos, float yPos, int movement, int attackRange, int maxHealth, int damage, int defense) {
		super(mapImage, Assets.redBar, name, xPos, yPos, movement, attackRange, maxHealth, damage, defense);
		currentUnits += 1;
		totalUnits += 1;
	}

	/**
	 * Moves the enemy to a unit of chosen by the selectPriority function
	 * @param pUnits
	 * @param eUnits
	 * @param xBound
	 * @param yBound
	 */
	public void move(ArrayList<PlayerUnit> pUnits, ArrayList<EnemyUnit> eUnits, float xBound, float yBound) {
		
		//Selects the target from player units
		PlayerUnit selectedUnit = selectPriority(pUnits);
		target = selectedUnit;
		
		//Finds Movement Path
		List<Cell> path = getPath(selectedUnit, pUnits, eUnits, xBound, yBound);
		
		//Animation
		frameNumber = path.size() - 1; 		
		movementPath = path;
		
		//Cap to movement range
		if(frameNumber > movement) {
			int reduceMovement = frameNumber - movement - 1;
			while(reduceMovement > 0) {
				path.remove(0);
				reduceMovement--;
			}
		frameNumber = path.size() - 1; 		

		}
	}

	/**
	 * Assigns every unit a score value and selects the unit with the lowest score
	 * 
	 * @param units
	 * @return
	 */
	public PlayerUnit selectPriority(ArrayList<PlayerUnit> units) {
		if (units.size() <= 0) {
			return null;
		}

		// Initialize stuff
		int[] unitScore = new int[units.size()];
		for (int i = 0; i < units.size(); i++) {
			unitScore[i] = 0;
		}

		// Distance weighting
		for (int i = 0; i < units.size(); i++) {
			unitScore[i] += (int) Math.abs(xPos - units.get(i).xPos);
			unitScore[i] += (int) Math.abs(yPos - units.get(i).yPos);
		}

		// Damage weighting with health
		for (int i = 0; i < units.size(); i++) {
			unitScore[i] -= ((damage - units.get(i).defense) - units.get(i).currentHealth);
		}

		// Unit Selection
		PlayerUnit selectedUnit = units.get(0);
		int selectedScore = unitScore[0];
		for (int i = 0; i < units.size(); i++) {
			if (unitScore[i] < selectedScore) {
				selectedUnit = units.get(i);
				selectedScore = unitScore[i];
			}
		}
		return selectedUnit;
	}


	/*
	 * Gets a list containing the path (cells) that the unit should take to get to the goal
	 * Stores the list in movementPath list
	 */
	public List<Cell> getPath(PlayerUnit unit, ArrayList<PlayerUnit> pUnits, ArrayList<EnemyUnit> eUnits, float xBound, float yBound) {
		//Returns an empty list
		if(unit == null) {
			 return new ArrayList<Cell>();
		}
		
		//Gets rid of target from unreachable list
		ArrayList<PlayerUnit> pUnitMinusTarget = new ArrayList<PlayerUnit>();
		pUnitMinusTarget.addAll(pUnits);
		pUnitMinusTarget.remove(unit);
		
		List<Cell> openList =  new ArrayList<Cell>();
		List<Cell> closedList =  new ArrayList<Cell>();
		List<Cell> path = new ArrayList<Cell>();		
		
		//Sets starting cell and goal cell
		Cell start = new Cell(xPos, yPos, unit.xPos, unit.yPos, 0, null);
		openList.add(start);
		Cell goal = new Cell(unit.xPos, unit.yPos, unit.xPos, unit.yPos, 100000000, null);	
		
		//A*
		while(true) {
			Cell current = openList.get(0);
			openList.remove(0);
			closedList.add(current);
			
			//If goal is reached
			if (current.equals(goal)) {
				//Trace Path
				while(current!= null) {
					path.add(current);
					current = current.parent;
				}
				//Return path
				return path;
			}
			
			//Add neighbors
			for(Cell neighbour: getAdjacent(current, unit)) {
				//Make sure neighbor is not in closes list and is reachable
				if(checkCollision(neighbour.xPos, neighbour.yPos, pUnitMinusTarget, eUnits, xBound, yBound)) {
					if(!closedList.contains(neighbour)) {
						//Neighbor is not open or Path to the neighbor is shorter
						if(!openList.contains(neighbour)){
							openList.add(neighbour);
						}
						else if(openList.get(openList.indexOf(neighbour)).finalCost > neighbour.finalCost) {
							openList.remove(openList.indexOf(neighbour));
							openList.add(neighbour);
						}
					}
				}
			}
			openList.sort(new CellSort());
		}
	}

	/**
	 * Create a queue of all reachable neighbors from the base cell
	 **/
	public Queue<Cell> getAdjacent(Cell base, PlayerUnit unit) {
		Queue<Cell> adjQueue = new PriorityQueue<Cell>();		
		
		Cell Left = new Cell(base.xPos + 1, base.yPos, unit.xPos, unit.yPos, base.moveCost+1, base);
			adjQueue.add(Left);
		Cell Right = new Cell(base.xPos - 1, base.yPos, unit.xPos, unit.yPos, base.moveCost+1, base);
			adjQueue.add(Right);
		Cell Up = new Cell(base.xPos, base.yPos + 1, unit.xPos, unit.yPos, base.moveCost+1, base);
			adjQueue.add(Up);
		Cell Down = new Cell(base.xPos, base.yPos - 1, unit.xPos, unit.yPos, base.moveCost+1, base);	
			adjQueue.add(Down);
		
		return adjQueue;
	}

	/**
	 * Checks if a collide-able coordinates would collide with
	 * one of the objects in the input
	 **/
	private boolean checkCollision(float xPos, float yPos, ArrayList<PlayerUnit> pUnits, ArrayList<EnemyUnit> eUnits, float xBounds, float yBounds) {
		// Player Units
		for (int i = 0; i < pUnits.size(); i++) {
			if (xPos == pUnits.get(i).xPos && yPos == pUnits.get(i).yPos) {
				return false;
			}
		}

		// Enemy Units
		for (int i = 0; i < eUnits.size(); i++) {
			if (xPos == eUnits.get(i).xPos && yPos == eUnits.get(i).yPos) {
				return false;
			}
		}

		// Check Map Bounds
		if (xPos >= xBounds || xPos < 0 || yPos >= yBounds || yPos < 0) {
			return false;
		}

		return true;
	}

}


/**
 * Sorts cells based on heuristic plus cost
 * @author Dewansh
 */
class CellSort implements Comparator<Cell> 
{ 
    // Sort Cells
    public int compare(Cell a, Cell b) 
    { 
        return a.compareTo(b);
    } 
} 


/**
 * Helper Class for A* search (Basically a graph Node)
 * @author Dewansh
 */
class Cell implements Comparable<Cell> {
	public float moveCost, hueristicCost, finalCost, xPos, yPos;
	public Cell parent;

	Cell(float xPos, float yPos, float goalXPos, float goalYPos, float moveCost, Cell parent) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.hueristicCost = Math.abs(xPos - goalXPos) + Math.abs(yPos - goalYPos);
		this.moveCost = moveCost;	
		this.finalCost = hueristicCost + this.moveCost;
		this.parent = parent;
	}

	//Less cost is less than 0
	@Override
	public int compareTo(Cell other) {
		return (int) (finalCost - other.finalCost);
	}

	// Checks if two cells are equal so that if the positions are equal the cells are equal
	@Override
	public boolean equals(Object other) {
		// Check for class
		if (other.getClass() != this.getClass()) {
			return false;
		} else {
			// Assume class
			Cell otherCell = (Cell) other;
			// Check if positions match
			if (otherCell.xPos == this.xPos && otherCell.yPos == this.yPos) {
				return true;
			}
			return false;
		}
	}
}