package pub.platform.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import zt.concurrent.concurrent.ConcurrentHashMap;

public class SequenceManager {
    
	private static final Log logger = LogFactory.getLog(SequenceManager.class);

	// Statically startup a sequence manager for each of the five sequence
    // counters.
    private static ConcurrentHashMap sequences = new ConcurrentHashMap();
    
    static {
    	try {
			load();
			logger.error("初始化序列号成功！");
		} catch (Exception e) {
			logger.error("初始化序列号失败：",e);
		}
    }
    
    public static void load() throws Exception {
    	init(true);
    }
    
    public static void reload() throws Exception {
    	init(false);
    }
    
    /**
     * Init all sequence from database.
     *
     */
    private static void init(boolean autoClose) throws Exception {
    	
        DatabaseConnection con = null;
        try {
             if(autoClose) //自动关闭连接时使用非线程数据库
                 con = ConnectionManager.getInstance().getConnection();
               //con = ConnectionManager.getInstance().get();
             else //非自动关闭连接时使用线程数据库
                  con = ConnectionManager.getInstance().get();
             
             RecordSet rs = con.executeQuery("SELECT * FROM cp_sequence");
             
             while (rs.next()) {
            	 Sequence temp = new Sequence(rs.getString("seqName"));
            	 
            	 temp.set_minValue(rs.getLong("minValue"));
            	 temp.set_maxValue(rs.getLong("maxValue"));
            	 temp.set_cycle(rs.getString("cycle"));
            	 temp.set_stepValue(rs.getInt("stepValue"));
            	 temp.set_cache(rs.getInt("stepValue")*rs.getInt("cache"));
            	 
            	 sequences.put(rs.getString("seqName"),temp);
             }
             
        } catch(Exception e) {
        	logger.error("",e);
        	throw new Exception();
        } finally {
            if(con != null && autoClose) //只处理自动关闭连接的情况
                 con.close();
       }

    }

    /**
     * Returns the next ID of the specified type.
     *
     * @param type the type of unique ID.
     * @return the next unique ID of the specified type.
     */
    public static long nextID(String type) {
        return ((Sequence)sequences.get(type)).nextUniqueID();
    }

}
