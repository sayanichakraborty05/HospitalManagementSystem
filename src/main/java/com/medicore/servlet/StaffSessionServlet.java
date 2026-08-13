package com.medicore.servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/StaffSessionServlet")
public class StaffSessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        if (session != null && session.getAttribute("staffRole") != null) {
            response.getWriter().print(session.getAttribute("staffRole"));
        } else {
            response.getWriter().print("none");
        }
    }
}