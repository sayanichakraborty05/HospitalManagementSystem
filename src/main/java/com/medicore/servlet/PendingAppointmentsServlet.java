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

@WebServlet("/PendingAppointmentsServlet")
public class PendingAppointmentsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"doctor".equals(session.getAttribute("staffRole"))) {
            response.getWriter().println("<tr><td colspan='6'>Please log in as a doctor.</td></tr>");
            return;
        }

        String doctorName = (String) session.getAttribute("staffName");
        int staffId = (Integer) session.getAttribute("staffId");

        try (Connection conn = DBConnection.getConnection()) {

            String myDepartment = null;
            try (PreparedStatement psDept = conn.prepareStatement(
                    "SELECT department FROM staff WHERE staff_id = ?")) {
                psDept.setInt(1, staffId);
                try (ResultSet rsDept = psDept.executeQuery()) {
                    if (rsDept.next()) myDepartment = rsDept.getString("department");
                }
            }

            String sql = "SELECT appointment_id, patient_name, appt_date, appt_time, department, reason FROM appointments "
                    + "WHERE status = 'pending' AND ("
                    + "  doctor_name = ? "
                    + "  OR (doctor_name = 'Any available' AND department = ?)"
                    + ") ORDER BY appt_date ASC";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, doctorName);
                ps.setString(2, myDepartment);

                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder rows = new StringBuilder();
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        long id = rs.getLong("appointment_id");
                        String preferredDate = rs.getDate("appt_date").toString();
                        String preferredTime = rs.getTime("appt_time").toString().substring(0, 5);

                        rows.append("<tr>")
                            .append("<td>").append(rs.getString("patient_name")).append("</td>")
                            .append("<td class='mono'>").append(preferredDate).append(" ").append(preferredTime)
                            .append(" <span class='form-hint'>(preferred)</span></td>")
                            .append("<td>").append(rs.getString("department")).append("</td>")
                            .append("<td>").append(rs.getString("reason") == null ? "-" : rs.getString("reason")).append("</td>")
                            .append("<td>")
                            .append("<form style='display:flex;flex-direction:column;gap:4px' method='POST' action='ApproveAppointmentServlet'>")
                            .append("<input type='hidden' name='appointmentId' value='").append(id).append("'>")
                            .append("<label style='font-size:.7rem'>Confirmed date</label>")
                            .append("<input type='date' name='confirmedDate' value='").append(preferredDate).append("' required style='width:140px'>")
                            .append("<label style='font-size:.7rem'>Confirmed time</label>")
                            .append("<input type='time' name='confirmedTime' value='").append(preferredTime).append("' required style='width:140px'>")
                            .append("<label style='font-size:.7rem'>Fee (Rs.)</label>")
                            .append("<input type='number' name='fee' placeholder='e.g. 500' required min='0' style='width:140px'>")
                            .append("<button type='submit' class='btn btn-primary btn-sm'>Approve</button>")
                            .append("</form>")
                            .append("</td>")
                            .append("<td>")
                            .append("<form method='POST' action='RejectAppointmentServlet'>")
                            .append("<input type='hidden' name='appointmentId' value='").append(id).append("'>")
                            .append("<button type='submit' class='btn btn-outline btn-sm'>Reject</button>")
                            .append("</form>")
                            .append("</td>")
                            .append("</tr>");
                    }
                    if (!any) rows.append("<tr><td colspan='6'>No pending appointments.</td></tr>");
                    response.getWriter().println(rows.toString());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='6'>Error loading appointments.</td></tr>");
        }
    }
}