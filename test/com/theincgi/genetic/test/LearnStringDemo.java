package com.theincgi.genetic.test;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.theincgi.genetic.Entity;
import com.theincgi.genetic.Gene;
import com.theincgi.genetic.GeneBundle;
import com.theincgi.genetic.OptionGene;
import com.theincgi.genetic.Population;

public class LearnStringDemo {
	
	public static final String LEARN_THIS = "Cow foo bar cowbar";
	public static final Random random = new Random( 123 );
	
	public static void main(String[] args) {
		Population population = new Population(random, 
			(rand)->{ return new StringEntity(rand); },
			(geneBundle)->{ return new StringEntity(geneBundle); },
			500, 1000
		);
		
		do {
			population.epoch();
			System.out.printf("Gen %4d | %6.3f | %s\n", population.getGenerations(), population.getBest().getMaxMutationChance(), population.getBest());
		} while( population.getBest().getMaxMutationChance() < .1 );
		System.out.println();
		System.out.println("Expected: "+LEARN_THIS);
		System.out.println("Result:   "+population.getBest());
	}
	
	public static class LetterGene extends OptionGene<Character> {
		
		public static List<Character> options = new ArrayList<>();
		static {
			for( int i = 32; i < 127; i++ )
				options.add((char) i);
		}
		
		public LetterGene(Random random) {
			super(random, options);
		}
		
		@Override
		public String toString() {
			return getValue().toString();
		}
		
	}
	
	public static class StringEntity extends Entity {
		
		public StringEntity(Random random) {
			super( new GeneBundle(random, ()-> {
				return new LetterGene(random);
			}));
		}
		public StringEntity(GeneBundle genes) {
			super( genes );
		}
		
		@Override
		public void live() {
			score = 0f; //starts as null
			score += -abs(LEARN_THIS.length() - getGenes().size());
			if(score > -.5f) {//correct length
				int i = 0;
				for( Gene gene : getGenes().getValue().values() ) {
					if(!(gene instanceof LetterGene)) throw new IllegalArgumentException();
					var letterGene = (LetterGene) gene;
					if( LEARN_THIS.charAt(i++) == letterGene.getValue() )
						score++;
				}
			}
		}
		
		@Override
		public void reset() {}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			int i = 0;
			for( Gene gene : getGenes().getValue().values() ) {
				if(!(gene instanceof LetterGene)) throw new IllegalArgumentException();
				var letterGene = (LetterGene) gene;
				b.append(letterGene.getValue());
			}
			return b.toString();
		}
		
	}
}
