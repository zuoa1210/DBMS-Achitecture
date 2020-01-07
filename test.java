package base;
import java.util.*;
import java.io.*;
public class test
{
    public static void main(String[] args)
    {
        // //建表并存表
        // Table t = new Table("Table1");
        // Attr a = new Attr();
        // a.setName("attr1");
        // a.setType("int");
        // a.setPrimaryKey(false);
        // for(int i = 0; i < 10000; i++)
        //     a.addTuple(i);
        // t.addAttr(a);
        // Attr b = new Attr();
        // b.setName("attr2");
        // b.setType("int");
        // b.setPrimaryKey(false);
        // for(int i = 0; i < 10000; i++)
        //     b.addTuple(i);
        // t.addAttr(b);
        // t.saveTable();
        // //saveTable 自动创建路径保存dict了
        
        // //读表并显示
        // Table t2 = new Table();
        // t2.getTable("Table1");
        // t2.displayTable();

        ////删表
        // Table t3 = new Table();
        // t3.dropTable("Table1");

        //建表 测类型
        // Table t4 = new Table("Table2");
        // Attr c = new Attr();
        // c.setName("NO");
        // c.setType("int");
        // c.setPrimaryKey(true);
        // for(int i = 0; i < 5; i++)
        // {
        //     c.addTuple(i);
        // }
        // t4.addAttr(c);
        // Attr d = new Attr();
        // d.setName("weight");
        // d.setType("double");
        // d.addTuple(52.3);
        // d.addTuple(55);
        // d.addTuple(60.2);
        // d.addTuple(62.2);
        // d.addTuple(65.6);
        // t4.addAttr(d);
//        // t4.saveTable();
//        Table t5 = new Table();
//        t5.getTable("Table2");
//        Table t6 = new Table("Table3");
//        Attr t6_1 = new Attr();
//        t6_1.setName("str");
//        t6_1.setType("String");
//        t6_1.addTuple("apple");
//        t6_1.addTuple("welcome");
//        t6_1.addTuple("amy");
//        t6_1.addTuple("Linda");
//        t6_1.addTuple("hello");
//        t6_1.addTuple("apple");
//        t6.addAttr(t6_1);
//        Attr t6_2 = new Attr();
//        t6_2.setName("int");
//        t6_2.setType("int");
//        t6_2.addTuple(2);
//        t6_2.addTuple(5);
//        t6_2.addTuple(2);
//        t6_2.addTuple(5);
//        t6_2.addTuple(1);
//        t6_2.addTuple(3);
//        t6.addAttr(t6_2);
//        //t5.changeAttrOrder(2);
//        t6.displayTable();
//        sql s = new sql();
//        s.join(t5,t6,"str");

        // String s = t5.getAttr("weight").getTuple(2).getClass().getName();
        // System.out.println(s);

        
        



        
    }
}
