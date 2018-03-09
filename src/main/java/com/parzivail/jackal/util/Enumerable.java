package com.parzivail.jackal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Enumerable<T> extends ArrayList<T>
{
	@FunctionalInterface
	interface DualFunction<A, B, R>
	{
		public R apply(A one, B two);
	}

	private Enumerable()
	{
	}

	public static <T> Enumerable<T> from(List<T> l)
	{
		Enumerable<T> e = new Enumerable<>();
		e.addAll(l);
		return e;
	}

	public static <T> Enumerable<T> empty()
	{
		return new Enumerable<>();
	}

	public <R> Enumerable<R> select(Function<T, R> f)
	{
		ArrayList<R> r = new ArrayList<>();
		for (T t : this)
			r.add(f.apply(t));
		return Enumerable.from(r);
	}

	public <R> Enumerable<R> selectMany(Function<T, Collection<R>> f)
	{
		ArrayList<R> r = new ArrayList<>();
		for (T t : this)
			r.addAll(f.apply(t));
		return Enumerable.from(r);
	}

	public Enumerable<T> where(Function<T, Boolean> f)
	{
		ArrayList<T> r = new ArrayList<>();
		for (T t : this)
			if (f.apply(t))
				r.add(t);
		return Enumerable.from(r);
	}

	public boolean any(Function<T, Boolean> f)
	{
		for (T t : this)
			if (f.apply(t))
				return true;
		return false;
	}

	public boolean all(Function<T, Boolean> f)
	{
		for (T t : this)
			if (!f.apply(t))
				return false;
		return true;
	}

	public float max(Function<T, Float> f)
	{
		if (size() == 0)
			throw new IllegalArgumentException();
		float max = f.apply(get(0));
		for (T t : this)
			max = Math.max(max, f.apply(t));
		return max;
	}

	public float min(Function<T, Float> f)
	{
		if (size() == 0)
			throw new IllegalArgumentException();
		float min = f.apply(get(0));
		for (T t : this)
			min = Math.min(min, f.apply(t));
		return min;
	}

	public T aggregate(DualFunction<T, T, T> f)
	{
		if (size() == 0)
			throw new IllegalArgumentException();
		T agg = get(0);
		for (int i = 1; i < size(); i++)
			agg = f.apply(agg, get(i));
		return agg;
	}

	public float sum(Function<T, Float> f)
	{
		if (size() == 0)
			throw new IllegalArgumentException();
		float sum = 0;
		for (T t : this)
			sum += f.apply(t);
		return sum;
	}

	public float average(Function<T, Float> f)
	{
		if (size() == 0)
			throw new IllegalArgumentException();
		return sum(f) / size();
	}
}
