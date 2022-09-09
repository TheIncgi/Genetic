package com.theincgi.genetic.test;

import java.util.Random;

import com.theincgi.genetic.GeneArrayBundle;

public class MutationTest {
	
	
	public static void main(String[] args) {		
		Random random = new Random(1235);
		StringEntity str = new StringEntity( random );
		
		for(int i = 0; i<50; i++)
			str.getGenes().addGene();
		GeneArrayBundle gab = str.getGenes();
		System.out.println( str );
		gab.get(0).get().mutate();
		System.out.println( str );
		gab.mutate();
		System.out.println( str );
		
	}
	
}
