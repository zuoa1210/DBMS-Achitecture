package base;
import java.util.*;

import query_tree.index;

import java.io.*;
import java.lang.*;

public class sql{
	ArrayList<Table> all_tables;
	ArrayList<index> index_list;
	public sql(ArrayList<Table> all_tables2, ArrayList<index> index_list){
		this.all_tables = all_tables2;
		this.index_list = index_list;
	}
	public Table select(Table t)
    {
        t.displayTable();
        return t;
    }

    public Table select(ArrayList<String> projects, Table t)
    {
        ArrayList<Object[]> res = new ArrayList<>();
        
        for(int i = 0; i < t.gettablesize(); i++)
        {
            Object[] a = new Object[projects.size()];
            for(int j = 0; j < projects.size(); j++)
            {
                a[j] = t.getAttr(projects.get(j)).getTuple(i);
            }
        
            res.add(a);
        }
        //print
        Table c = new Table(t.getName());
        c.ListToTable(res, projects);
        c.displayTable();
        return c;
    }
    public Table where_index(String from_tbName, String where_attr, String where_command, String where_value, index finder)
    {
    	Table t = new Table();
        t.getTable(from_tbName);
        //System.out.println(finder.index_map.get(900).get(0));
        //prepare for arraytotable
        ArrayList<String> attr = new ArrayList<>();
        for(int i = 0; i < t.getAttrNum(); i++)
        {
            String n = t.getAttr(i).getName();
            attr.add(n);
        }
        //正文
        Object[][] all = t.TabletoArray();
        ArrayList<Object[]> res = new ArrayList<>();
        ArrayList<Integer> list = new ArrayList<>();
        if(where_command.equals("=")){
        	if(t.getAttr(where_attr).getType().equals("java.lang.Integer"))
				list = finder.index_map.get(Integer.parseInt(where_value));
        	if(t.getAttr(where_attr).getType().equals("java.lang.Double"))
        		list = finder.index_map.get(Double.parseDouble(where_value));
        	if(t.getAttr(where_attr).getType().equals("java.lang.Character"))
        		list = finder.index_map.get(where_value.charAt(0));
        	if(t.getAttr(where_attr).getType().equals("java.lang.String"))
        		list = finder.index_map.get(where_value);
            //System.out.println(list.);
            for(int i = 0; i < list.size(); i++)
            	res.add(all[list.get(i)]);
        }
        if(where_command.equals("!=")){
        	if(t.getAttr(where_attr).getType().equals("java.lang.Integer"))
				list = finder.index_map.get(Integer.parseInt(where_value));
        	if(t.getAttr(where_attr).getType().equals("java.lang.Double"))
        		list = finder.index_map.get(Double.parseDouble(where_value));
        	if(t.getAttr(where_attr).getType().equals("java.lang.Character"))
        		list = finder.index_map.get(where_value.charAt(0));
        	if(t.getAttr(where_attr).getType().equals("java.lang.String"))
        		list = finder.index_map.get(where_value);
        	for(int i = 0; i < t.gettablesize();i++) {
        		res.add(all[i]);
        	}
        	res.removeAll(list);
        }
        if(where_command.equals("<")){
        	Object[] keys = finder.index_map.keySet().toArray();
        	Arrays.sort(keys);
//        	for(int i = 0; i < keys.length; i++)
//        	{
//        		System.out.println(keys[i]);
//        	}
        	for(int i = 0; i < t.gettablesize(); i++)
        	{
        		if(objectIsLarger(keys[i], where_value, t.getAttr(where_attr).getType()))
        		{
        			if(t.getAttr(where_attr).getType().equals("java.lang.Integer"))
        				list = finder.index_map.get(Integer.parseInt(keys[i].toString()));
                	if(t.getAttr(where_attr).getType().equals("java.lang.Double"))
                		list = finder.index_map.get(Double.parseDouble(keys[i].toString()));
                	if(t.getAttr(where_attr).getType().equals("java.lang.Character"))
                		list = finder.index_map.get(((String) keys[i]).charAt(0));
                	if(t.getAttr(where_attr).getType().equals("java.lang.String"))
                		list = finder.index_map.get(keys[i].toString());
                	for(int j = 0; j < list.size(); j++)
                		res.add(all[list.get(j)]);
        		}
        		else
        			break;
        	}
        }
        if(where_command.equals(">")){
        	
        	Object[] keys = finder.index_map.keySet().toArray();
        	Arrays.sort(keys,Collections.reverseOrder());
        	
        	for(int i = 0; i < t.gettablesize(); i++)
        	{
        		if(objectIsLarger(where_value, keys[i], t.getAttr(where_attr).getType()))
        		{
        			if(t.getAttr(where_attr).getType().equals("java.lang.Integer"))
        				list = finder.index_map.get(Integer.parseInt(keys[i].toString()));
                	if(t.getAttr(where_attr).getType().equals("java.lang.Double"))
                		list = finder.index_map.get(Double.parseDouble(keys[i].toString()));
                	if(t.getAttr(where_attr).getType().equals("java.lang.Character"))
                		list = finder.index_map.get(((String) keys[i]).charAt(0));
                	if(t.getAttr(where_attr).getType().equals("java.lang.String"))
                		list = finder.index_map.get(keys[i].toString());
                	for(int j = 0; j < list.size(); j++)
                		res.add(all[list.get(j)]);
        		}
        		else
        			break;
        	}
           
        }
        if(where_command.equals(">=")){
        	Object[] keys = finder.index_map.keySet().toArray();
        	Arrays.sort(keys,Collections.reverseOrder());
        	for(int i = 0; i < t.gettablesize(); i++)
        	{
        		if(!objectIsLarger(keys[i],where_value, t.getAttr(where_attr).getType()))
        		{
        			if(t.getAttr(where_attr).getType().equals("java.lang.Integer"))
        				list = finder.index_map.get(Integer.parseInt(keys[i].toString()));
                	if(t.getAttr(where_attr).getType().equals("java.lang.Double"))
                		list = finder.index_map.get(Double.parseDouble(keys[i].toString()));
                	if(t.getAttr(where_attr).getType().equals("java.lang.Character"))
                		list = finder.index_map.get(((String) keys[i]).charAt(0));
                	if(t.getAttr(where_attr).getType().equals("java.lang.String"))
                		list = finder.index_map.get(keys[i].toString());
                	for(int j = 0; j < list.size(); j++)
                		res.add(all[list.get(j)]);
        		}
        		else
        			break;
        	}
        }
        if(where_command.equals("<=")){
        	Object[] keys = finder.index_map.keySet().toArray();
        	Arrays.sort(keys);
        	for(int i = 0; i < t.gettablesize(); i++)
        	{
        		if(!objectIsLarger(where_value, keys[i], t.getAttr(where_attr).getType()))
        		{
        			if(t.getAttr(where_attr).getType().equals("java.lang.Integer"))
        				list = finder.index_map.get(Integer.parseInt(keys[i].toString()));
                	if(t.getAttr(where_attr).getType().equals("java.lang.Double"))
                		list = finder.index_map.get(Double.parseDouble(keys[i].toString()));
                	if(t.getAttr(where_attr).getType().equals("java.lang.Character"))
                		list = finder.index_map.get(((String) keys[i]).charAt(0));
                	if(t.getAttr(where_attr).getType().equals("java.lang.String"))
                		list = finder.index_map.get(keys[i].toString());
                	for(int j = 0; j < list.size(); j++)
                		res.add(all[list.get(j)]);
        		}
        		else
        			break;
        	}
           
        }
        //printList(res);
        Table tool = new Table(from_tbName);
        //printList(res);
        if(res.size() == 0)
        {
        	System.out.println("Nothing selected.");
        }
        else {
        tool.ListToTable(res, attr);}
        //tool.displayTable();
        return tool;
    }
    
