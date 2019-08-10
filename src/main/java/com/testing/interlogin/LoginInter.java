package com.testing.interlogin;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.testing.mysql.ConnectMysql;
import com.testing.mysql.UseMysql;

/**
 * Servlet implementation class LoginInter
 */
@WebServlet("/LoginInter")
public class LoginInter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	// 构造方法
	public LoginInter() {
		// 调用父类的构造方法
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// 返回值编码的修改
		response.setContentType("text/html;charset=UTF-8");
		// 请求参数的编码
		request.setCharacterEncoding("UTF-8");
//		// 从请求中获取loginName和passWord的参数
//		String user = request.getParameter("loginName");
//		String pwd = request.getParameter("passWord");
//		LoginSample loginclass = new LoginSample();
//		boolean loginResult = loginclass.login(user, pwd);
		// 从请求中获取loginName和passWord参数。
		String user = request.getParameter("loginName");
		String pwd = request.getParameter("passWord");
		// 调用login的方法进行登录验证
//				LoginSample loginClass = new LoginSample();
//				boolean loginResult = loginClass.login(user, pwd);
		ConnectMysql conSql = new ConnectMysql();
		UseMysql mySql = new UseMysql(conSql.conn);
		boolean loginResult = mySql.Login(user, pwd);
		String responseResult = "{";
		if (loginResult) {
			responseResult += "\"msg\":\"恭喜您，登录成功!\"}";
		} else {
			responseResult += "\"msg\":\"登录失败，用户名密码错误！\"}";
		}
		// pwd.split("123");
		// System.out.println(user + pwd);
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		// response.getWriter().append("Served at: ").append("练习接口项目");
		// text格式的
		// response.getWriter().append("get方法登录").append(responseResult);
		// json格式的ajax
		response.getWriter().append(responseResult);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		// 返回值编码的修改
		response.setContentType("text/html;charset=UTF-8");
		// 请求参数的编码
		request.setCharacterEncoding("UTF-8");
		// 获取sessionID
		String sessionId = request.getSession().getId();
		System.out.println("SessionId是：" + sessionId);
		// 设置session的有效时间
		request.getSession().setMaxInactiveInterval(1800);
		// 从请求中获取loginName和passWord的参数
		String user = request.getParameter("loginName");
		String pwd = request.getParameter("passWord");
//		LoginSample  loginclass=new LoginSample();
//		boolean loginResult=loginclass.login(user, pwd);
		// 不是字符、数字以及下划线的字符就是特殊字符
		String regex = "[^a-zA-Z0-9_]";
		Pattern p = Pattern.compile(regex);
		// 创建两个匹配器来对字符串进行匹配
		Matcher mu = p.matcher(user);
		Matcher mp = p.matcher(pwd);
		String responseResult = "{";

// 判断输入不为空
		if (user != null && pwd != null && !user.equals("") && !pwd.equals("")) {
			// 判断用户名密码长度为[3,8]
			if (user.length() > 2 && user.length() < 9 && pwd.length() > 2 && pwd.length() < 9) {
				// 通过matcher进行查找判断是否包含特殊字符
				if (!mu.find() && !mp.find()) {
					// 如果session中没有记录loginName，则说明没有登录，可以正常地进行登录
					if (request.getSession().getAttribute("loginName") == null) {

						ConnectMysql consql = new ConnectMysql();
						UseMysql mysql = new UseMysql(consql.conn);
						// 普通是登录会有sql注入
//		boolean loginResult=mysql.Login(user, pwd);
						boolean loginResult = mysql.PLogin(user, pwd);

						if (loginResult) {
							responseResult += "\"msg\":\"恭喜您，登录成功!\"}";
							// 如果登录成功通过了校验，就在服务端记录用户名的信息
							request.getSession().setAttribute("loginName", user);
							// 返回给客户端一个cookie，记录的是本次的sessionID（也就是房间号），
							// 名称和servlet默认返回的cookie名一致
							System.out.println("loginName");
							Cookie ssID = new Cookie("JSESSIONID", sessionId);
							// 设置cookie的超时时间
							ssID.setMaxAge(1800);
							// 返回cookie给客户端
							response.addCookie(ssID);
						} else {
							responseResult += "\"msg\":\"登录失败，用户名密码错误！\"}";
						}
					}
					// 调用数据库查询结束
					// 如果session有记录，分为两种情况：第一种同一个用户第二次登录，第二种，不同用户登录
					else {
						if (request.getSession().getAttribute("loginName").equals(user)) {
							responseResult += "\"msg\":\"用户已经登录，无法再次登录\"}";
						} else {
							responseResult += "\"msg\":\"已经有其它用户登录，无法再次登录\"}";
						}
					}
				}
				// 判断特殊字符结束
				else {
					responseResult += "\"msg\":\"输入有误，不能包含特殊字符！\"}";
				}
			} // 长度判断结束
			else {
				// 长度判断有误
				responseResult += "\"msg\":\"输入有误，用户名密码必须是3到8位！\"}";
			}
		} // 为空判断结束
		else {
			// 输入为空的返回信息
			responseResult += "\"msg\":\"输入有误，用户名密码不能为空！\"}";

		}
		// pwd.split("123");
		// System.out.println(user + pwd);
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		// response.getWriter().append("Served at: ").append("练习接口项目");
		// text格式的
		// response.getWriter().append("post方法登录").append(responseResult);
		// json格式的ajax
		System.out.println(responseResult);
		response.getWriter().append(responseResult);
	}
}
