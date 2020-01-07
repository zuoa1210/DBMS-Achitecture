package query_tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import base.Attr;
import base.Table;
import base.sql;
import temp.myUtils;




public class query_tree {
	List<List<String>> operations;
	// contains all the table of the database
	// the relation that will be use in the query
	ArrayList<Table> involve_tables = new ArrayList<Table>();
	List<String> involve_tables_names = new ArrayList<String>();
	sql utils;
	ArrayList<index> index_list;
	
	
	
	//receive the input for the object
	public query_tree(List<List<String>> operations, ArrayList<Table> involve_relations,sql u, ArrayList<index> index_list){
		this.operations = operations;
		this.involve_tables = involve_relations;
		this.utils = u;
		this.index_list = index_list;
		
	}
	

	
	//check whether the attribute is available
	//not in form t.a
	public String check_attribute_1(String attribute_name) {
		StringBuffer stb = new StringBuffer();
		for(Table t : this.involve_tables) {
			if(t.isExistAttr(attribute_name)) {
				stb.append(t.getName().toLowerCase());
				stb.append(".");
				stb.append(attribute_name);
				return stb.toString();
			}
		}
		return null;
	}
	//in form t.a
	public String check_attribute_2(String table_name, String attribute_name) {
		StringBuffer stb = new StringBuffer();
		for(Table t : this.involve_tables) {
			if(t.getName().toLowerCase().equals(table_name)) {
				if(t.isExistAttr(attribute_name)) {
					stb.append(t.getName().toLowerCase());
					stb.append(".");
					stb.append(attribute_name);
					return stb.toString();
				}
			}	
		}
		return null;
	}
	public static boolean isNumber(String str){
		String reg = "^-?[0-9]+(.[0-9]+)?$";
		return str.matches(reg);
	}
	
	
	public boolean operation_handler() {
		
		
		ArrayList<String> projects = new ArrayList<String>();
		// if there are or in where, need union
		List<ArrayList<String>> conditions_union = new ArrayList<ArrayList<String>>();
		List<String> join_conditions = new ArrayList<String>();
		List<String> group = new ArrayList<String>();
		List<ArrayList<String>> having = new ArrayList<ArrayList<String>>();
		List<String> order = new ArrayList<String>();
		
		for (List<String> op : this.operations) {
			if(op.get(0).equals( "select")) {
				for(int i = 1; i < op.size(); i++){
					//System.out.println(op.get(i));
					String tempS = null;
					//consider the situation that attribute in the form table.attribute
					String[] temp = op.get(i).split("\\.");
					// not in the form t.a
					if(temp.length == 1) {
						tempS = check_attribute_1(temp[0]);
					}
					else if(temp.length == 2) {
						tempS = check_attribute_2(temp[0], temp[1]);
					}
					//System.out.println(tempS);
					if(tempS == null) {
						//op.get(i) migtht in the form: sum()...or *...or have multiple attributes involve
						tempS = op.get(i);
					}
					projects.add(tempS);
				}
			}
			
			if(op.get(0).equals( "where")) {
				//separate the union conditions
				List<ArrayList<String>> union_conditions = new ArrayList<ArrayList<String>> ();
				ArrayList<String> temp = new ArrayList<String>();
				for(int i = 1; i < op.size(); i++) {
					if(!op.get(i).equals("or")) {
						temp.add(op.get(i));
					}
					else if(op.get(i).equals("or")) {
						union_conditions.add(temp);
						temp = new ArrayList<String>();
					}
				}
				union_conditions.add(temp);
				
				//assume there are no or in join conditions
				for(ArrayList<String> cs : union_conditions) {
					ArrayList<String> conditions = new ArrayList<String>();
						
					//check if the attributes that mentioned are available
					//check if the attribute is exist and  if the condition is join condition
					//identifier if starting bracket exist
					
					//remove "and" in cs
					for(int i = 0; i < cs.size(); i++) {
						if(cs.get(i).equals("and")) {
							cs.remove(i);
							i--;
						}	
					}
					
					//remove the bracket at the start and end of ArrayList
					String start = cs.get(0);
					String end = cs.get(cs.size() - 1);
					if(cs.get(0).charAt(0) == '(') {
						if(end.charAt(end.length() - 1) != ')') {
							System.out.println("lack of )");
							return false;
						}
						else {
							start = start.substring(1, start.length());
							end = end.substring(0, end.length() - 1);
							cs.set(0, start);
							cs.set(cs.size() - 1, end);
						}	
					}
					
					//dig into each condition in cs
					for(String condition : cs) {
						//get the name involved in the condition
						ArrayList<String> condition_tables_names = new ArrayList<String>();
						
						//separate the components in the condition 
						String[] components = condition.split("=|!=|>|>=|<|<=|\\+|-|\\*|/");
						//remove the "" in components
						ArrayList<String> removed_components = new ArrayList<String>();
						for(String ss : components) {
							if(!ss.equals(""))
								removed_components.add(ss);
						}
						
						
						for(int i = 0; i < removed_components.size(); i++) {
							String component = removed_components.get(i);
							//System.out.println(component +" " + isNumber(component));
							//check if the component in the form of attribute (not a number, not start with "), the first component in a condition must be an attribute
							if(i == 0 || (!(component.charAt(0) == '"' ) && !isNumber(component))) {
								//System.out.println(i + " " +component);
								// if component in attribute form, it must in tables
								String[] split_component = component.split("\\.");
								String checked_component;
								if(split_component.length == 1) {
									checked_component = check_attribute_1(split_component[0]);
								}
								else if(split_component.length == 2) {
									checked_component = check_attribute_2(split_component[0], split_component[1]);
								}
								else {
									System.out.println("Wrong Input in [where]: input form");
									return false;
								}
								if(checked_component == null) {
									System.out.println("Wrong Input in [where]: no such attribute");
									return false;
								}
								
								//get the tables' names that used in the condition
								String[] find_table_name = checked_component.split("\\.");
								if(!condition_tables_names.contains(find_table_name[0])) {
									condition_tables_names.add(find_table_name[0]);
								}
								
								//used the checked_component to replace the attribute expression in condition
								condition = condition.replace(component, checked_component);	
							}
						}
						//if more than one table show in the condition, it is a join condition
						if(condition_tables_names.size() > 1) {
							if(!join_conditions.contains(condition))
								join_conditions.add(condition);
						}
						//not a join condition
						else
							conditions.add(condition);
					}
					if(conditions.size() > 0)
						conditions_union.add(conditions);
					
					
				}
//				System.out.println("join conditions");
//				for(String ddd : join_conditions) {
//					System.out.println(ddd);
//				}
//				System.out.println("conditions");
//				for(ArrayList<String> aaa : conditions_union) {
//					System.out.println("Union");
//					for(String mmm : aaa) {
//						System.out.println(mmm);
//					}
//				}
			}
			
			
			if(op.get(0).equals( "group by")) {
				//check if the attributes show in group by are available
				for(int i = 1; i < op.size(); i++) {
					String tempS;
					//consider the situation that attribute in the form table.attribute
					String[] temp = op.get(i).split("\\.");
					// not in the form t.a
					if(temp.length == 1) {
						tempS = check_attribute_1(temp[0]);
					}
					else if(temp.length == 2) {
						tempS = check_attribute_2(temp[0], temp[1]);
					}
					else {
						System.out.println("Wrong Input in [group by]: input form");
						return false;
					}
					
					if(tempS == null) {
						System.out.println("Wrong Input in [group by]: no such attribute");
						return false;
					}
					else {
						group.add(tempS);
					}
				}
//				for(String sss : group) {
//					System.out.println(sss);
//				}
			}
			
			
			
			
			
			
			if(op.get(0).equals( "group by")) {
				//check if the attributes show in group by are available
				for(int i = 1; i < op.size(); i++) {
					String tempS;
					//consider the situation that attribute in the form table.attribute
					String[] temp = op.get(i).split("\\.");
					// not in the form t.a
					if(temp.length == 1) {
						tempS = check_attribute_1(temp[0]);
					}
					else if(temp.length == 2) {
						tempS = check_attribute_2(temp[0], temp[1]);
					}
					else {
						System.out.println("Wrong Input in [group by]: input form");
						return false;
					}
					
					if(tempS == null) {
						System.out.println("Wrong Input in [group by]: no such attribute");
						return false;
					}
					else {
						group.add(tempS);
					}
				}
				
			}
			
			if(op.get(0).equals( "having")) {
				//having is quit similar with select, but don't need to consider the join condition
				List<ArrayList<String>> union_conditions = new ArrayList<ArrayList<String>> ();
				ArrayList<String> temp = new ArrayList<String>();
				for(int i = 1; i < op.size(); i++) {
					if(!op.get(i).equals("or")) {
						temp.add(op.get(i));
					}
					else if(op.get(i).equals("or")) {
						union_conditions.add(temp);
						temp = new ArrayList<String>();
					}
				}
				union_conditions.add(temp);
				
				//assume there are no or in join conditions
				for(ArrayList<String> cs : union_conditions) {
					ArrayList<String> conditions = new ArrayList<String>();
						
					//check if the attributes that mentioned are available
					//identifier if starting bracket exist
					
					//remove "and" in cs
					for(int i = 0; i < cs.size(); i++) {
						if(cs.get(i).equals("and")) {
							cs.remove(i);
							i--;
						}	
					}
					
					//remove the bracket at the start and end of ArrayList
					String start = cs.get(0);
					String end = cs.get(cs.size() - 1);
					if(cs.get(0).charAt(0) == '(') {
						if(end.charAt(end.length() - 1) != ')') {
							System.out.println("lack of )");
							return false;
						}
						else {
							start = start.substring(1, start.length());
							end = end.substring(0, end.length() - 1);
							cs.set(0, start);
							cs.set(cs.size() - 1, end);
						}	
					}
					
					//dig into each condition in cs
					for(String condition : cs) {
						
						//find the component that in ()
						String[] components = condition.split("\\(|\\)");
						
						//extract the components in ()
						ArrayList<String> removed_components = new ArrayList<String>();
						for(int i = 0; i < components.length; i++) {
							if(i % 2 == 1) {
								removed_components.add(components[i]);
							}
						}
						for(int i = 0; i < removed_components.size(); i++) {
							String component = removed_components.get(i);
							
							// if component in attribute form, it must in tables
							String[] split_component = component.split("\\.");
							String checked_component;
							if(split_component.length == 1) {
								checked_component = check_attribute_1(split_component[0]);
							}
							else if(split_component.length == 2) {
								checked_component = check_attribute_2(split_component[0], split_component[1]);
							}
							else {
								System.out.println("Wrong Input in [having]: input form");
								return false;
							}
							if(checked_component == null) {
								System.out.println("Wrong Input in [having]: no such attribute");
								return false;
							}
							
							//used the checked_component to replace the attribute expression in condition
							//condition = condition.replace(component, checked_component);	
						}
						conditions.add(condition);
					}
					if(conditions.size() > 0)
						having.add(conditions);
				}
//				System.out.println("having");
//				for(ArrayList<String> aaa : having) {
//					System.out.println("Union");
//					for(String mmm : aaa) {
//						System.out.println(mmm);
//					}
//				}	
				
			}
			
			
			if(op.get(0).equals( "order by")) {
				
				//check if the asc or desc has been identified 
				int length = 0;
				String last;
				if(op.get(op.size() - 1).equals("asc") || op.get(op.size() - 1).equals("desc")) {
					length = op.size() - 1;
					last = op.get(op.size() - 1);
				}
				else {
					length = op.size();
					last = "asc";
				}
				
				//check the attribute in order by
				for(int i = 1; i < length; i++) {
					String tempS;
					//consider the situation that attribute in the form table.attribute
					String[] temp = op.get(i).split("\\.");
					// not in the form t.a
					if(temp.length == 1) {
						tempS = check_attribute_1(temp[0]);
					}
					else if(temp.length == 2) {
						tempS = check_attribute_2(temp[0], temp[1]);
					}
					else {
						System.out.println("Wrong Input in [order by]: input form");
						return false;
					}
					
					if(tempS == null) {
						tempS = " ." + op.get(i);
						//System.out.println("Wrong Input in [order by]: no such attribute");
						//return false;
					}
					order.add(tempS);
				}
				order.add(last);
//				for(String mmm : order) {
//					System.out.println(mmm);
//				}
				
			}
			
			
		}
		sequence(join_conditions, conditions_union, group, having, order, projects);
		return true;
	}
	
	
	
	
	public void sequence(List<String> join_conditions,
			List<ArrayList<String>> conditions_union, List<String> group, 
			List<ArrayList<String>> having, List<String> order, ArrayList<String> projects) {
		
		//System.out.println(this.involve_tables.size());
		//handle the part conditions union:
		ArrayList<Table> selected_table;
		//have condition
		// rank the where condition
		if(conditions_union.size() >0) {
			for(int i = 0; i < conditions_union.size(); i++) {
				ArrayList<String> conditions = conditions_union.get(i);
				//rank the order of condition according to the selectivity of the attribute
				ArrayList<pair> rank = new ArrayList<pair>();
				for(String condition : conditions) {
					pair temp = new pair();
					temp.key = condition;
					//the expression may change
					Table the_table;
					String[] name = condition.split("!=|>=|<=|=|>|<")[0].split("\\.");
					for(Table t : this.involve_tables) {
						if(t.getName().equals(name[0])) {
							the_table = t;
							temp.selectivity = the_table.getAttr(name[1]).selectivity();//??
							break;
						}
					}
					
					rank.add(temp);
				}
				rank.sort(new Comparator() {
					@Override
		            public int compare(Object o1, Object o2) {
		                pair s1 = (pair)o1;
		                pair s2 = (pair)o2;
		                if(s1.selectivity > s2.selectivity)
		                	return 1;
		                else
		                	return -1;
		        
		            }
		        });
				
				ArrayList<String> ranked_conditions = new ArrayList<String>();
				for(pair temp : rank) {
					ranked_conditions.add(temp.key);
				}
				
				conditions_union.set(i, ranked_conditions);
//						for(ArrayList<String> aa : conditions_union) {
//							for(String tt : aa) {
//								System.out.println(tt);
//							}
//						}
				
				
			}
			
			//call the select function
			ArrayList<ArrayList<Table>> table_waited_to_be_union = new ArrayList<ArrayList<Table>>();
			for(ArrayList<String> conditions : conditions_union) {
				ArrayList<Table> wait_conjun = new ArrayList<Table>();
				for(String condition : conditions) {
					if(condition.split("!=").length > 1) {
						String[] tempS = condition.split("!=");
						String[] names = tempS[0].split("\\.");
						for(Table t : this.involve_tables) {
							if(t.getName().equals(names[0])) {
								for(index ind : this.index_list) {
									if(ind.attri_name.equals(names[1]) && ind.table_name.equals(t.getName())) {
										//System.out.println(ind.index_map.containsKey(1));
										wait_conjun.add(utils.where_index(t.getName(), names[1], "!=", tempS[1], ind));
										break;
									}
								}
								wait_conjun.add(utils.where(t.getName(), names[1], "!=", tempS[1]));
								break;
							}
						}
					}
					else if(condition.split(">=").length > 1) {
						String[] tempS = condition.split(">=");
						String[] names = tempS[0].split("\\.");
						for(Table t : this.involve_tables) {
							if(t.getName().equals(names[0])) {
								for(index ind : this.index_list) {
									if(ind.attri_name.equals(names[1]) && ind.table_name.equals(t.getName())) {
										//System.out.println(ind.index_map.containsKey(1));
										wait_conjun.add(utils.where_index(t.getName(), names[1], ">=", tempS[1], ind));
										break;
									}
								}
								wait_conjun.add(utils.where(t.getName(), names[1], ">=", tempS[1]));
								break;
							}
						}
					}
					else if(condition.split("<=").length > 1) {
						String[] tempS = condition.split("<=");
						String[] names = tempS[0].split("\\.");
						for(Table t : this.involve_tables) {
							if(t.getName().equals(names[0])) {
								for(index ind : this.index_list) {
									if(ind.attri_name.equals(names[1]) && ind.table_name.equals(t.getName())) {
										//System.out.println(ind.index_map.containsKey(1));
										wait_conjun.add(utils.where_index(t.getName(), names[1], "<=", tempS[1], ind));
										break;
									}
								}
								wait_conjun.add(utils.where(t.getName(), names[1], "<=", tempS[1]));
								break;
							}
						}
					}
					else if(condition.split("=").length > 1) {
						String[] tempS = condition.split("=");
						String[] names = tempS[0].split("\\.");
						for(Table t : this.involve_tables) {
							if(t.getName().equals(names[0])) {
								//System.out.println(utils.where(t.getName(), names[1], ">", tempS[1]).gettablesize());
								for(index ind : this.index_list) {
									if(ind.attri_name.equals(names[1]) && ind.table_name.equals(t.getName())) {
										//System.out.println(ind.index_map.containsKey(1));
										wait_conjun.add(utils.where_index(t.getName(), names[1], "=", tempS[1], ind));
										break;
									}
								}
								wait_conjun.add(utils.where(t.getName(), names[1], "=", tempS[1]));
								break;
							}
						}
					}
					else if(condition.split(">").length > 1) {
						String[] tempS = condition.split(">");
						String[] names = tempS[0].split("\\.");
						//System.out.println(names[0]);
						for(Table t : this.involve_tables) {
//							System.out.println(t.getName());
//							System.out.println(names[0]);
							if(t.getName().equals(names[0])) {
								for(index ind : this.index_list) {
									if(ind.attri_name.equals(names[1]) && ind.table_name.equals(t.getName())) {
										//System.out.println(ind.index_map.containsKey(1));
										wait_conjun.add(utils.where_index(t.getName(), names[1], ">", tempS[1], ind));
										break;
									}
								}
//								System.out.println(t.getName());
//								System.out.println(names[1]);
//								System.out.println(tempS[1]);
								//utils.where(t.getName(), names[1], ">", tempS[1]).displayTable();
								wait_conjun.add(utils.where(t.getName(), names[1], ">", tempS[1]));
								//System.out.println(wait_conjun.size());
								break;
							}
						}
					}else if(condition.split("<").length > 1) {
						String[] tempS = condition.split("<");
						String[] names = tempS[0].split("\\.");
						for(Table t : this.involve_tables) {
							if(t.getName().equals(names[0])) {
								for(index ind : this.index_list) {
									if(ind.attri_name.equals(names[1]) && ind.table_name.equals(t.getName())) {
										//System.out.println(ind.index_map.containsKey(1));
										wait_conjun.add(utils.where_index(t.getName(), names[1], "<", tempS[1], ind));
										break;
									}
								}
								wait_conjun.add(utils.where(t.getName(), names[1], "<", tempS[1]));
								break;
							}
						}
					}
				}
//				for(Table t : wait_conjun) {
//					t.displayTable();
//				}
				//conjunction the conditions
				ArrayList<Table> conjuned = new ArrayList<Table>();
				for(Table t : wait_conjun) {
					boolean find = false;
					for(int i = 0; i < conjuned.size(); i++) {
						Table cd = conjuned.get(i);
						//already go through part of conjuntion
						if(cd.getName().equals(t.getName())) {
							//System.out.println(utils.intersection(cd, t).getName());
							conjuned.set(i, utils.intersection(cd, t));
							find = true;
							break;
						}
					}
					if(find == false)
						conjuned.add(t);
				}
				table_waited_to_be_union.add(conjuned);
			}
			//System.out.println(table_waited_to_be_union.size());
//			for(ArrayList<Table> tl :table_waited_to_be_union) {
//				for(Table t : tl) {
//					t.displayTable();
//				}
//			}
			//union the tables
			ArrayList<Table> unioned = new ArrayList<Table>();
			for(ArrayList<Table> u : table_waited_to_be_union) {
				for(Table temp : u) {
					boolean find = false;
					for(int i = 0; i < unioned.size(); i++) {
						Table ud = unioned.get(i);
						if(temp.getName().equals(ud.getName())) {
							find = true;
							unioned.set(i, utils.union(temp, ud));
							break;
						}
					}
					if(find == false) {
						//System.out.println(temp.getName());
						//temp.displayTable();
						unioned.add(temp);
						//System.out.println(temp.getName());
					}
					//System.out.println(find);
				}
			}
			//System.out.println(unioned.size());
			
			selected_table = unioned;
		}
		else
			selected_table = this.involve_tables;
		
	
		// the table waited to be join
		ArrayList<Table> wait_tables = (ArrayList<Table>) selected_table.clone();
		//System.out.println(wait_tables.size());
		//the table generated 
		ArrayList<Table> unfinished_join_tables = new ArrayList<Table>();
		Table finished_join_table;
		if(wait_tables.size() > 1) {
			for(String join_condition : join_conditions) {	
				
				//find the operator. assume right hand side only have one
				String left = null;
				String right = null;
				String operator = null;
				Table result;
				// the operator can only be =
//				ArrayList<String> separators = new ArrayList<String>();
//				separators.add("!=");
//				separators.add(">=");
//				separators.add("<=");
//				separators.add("=");
//				separators.add(">");
//				separators.add("<");
//				
//				for(String separator : separators) {
				
				String[] temp = join_condition.split("=");
				//the separator is used on the join_condition
				if(temp.length > 1) {
					left = temp[0];
					right = temp[1];
				}
				
				//}
				
				//get the tables related to the condition
				ArrayList<Table> table_need = new ArrayList<Table>();
				ArrayList<String> table_names = new ArrayList<String>();
				table_names.add(left.split("\\.")[0]);
				table_names.add(right.split("\\.")[0]);
				//find the table we need
				for(String name : table_names) {
					//check if the table is in table wait list
					for(int i = 0; i < wait_tables.size(); i++) {
						//System.out.println(wait_tables.get(i).getName());
						if(wait_tables.get(i).getName().toLowerCase().equals(name)) {
							table_need.add(wait_tables.get(i));
							//System.out.println("find in wait");
							wait_tables.remove(i);
							break;
						}
					}
					//the table is in unfinished_join_tables
					for(int i = 0; i < unfinished_join_tables.size(); i++) {
						boolean find = false;
						String[] unfinished_join_tables_names = unfinished_join_tables.get(i).getName().split(" ");
						for(String u_name : unfinished_join_tables_names) {
							if(u_name.equals(name)) {
								table_need.add(unfinished_join_tables.get(i));
								//System.out.println("find in unfinished");
								find = true;
								break;
							}
						}
						if(find == true) {
							unfinished_join_tables.remove(i);
							break;
						}
							
					}
					
				}
				//join the 2 tables
				Table t1 = table_need.get(0);
				Table t2 = table_need.get(1);
				//decide use merge join or nested join
				float threshold_ratio = (float) 0.5;
				//merge join
				if(t1.gettablesize() <= t2.gettablesize() * threshold_ratio) {
					//System.out.println(left.split("\\.")[1]);
					//System.out.println(t2.getName());
					result = utils.nested_join(t1, t2, left.split("\\.")[1], right.split("\\.")[1]);
				}
					
				else if(t2.gettablesize() <= t1.gettablesize() * threshold_ratio)
					result = utils.nested_join(t2, t1, right.split("\\.")[1], left.split("\\.")[1]);
				else {
//					System.out.println(t1.getName());
//					System.out.println(left.split("\\.")[1]);
//					System.out.println(t2.getName());
//					System.out.println(right.split("\\.")[1]);
					result = utils.merge_join(t1, t2, left.split("\\.")[1], right.split("\\.")[1]);
				}
					
				unfinished_join_tables.add(result);	
			}
			finished_join_table = unfinished_join_tables.get(0);
	//		System.out.println(finished_join_table.getName());
	//		System.out.println("end");
		}
		else
			finished_join_table = wait_tables.get(0);
		
		
		
		
		// handle the [group], need farther consideration, need to combine with select part
		//no group by, no order
		
		//finished_join_table.displayTable();
		for(int i = 0; i < projects.size(); i++) {
			if(projects.get(i).contains(".")) {
				projects.set(i, projects.get(i).split("\\.")[1]);
				
			}
				
				
		}
		if(group.size() == 0 && order.size() == 0) {
			if(projects.get(0).equals("*"))
				utils.select(finished_join_table);
			else {
				System.out.println(projects.get(0));
				utils.select(projects, finished_join_table);
			}
				
		}
		else if(group.size() == 0 && order.size() != 0) {
			if(projects.get(0).equals("*"))
				utils.select_orderby(finished_join_table, order.get(0).split("\\.")[1], order.get(1));
			else {
				utils.select_orderby(finished_join_table, order.get(0).split("\\.")[1], order.get(1), projects);
			}
		}
		else if(group.size()!= 0 && order.size() == 0 && having.size() == 0) {
			String[] temps = projects.get(1).split("\\(|\\)");
			Table result = utils.groupby(group.get(0).split("\\.")[1], temps[0], 
					temps[1], finished_join_table);
			result.displayTable();
		}
		else if(group.size()!= 0 && order.size() != 0 && having.size() == 0) {
			//System.out.println("enter");
			String[] temps = projects.get(1).split("\\(|\\)");
			Table result = utils.groupby(group.get(0).split("\\.")[1], temps[0], 
					temps[1], finished_join_table);
			//result.displayTable();
			//System.out.println(order.get(0));
			utils.select_orderby(result, order.get(0).split("\\.")[1], order.get(1));
		}
		else if(group.size()!= 0 && order.size() == 0 && having.size() != 0) {
			
			String[] temps = projects.get(1).split("\\(|\\)");
			Table result = utils.groupby(group.get(0).split("\\.")[1], temps[0], 
					temps[1], finished_join_table);
			Table _final = new Table();
			String tempss = having.get(0).get(0);
			if(tempss.split("!=").length > 1) {
				_final = utils.having(result, tempss.split("!=")[0], "!=", tempss.split("!=")[1]);
			}
			else if(tempss.split(">=").length > 1) {
				_final = utils.having(result, tempss.split(">=")[0], ">=", tempss.split(">=")[1]);
			}
			else if(tempss.split("<=").length > 1) {
				_final = utils.having(result, tempss.split("<=")[0], "<=", tempss.split("<=")[1]);
			}
			else if(tempss.split("=").length > 1) {
				_final = utils.having(result, tempss.split("=")[0], "=", tempss.split("=")[1]);
			}
			else if(tempss.split("<").length > 1) {
				System.out.println(tempss.split("<")[0]);
				_final = utils.having(result, tempss.split("<")[0], "<", tempss.split("<")[1]);
			}
			else if(tempss.split(">").length > 1) {
				_final = utils.having(result, tempss.split(">")[0], ">", tempss.split(">")[1]);
			}
			_final.displayTable();
		}
		else if(group.size()!= 0 && order.size() != 0 && having.size() != 0) {
			String[] temps = projects.get(1).split("\\(|\\)");
			Table result = utils.groupby(group.get(0).split("\\.")[1], temps[0], 
					temps[1], finished_join_table);
			Table _final = new Table();
			String tempss = having.get(0).get(0);
			if(tempss.split("!=").length > 1) {
				_final = utils.having(result, tempss.split("!=")[0], "!=", tempss.split("!=")[1]);
			}
			else if(tempss.split(">=").length > 1) {
				_final = utils.having(result, tempss.split(">=")[0], ">=", tempss.split(">=")[1]);
			}
			else if(tempss.split("<=").length > 1) {
				_final = utils.having(result, tempss.split("<=")[0], "<=", tempss.split("<=")[1]);
			}
			else if(tempss.split("=").length > 1) {
				_final = utils.having(result, tempss.split("=")[0], "=", tempss.split("=")[1]);
			}
			else if(tempss.split("<").length > 1) {
				System.out.println(tempss.split("<")[0]);
				_final = utils.having(result, tempss.split("<")[0], "<", tempss.split("<")[1]);
			}
			else if(tempss.split(">").length > 1) {
				_final = utils.having(result, tempss.split(">")[0], ">", tempss.split(">")[1]);
			}
			//_final.displayTable();
			utils.select_orderby(_final, order.get(0).split("\\.")[1], order.get(1));
		}
		
	}
	

}
