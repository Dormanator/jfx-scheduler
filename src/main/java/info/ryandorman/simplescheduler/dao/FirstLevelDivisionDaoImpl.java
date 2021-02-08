package info.ryandorman.simplescheduler.dao;

import info.ryandorman.simplescheduler.common.DBConnection;
import info.ryandorman.simplescheduler.common.L10nUtil;
import info.ryandorman.simplescheduler.model.Country;
import info.ryandorman.simplescheduler.model.FirstLevelDivision;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FirstLevelDivisionDaoImpl implements FirstLevelDivisionDao {
    private static final Logger sysLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String GET_BY_COUNTRY_ID = "SELECT co.*, fld.* FROM first_level_divisions fld " +
            "LEFT JOIN countries co ON fld.country_id = co.country_id " +
            "WHERE fld.country_id = ?;";

    public static FirstLevelDivision mapResult(ResultSet rs) throws SQLException {
        int[] counter = new int[]{1};
        return mapResult(rs, counter);
    }

    public static FirstLevelDivision mapResult(ResultSet rs, int[] counter) throws SQLException {
        Country country = CountryDaoImpl.mapResult(rs, counter);
        FirstLevelDivision division = new FirstLevelDivision(
                rs.getInt(counter[0]++),
                rs.getString(counter[0]++),
                country,
                L10nUtil.utcToLocal(rs.getTimestamp(counter[0]++)),
                rs.getString(counter[0]++),
                L10nUtil.utcToLocal(rs.getTimestamp(counter[0]++)),
                rs.getString(counter[0]++)
        );

        // Skip country_id column, instead we store the Country class directly in FirstLevelDivision
        counter[0]++;

        return division;
    }

    @Override
    public List<FirstLevelDivision> getByCountryId(int countryId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        List<FirstLevelDivision> divisions = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(GET_BY_COUNTRY_ID);

            stmt.setInt(1, countryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FirstLevelDivision division = mapResult(rs);
                divisions.add(division);
            }

        } catch (SQLException | IOException e) {
            sysLogger.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(stmt);
        }

        sysLogger.info(divisions.size() + " Divisions returned from database by FirstLevelDivisionDao.getAll");
        return divisions;
    }
}
