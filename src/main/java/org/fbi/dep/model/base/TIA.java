package org.fbi.dep.model.base;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-1-31
 * Time: обнГ2:45
 */
public abstract class TIA implements Serializable {
    public abstract TIAHeader getHeader();
    public abstract TIABody getBody();
}
