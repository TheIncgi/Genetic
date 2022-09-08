package com.theincgi.genetic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class GeneBundle extends Gene implements Serializable {
//	protected LinkedList<Gene> arrayPart = new LinkedList<>();
//	protected LinkedHashMap<String, Gene> hashPart = new LinkedHashMap<>();
	protected FloatGene addRemoveFactor;
	
	
	/**Nullable factory if no genes are to be added or removed by chance*/
	public GeneBundle(Random random ) {
		super(random);
		addRemoveFactor = new FloatGene(random, .5f, 0f, 1f);
	}
	
	/**Nullable factory if no genes are to be added or removed by chance*/
	protected GeneBundle(Random random, FloatGene addRemoveFactor ) {
		super(random);
		this.addRemoveFactor = addRemoveFactor;
	}
	
	
	
	/**Returns true if the geneFactory was set*/
	abstract public boolean canMutate();
	
	
	public float getMaxMutationChance() {
		float max = addRemoveFactor.getMutationChance();
		for( var g : getGenesIterable() )
			if( g instanceof GeneBundle ) {
				max = Math.max(max, ((GeneBundle)g).getMaxMutationChance());
			}else {				
				max = Math.max(max, g.getMutationChance());
			}
		return max;
	}
	
	protected static class Avg {float sum=0; int count=0; public void add(float v) {sum+=v;count++;}public float value() {return sum/count;}}
	protected Avg _getAvgMutationChance() {
		Avg avg = new Avg();
		avg.add( addRemoveFactor.getMutationChance() );
		for( var g : getGenesIterable() )
			if( g instanceof GeneBundle ) {
				avg.add( ((GeneBundle) g)._getAvgMutationChance().value() );
			}else {				
				avg.add( g.getMutationChance() );
			}
		return avg;
		
	}
	
	public final float getAvgMutationChance() {
		return _getAvgMutationChance().value();
	}
	
	abstract public int size();
	
	/**in place mix, mutation should be called next*/
	abstract public void mix( List<GeneBundle> parentBundles );
	
	@Override
	public boolean shouldMutateNow() {
		return true;
	}
	
	abstract public Iterable<Gene> getGenesIterable();
	abstract public void addGene();
	abstract public void removeGene();
	@Override
	public void mutate() {
		super.mutate();
		for( var g : getGenesIterable() )
			if(g.shouldMutateNow())
				g.mutate();

		if( canMutate() && super.shouldMutateNow()) {
			if( random.nextFloat() <= addRemoveFactor.getValue() ) {
				addGene();
			} else {
				removeGene();
			}
		}
		if(addRemoveFactor.shouldMutateNow())
			addRemoveFactor.mutate();
	}
	
	@Override
	abstract public Object getValue();
	
	@Override
	abstract public GeneBundle copy(); 

}
