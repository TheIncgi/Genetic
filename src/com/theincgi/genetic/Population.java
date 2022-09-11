package com.theincgi.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Population {
		
	private Function<Random, Entity> entityFactory;
	private Function<GeneBundle, Entity> childFactory;
	protected int minPopulation, maxPopulation;
	
	protected ArrayList<Entity> entities;
	protected int generations = 0;
	private Random random;
	private int parents = 2;
	
	public boolean liveInParallel = false;
	
	protected ThreadPoolExecutor threadPool;
	
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
		while( entities.size() < maxPopulation ) {
			var entity = entityFactory.apply(random);
			entity.getGenes().mutate();
			entities.add( entity );
		}
	}
	
	public void setNumParents( int parents ) {
		if( parents < 1 )
			throw new IllegalArgumentException("number of parents must be >= 1");
		this.parents = parents;
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
			List<Entity> otherParents = new ArrayList<>( parents - 1 );
			for( int i = 1; i<parents; i++)
				otherParents.add( getRandom() );
			entities.add( a.makeChild(otherParents, childFactory) );
		}
	}
	protected void reset() {
		for( Entity e : entities )
			e.reset();
	}
	protected void live() {
		if( liveInParallel ) {
			var tp = getThreadPool();
			for( Entity e : entities ) {
				tp.getQueue().add( ()->{
					e.live(this);
					e.age++;
				});
			}
			//TODO wait for tasks to finish
			//TODO Ensure tasks always get same random somehow...
		}else{
			for( Entity e : entities ) {
				e.live( this );
				e.age++;
			}
		}
	}
	protected void sort() {
		entities.sort((a,b)->{
			if(a.getScore() == null) throw new NullPointerException("Entity %s gave a null score".formatted(a.toString()));
			if(b.getScore() == null) throw new NullPointerException("Entity %s gave a null score".formatted(b.toString()));
			return (int) Math.signum(b.getScore() - a.getScore());
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
	
	public Entity getBetterRandom() {
		float r = random.nextFloat() + random.nextFloat() + random.nextFloat();
		r /= 3;
		r *= entities.size();
		return entities.get( (int) r );
	}
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}
	
	public int getGenerations() {
		return generations;
	}
	
	protected ThreadPoolExecutor getThreadPool() {
		if(threadPool == null) {
			threadPool = new ThreadPoolExecutor(2, 12, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		}
		return threadPool;
	}
	
	
	
}
