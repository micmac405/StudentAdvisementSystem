/*
package edu.uco.teamfreelabor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.inject.Singleton;
import javax.sql.DataSource;


@Named(value = "backgroundJobManager")
@Singleton
@Startup
public class BackgroundJobManager {

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;
    
    @Schedule(hour = "*", minute = "*\1", second = "0", persistent = false)
    public void clearTempDB() throws SQLException{
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "DELTE * from TEMPUSERTABLE"
            );
            
            ps.executeUpdate();
        }
        finally{
            conn.close();
        }
    }
    
}*/
