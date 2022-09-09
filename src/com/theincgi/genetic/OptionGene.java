package com.theincgi.genetic;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class OptionGene<T> extends Gene implements Serializable {
	private List<T> options;
	protected T selected;
	
	public OptionGene( Random random, List<T> options ) {
		super(random);
		this.options = options;
		mutate();
	}
	
	public OptionGene( Random random, List<T> options, T initIndex ) {
		super(random);
		this.options = options;
		//if !options.contains(initIndex) err
		selected = initIndex;
	}
	
	@Override
	public T getValue() {
		return selected;
	}

	@Override
	public void mutate() {
		super.mutate();
		selected = options.get(random.nextInt(options.size()));
	}

	@Override
	public OptionGene<T> copy() {
		OptionGene<T> copy = new OptionGene<>(random, options, selected);
		copy.mutationChance = this.mutationChance;
		return copy;
	}

}
