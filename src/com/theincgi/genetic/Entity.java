package com.theincgi.genetic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

abstract public class Entity implements Serializable {
	GeneBundle genes;
	int age = 0;
	protected boolean rescoreEveryCycle = false;
	protected Float score = null;
	
	public Entity( GeneBundle genes ) {
		this.genes = genes;
	}
	
	public float getMaxMutationChance() {
		return genes.getMaxMutationChance();
	}
	
	public Entity makeChild( List<Entity> parents, Function<GeneBundle, Entity> entityFactory ) {
		List<GeneBundle> bundles = new ArrayList<>();
		for (var parent : parents) {
			bundles.add(parent.getGenes());
		}
		Entity child = entityFactory.apply( genes.copy() );
		child.genes.mix(bundles);
		child.genes.mutate();
		return child;
	}
	
	abstract public void live();
	abstract public void reset();
	public Float getScore() {
		return score;
	}
	public GeneBundle getGenes() {
		return genes;
	}
	public int getAge() {
		return age;
	}
	public boolean isRescoreEveryCycle() {
		return rescoreEveryCycle;
	}
}
