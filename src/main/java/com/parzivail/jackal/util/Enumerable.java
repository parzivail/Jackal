package com.parzivail.jackal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Enumerable<T>
{
	private final List<T> e;

	private Enumerable(List<T> e)
	{
		if (e.size() == 0)
			throw new IllegalArgumentException();
		this.e = e;
	}

	public static <T> Enumerable<T> from(List<T> l)
	{
		return new Enumerable<>(l);
	}

	public <R> Enumerable<R> select(Function<T, R> f)
	{
		ArrayList<R> r = new ArrayList<>();
		for (T t : e)
			r.add(f.apply(t));
		return new Enumerable<>(r);
	}

	public boolean any(Function<T, Boolean> f)
	{
		for (T t : e)
			if (f.apply(t))
				return true;
		return false;
	}

	public boolean all(Function<T, Boolean> f)
	{
		for (T t : e)
			if (!f.apply(t))
				return false;
		return true;
	}

	public float max(Function<T, Float> f)
	{
		float max = f.apply(e.get(0));
		for (T t : e)
			max = Math.max(max, f.apply(t));
		return max;
	}

	public float min(Function<T, Float> f)
	{
		float min = f.apply(e.get(0));
		for (T t : e)
			min = Math.min(min, f.apply(t));
		return min;
	}
}
