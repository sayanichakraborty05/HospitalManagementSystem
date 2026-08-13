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

@WebServlet("/PendingStaffServlet")
public class PendingStaffServlet extends HttpServlet {

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

        String sql = "SELECT staff_id, full_name, role, department, email FROM staff "
                + "WHERE status = 'pending' ORDER BY staff_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder rows = new StringBuilder();
            boolean any = false;
            while (rs.next()) {
                any = true;
                long id = rs.getLong("staff_id");
                rows.append("<tr>")
                    .append("<td>").append(rs.getString("full_name")).append("</td>")
                    .append("<td>").append(rs.getString("role")).append("</td>")
                    .append("<td>").append(rs.getString("department") == null ? "-" : rs.getString("department")).append("</td>")
                    .append("<td>").append(rs.getString("email")).append("</td>")
                    .append("<td>")
                    .append("<form style='display:inline' method='POST' action='ApproveStaffServlet'>")
                    .append("<input type='hidden' name='staffId' value='").append(id).append("'>")
                    .append("<button type='submit' class='btn btn-primary btn-sm'>Approve</button>")
                    .append("</form> ")
                    .append("<form style='display:inline' method='POST' action='RejectStaffServlet'>")
                    .append("<input type='hidden' name='staffId' value='").append(id).append("'>")
                    .append("<button type='submit' class='btn btn-outline btn-sm'>Reject</button>")
                    .append("</form>")
                    .append("</td>")
                    .append("</tr>");
            }
            if (!any) rows.append("<tr><td colspan='5'>No pending staff requests.</td></tr>");
            response.getWriter().println(rows.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='5'>Error loading requests.</td></tr>");
        }
    }
}