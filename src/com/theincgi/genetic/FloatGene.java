package com.theincgi.genetic;

import java.io.Serializable;
import java.util.Random;

public class FloatGene extends Gene implements Serializable {
	private float value = 0;
	private float mutationStrength = 1;
	private Float min = null, max = null;
	
	public FloatGene(Random random) {
		super( random );
		mutate();
	}
	public FloatGene(Random random, float initValue, Float min, Float max) {
		super( random );
		value = initValue;
		this.min = min;
		this.max = max;
	}
	
	public FloatGene setMin(Float min) {
		this.min = min;
		return this;
	}
	
	public FloatGene setMax(Float max) {
		this.max = max;
		return this;
	}
	
	@Override
	public Float getValue() {
		return value;
	}
	
	@Override
	public void mutate() {
		super.mutate();
		value = clamp(value + rand() * mutationStrength, min, max);
		mutationStrength += 3*rand() * mutationStrength;
	}
	
	@Override
	public FloatGene copy() {
		FloatGene copy = new FloatGene(random, value, min, max);
		copy.mutationChance = mutationChance;
		copy.mutationStrength = mutationStrength;
		return copy;
	}
	
	
}
