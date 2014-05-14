package pub.platform.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Manages sequences of unique ID's that get stored in the database. Database
 * support for sequences varies widely; some don't support them at all. So,
 * we handle unique ID generation with a combination VM/database solution.<p>
 *
 * A special table in the database doles out blocks of unique ID's to each
 * virtual machine that interacts with Jive. This has the following consequences:
 * <ul>
 *  <li>There is no need to go to the database every time we want a new unique
 *      id.
 *  <li>Multiple app servers can interact with the same db without consequences
 *      in terms of id's.
 *  <li>The order of unique id's may not correspond to creation date
 *      (as they once did).
 * </ul>
 */
public class Sequence {
	
    private static final String LOAD_ID =
        "SELECT * FROM cp_sequence WHERE seqName=?";
    private static final String UPDATE_ID =
        "UPDATE cp_sequence SET curValue=? WHERE seqName=? AND curValue=?";

    /**
     * The number of ID's to checkout at a time. 15 should provide a good
     * balance between speed and not wasing too many ID's on appserver restarts.
     * Feel free to change this number if you believe your Jive setup warrants
     * it.
     */

    private String _seqName;
    private long _curValue;
    private long _minValue;
    private long _maxValue;

    private long _tempMaxValue;
    
    private int _stepValue;
    
    private String _cycle;
    private int _cache;

    /**
     * Creates a new DbSequenceManager.
     */
    public Sequence(String type) {
        this._seqName = type;
        this._curValue = 0l;
        this._tempMaxValue = 0l;
    }

    /**
     * Returns the next available unique ID. Essentially this provides for the
     * functionality of an auto-increment database field.
     */
    public synchronized long nextUniqueID() {
        if (! (_curValue < _tempMaxValue)) {
            // Get next block -- make 5 attempts at maximum.
            getNextBlock(5);
        }
        long id = _curValue;
        _curValue+=_stepValue;
        
        return id;
    }

    /**
     * Performs a lookup to get the next availabe ID block. The algorithm is as
     * follows:<ol>
     *  <li> Select currentID from appropriate db row.
     *  <li> Increment id returned from db.
     *  <li> Update db row with new id where id=old_id.
     *  <li> If update fails another process checked out the block first; go
     *          back to step 1. Otherwise, done.
     * </ol>
     */
    private void getNextBlock(int count) {
        if (count == 0) {
            System.err.println("Failed at last attempt to obtain an ID, aborting...");
            return;
        }
        boolean success = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getInstance().getConnection().getConnection();
            // Get the current ID from the database.
            pstmt = con.prepareStatement(LOAD_ID);
            pstmt.setString(1, _seqName);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Loading the current ID failed. The " +
                    "jiveID table may not be correctly populated.");
            }
            
            //当前值
            long currentID = rs.getLong("curValue");
            
            pstmt.close();
            // Increment the id to define our block.
            //新的开始值，不能确定上次的具体值，所以按最大化处理
            long newID = currentID + _cache;
            // The WHERE clause includes the last value of the id. This ensures
            // that an update will occur only if nobody else has performed an
            // update first.
            pstmt = con.prepareStatement(UPDATE_ID);
            pstmt.setLong(1, newID);
            pstmt.setString(2, _seqName);
            pstmt.setLong(3, currentID);
            // Check to see if the row was affected. If not, some other process
            // already changed the original id that we read. Therefore, this
            // round failed and we'll have to try again.
            success = pstmt.executeUpdate() == 1;
            if (success) {
                this._curValue = currentID;
                this._tempMaxValue = newID;
                
                //根据是否循环进行调整
                if (_tempMaxValue>_maxValue && _cycle.equals("1")) {
                    this._curValue = _minValue;
                    this._tempMaxValue = _minValue;                
                }
                
            }
        }
        catch( Exception sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        if (!success) {
            System.err.println("WARNING: failed to obtain next ID block due to " +
                "thread contention. Trying again...");
            // Call this method again, but sleep briefly to try to avoid thread
            // contention.
            try {
                Thread.currentThread().sleep(75);
            } catch (InterruptedException ie) { }
            getNextBlock(count-1);
        }
    }

	public String get_cycle() {
		return _cycle;
	}

	public void set_cycle(String _cycle) {
		this._cycle = _cycle;
	}

	public int get_stepValue() {
		return _stepValue;
	}

	public void set_stepValue(int _increment) {
		this._stepValue = _increment;
	}

	public long get_minValue() {
		return _minValue;
	}

	public void set_minValue(long value) {
		_minValue = value;
	}

	public int get_cache() {
		return _cache;
	}

	public void set_cache(int _cache) {
		this._cache = _cache;
	}

	public long get_maxValue() {
		return _maxValue;
	}

	public void set_maxValue(long value) {
		_maxValue = value;
	}
}