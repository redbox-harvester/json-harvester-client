package au.com.redboxresearchdata.types

import groovy.json.JsonException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import com.google.gson.JsonObject;



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

	

	
	public static JSONObject deepMerge(final JSONObject src, final JSONObject tgt) throws JsonException {

		def checkFilled
		checkFilled = { def source, def target->
			source.each{
				Object sourceKey = it.key
				Object sourceValue = it.value
				if ( (!(target.containsKey(sourceKey))) || (!(sourceValue instanceof JSONAware))) {
					target.put(sourceKey, sourceValue);
				} else {
//					JSONObject valueJson = (JSONObject)sourceValue;
//					JSONObject targetJson = (JSONObject)target.get(sourceKey)
					
					checkFilled(sourceValue, target[sourceKey])
				}
			}
		}
		
		JSONObject target = checkFilled(src, tgt)
		return target;
	}

	//	public static JsonObject deepMerge(JSONObject source, JSONObject target) throws JsonException {
	//
	//		for (Object key in source.) {
	//			Object value = source.key;
	//			if (!target.key) {
	//				target.put(key, value);
	//			} else {
	//				if (value instanceof JSONObject) {
	//					JSONObject valueJson = (JSONObject)value;
	//					deepMerge(valueJson, target.find(key));
	//				} else {
	//					target.put(key, value);
	//				}
	//			}
	//			System.out.println(key)
	//			System.out.println(value)
	//		}
	//		return target;
	//	}
}