    public Table having(Table t, String where_attr, String where_command, String where_value)
    {

        ArrayList<String> attr = new ArrayList<>();
        for(int i = 0; i < t.getAttrNum(); i++)
        {
            String n = t.getAttr(i).getName();
            attr.add(n);
        }
        Object[][] all = t.TabletoArray();
        ArrayList<Object[]> res = new ArrayList<>();
        if(where_command.equals("=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                        res.add(all[i]);
            }       
        }
        if(where_command.equals("!=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        if(where_command.equals("<")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        if(where_command.equals(">")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        if(where_command.equals(">=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        if(where_command.equals("<=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        //printList(res);
        Table tool = new Table(t.getName());
        //printList(res);
        if(res.size() == 0)
        {
        	System.out.println("Nothing selected.");
        }
        else {
        tool.ListToTable(res, attr);}
        //tool.displayTable();
        return tool;
    }
    
    
    
    public Table where(String from_tbName, String where_attr, String where_command, String where_value)
    {
        Table t = new Table();
        t.getTable(from_tbName);

        ArrayList<String> attr = new ArrayList<>();
        for(int i = 0; i < t.getAttrNum(); i++)
        {
            String n = t.getAttr(i).getName();
            attr.add(n);
        }
        Object[][] all = t.TabletoArray();
        ArrayList<Object[]> res = new ArrayList<>();
        if(where_command.equals("=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                        res.add(all[i]);
            }       
        }
        if(where_command.equals("!=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        if(where_command.equals("<")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        if(where_command.equals(">")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        if(where_command.equals(">=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        if(where_command.equals("<=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                    res.add(all[i]);
            }    
        }
        Table tool = new Table(from_tbName);
        //printList(res);
        if(res.size() == 0)
        {
        	System.out.println("Nothing selected.");
        }
        else {
        tool.ListToTable(res, attr);}
        //tool.displayTable();
        return tool;
    }
    public ArrayList<Object[]> select(String select_attr, String from_tbName, String where_attr, String where_command, Object where_value)
    {
        Table t = new Table();
        t.getTable(from_tbName);
        Object[][] all = t.TabletoArray();
        ArrayList<Object[]> res = new ArrayList<>();
        if(where_command.equals("=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    if(select_attr.equals("*")){
                        Object[] a = all[i];
                        res.add(a);
                    }
                    else{
                        Object[] a = {t.getAttr(select_attr).getTuple(i)};
                        res.add(a);
                    }
                }
            }       
        }
        if(where_command.equals("!=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    if(select_attr.equals("*")){
                        Object[] a = all[i];
                        res.add(a);
                    }
                    else{
                        Object[] a = {t.getAttr(select_attr).getTuple(i)};
                        res.add(a);
                    }
                }
            }    
        }
        if(where_command.equals("<")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    if(select_attr.equals("*")){
                        Object[] a = all[i];
                        res.add(a);
                    }
                    else{
                        Object[] a = {t.getAttr(select_attr).getTuple(i)};
                        res.add(a);
                    }
                }
            }    
        }
        if(where_command.equals(">")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                {
                    if(select_attr.equals("*")){
                        Object[] a = all[i];
                        res.add(a);
                    }
                    else{
                        Object[] a = {t.getAttr(select_attr).getTuple(i)};
                        res.add(a);
                    }
                }
            }    
        }
        if(where_command.equals(">=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    if(select_attr.equals("*")){
                        Object[] a = all[i];
                        res.add(a);
                    }
                    else{
                        Object[] a = {t.getAttr(select_attr).getTuple(i)};
                        res.add(a);
                    }
                }
            }    
        }
        if(where_command.equals("<=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                {
                    if(select_attr.equals("*")){
                        Object[] a = all[i];
                        res.add(a);
                    }
                    else{
                        Object[] a = {t.getAttr(select_attr).getTuple(i)};
                        res.add(a);
                    }
                }
            }    
        }

        //print
        for (int i = 0; i < res.size(); i++) {
            Object[] arr = res.get(i);
            for(int j = 0; j < arr.length; j++)
            {
                System.out.print(arr[j]+ " ");
            }
            System.out.print("\n");
        }
        return res;
    }

    //?having是个啥
    //整一点having的函数吧
    public double sum(Table t, String attr, ArrayList<Integer> index)
    {
        double sum = 0;
        for(int i = 0; i < index.size(); i++)
            sum = sum + Double.parseDouble(t.getAttr(attr).getTuple(index.get(i)).toString());
        return sum;
    }

    public double avg(Table t, String attr, ArrayList<Integer> index)
    {
        double avg = sum(t,attr,index) / index.size();
        return avg;
    }
    public double min(Table t, String attr, ArrayList<Integer> index)
    {
        double min = Integer.MAX_VALUE;
        for(int i = 0; i < index.size(); i++)
        {
            if(Double.parseDouble(t.getAttr(attr).getTuple(index.get(i)).toString()) < min)
                min = Double.parseDouble(t.getAttr(attr).getTuple(index.get(i)).toString());
        }
        return min;
    }
    public double max(Table t, String attr, ArrayList<Integer> index)
    {
        double max = Integer.MIN_VALUE;
        for(int i = 0; i < index.size(); i++)
        {
            if(Double.parseDouble(t.getAttr(attr).getTuple(index.get(i)).toString()) > max)
                max = Double.parseDouble(t.getAttr(attr).getTuple(index.get(i)).toString());
        }
        return max;
    }
    public void printList(ArrayList<Object[]> res){
        for (int i = 0; i < res.size(); i++) {
            Object[] merged_array = res.get(i);
            for(int j = 0; j < merged_array.length; j++)
            {
                System.out.print(merged_array[j]+ " ");
            }
            System.out.print("\n");
		}
    }
    //？count是个啥。。
    // public void select (String select_attr, String select_func, String select_command, String select_value, String from_tbName, String groupby_attr)
    // {
    //     Table t = new Table();
    //     t.getTable(from_tbName);
    //     //分组
    //     HashMap<Object, ArrayList<Integer>> group = new HashMap<>();
    //     ArrayList<Object> group_tuple = t.getAttr(groupby_attr).getAllTuple();
    //     for(int i = 0; i < group_tuple.size(); i++)
    //     {
    //         if(group.containsKey(t.getAttr(groupby_attr).getTuple(i)))
    //         {
    //             ArrayList<Integer> arr = group.get(t.getAttr(groupby_attr).getTuple(i));
    //             arr.add(i);
    //             group.put(t.getAttr(groupby_attr).getTuple(i),arr);
    //         }
    //         else
    //         {
    //             ArrayList<Integer> arr = new ArrayList<>();
    //             arr.add(i);
    //             group.put(t.getAttr(groupby_attr).getTuple(i), arr);
    //         }
    //     }
    //     //然后想干嘛呢

    // }






    //判断相等
    public boolean objectIsEqual(Object a, Object b, String type)
    {	//System.out.println(type);
        if(type.equals("java.lang.Integer"))
        {
        	//System.out.println("enter int");
            int b_int = Integer.parseInt(b.toString());
            //System.out.println((int)a == b_int);
            return (int)a == b_int;
        } 
        if(type.equals("java.lang.Double"))
        {   
            double b_double = Double.parseDouble(b.toString());
            return (double)a == b_double;
        }
            
        if(type.equals("java.lang.Character"))
        {
            char b_char = ((String) b).charAt(0);
            return a.equals(b_char);
        }
        if(type.equals("java.lang.String"))
        {
        	//System.out.println(a.equals(b));
            return a.equals(b);
        }
        return false;
    }
    //判断大小,如果a<b 返回true, >=返回false
    public boolean objectIsLarger(Object a, Object b, String type)
    {
        if(type.equals("java.lang.Integer") || type.equals("java.lang.Double"))
        {
            double x = Double.parseDouble(a.toString());
            double y = Double.parseDouble(b.toString());
            if(x<y)
                return true;
            else
                return false;
        }
        if(type.equals("java.lang.String")|| type.equals("java.lang.Character")) {
	    	if(a.toString().compareTo(b.toString())<0)
	    		return true;
	        else
	            return false;
        }
        return false;
    }
    //union after select
    public Table union(Table a, Table b){
        ArrayList<Object[]> res = new ArrayList<>();
        //get info for return
        String tbName = a.getName();
        ArrayList<String> attr = new ArrayList<>();
        for(int i = 0; i < a.getAttrNum(); i++)
        {
            String n = a.getAttr(i).getName();
            attr.add(n);
        }
        Table t = new Table(tbName);
        //给a排序
        Object[][] arr_a = a.TabletoArray();
//        String type = a.getAttr(0).getType();
//        arr_a = this.sortList_asc(arr_a, type);
             
        //给b排序
        Object[][] arr_b = b.TabletoArray();
//        String type_b = b.getAttr(0).getType();
//        arr_b = this.sortList_asc(arr_b, type_b);
        
        for(int i = 0; i < arr_a.length; i++)
        {
            res.add(arr_a[i]);
        }
        
        for(int i = 0; i < arr_b.length; i++)
        {
        	int j = 0;
            while(j < arr_a.length)
            {
                if(j == arr_a.length - 1 && !Arrays.equals(arr_a[j], arr_b[i]))
                {
                	
                    res.add(arr_b[i]);
                }
                if(j < arr_a.length - 1 && Arrays.equals(arr_a[j], arr_b[i]))
                    break;
                j++;
            }
        }
        
        
        t.ListToTable(res, attr);
        //t.displayTable();
        return t;
    }
    
    public Table intersection(Table a, Table b){
        ArrayList<Object[]> res = new ArrayList<>();
        String tbName = a.getName();
        ArrayList<String> attr = new ArrayList<>();
        for(int i = 0; i < a.getAttrNum(); i++)
        {
            String n = a.getAttr(i).getName();
            attr.add(n);
        }
        Table t = new Table(tbName);
        //给a排序
        Object[][] arr_a = a.TabletoArray();
        String type = a.getAttr(0).getType();
        arr_a = this.sortList_asc(arr_a, type);
             
        //给b排序
        Object[][] arr_b = b.TabletoArray();
        String type_b = b.getAttr(0).getType();
        arr_b = this.sortList_asc(arr_b, type_b);

        for(int i = 0; i < arr_a.length; i++)
        {
            for(int j = 0; j < arr_b.length; j++)
            {
                if(Arrays.equals(arr_a[i], arr_b[j]))
                {
                    res.add(arr_b[j]);
                }
                    
            }
        }
        t.ListToTable(res, attr);
        //t.displayTable();
        return t;
    }

    //join after select(intersection)
