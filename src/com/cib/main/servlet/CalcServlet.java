package com.cib.main.servlet;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cib.db.BaseIbatisDao;
import com.cib.db.SelectBatch;

@WebServlet(urlPatterns = { "/calc" })
public class CalcServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected Logger log = Logger.getLogger(this.getClass());


	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		FileOutputStream out = new FileOutputStream("d:\\temp\\id.csv");
		OutputStreamWriter osw = new OutputStreamWriter(out);
		final BufferedWriter bw = new BufferedWriter(osw);
		
		final BaseIbatisDao mensqldao = (BaseIbatisDao) findBean("mensqldao");
		mensqldao.startBatch();
		
		try {
			new SelectBatch(mensqldao.getSqlSessionFactory()) {
				@SuppressWarnings("resource")
				@Override
				public void processRecord(Object row) throws Exception {
					final Map<String, Object> data1 = (Map<String, Object>) row;
					
					new SelectBatch(mensqldao.getSqlSessionFactory()) {
						@Override
						public void processRecord(Object row) throws Exception {
							Map<String, Object> data2 = (Map<String, Object>) row;
							String id1 = data1.get("IDENTITY_NUM").toString();
							String id2 = data2.get("IDENTITY_NUM").toString();
							
							//TEST DEMO
							if(id1.equals(id2))
								bw.append(id2).append("\r");
						}
					}.executeSelect("mem.query_derive2");
				}
			}.executeSelect("mem.query_derive1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mensqldao.endBatch();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	private static WebApplicationContext context;

	public Object findBean(String beanName) {
		if (context == null) {
			context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		}
		return context.getBean(beanName);
	}
	
	  
}
