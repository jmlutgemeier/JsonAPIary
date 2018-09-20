package com.cradlepoint.jsonapiary.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class SortedMultiValueMap<K, V> {
	
	// Attributes //
	private TreeMap<K, List<V>> map;
	
	// Constructor //
	public SortedMultiValueMap() {
		map = new TreeMap<K, List<V>>();
	}
	
	// Public Functions //
	public void put(K key, V value) {
		List<V> valueList =  map.get(key);
		if(valueList == null) {
			valueList = new ArrayList<V>();
		}
		valueList.add(value);
		map.put(key, valueList);
	}
	
	public List<V> get(K key) {
		return map.get(key);
	}
	
	public boolean contains(K key) {
		return map.containsKey(key);
	}
	
	public List<V> getSortedValueList() {
		List<V> sortedList = new ArrayList<V>();
		for(List<V> valueSet : map.values()) {
			sortedList.addAll(valueSet);
		}
		return sortedList;
	}
	
	public List<V> getSortedValueListDescending() {
		List<V> sortedList = getSortedValueList();
		Collections.reverse(sortedList);
		return sortedList;
	}
	
	public int size() {
		int size = 0;
		for(List<V> subList : map.values()) {
			size += subList.size();
		}
		return size;
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
}
