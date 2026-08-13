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

@WebServlet("/AdminAmbulanceServlet")
public class AdminAmbulanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"admin".equals(session.getAttribute("staffRole"))) {
            response.getWriter().println("<tr><td colspan='5'>Please log in as admin.</td></tr>");
            return;
        }

        String sql = "SELECT id, caller_name, phone, pickup_location, status "
                + "FROM ambulance_requests ORDER BY id DESC LIMIT 30";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder rows = new StringBuilder();
            boolean any = false;
            while (rs.next()) {
                any = true;
                int id = rs.getInt("id");
                String currentStatus = rs.getString("status");
                if (currentStatus == null) currentStatus = "requested";

                rows.append("<tr>")
                    .append("<td>AMB").append(id).append("</td>")
                    .append("<td>").append(rs.getString("caller_name")).append(" - ").append(rs.getString("phone")).append("</td>")
                    .append("<td>").append(rs.getString("pickup_location")).append("</td>")
                    .append("<td>")
                    .append("<form style='display:flex;gap:6px' method='POST' action='UpdateAmbulanceStatusServlet'>")
                    .append("<input type='hidden' name='requestId' value='").append(id).append("'>")
                    .append("<select name='newStatus'>")
                    .append(opt("requested", currentStatus))
                    .append(opt("dispatched", currentStatus))
                    .append(opt("completed", currentStatus))
                    .append("</select>")
                    .append("<button type='submit' class='btn btn-primary btn-sm'>Update</button>")
                    .append("</form>")
                    .append("</td>")
                    .append("</tr>");
            }
            if (!any) rows.append("<tr><td colspan='4'>No ambulance requests yet.</td></tr>");
            response.getWriter().println(rows.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='4'>Error loading requests.</td></tr>");
        }
    }

    private String opt(String value, String current) {
        String selected = value.equals(current) ? " selected" : "";
        return "<option value='" + value + "'" + selected + ">" + value + "</option>";
    }
}