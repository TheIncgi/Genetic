package com.theincgi.genetic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

abstract public class Entity implements Serializable {
	GeneBundle genes;
	public int age = 0;
	protected boolean rescoreEveryCycle = false;
	protected Float score = null;
	
	/**Call {@link #updateGeneParenting()} after constructing*/
	public Entity( GeneBundle genes ) {
		this.genes = genes;
	}
	
	/**Call {@link #updateGeneParenting()} after constructing*/
	protected Entity() {}
	protected void setGenes(GeneBundle genes) {
		this.genes = genes;
	}
	
	public float getMaxMutationChance() {
		return genes.getMaxMutationChance();
	}
	
	public float getAvgMutationChance() {
		return genes.getAvgMutationChance();
	}
	
	public Entity makeChild( List<Entity> parents, Function<GeneBundle, Entity> entityFactory ) {
		return makeChild(parents, entityFactory, true);
	}
	public Entity makeChild( List<Entity> parents, Function<GeneBundle, Entity> entityFactory, boolean mutate ) {
		List<GeneBundle> bundles = new ArrayList<>();
		for (var parent : parents) {
			bundles.add(parent.getGenes());
		}
		Entity child = entityFactory.apply( genes.copy() );
		child.updateGeneParenting();
		child.genes.mix(bundles);
		if(mutate)
			child.genes.mutate();
		return child;
	}
	
	public void updateGeneParenting() {
		genes.updateParenting( Optional.empty() );
	}
	
	abstract public void live( Population population );
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
		return rescoreEveryCycle; //TODO use this
	}
}
