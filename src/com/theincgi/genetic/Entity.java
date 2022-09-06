package com.theincgi.genetic;

import java.io.Serializable;

abstract public class Entity implements Serializable {
	GeneBundle genes;
	int age = 0;
	boolean isAlive = true;
	boolean rescoreEveryCycle = false;
	private Float score = null;
	
	
	abstract public boolean shouldDie();
	abstract public void live();
	abstract float getMaxMutationChance();
	abstract public void reset();
	abstract public float score();
}
