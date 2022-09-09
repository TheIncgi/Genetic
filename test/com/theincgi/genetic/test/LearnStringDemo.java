package com.theincgi.genetic.test;

import java.util.Random;

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
			System.out.printf("Gen %4d | Score: %5.2f | Mutation Max: %6.3f Avg: %6.3f | %s\n", 
				population.getGenerations(), 
				population.getBest().getScore(), 
				population.getBest().getMaxMutationChance(), 
				population.getBest().getAvgMutationChance(), 
				population.getBest()
			);
			if( population.getBest().getMaxMutationChance() <= .1 )
				done++;
			else
				done = 0;
		} while( done < 5 );
		System.out.println();
		System.out.println("Expected: "+LEARN_THIS);
		System.out.println("Result:   "+population.getBest());
	}
}
