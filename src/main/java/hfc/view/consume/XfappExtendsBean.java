package hfc.view.consume;

import fip.repository.model.Xfapp;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-10
 * Time: обнГ5:04
 * To change this template use File | Settings | File Templates.
 */
public class XfappExtendsBean extends Xfapp {
    private String appStatusLbl;

    public String getAppStatusLbl() {
        return appStatusLbl;
    }

    public void setAppStatusLbl(String appStatusLbl) {
        this.appStatusLbl = appStatusLbl;
    }
}
