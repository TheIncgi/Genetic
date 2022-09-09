package com.theincgi.genetic.test;

import java.util.List;
import java.util.Random;

import com.theincgi.genetic.GeneArrayBundle;

public class MixingTest {
	
	
	
	
	public static void main(String[] args) {
		StringEntity a, b;
		Random random = new Random(1235);
		a = new StringEntity(random);
		b = new StringEntity(random);
		
		GeneArrayBundle gaba = a.getGenes();
		GeneArrayBundle gabb = b.getGenes();
		for(int i = 0; i<50; i++) {
			gaba.getGenes().add( new LetterGene(random, LetterGene.options, 'A') );
			gabb.getGenes().add( new LetterGene(random, LetterGene.options, 'B') );
		}
		
		System.out.println(a);
		System.out.println(b);
		StringEntity c = (StringEntity) a.makeChild(List.of(b), (gb)->{return new StringEntity(gb);}, false);
		System.out.println(c);
	}
	
	
}
