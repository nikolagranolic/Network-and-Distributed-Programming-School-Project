package net.etfbl.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.etfbl.model.Product;

public class HashMapCustom {
	public HashMap<Product, Integer> hashMap = new HashMap<Product, Integer>();
	
	public HashMapCustom(HashMap<Product, Integer> hashMap) {
		this.hashMap = hashMap;
	}

	@Override
	public String toString() {
		String result = "<Product, Quantity>\n";
		Set<Map.Entry<Product, Integer>> set = hashMap.entrySet();
		for(Map.Entry<Product, Integer> entry : set) {
			result += entry.toString();
			result += "\n";
		}
		return result;
	}
}
