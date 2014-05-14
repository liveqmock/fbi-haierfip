package pub.cenum;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-11
 * Time: ионГ11:16
 * To change this template use File | Settings | File Templates.
 */
public class LevelItem {
    private String itemNo = null;
        private String itemLabel = null;

        public LevelItem(String itemno, String itemlabel) {
            this.itemNo = itemno;
            this.itemLabel = itemlabel;
        }

        public String getItemNo() {
            return this.itemNo;
        }

        public String getItemLabel() {
            return this.itemLabel;
        }
}
