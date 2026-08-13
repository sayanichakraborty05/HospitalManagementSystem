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

@WebServlet("/AdminLabBookingsServlet")
public class AdminLabBookingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"admin".equals(session.getAttribute("staffRole"))) {
            response.getWriter().println("<tr><td colspan='6'>Please log in as admin.</td></tr>");
            return;
        }

        String sql = "SELECT booking_id, patient_name, booking_date, mode, total_amount, status "
                + "FROM lab_bookings ORDER BY booking_id DESC LIMIT 30";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder rows = new StringBuilder();
            boolean any = false;
            while (rs.next()) {
                any = true;
                long id = rs.getLong("booking_id");
                String currentStatus = rs.getString("status");

                rows.append("<tr>")
                    .append("<td>LB").append(id).append("</td>")
                    .append("<td>").append(rs.getString("patient_name")).append("</td>")
                    .append("<td class='mono'>").append(rs.getDate("booking_date")).append("</td>")
                    .append("<td>").append(rs.getString("mode")).append("</td>")
                    .append("<td class='mono'>Rs. ").append(rs.getDouble("total_amount")).append("</td>")
                    .append("<td>")
                    .append("<form style='display:flex;gap:6px' method='POST' action='UpdateLabBookingStatusServlet'>")
                    .append("<input type='hidden' name='bookingId' value='").append(id).append("'>")
                    .append("<select name='newStatus'>")
                    .append(opt("booked", currentStatus))
                    .append(opt("sample_collected", currentStatus))
                    .append(opt("report_ready", currentStatus))
                    .append(opt("completed", currentStatus))
                    .append("</select>")
                    .append("<button type='submit' class='btn btn-primary btn-sm'>Update</button>")
                    .append("</form>")
                    .append("</td>")
                    .append("</tr>");
            }
            if (!any) rows.append("<tr><td colspan='6'>No lab bookings yet.</td></tr>");
            response.getWriter().println(rows.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='6'>Error loading bookings.</td></tr>");
        }
    }

    private String opt(String value, String current) {
        String selected = value.equals(current) ? " selected" : "";
        return "<option value='" + value + "'" + selected + ">" + value + "</option>";
    }
}