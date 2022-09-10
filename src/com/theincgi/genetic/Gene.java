package com.theincgi.genetic;


import java.io.Serializable;
import java.util.Optional;
import java.util.Random;

public abstract class Gene implements Serializable {
	protected float mutationChance = 2;
//	float mutationStrength = 1;
	protected final Random random;
	private Gene parent;
	
	public Gene( Random random ) {
		this.random = random;
	}
	
	abstract public Object getValue();
	
	/**random value for mutating, can be positive or negative, [-.5, .5]*/
	protected float rand() {
		var x = random.nextFloat() + random.nextFloat() + random.nextFloat();
		return x / 3 - .5f;
	}
	
	protected float clamp( float v, Float min, Float max ) {
		if( min != null )
			v = Math.max(min, v);
		if( max != null)
			v = Math.min(max, v);
		return v;
	}
	
	/**raw mutation chance passed through a sigmoid function*/
	public float getMutationChance() {
		return (float) (1 / ( 1 + Math.exp(-mutationChance)));
	}
	public float getRawMutationChance() {
		return mutationChance;
	}
	
	/**Randomly returns true depending on mutation chance*/
	public boolean shouldMutateNow() {
		return random.nextFloat() <= getMutationChance();
	}
	
	/**Cause the gene to change*/
	public void mutate() {
		mutationChance = clamp( mutationChance + 2*rand(), -5f, 3f );
	}
	
	/**copy, but with mutations*/
	public Gene clone() {
		Gene g = copy();
		g.mutate();
		return g;
	}
	
	/**a perfect copy, no mutations*/
	abstract public Gene copy();
	
	public Optional<Gene> getParent() {
		return Optional.ofNullable(parent);
	}
	public void setParent(Gene parent) {
		this.parent = parent;
	}
	public void setParent(Optional<Gene> parent) {
		this.parent = parent.isPresent() ? parent.get() : null;
	}
	
	public void updateParenting( Optional<Gene> parent ) {
		setParent(parent);
	}
}
