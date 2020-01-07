package temp;

import base.Attr;
import base.Table;

public class data {
	public static void main(String args[]) {	
		String tName = "i_1000";
		String attr1 = tName +"@"+"id1";
		String attr2 = tName +"@"+"id2";
		Table t6 = new Table(tName);
        Attr t6_1 = new Attr();
        
        t6_1.setName(attr1);
        t6_1.setType("int");
        for(int i = 1; i < 1000; i++)
        	t6_1.addTuple(i);
        t6.addAttr(t6_1);
        Attr t6_2 = new Attr();
        t6_2.setName(attr2);
        t6_2.setType("int");
        for(int i = 1; i < 1000; i++)
        	t6_2.addTuple(i);
        t6.addAttr(t6_2);
        t6.displayTable();
        t6.saveTable();
	}
}
