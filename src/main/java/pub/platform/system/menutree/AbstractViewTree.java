// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi
// Source File Name:   AbstractViewTree.java

package pub.platform.system.menutree;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.jdom.*;

public abstract class AbstractViewTree
{

    private int _$12219;

    public AbstractViewTree()
    {
        _$12219 = 0;
    }

    protected final void output(Writer out, Document doc)
        throws JDOMException, IOException
    {
        out.write("var menu=new MTMenu();\n");
        String parentMenu = "menu";
        _$12221(out, doc.getRootElement(), parentMenu);
    }

    private void _$12221(Writer out, Element tree, String parentMenu)
        throws IOException
    {
        List trees = tree.getChildren();
        int j = 0;
        for(int iCount = trees.size(); j < iCount; j++)
        {
            Element element = (Element)trees.get(j);
            StringBuffer strBuf = new StringBuffer();
            strBuf.append(parentMenu);
            strBuf.append(".MTMAddItem(new MTMenuItem(\"");
            strBuf.append(element.getAttributeValue("text"));
            //System.out.println(element.getAttribute("action"));

            if(element.getAttributeValue("action").equals("#"))
            {
                strBuf.append("\",\"javascript:\",\"mainFrame\",\"\"));\n");
                strBuf.append("var menu");
                strBuf.append(++_$12219);
                strBuf.append("=new MTMenu();\n");
                strBuf.append(parentMenu);
                strBuf.append(".items[");
                strBuf.append(j);
                strBuf.append("].MTMakeSubmenu(menu");
                strBuf.append(_$12219);
                strBuf.append(");\n");
                out.write(strBuf.toString());
                _$12221(out, (Element)trees.get(j), "menu" + _$12219);
            } else
            {
                strBuf.append("\",\"");
                strBuf.append(element.getAttributeValue("action"));
                strBuf.append("\",\"mainFrame\",\"\"));\n");
                out.write(strBuf.toString());
            }
        }

    }
}
