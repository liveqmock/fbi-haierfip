package pub.platform.utils;

import pub.platform.db.*;
public interface DataDAO
{
    public boolean create(boolean isTrab, DatabaseConnection dc, String SqlStr);
    public boolean update(boolean isTrab, DatabaseConnection dc, String SqlStr);
    public boolean delete(boolean isTrab, DatabaseConnection dc, String SqlStr);
    public RecordSet get(DatabaseConnection dc, String SqlStr);

}