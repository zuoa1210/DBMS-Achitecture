package query_tree;

import java.util.ArrayList;
import java.util.HashMap;

import base.Attr;

public class index {
	public String index_name;
	public String table_name;
	public String attri_name;
	
	public HashMap<Object, ArrayList<Integer>> index_map = new HashMap<Object, ArrayList<Integer>>();
	public index(Attr a, String index_name, String table_name) {
		this.attri_name = a.getName();
		this.table_name = table_name;
		this.index_name = index_name;
		for(int i = 0; i < a.getTupleSize();i++) {
			
			
			Object key = a.getTuple(i);
			//the key in already in hashmap
			if(index_map.containsKey(key)) {
				ArrayList<Integer> temp = index_map.get(key);
				temp.add(i);
				index_map.put(key, temp);
			}
			//the key isn't in hashmap
			else {
				ArrayList<Integer> temp = new ArrayList<Integer>();
				temp.add(i);
				index_map.put(key, temp);
			}
		}
	}
	
}
