package com.theincgi.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Population {
		
	private Function<Random, Entity> entityFactory;
	private Function<GeneBundle, Entity> childFactory;
	protected int minPopulation, maxPopulation;
	
	protected ArrayList<Entity> entities;
	protected int generations = 0;
	private Random random;
	
	public Population( Random random, Function<Random, Entity> entityFactory, Function<GeneBundle, Entity> childFactory, int minPopulation, int maxPopulation ) {
		this.random = random;
		this.entityFactory = entityFactory;
		this.childFactory  = childFactory;
		this.minPopulation = minPopulation;
		this.maxPopulation = maxPopulation;
		
		entities = new ArrayList<>( maxPopulation );
		setup();
	}
	
	protected void setup() {
		while( entities.size() < maxPopulation )
			entities.add( entityFactory.apply(random) );
	}
	
	public void epoch() {
		grow();
		reset();
		live();
		sort();
		reduce();
		generations++;
	}
	public void grow() {
		while( entities.size() < maxPopulation ) {
			Entity a = getRandom();
			Entity b = getRandom();
			entities.add( a.makeChild(List.of(b), childFactory) );
		}
	}
	protected void reset() {
		for( Entity e : entities )
			e.reset();
	}
	protected void live() {
		for( Entity e : entities )
			e.live();
	}
	protected void sort() {
		entities.sort((a,b)->{
			if(a.getScore() == null) throw new NullPointerException("Entity %s gave a null score".formatted(a.toString()));
			if(b.getScore() == null) throw new NullPointerException("Entity %s gave a null score".formatted(b.toString()));
			return -(int) Math.signum(b.getScore() - a.getScore());
		});
	}
	protected void reduce() {
		while( entities.size() > minPopulation ) 
			entities.remove(entities.size()-1);
	}
	
	public Entity getBest() {
		return entities.get(0);
	}
	
	public Entity getRandom() {
		return entities.get( random.nextInt(entities.size()) );
	}
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}
	
	public int getGenerations() {
		return generations;
	}
}
