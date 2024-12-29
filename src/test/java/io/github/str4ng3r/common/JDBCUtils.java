package io.github.str4ng3r.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCUtils {

    public static void addParameters(PreparedStatement ps, List<Object> parameters) throws SQLException {
        ps.clearParameters();
        System.out.println(parameters);
        for (int i = 0; i < parameters.size(); i++) ps.setObject(i + 1, parameters.get(i));
    }


    public static int getCount(Connection connection, Selector s, SqlParameter sqlParameter) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(s.getCount(sqlParameter.sql));
        addParameters(ps, sqlParameter.getListParameters());
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return rs.getInt(1);
        return 0;
    }
}
