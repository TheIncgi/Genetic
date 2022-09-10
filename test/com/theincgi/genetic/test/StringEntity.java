package com.theincgi.genetic.test;

import static java.lang.Math.abs;

import java.util.Random;

import com.theincgi.genetic.Entity;
import com.theincgi.genetic.Gene;
import com.theincgi.genetic.GeneArrayBundle;
import com.theincgi.genetic.GeneBundle;
import com.theincgi.genetic.Population;

public class StringEntity extends Entity {
	private static final long serialVersionUID = 9058208592191090151L;

	public StringEntity(Random random) {
		super( new GeneArrayBundle(random, ()-> {
			return new LetterGene(random);
		}));
	}
	public StringEntity(GeneBundle genes) {
		super( genes );
	}
	
	@Override
	public void live( Population unused ) {
		score = 0f;// getMaxMutationChance()/26f; //starts as null
		score += -abs(LearnStringDemo.LEARN_THIS.length() - getGenes().size());
		if(LearnStringDemo.LEARN_THIS.length() == getGenes().size()) {//correct length
			String self = toString();
			for( int i = 0; i < LearnStringDemo.LEARN_THIS.length(); i++ ) {
				if( LearnStringDemo.LEARN_THIS.charAt(i) == self.charAt(i) )
					score += (float)LearnStringDemo.LEARN_THIS.length()+1;
			}
		}
		
			score -= age / 4f;
	}
	
	@Override
	public void reset() {}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for( Gene gene : getGenes().getGenesIterable() ) {
			if(gene == null)
				throw new NullPointerException("Null gene");
			if(!(gene instanceof LetterGene)) 
				throw new IllegalArgumentException();
			var letterGene = (LetterGene) gene;
			b.append(letterGene.getValue());
		}
		return b.toString();
	}
	
	@Override
	public GeneArrayBundle getGenes() {
		return (GeneArrayBundle) super.getGenes();
	}
	
}