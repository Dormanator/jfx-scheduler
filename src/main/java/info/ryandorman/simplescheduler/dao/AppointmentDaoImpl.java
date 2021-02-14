package info.ryandorman.simplescheduler.dao;

import info.ryandorman.simplescheduler.common.DBConnection;
import info.ryandorman.simplescheduler.common.L10nUtil;
import info.ryandorman.simplescheduler.common.ResultColumnIterator;
import info.ryandorman.simplescheduler.model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AppointmentDaoImpl implements AppointmentDao {
    private static final Logger sysLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final String GET_ALL = "SELECT co.*, fld.*, c.*, u.*, con.*, app.* " +
            "FROM appointments app " +
            "LEFT JOIN customers c ON app.customer_id = c.customer_id " +
            "LEFT JOIN first_level_divisions fld ON c.division_id = fld.division_id " +
            "LEFT JOIN countries co ON fld.country_id = co.country_id " +
            "LEFT JOIN users u ON app.user_id = u.user_id " +
            "LEFT JOIN contacts con ON app.contact_id = con.contact_Id;";

    private final String GET_BY_DATE_TIME_WINDOW = "SELECT co.*, fld.*, c.*, u.*, con.*, app.* " +
            "FROM appointments app " +
            "LEFT JOIN customers c ON app.customer_id = c.customer_id " +
            "LEFT JOIN first_level_divisions fld ON c.division_id = fld.division_id " +
            "LEFT JOIN countries co ON fld.country_id = co.country_id " +
            "LEFT JOIN users u ON app.user_id = u.user_id " +
            "LEFT JOIN contacts con ON app.contact_id = con.contact_Id " +
            "WHERE app.start >= ? " +
            "AND app.end <= ?;";

    private final String GET_BY_ID = "SELECT co.*, fld.*, c.*, u.*, con.*, app.* " +
            "FROM appointments app " +
            "LEFT JOIN customers c ON app.customer_id = c.customer_id " +
            "LEFT JOIN first_level_divisions fld ON c.division_id = fld.division_id " +
            "LEFT JOIN countries co ON fld.country_id = co.country_id " +
            "LEFT JOIN users u ON app.user_id = u.user_id " +
            "LEFT JOIN contacts con ON app.contact_id = con.contact_Id " +
            "WHERE app.appointment_id = ?;";

    private final String GET_BY_CUSTOMER_ID_AND_DATE_TIME = "SELECT co.*, fld.*, c.*, u.*, con.*, app.* " +
            "FROM appointments app " +
            "LEFT JOIN customers c ON app.customer_id = c.customer_id " +
            "LEFT JOIN first_level_divisions fld ON c.division_id = fld.division_id " +
            "LEFT JOIN countries co ON fld.country_id = co.country_id " +
            "LEFT JOIN users u ON app.user_id = u.user_id " +
            "LEFT JOIN contacts con ON app.contact_id = con.contact_Id " +
            "WHERE app.customer_id = ? " +
            "AND ? BETWEEN app.start AND app.end;";

    private Appointment mapResult(ResultSet rs) throws SQLException {
        ResultColumnIterator resultColumn = new ResultColumnIterator(1);
        return mapResult(rs, resultColumn);
    }

    public static Appointment mapResult(ResultSet rs, ResultColumnIterator resultColumn) throws SQLException {
        Customer customer = CustomerDaoImpl.mapResult(rs, resultColumn);
        User user = UserDaoImpl.mapResult(rs, resultColumn);
        Contact contact = ContactDaoImpl.mapResult(rs, resultColumn);

        return new Appointment(
                rs.getInt(resultColumn.next()),
                rs.getString(resultColumn.next()),
                rs.getString(resultColumn.next()),
                rs.getString(resultColumn.next()),
                rs.getString(resultColumn.next()),
                L10nUtil.utcToLocal(rs.getTimestamp(resultColumn.next())),
                L10nUtil.utcToLocal(rs.getTimestamp(resultColumn.next())),
                customer,
                user,
                contact,
                L10nUtil.utcToLocal(rs.getTimestamp(resultColumn.next())),
                rs.getString(resultColumn.next()),
                L10nUtil.utcToLocal(rs.getTimestamp(resultColumn.next())),
                rs.getString(resultColumn.next())
                );
    }

    @Override
    public List<Appointment> getAll() {
        Connection conn;
        PreparedStatement stmt = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(GET_ALL);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appointment appointment = mapResult(rs);
                appointments.add(appointment);
            }

        } catch (SQLException | IOException e) {
            sysLogger.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(stmt);
        }

        sysLogger.info(appointments.size() + " Appointments returned from database by AppointmentDao.getAll");
        return appointments;
    }

    @Override
    public List<Appointment> getByDateTimeWindow(ZonedDateTime start, ZonedDateTime end) {
        Connection conn;
        PreparedStatement stmt = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(GET_BY_DATE_TIME_WINDOW);

            ResultSet rs = stmt.executeQuery();

            stmt.setTimestamp(1, L10nUtil.LocalToUtc(start));
            stmt.setTimestamp(2, L10nUtil.LocalToUtc(end));

            while (rs.next()) {
                Appointment appointment = mapResult(rs);
                appointments.add(appointment);
            }

        } catch (SQLException | IOException e) {
            sysLogger.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(stmt);
        }

        sysLogger.info(appointments.size() + " Appointments returned from database by " +
                "AppointmentDao.getByDateTimeWindow=" + start + ", " + end);
        return appointments;
    }

    @Override
    public Appointment getById(int id) {
        Connection conn;
        PreparedStatement stmt = null;
        Appointment appointment = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(GET_BY_ID);

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                appointment = mapResult(rs);
            }

        } catch (SQLException | IOException e) {
            sysLogger.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(stmt);
        }

        sysLogger.info(appointment.getId() + ":" + appointment.getTitle()
                + " returned from database by AppointmentDao.getById=" + id);
        return appointment;
    }

    @Override
    public Appointment getByCustomerIdAndDateTime(int customerId, ZonedDateTime appointmentTime) {
        Connection conn;
        PreparedStatement stmt = null;
        Appointment appointment = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(GET_BY_CUSTOMER_ID_AND_DATE_TIME);

            stmt.setInt(1, customerId);
            stmt.setTimestamp(2, L10nUtil.LocalToUtc(appointmentTime));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                appointment = mapResult(rs);
            }

        } catch (SQLException | IOException e) {
            sysLogger.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.close(stmt);
        }

        sysLogger.info(appointment.getId() + ":" + appointment.getTitle()
                + " returned from database by AppointmentDao.getByCustomerIdAndDateTime=" + customerId + ", "
                + appointmentTime);
        return appointment;
    }

    @Override
    public int create() {
        return 0;
    }

    @Override
    public int update() {
        return 0;
    }

    @Override
    public int delete() {
        return 0;
    }
}
