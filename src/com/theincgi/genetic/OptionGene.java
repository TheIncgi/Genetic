package com.theincgi.genetic;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class OptionGene<T> extends Gene implements Serializable {
	private List<T> options;
	private int selected;
	
	public OptionGene( Random random, List<T> options ) {
		super(random);
		this.options = options;
		mutate();
	}
	
	public OptionGene( Random random, List<T> options, int initIndex ) {
		super(random);
		this.options = options;
		selected = initIndex;
	}
	
	@Override
	public T getValue() {
		return options.get(selected);
	}

	@Override
	public void mutate() {
		super.mutate();
		selected = (int) (Math.random() * options.size());
	}

	@Override
	public OptionGene<T> copy() {
		OptionGene<T> copy = new OptionGene<>(random, options, selected);
		copy.mutationChance = this.mutationChance;
		return copy;
	}

}
