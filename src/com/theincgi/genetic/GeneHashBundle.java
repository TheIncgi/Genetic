package com.theincgi.genetic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class GeneHashBundle extends GeneBundle {
	private static final long serialVersionUID = -7009966794646999556L;
	private final LinkedHashMap<String, Gene> genes = new LinkedHashMap<>();
	private Supplier<NamedGene> geneFactory;
	
	
	public static record NamedGene(String name, Gene gene) {};
	
	public GeneHashBundle(Random random, Supplier<NamedGene> geneFactory, FloatGene addRemoveFactor) {
		super(random, addRemoveFactor);
		this.geneFactory = geneFactory;
	}

	public GeneHashBundle(Random random, Supplier<NamedGene> geneFactory) {
		super(random);
		this.geneFactory = geneFactory;
	}

	public LinkedHashMap<String, Gene> getGenes() {
		return genes;
	}
	
	@Override
	public Iterable<Gene> getGenesIterable() {
		return genes.values();
	}
	
	@Override
	public void addGene() {
		var pair = geneFactory.get();
		if(pair == null) return;
		genes.put(pair.name, pair.gene);		
	}
	@Override
	public void removeGene() {
		if (genes.size() == 0)
			return;
		var keys = genes.keySet().toArray();
		genes.remove( keys[random.nextInt(keys.length)] );
	}
	
	@Override
	public LinkedHashMap<String, Gene> getValue() {
		return genes;
	}
	
	@Override
	public int size() {
		return genes.size();
	}
	
	@Override
	public boolean canMutate() {
		return geneFactory != null;
	}
	
	/**List of bundles to mix into this, probably just 1*/
	public void mix(List<? extends GeneBundle> parentBundles) {
		HashSet<String> combinedKeys = new HashSet<>();
		combinedKeys.addAll(genes.keySet());
		for(GeneBundle parentBundle : parentBundles) {
			if( !(parentBundle instanceof GeneHashBundle) )
				throw new IllegalArgumentException("Expected GeneHashBundles, structures incompatable");
			combinedKeys.addAll( ((GeneHashBundle)parentBundle).genes.keySet());
		}
		
		for( var k : combinedKeys ) {
			var selfGene = genes.get(k);
			if( selfGene instanceof GeneBundle ) {
				GeneBundle gb = (GeneBundle) selfGene;
				ArrayList<GeneBundle> subBundles = new ArrayList<>();
				for(var parent : parentBundles)
					subBundles.add((GeneBundle) ((GeneHashBundle) parent).genes.get(k));
				gb.mix( subBundles );
				continue;
			}
			
			float keepChance = 1 / (1+parentBundles.size());
			Gene gene;
			if( random.nextFloat() < keepChance ) {
				gene = selfGene;
			}else{
				var pb = (GeneHashBundle)parentBundles.get(random.nextInt(parentBundles.size()));
				gene = pb.genes.get(k).copy();				
			}
			
			if( gene == null ) {
				genes.remove(k);
				continue;
			}
			
			genes.put( k, gene );
		}
		
	}
	
	@Override
	public GeneHashBundle copy() {
		GeneHashBundle copy = new GeneHashBundle(random, geneFactory, addRemoveFactor.copy());
		for(var e : genes.entrySet()) {
			copy.genes.put( e.getKey() , e.getValue().copy() );
		}
		
		return copy;
	}
}
