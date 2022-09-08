package com.theincgi.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class GeneArrayBundle extends GeneBundle {
	private static final long serialVersionUID = 7499365472644956188L;
	private ArrayList<Gene> genes = new ArrayList<>();
	private Supplier<Gene> geneFactory;
	
	
	
	public GeneArrayBundle(Random random, Supplier<Gene> geneFactory, FloatGene addRemoveFactor) {
		super(random, addRemoveFactor);
		this.geneFactory = geneFactory;
	}
	public GeneArrayBundle(Random random, Supplier<Gene> geneFactory) {
		super(random);
		this.geneFactory = geneFactory;
	}
	
	@Override
	public Iterable<Gene> getGenesIterable() {
		return genes;
	}
	
	
	/**Adds an un-named gene with an integer key using the factory*/
	public void addGene() {
		Gene value = geneFactory.get();
		if(value == null) throw new NullPointerException("Expected gene from factory, got null");
		genes.add(value);
	}
	public void removeGene() {
		if (genes.size() == 0)
			return;
		genes.remove(genes.get(random.nextInt(genes.size())));
	}
	
	@Override
	public boolean canMutate() {
		return geneFactory != null;
	}
	
	@Override
	public ArrayList<Gene> getValue() {
		return genes;
	}
	
	public ArrayList<Gene> getGenes() {
		return genes;
	}
	
	public Optional<Gene> get( int k ) {
		if( k < 0 || k >= size()) return Optional.empty();
		return Optional.of( genes.get(k) );
	}
	
	@Override
	public void mix(List<GeneBundle> parentBundles) {
		int maxSize = size();
		for( var p : parentBundles ) {
			if(!(p instanceof GeneArrayBundle))
				throw new IllegalArgumentException("Expected GeneArrayBundles, structures incompatable");
			maxSize = Math.max(maxSize, p.size());
		}
		
		for( int i = 0; i<maxSize; i++) {
			var selfGene = get(i);
			if( selfGene.isPresent() && selfGene.get() instanceof GeneBundle ) {
				GeneBundle gb = (GeneBundle) selfGene.get();
				ArrayList<GeneBundle> subBundles = new ArrayList<>();
				for(var parent : parentBundles)
					subBundles.add((GeneBundle) ((GeneArrayBundle) parent).genes.get(i));
				gb.mix( subBundles );
				continue;
			}
			
			float keepChance = 1 / (1+parentBundles.size());
			Optional<Gene> gene;
			if( random.nextFloat() < keepChance ) {
				gene = selfGene;
			}else{
				var pb = (GeneArrayBundle)parentBundles.get(random.nextInt(parentBundles.size()));
				gene = pb.get(i);
				if(gene.isPresent())
					gene = Optional.of(gene.get().copy());
			}
			
			if( gene.isEmpty() ) {
				if( size() > i )
				genes.remove(i);
				continue;
			}
			
			if( i >= genes.size() )
				genes.add( gene.get() );
			else
				genes.set(i, gene.get() );
		}
	}
	
	@Override
	public GeneArrayBundle copy() {
		GeneArrayBundle copy = new GeneArrayBundle(random, geneFactory, addRemoveFactor.copy());
		for(var g : genes)
			copy.genes.add( g.copy() );
		return copy;
	}
	@Override
	public int size() {
		return genes.size();
	}
}
