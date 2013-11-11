package au.com.redboxresearchdata.types

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation



public class SyncIterator implements Iterator, Iterable{
	private final theobjects

	SyncIterator(Object[] objects){
		theobjects=objects.collect{
			if (it instanceof Iterator) return /*from closure*/ it
			else return /*from closure*/ DefaultTypeTransformation.asCollection(it).iterator()
		}
	}

	boolean hasNext(){
		return theobjects.any{it.hasNext()}
	}
	
	Object next(){
		if (!hasNext()) throw new java.util.NoSuchElementException()
		return theobjects.collect{
			try{
				return /*from closure*/ it.next()
			}catch(NoSuchElementException e){
				return /*from closure*/ null
			}
		}
	}

	Iterator iterator(){
		return this;
	}


	void remove(){
		throw new UnsupportedOperationException("remove() not supported")
	}
}
