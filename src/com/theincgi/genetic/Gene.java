package com.theincgi.genetic;


import java.io.Serializable;
import java.util.Random;

public abstract class Gene implements Serializable {
	float mutationChance = 2;
//	float mutationStrength = 1;
	protected final Random random;
	
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
		return getMutationChance() < random.nextFloat();
	}
	
	/**Cause the gene to change*/
	public void mutate() {
		mutationChance = clamp( mutationChance + rand(), -5f, 3f );
	}
	
	/**copy, but with mutations*/
	public Gene clone() {
		Gene g = copy();
		g.mutate();
		return g;
	}
	
	/**a perfect copy, no mutations*/
	abstract public Gene copy();
}
