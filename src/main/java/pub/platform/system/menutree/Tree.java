// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi
// Source File Name:   DemoTree.java

package pub.platform.system.menutree;


import java.io.*;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public final class Tree extends AbstractViewTree
{

    private String _$10738;

    public Tree(String str)
    {
        _$10738 = str;
    }

    public void output(Writer out)   throws JDOMException, IOException
    {
       Reader reader = new StringReader(_$10738);

        SAXBuilder builder = new SAXBuilder();
        org.jdom.Document doc = builder.build(reader);
        output(out, doc);
        reader.close();
    }
}
