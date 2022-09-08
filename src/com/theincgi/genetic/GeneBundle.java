package com.theincgi.genetic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class GeneBundle extends Gene implements Serializable {
	protected LinkedHashMap<Object, Gene> genes = new LinkedHashMap<>();
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
		this.addRemoveFactor = addRemoveFactor;
		this.geneFactory = geneFactory;
	}
	
	/**Adds an un-named gene with an integer key using the factory*/
	public int addGene() {
		Gene value = geneFactory.get();
		if(value == null) throw new NullPointerException("Expected gene from factory, got null");
		genes.put(next, value);
		return next++;
	}
	
	/**Returns true if the geneFactory was set*/
	public boolean canMutate() {
		return geneFactory != null;
	}
	
	public void addGene(Object key, Gene gene) {
		genes.put(key, gene);
	}
	
	public void removeGene() {
		if(genes.size() == 0) return;
		var keys = genes.keySet().toArray();
		genes.remove( keys[ random.nextInt(keys.length) ] );
	}
	
	protected void mutateChildren() {
		for( var e : genes.entrySet() ) {
			if( e.getValue().shouldMutateNow() )
				e.getValue().mutate();
		}
	}
	
	public float getMaxMutationChance() {
		float max = addRemoveFactor.getMutationChance();
		for( var g : genes.values() )
			max = Math.max(max, g.getMutationChance());
		return max;
	}
	
	public int size() {
		return genes.size();
	}
	
	/**in place mix, mutation should be called next*/
	public void mix( List<GeneBundle> parentBundles ) {
		float keepChance = 1 / (1+parentBundles.size());
		HashSet<Object> combinedKeys = new HashSet<>();
		combinedKeys.addAll(genes.keySet());
		for(GeneBundle parentBundle : parentBundles) { 
			combinedKeys.addAll(parentBundle.genes.keySet());
			next = Math.max(next, parentBundle.next);
		}
		
		
		
		for( var k : combinedKeys ) {
			if( random.nextFloat() <= keepChance ) {
				var g = genes.get(k);
				if( g instanceof GeneBundle ) {
					ArrayList<GeneBundle> subBundles = new ArrayList<>();
					for (GeneBundle geneBundle : parentBundles) {
						subBundles.add( (GeneBundle) geneBundle.genes.get( k ) );
					}
					((GeneBundle)g).mix(subBundles);
					continue;
				}
				continue;
			}
			var pb = parentBundles.get(random.nextInt(parentBundles.size()));
			var g = pb.genes.get(k);
			if( g == null ) {
				genes.remove(k);
				continue;
			}
			if( g instanceof GeneBundle ) {
				ArrayList<GeneBundle> subBundles = new ArrayList<>();
				for (GeneBundle geneBundle : parentBundles) {
					subBundles.add( (GeneBundle) geneBundle.genes.get( k ) );
				}
				((GeneBundle)g).mix(subBundles);
				continue;
			}
			GeneBundle other = parentBundles.get(random.nextInt( parentBundles.size() ));
			genes.put( k, other.genes.get( k ) );
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
	public LinkedHashMap<Object, Gene> getValue() {
		return genes;
	}
	
	
	
	@Override
	public GeneBundle copy() {
		GeneBundle copy = new GeneBundle(random, geneFactory, addRemoveFactor.copy(), next);
		for(var e : genes.entrySet()) {
			copy.genes.put( e.getKey() , e.getValue().copy() );
		}
		
		return copy;
	}

}
