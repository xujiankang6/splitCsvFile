package com.jiankang.splitfile.sqlUtils;

/*
 *@create by jiankang
 *@date 2020/4/8 time 11:49
 */

public class Test {
    public static void main(String[] args) {

        String tile = "归属部门\t辅导人\t被辅导人\t协防日期\t产品\t协访目的\t开场白\t询问\t信息传递\t处理异议\t缔结\t访后分析\t客情关系\t协访评语\t新建时间\t确认时间\t\t\t\t\t\t\t\t\n";
        String[] columns = tile.split("\\s+");
        int i = 1;
        for (String item : columns) {
            //select '归属部门'  as label  union all
            System.out.println("select " + (i++) + " as seq,\'" + item.trim() + "\' as label union all ");
        }
    }
}
