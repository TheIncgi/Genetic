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
		
		int done = 0;
		do {
			population.epoch();
			System.out.printf("Gen %4d | Score: %5.2f | Mutation: %6.3f | %s\n", population.getGenerations(), population.getBest().getScore(), population.getBest().getMaxMutationChance(), population.getBest());
			if( population.getBest().getMaxMutationChance() <= .1 )
				done++;
			else
				done = 0;
		} while( done < 5 );
		System.out.println();
		System.out.println("Expected: "+LEARN_THIS);
		System.out.println("Result:   "+population.getBest());
	}
	
	public static class LetterGene extends OptionGene<Character> {
		private static final long serialVersionUID = -8001866639391690859L;
		public static List<Character> options = new ArrayList<>();
		static {
			for( int i = 32; i < 127; i++ )
				options.add((char) i);
		}
		
		public LetterGene(Random random, List<Character> options, int initIndex) {
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
			LetterGene copy = new LetterGene(random, options, 0);
			copy.mutationChance = this.mutationChance;
			return copy;
		}
		
	}
	
	public static class StringEntity extends Entity {
		private static final long serialVersionUID = 9058208592191090151L;

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
			score = getMaxMutationChance(); //starts as null
			score += -abs(LEARN_THIS.length() - getGenes().size());
			if(LEARN_THIS.length() == getGenes().size()) {//correct length
				String self = toString();
				for( int i = 0; i < LEARN_THIS.length(); i++ ) {
					if( LEARN_THIS.charAt(i) == self.charAt(i) )
						score++;
				}
			}
		}
		
		@Override
		public void reset() {}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			for( Gene gene : getGenes().getValue().values() ) {
				if(gene == null)
					throw new NullPointerException("Null gene");
				if(!(gene instanceof LetterGene)) 
					throw new IllegalArgumentException();
				var letterGene = (LetterGene) gene;
				b.append(letterGene.getValue());
			}
			return b.toString();
		}
		
	}
}
