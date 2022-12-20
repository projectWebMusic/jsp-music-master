package music.action;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import music.Constant;
import music.dao.AdminDao;
import music.dao.impl.AdminDaoImpl;
import music.vo.Admin;

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private String info = "";
       
  
    public AdminServlet() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Thực hiện các hoạt động khác nhau theo các giá trị khác nhau của tham số thông tin
		info = request.getParameter("info");
		// Đăng nhập
		if (info.equals("login")){
			this.admin_login(request, response);
		}
		// đăng xuất
		if (info.equals("logout")){
			this.admin_logout(request, response);
		}
		// thêm quản trị viên
		if (info.equals("add")){
			this.admin_add(request, response);
		}
		//đổi mật khẩu
		if (info.equals("psw")){
			this.admin_modifyPsw(request, response);
		}
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Thao tác đăng nhập quản trị viên
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void admin_login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Admin adminData = new Admin();
		adminData.setAdminUsername(username);
		adminData.setAdminPassword(password);
		
		AdminDao adminDao = new AdminDaoImpl();
		Admin admin = adminDao.login(adminData);
		if (admin != null){
//			request.setAttribute("admin", admin);
//			request.setAttribute("login_flag", Constant.LOGIN_SUCCESS);
			HttpSession session = request.getSession();
			session.setAttribute("admin", admin);
			session.setAttribute("admin_login_flag", Constant.LOGIN_SUCCESS);
			request.getRequestDispatcher("manager.jsp").forward(request, response);
			
		}
	}
	/**
	 * Thao tác đăng xuất của quản trị viên
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void admin_logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("admin");
		session.setAttribute("admin_login_flag", Constant.LOGIN_FAILURE);
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
	/** 
	 * Quản trị viên đã thêm
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void admin_add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		Date register = new Date();
		Date lastDate = new Date();
		
		Admin adminData = new Admin();
		adminData.setAdminUsername(username);
		adminData.setAdminPassword(password);
		adminData.setAdminRegisterDate(register);
		adminData.setAdminLastDate(lastDate);
		
		AdminDao adminDao = new AdminDaoImpl();
		
		request.setAttribute("message", adminDao.save(adminData) ? "Thành công!" : "Thất Bại!");
		request.setAttribute("flag", true);
		request.getRequestDispatcher("admin/newAdmin.jsp").forward(request, response);
	}
	/**
	 * Thay đổi mật khẩu quản trị viên
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void admin_modifyPsw(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String newPassword = request.getParameter("newPassword");
		
		Admin adminData = new Admin();
		adminData.setAdminUsername(username);
		adminData.setAdminPassword(password);
		
		// Kiểm tra xem mật khẩu có đúng không
		
		AdminDao adminDao = new AdminDaoImpl();
		Admin admin = adminDao.login(adminData);
		if (admin != null){
			System.out.println("Mật khẩu chính xác!");
			admin.setAdminPassword(newPassword);
			if (adminDao.update(admin)){
				request.setAttribute("message", "Mật khẩu đã được cập nhật!");
			} else {
				request.setAttribute("message","Không đổi được mật khẩu");
			}
		} else {
			request.setAttribute("message", "Không đổi được mật khẩu");
			System.out.println("Sai mật khẩu!");
		}
		System.out.println("Kết thúc!");
		request.setAttribute("flag", true);
		request.getRequestDispatcher("admin/psw.jsp").forward(request, response);
		
	}
}
