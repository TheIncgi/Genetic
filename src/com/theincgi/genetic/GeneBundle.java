package com.theincgi.genetic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class GeneBundle extends Gene implements Serializable {
	protected HashMap<Object, Gene> genes = new HashMap<>();
	protected FloatGene addRemoveFactor;
	private Supplier<Gene> geneFactory;
	private int next = 0;
	
	
	/**Nullable factory if no genes are to be added or removed by chance*/
	public GeneBundle(Random random, Supplier<Gene> geneFactory ) {
		super(random);
		addRemoveFactor = new FloatGene(random, .5f, 0f, 1f);
		this.geneFactory = geneFactory;
	}
	
	/**Nullable factory if no genes are to be added or removed by chance*/
	protected GeneBundle(Random random, Supplier<Gene> geneFactory, FloatGene addRemoveFactor, int next ) {
		super(random);
		addRemoveFactor = new FloatGene(random, .5f, 0f, 1f);
		this.geneFactory = geneFactory;
	}
	
	/**Adds an un-named gene with an integer key using the factory*/
	public int addGene() {
		genes.put(next, geneFactory.get());
		return next++;
	}
	
	/**Returns true if the geneFactory was set*/
	public boolean canMutate() {
		return geneFactory != null;
	}
	
	public void addGene(Object key, Gene gene) {
		genes.put(key, gene);
	}
	
	protected void mutateChildren() {
		for( var e : genes.entrySet() ) {
			if( e.getValue().shouldMutateNow() )
				e.getValue().mutate();
		}
	}
	
	@Override
	public boolean shouldMutateNow() {
		return true;
	}
	
	@Override
	public void mutate() {
		super.mutate();
		mutateChildren();
		if( canMutate() && super.shouldMutateNow()) {
			if( random.nextFloat() < addRemoveFactor.getValue() ) {
				addGene();
			}
		}
		if(addRemoveFactor.shouldMutateNow())
			addRemoveFactor.mutate();
	}
	
	@Override
	public HashMap<Object, Gene> getValue() {
		return genes;
	}
	
	
	
	@Override
	public Gene copy() {
		GeneBundle copy = new GeneBundle(random, geneFactory, addRemoveFactor, next);
		for(var e : genes.entrySet()) {
			copy.genes.put( e.getKey() , e.getValue().copy() );
		}
		
		return copy;
	}
}
