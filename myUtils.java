package temp;

import java.util.ArrayList;
import java.util.List;

import base.Table;

public class myUtils {
	
	public static Table  merge_join(Table t1, Table t2, String left, String right, String operator ) {
		//System.out.println("merge join");
		Table result = new Table();
		StringBuffer name = new StringBuffer();
		name.append(t1.getName().toLowerCase());
		name.append(" ");
		name.append(t2.getName().toLowerCase());
		//System.out.println(name.toString());
		result.setName(name.toString());
		
		return result;
		
	}
	
	public static Table  nested_join(Table t1, Table t2, String left, String right, String operator ) {
		//System.out.println("nested join");
		Table result = new Table();
		StringBuffer name = new StringBuffer();
		name.append(t1.getName().toLowerCase());
		name.append(" ");
		name.append(t2.getName().toLowerCase());
		//System.out.println(name.toString());
		result.setName(name.toString());
		
		return result;
	}
	
	public static Table condition_handler(Table t, List<ArrayList<String>> conditions_union) {
		Table result = new Table();
		return result;
	}
	
	public static Table project_handler(Table t, List<String> projects, List<String> groups, List<String> order) {
		Table result = new Table();
		return result;
	}
}
