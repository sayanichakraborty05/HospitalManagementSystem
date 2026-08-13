package com.medicore.servlet;
import com.medicore.db.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

@WebServlet("/LabTestServlet")
public class LabTestServlet extends HttpServlet {

    private void sendResult(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println(
            "<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head>"
            + "<body style='padding:60px 24px'><div class='card card-pad' style='max-width:480px;margin:0 auto'>"
            + "<h2>" + (success ? "Lab test booked!" : "Booking failed") + "</h2>"
            + "<p>" + message + "</p>"
            + "<a href='" + (success ? "patient-dashboard.html" : "lab-test.html") + "' class='btn btn-primary'>"
            + (success ? "Go to dashboard" : "Try again") + "</a>"
            + "</div></body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String patientName = request.getParameter("name");
        String date = request.getParameter("date");
        String mode = request.getParameter("mode");
        String address = request.getParameter("address");
        String[] tests = request.getParameterValues("tests");

        if (tests == null || tests.length == 0) {
            sendResult(response, false, "No tests were selected.");
            return;
        }

        String modeDb = "Home sample collection".equals(mode) ? "home_collection" : "walk_in";

        HttpSession session = request.getSession(false);
        Integer patientId = (session != null) ? (Integer) session.getAttribute("patientId") : null;

        try (Connection conn = DBConnection.getConnection()) {

            double total = 0;
            String priceSql = "SELECT price FROM lab_tests WHERE name = ?";
            try (PreparedStatement psPrice = conn.prepareStatement(priceSql)) {
                for (String testName : tests) {
                    psPrice.setString(1, testName);
                    try (ResultSet rs = psPrice.executeQuery()) {
                        if (rs.next()) total += rs.getDouble("price");
                    }
                }
            }

            String bookingSql = "INSERT INTO lab_bookings (patient_id, patient_name, booking_date, mode, address, total_amount) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            long bookingId;
            try (PreparedStatement psBooking = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS)) {
                if (patientId != null) psBooking.setInt(1, patientId); else psBooking.setNull(1, Types.INTEGER);
                psBooking.setString(2, patientName);
                psBooking.setString(3, date);
                psBooking.setString(4, modeDb);
                psBooking.setString(5, address);
                psBooking.setDouble(6, total);
                psBooking.executeUpdate();

                try (ResultSet keys = psBooking.getGeneratedKeys()) {
                    keys.next();
                    bookingId = keys.getLong(1);
                }
            }

            String itemSql = "INSERT INTO lab_booking_items (booking_id, test_id) "
                    + "SELECT ?, test_id FROM lab_tests WHERE name = ?";
            try (PreparedStatement psItem = conn.prepareStatement(itemSql)) {
                for (String testName : tests) {
                    psItem.setLong(1, bookingId);
                    psItem.setString(2, testName);
                    psItem.executeUpdate();
                }
            }

            sendResult(response, true,
                patientName + ", " + tests.length + " test(s) booked. Total: Rs. " + total);

        } catch (SQLException e) {
            e.printStackTrace();
            sendResult(response, false, e.getMessage());
        }
    }
}