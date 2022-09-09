package com.theincgi.genetic.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.theincgi.genetic.OptionGene;

public class LetterGene extends OptionGene<Character> {
	private static final long serialVersionUID = -8001866639391690859L;
	public static final List<Character> options = new ArrayList<>();
	static {
		for( int i = 32; i < 127; i++ )
			options.add((char) i);
	}
	
	public LetterGene(Random random, List<Character> options, Character initIndex) {
		super(random, options, initIndex);
	}

	public LetterGene(Random random, List<Character> options) {
		super(random, options);
	}

	public LetterGene(Random random) {
		super(random, options);
	}
	
	@Override
	public String toString() {
		return getValue().toString();
	}
	
	@Override
	public LetterGene copy() {
		LetterGene copy = new LetterGene(random, options, selected);
		copy.mutationChance = this.mutationChance;
		return copy;
	}
	
}