//    public ArrayList<Object[]> join_and(ArrayList<Object[]> a, ArrayList<Object[]> b)
//    {
//        //a的大小永远小于b
//        if(a.size() > b.size())
//        {
//            ArrayList<Object[]> tool = b;
//            b = a;
//            a = tool;
//        }
//        Object[][] arr_a = ListToArray(a);
//        String type_a = arr_a[0][0].getClass().getName();
//        arr_a = sortList(arr_a, type_a);
//        Object[][] arr_b = ListToArray(b);
//        String type_b = arr_b[0][0].getClass().getName();
//        arr_b = sortList(arr_b, type_b);
//
//        ArrayList<Object[]> join_res = new ArrayList<>();
//        
//        //把两边都有的merge
//        for(int i = 0; i < arr_a.length; i++)
//        {
//            for(int j = 0; j < arr_b.length; j++)
//            {
//                if(arr_b[j][0].equals(arr_a[i][0]))
//                {
//                    Object[] merged_array = new Object[columns];
//                    System.arraycopy(arr_a[i], 0, merged_array, 0, arr_a[0].length);
//                    System.arraycopy(arr_b[j], 1, merged_array, arr_a[0].length, arr_b[0].length-1);
//                    join_res.add(merged_array);
//                }
//            }
//        }
//        return join_res;
//    }

    Object[][] ListToArray(ArrayList c)
    {
        Object[][] res = new Object[c.size()][];
        for(int i = 0; i < c.size(); i++)
        {
            res[i] = (Object[]) c.get(i);
        }   
        return res;
    }
    
    public void delete(Table t, String where_attr, String where_command, Object where_value)
    {
        if(where_command.equals("=")){
            int len = t.getAttr(where_attr).getTupleSize();
            for(int i = 0; i < len; i++)
            {
                if(objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    for(int j = 0; j<t.getAttrNum(); j++)
                    {
                        if(t.getAttr(j).isForeignKey())
                        {
                            String[] fkinfo= t.getAttr(j).getFkInfo();
                            Table ft = new Table();
                            ft.getTable(fkinfo[0]);
                            delete(ft,fkinfo[1], "=", t.getAttr(j).getTuple(i));
                        }
                        t.getAttr(j).removeTuple(i);
                    }
                    len--;
                    i--;
                }
            }       
        }
        if(where_command.equals("!=")){
            int len = t.getAttr(where_attr).getTupleSize();
            for(int i = 0; i < len; i++)
            {
                if(!objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    for(int j = 0; j<t.getAttrNum(); j++)
                    {
                        if(t.getAttr(j).isForeignKey())
                        {
                            String[] fkinfo= t.getAttr(j).getFkInfo();
                            Table ft = new Table();
                            ft.getTable(fkinfo[0]);
                            delete(ft,fkinfo[1], "=", t.getAttr(j).getTuple(i));
                        }
                        t.getAttr(j).removeTuple(i);
                    }
                    len--;
                    i--;
                }
            }    
        }
        if(where_command.equals("<")){
            int len = t.getAttr(where_attr).getTupleSize();
            for(int i = 0; i < len; i++)
            {
                if(objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    for(int j = 0; j<t.getAttrNum(); j++)
                    {
                        if(t.getAttr(j).isForeignKey())
                        {
                            String[] fkinfo= t.getAttr(j).getFkInfo();
                            Table ft = new Table();
                            ft.getTable(fkinfo[0]);
                            delete(ft,fkinfo[1], "=", t.getAttr(j).getTuple(i));
                        }
                        t.getAttr(j).removeTuple(i);
                    }
                    len--;
                    i--;
                }
            }    
        }
        if(where_command.equals(">")){
            int len = t.getAttr(where_attr).getTupleSize();
            for(int i = 0; i < len; i++)
            {
                if(objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                {
                    for(int j = 0; j<t.getAttrNum(); j++)
                    {
                        if(t.getAttr(j).isForeignKey())
                        {
                            String[] fkinfo= t.getAttr(j).getFkInfo();
                            Table ft = new Table();
                            ft.getTable(fkinfo[0]);
                            delete(ft,fkinfo[1], "=", t.getAttr(j).getTuple(i));
                        }
                        t.getAttr(j).removeTuple(i);
                    }
                    len--;
                    i--;
                }
            }    
        }
        if(where_command.equals(">=")){
            int len = t.getAttr(where_attr).getTupleSize();
            for(int i = 0; i < len; i++)
            {
                if(!objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    for(int j = 0; j<t.getAttrNum(); j++)
                    {
                        if(t.getAttr(j).isForeignKey())
                        {
                            String[] fkinfo= t.getAttr(j).getFkInfo();
                            Table ft = new Table();
                            ft.getTable(fkinfo[0]);
                            delete(ft,fkinfo[1], "=", t.getAttr(j).getTuple(i));
                        }
                        t.getAttr(j).removeTuple(i);
                    }
                    len--;
                    i--;
                }
            }    
        }
        if(where_command.equals("<=")){
            int len = t.getAttr(where_attr).getTupleSize();
            for(int i = 0; i < len; i++)
            {
                if(!objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                {
                    for(int j = 0; j<t.getAttrNum(); j++)
                    {
                        if(t.getAttr(j).isForeignKey())
                        {
                            String[] fkinfo= t.getAttr(j).getFkInfo();
                            Table ft = new Table();
                            ft.getTable(fkinfo[0]);
                            delete(ft,fkinfo[1], "=", t.getAttr(j).getTuple(i));
                        }
                        t.getAttr(j).removeTuple(i);
                    }
                    len--;
                    i--;
                }
            }    
        }
        t.saveTable();
        System.out.println("Delete success!");
        //t.displayTable();
    }
    public void create(String tbName, ArrayList<String> attr, ArrayList<String> type)
    {
        Table tool = new Table(tbName);
        for(int i = 0; i < attr.size(); i++)
        {
            Attr a = new Attr();
            a.setName(attr.get(i));
            a.setType(type.get(i));
            tool.addAttr(a);
        }
        tool.saveTable();
        this.all_tables.add(tool);
        System.out.println("create success!");
    }
    public void create(String tbName, ArrayList<String> attr, ArrayList<String> type, String pk)
    {
        Table tool = new Table(tbName);
        for(int i = 0; i < attr.size(); i++)
        {
            Attr a = new Attr();
            a.setName(attr.get(i));
            a.setType(type.get(i));
            tool.addAttr(a);
            if(pk.equals(a.getName()))
                a.setPrimaryKey(true);
        }
        tool.saveTable();
        this.all_tables.add(tool);
        System.out.println("create success!");
    }
    public void create(String tbName, ArrayList<String> attr, ArrayList<String> type,String fk, String ref_table, String ref_attr)
    {
        Table tool = new Table(tbName);
        for(int i = 0; i < attr.size(); i++)
        {
            Attr a = new Attr();
            a.setName(attr.get(i));
            a.setType(type.get(i));
            if(fk.equals(a.getName()))
                a.setForeignKey(true, ref_table, ref_attr);
            tool.addAttr(a);
        }
        this.all_tables.add(tool);
        tool.saveTable();
        System.out.println("create success!");
    }

     public void create(String tbName, ArrayList<String> attr, ArrayList<String> type, String pk, String fk, String ref_table, String ref_attr)
    {
        Table tool = new Table(tbName);
        for(int i = 0; i < attr.size(); i++)
        {
            Attr a = new Attr();
            a.setName(attr.get(i));
            a.setType(type.get(i));
            if(pk.equals(a.getName()))
                a.setPrimaryKey(true);
            if(fk.equals(a.getName()))
                a.setForeignKey(true, ref_table, ref_attr);
            tool.addAttr(a);
        }
        tool.saveTable();
        this.all_tables.add(tool);
        System.out.println("create success!");
    }

    public void drop(String tbName)
    {
        Table tool = new Table();
        for(int i = 0; i < this.all_tables.size(); i++) {
        	if(this.all_tables.get(i).getName().equals(tbName))
        		this.all_tables.remove(i);
        }
        System.out.print(tool.dropTable(tbName));
    }
    public void update(String tbName, String where_attr, String where_command, Object where_value, String target_attr, Object target_value)
    {
        Table t = new Table();
        t.getTable(tbName);
        String type = t.getAttr(target_attr).getType();
    
        //正文
        if(where_command.equals("=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    if(type.equals("java.lang.Integer"))
                        t.getAttr(target_attr).setTuple_index(i, Integer.parseInt(target_value.toString()));
                    if(type.equals("java.lang.Double"))
                        t.getAttr(target_attr).setTuple_index(i, Double.parseDouble(target_value.toString()));   
                    if(type.equals("java.lang.Character"))
                        t.getAttr(target_attr).setTuple_index(i, ((String)target_value).charAt(0));   
                    if(type.equals("java.lang.String"))
                        t.getAttr(target_attr).setTuple_index(i, target_value);
                }
            }
        }
        if(where_command.equals("!=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsEqual(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    if(type.equals("java.lang.Integer"))
                        t.getAttr(target_attr).setTuple_index(i, Integer.parseInt(target_value.toString()));
                    if(type.equals("java.lang.Double"))
                        t.getAttr(target_attr).setTuple_index(i, Double.parseDouble(target_value.toString()));   
                    if(type.equals("java.lang.Character"))
                        t.getAttr(target_attr).setTuple_index(i, ((String)target_value).charAt(0));   
                    if(type.equals("java.lang.String"))
                        t.getAttr(target_attr).setTuple_index(i, target_value);
                }
            }    
        }
        if(where_command.equals("<")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    if(type.equals("java.lang.Integer"))
                        t.getAttr(target_attr).setTuple_index(i, Integer.parseInt(target_value.toString()));
                    if(type.equals("java.lang.Double"))
                        t.getAttr(target_attr).setTuple_index(i, Double.parseDouble(target_value.toString()));   
                    if(type.equals("java.lang.Character"))
                        t.getAttr(target_attr).setTuple_index(i, ((String)target_value).charAt(0));   
                    if(type.equals("java.lang.String"))
                        t.getAttr(target_attr).setTuple_index(i, target_value);
                }
            }    
        }
        if(where_command.equals(">")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                {
                    if(type.equals("java.lang.Integer"))
                        t.getAttr(target_attr).setTuple_index(i, Integer.parseInt(target_value.toString()));
                    if(type.equals("java.lang.Double"))
                        t.getAttr(target_attr).setTuple_index(i, Double.parseDouble(target_value.toString()));   
                    if(type.equals("java.lang.Character"))
                        t.getAttr(target_attr).setTuple_index(i, ((String)target_value).charAt(0));   
                    if(type.equals("java.lang.String"))
                        t.getAttr(target_attr).setTuple_index(i, target_value);
                }
            }    
        }
        if(where_command.equals(">=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsLarger(t.getAttr(where_attr).getTuple(i), where_value, t.getAttr(where_attr).getType()))
                {
                    if(type.equals("java.lang.Integer"))
                        t.getAttr(target_attr).setTuple_index(i, Integer.parseInt(target_value.toString()));
                    if(type.equals("java.lang.Double"))
                        t.getAttr(target_attr).setTuple_index(i, Double.parseDouble(target_value.toString()));   
                    if(type.equals("java.lang.Character"))
                        t.getAttr(target_attr).setTuple_index(i, ((String)target_value).charAt(0));   
                    if(type.equals("java.lang.String"))
                        t.getAttr(target_attr).setTuple_index(i, target_value);
                }
            }    
        }
        if(where_command.equals("<=")){
            for(int i = 0; i < t.getAttr(where_attr).getTupleSize(); i++)
            {
                if(!objectIsLarger(where_value, t.getAttr(where_attr).getTuple(i), t.getAttr(where_attr).getType()))
                {
                    if(type.equals("java.lang.Integer"))
                        t.getAttr(target_attr).setTuple_index(i, Integer.parseInt(target_value.toString()));
                    if(type.equals("java.lang.Double"))
                        t.getAttr(target_attr).setTuple_index(i, Double.parseDouble(target_value.toString()));   
                    if(type.equals("java.lang.Character"))
                        t.getAttr(target_attr).setTuple_index(i, ((String)target_value).charAt(0));   
                    if(type.equals("java.lang.String"))
                        t.getAttr(target_attr).setTuple_index(i, target_value);
                }
            }    
        }
        t.saveTable();

		for(int i = 0; i < this.all_tables.size(); i++) {
		        	Table temp = this.all_tables.get(i);
		        	if(temp.getName().equals(tbName)) {
		        		this.all_tables.set(i, t);
		        		break;
		        	}
		}
        //System.out.println("Update success!");
        //t.displayTable();
    }
 
      public void insert(String table_name, ArrayList<String> values) {
        Table tool = new Table();
        tool.getTable(table_name);
        for(int j = 0; j < tool.getAttrNum(); j++)
        {
            if(tool.getAttr(j).getType().equals("java.lang.Integer"))
            {
                int item_int = Integer.parseInt(values.get(j));
                tool.getAttr(j).addTuple(item_int);
            }
            else if(tool.getAttr(j).getType().equals("java.lang.Character"))
            {
                char item_c = values.get(j).charAt(0);
                tool.getAttr(j).addTuple(item_c);
            }
            else if(tool.getAttr(j).getType().equals("java.lang.Double"))
            {
                Double item_double = Double.parseDouble(values.get(j));
                tool.getAttr(j).addTuple(item_double);
            }
            else
                tool.getAttr(j).addTuple(values.get(j));
            
        }
        //tool.displayTable();
        for(int i = 0; i < this.all_tables.size(); i++) {
        	if(this.all_tables.get(i).getName().equals(table_name)) {
        		this.all_tables.set(i, tool);
        	}
        }
        tool.saveTable();
    }
    
    public Table nested_join(Table a, Table b, String attr_a, String attr_b)
    {
//    	System.out.println(attr_a);
//    	System.out.println("sssssssss");
//    	for(int i = 0; i < a.getAttrNum(); i++) {
//    		System.out.println(a.getAttr(i).getName());
//    	}
        //把attr变成第一列
        int index = a.getAttrIndex(attr_a);
        //System.out.println(a.numOfAttr());
        if(index != 0)
            a.changeAttrOrder(index);
        Object[][] arr_a = a.TabletoArray();

        int index_b = b.getAttrIndex(attr_b);
        if(index_b != 0)
            b.changeAttrOrder(index_b);
        Object[][] arr_b = b.TabletoArray();
        int columns = a.numOfAttr()+b.numOfAttr()-1;
        ArrayList<Object[]> join_res = new ArrayList<>();
        
        
        //if outer join needed
        //HashSet<Object> set_a = new HashSet<>();
        //HashSet<Object> set_b = new HashSet<>();
        
        //把两边都有的merge
        for(int i = 0; i < arr_a.length; i++)
        {
            // if(!set_a.contains(arr_a[i][0]))
            //     set_a.add(arr_a[i][0]);
            for(int j = 0; j < arr_b.length; j++)
            {
                // if(!set_b.contains(arr_b[j][0]))
                //     set_b.add(arr_b[j][0]);
                if(arr_b[j][0].equals(arr_a[i][0]))
                {
                    Object[] merged_array = new Object[columns];
                    System.arraycopy(arr_a[i], 0, merged_array, 0, arr_a[0].length);
                    System.arraycopy(arr_b[j], 1, merged_array, arr_a[0].length, arr_b[0].length-1);
                    join_res.add(merged_array);
                }
            }
        }
        //变成table

        ArrayList<String> attr = new ArrayList<>();
        for(int i = 0; i < a.getAttrNum(); i++)
        {
            String n = a.getAttr(i).getName();
            attr.add(n);
        }
        for(int i = 1; i < b.getAttrNum(); i++)
        {
            String n = b.getAttr(i).getName();
            attr.add(n);
        }

        String tbName = "";
        tbName+=a.getName();
        tbName+=" ";
        tbName+=b.getName();

        Table c = new Table(tbName);
        c.ListToTable(join_res, attr);
        //c.displayTable();
    
        return c;
    }
    public Table merge_join(Table a, Table b, String attr_a, String attr_b)
    {
        // if(a.gettablesize() > b.gettablesize())
        // {
        //     Table tool = new Table();
        //     tool = a;
        //     a = b;
        //     b = tool;
        // }
        //给a排序
        int index = a.getAttrIndex(attr_a);
        
        if(index != 0)
            a.changeAttrOrder(index);
        Object[][] arr_a = a.TabletoArray();
        String type_a = a.getAttr(attr_a).getType();
        arr_a = this.sortList_asc(arr_a, type_a);
             
        //给b排序
        int index_b = b.getAttrIndex(attr_b);
        if(index_b != 0)
            b.changeAttrOrder(index_b);
        Object[][] arr_b = b.TabletoArray();
        String type_b = b.getAttr(attr_b).getType();
        arr_b = this.sortList_asc(arr_b, type_b);

        //merge
        int columns = a.numOfAttr()+b.numOfAttr()-1;
        ArrayList<Object[]> join_res = new ArrayList<>();
        
       
        
        //把两边都有的merge
        for(int i = 0; i < arr_a.length; i++)
        {
            for(int j = 0; j < arr_b.length; j++)
            {
                //如果a>b就break
                if(objectIsLarger(arr_a[i][0], arr_b[j][0], type_a))
                    break;
                if(arr_b[j][0].equals(arr_a[i][0]))
                {
                    Object[] merged_array = new Object[columns];
                    System.arraycopy(arr_a[i], 0, merged_array, 0, arr_a[0].length);
                    System.arraycopy(arr_b[j], 1, merged_array, arr_a[0].length, arr_b[0].length-1);
                    join_res.add(merged_array);
                }
            }
        }
        
        String tbName = "";
        tbName+=a.getName();
        tbName+=" ";
        tbName+=b.getName();
        Table c = new Table(tbName);

        ArrayList<String> attr = new ArrayList<>();
        for(int i = 0; i < a.getAttrNum(); i++)
        {
            String n = a.getAttr(i).getName();
            attr.add(n);
        }
        for(int i = 1; i < b.getAttrNum(); i++)
        {
            String n = b.getAttr(i).getName();
            attr.add(n);
        }
        
        c.ListToTable(join_res, attr);
        //c.displayTable();
        
        return c;
    }
    public Object[][] sortList_desc(Object[][] array,String type)
    {
        int i = 0;
        //Object[][] array = new Object[][]{{5, "ss"}, {1, "aa"}, {4, "ll"}};
        Comparator<Object[]> arrayComparator = new Comparator<Object[]>() {
        @Override
        public int compare(Object[] o1, Object[] o2) {
            if(type.equals("java.lang.Double") )
            {
                double a = Double.parseDouble(o1[i].toString());
                double b = Double.parseDouble(o2[i].toString());
                return (int)(b-a);
            }
            if(type.equals("java.lang.Integer"))
            {
                int a = Integer.parseInt(o1[i].toString());
                int b = Integer.parseInt(o2[i].toString());
                return b-a;
            }
            if(type.equals("java.lang.Character") || type.equals("java.lang.String"))
            {
                String a = o1[i].toString();
                String b = o2[i].toString();
                return b.compareTo(a);
            }
            
            return 0;
        }
        };
        Arrays.sort(array, arrayComparator);
        return array;
    }

    //自定义object二维数组排序-升序
    public Object[][] sortList_asc(Object[][] array,String type)
    {
        int i = 0;
        //Object[][] array = new Object[][]{{5, "ss"}, {1, "aa"}, {4, "ll"}};
        Comparator<Object[]> arrayComparator = new Comparator<Object[]>() {
        @Override
        public int compare(Object[] o1, Object[] o2) {
            if(type.equals("java.lang.Double") )
            {
                double a = Double.parseDouble(o1[i].toString());
                double b = Double.parseDouble(o2[i].toString());
                return (int)(a-b);
            }
            if(type.equals("java.lang.Integer"))
            {
                int a = Integer.parseInt(o1[i].toString());
                int b = Integer.parseInt(o2[i].toString());
                return a-b;
            }
            if(type.equals("java.lang.Character") || type.equals("java.lang.String"))
            {
                String a = o1[i].toString();
                String b = o2[i].toString();
                return a.compareTo(b);
            }
            
            return 0;
        }
        };
        Arrays.sort(array, arrayComparator);
        return array;
    }
    public void select_orderby(Table t, String order_attr, String order_method, ArrayList<String> select_attr)
    {
        //把order attr换到第一列
        int index = t.getAttrIndex(order_attr);
        if(index != 0)
            t.changeAttrOrder(index);
        String type = t.getAttr(order_attr).getType();
        ArrayList<Object[]> select_arr = new ArrayList<>();
        //select
        for(int i = 0; i < t.gettablesize(); i++)
        {
            Object[] a = new Object[select_attr.size()];
            for(int j = 0; j < select_attr.size(); j++)
            {
                a[j] = t.getAttr(select_attr.get(j)).getTuple(i);
            }
        
            select_arr.add(a);
        }
        Table temp = new Table(t.getName());
        temp.ListToTable(select_arr, select_attr);
        
        
        //order
        Object[][] tool = temp.TabletoArray();
        //降序
        if(order_method.equals("desc"))
            tool = sortList_desc(tool, type);
        else
            tool = sortList_asc(tool, type);

        ArrayList<Object[]> res = new ArrayList<>();
        for(int i = 0; i < tool.length; i++)
        {
            res.add(tool[i]);
        }
        Table c = new Table(t.getName());
        c.ListToTable(res, select_attr);
        //print
        c.displayTable();
        
    }
    //*
    public void select_orderby(Table t, String order_attr, String order_method)
    {
        //把order attr换到第一列
        int index = t.getAttrIndex(order_attr);
        if(index != 0)
            t.changeAttrOrder(index);
        String type = t.getAttr(order_attr).getType();
        ArrayList<Object[]> select_arr = new ArrayList<>();
        
        ArrayList<String> attr = new ArrayList<>();
        for(int i = 0; i < t.getAttrNum(); i++){
            attr.add(t.getAttr(i).getName());
        }
        
        //order
        Object[][] tool = t.TabletoArray();
        //降序
        if(order_method.equals("desc"))
            tool = sortList_desc(tool, type);
        else
            tool = sortList_asc(tool, type);

        ArrayList<Object[]> res = new ArrayList<>();
        for(int i = 0; i < tool.length; i++)
        {
            res.add(tool[i]);
        }
        Table c = new Table(t.getName());
        c.ListToTable(res, attr);
        //print
        c.displayTable();
    }
    public Table groupby(String groupby_attr, String func, String func_attr, Table t){
        HashMap<Object, ArrayList<Integer>> group = new HashMap<>();
        ArrayList<Object> uniqueValue = new ArrayList<>();
        ArrayList<Object> group_tuple = t.getAttr(groupby_attr).getAllTuple();
        for(int i = 0; i < group_tuple.size(); i++)
        {
            if(group.containsKey(t.getAttr(groupby_attr).getTuple(i)))
            {
                ArrayList<Integer> arr = group.get(t.getAttr(groupby_attr).getTuple(i));
                arr.add(i);
                group.put(t.getAttr(groupby_attr).getTuple(i),arr);
            }
            else
            {
                ArrayList<Integer> arr = new ArrayList<>();
                arr.add(i);
                group.put(t.getAttr(groupby_attr).getTuple(i), arr);
                uniqueValue.add(t.getAttr(groupby_attr).getTuple(i));
            }
        }
        ArrayList<Object[]> res = new ArrayList<>();
        for(int i = 0; i < uniqueValue.size(); i++)
        {
            Object[] temp = new Object[2];
            temp[0] = uniqueValue.get(i);
            if(func.equals("avg"))
                temp[1] = avg(t, func_attr, group.get(uniqueValue.get(i)));
            if(func.equals("sum"))
                temp[1] = sum(t, func_attr, group.get(uniqueValue.get(i)));
            if(func.equals("min"))
                temp[1] = min(t, func_attr, group.get(uniqueValue.get(i)));
            if(func.equals("max"))
                temp[1] = max(t, func_attr, group.get(uniqueValue.get(i)));
            if(func.equals("count"))
                temp[1] = group.get(uniqueValue.get(i)).size();
            res.add(temp);
        }
        //ListtoTable
        String attrName = func + "(" + func_attr + ")";
        ArrayList<String> attr = new ArrayList<>();
        attr.add(groupby_attr);
        attr.add(attrName);
        
//        ArrayList<String> attr = new ArrayList<>();
//        attr.add(groupby_attr);
//        attr.add(func_attr);
        Table c = new Table(t.getName());
        c.ListToTable(res, attr);
        //c.displayTable();
        return c;
    }

}