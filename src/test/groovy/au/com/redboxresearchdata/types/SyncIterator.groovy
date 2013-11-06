package au.com.redboxresearchdata.types

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation



public class SyncIterator implements Iterator, Iterable{
	private theobjects

	public SyncIterator(Object[] objects){
		theobjects=objects.collect{
			if (it instanceof Iterator) return /*from closure*/ it
			else return /*from closure*/ DefaultTypeTransformation.asCollection(it).iterator()
		}
	}

	boolean hasNext(){
		return theobjects.any{it.hasNext()}
	}
	public Object next(){
		if (!hasNext()) throw new java.util.NoSuchElementException()
		return theobjects.collect{
			try{
				return /*from closure*/ it.next()
			}catch(NoSuchElementException e){
				return /*from closure*/ null
			}
		}
	}

	public Iterator iterator(){
		return this;
	}


	public void remove(){
		throw new UnsupportedOperationException("remove() not supported")
	}
}
