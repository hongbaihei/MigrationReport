package com.cib.main.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cib.db.BaseIbatisDao;
import com.cib.db.SelectBatch;
import com.cib.util.poi.ExcelReaderUtil;
import com.cib.util.poi.reader.IRowReader;

@WebServlet(urlPatterns = { "/upload" })
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected Logger log = Logger.getLogger(this.getClass());

	private static final String UPLOAD_DIRECTORY = "upload";

	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;
	// private static final int MAX_FILE_SIZE = 1024 * 1024 * 40;
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50;

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// 表单是否符合文件上传规则
		// 判断客户端请求是否为POST，并且enctype属性是否是“multipart/form-data"
		if (!ServletFileUpload.isMultipartContent(request)) {
			PrintWriter writer = response.getWriter();
			writer.println("Error: Form must has enctype=multipart/form-data.");
			writer.flush();
			return;
		}
		// 文件工厂类 判定初始文件内存以及文件超过内存时临时存放的位置
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 以byte为单位设定文件使用多少内存量后，将文件存入临时存储
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		// 设定临时文件的存储路径
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		// ServletFileUpload 处理同意HTML文件中多文件上传的类，继承自FileUpload
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 设置允许上传文件的最大大小
		upload.setSizeMax(MAX_REQUEST_SIZE);

		String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;

		// 根据存储路径生成文件夹
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		try {
			List<FileItem> formItems = upload.parseRequest(request);

			if (formItems != null && formItems.size() > 0) {
				for (FileItem item : formItems) {
					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator + fileName;
						System.out.println("filePath" + filePath);
						File storeFile = new File(filePath);
						// 写入文件
						item.write(storeFile);

						// 解析excel
						resolveExcel(filePath);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			request.setAttribute("message", "There was an error: " + ex.getMessage());
		}

		// getServletContext().getRequestDispatcher("/jsp/message.jsp").forward(request,
		// response);
		try {
//			compareTwo();
//			generDownLoadExcel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	/**
	 * 解析excel，将数据插入内存数据库
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	private void resolveExcel(String filePath) throws Exception {
		IRowReader rowReader = (IRowReader) findBean("rowReader");
		BaseIbatisDao mensqldao = (BaseIbatisDao) findBean("mensqldao");
		mensqldao.startBatch();
		ExcelReaderUtil.readExcel(rowReader, filePath);
		mensqldao.endBatch();
	}
	
	@SuppressWarnings("resource")
	private void compareTwo() throws Exception{
		
		FileOutputStream out = new FileOutputStream("d:\\temp\\id.csv");
		OutputStreamWriter osw = new OutputStreamWriter(out);
		final BufferedWriter bw = new BufferedWriter(osw);
		
		final BaseIbatisDao mensqldao = (BaseIbatisDao) findBean("mensqldao");
		mensqldao.startBatch();
		
		new SelectBatch(mensqldao.getSqlSessionFactory()) {
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
		
		mensqldao.endBatch();
	}

	@SuppressWarnings("resource")
	private void generDownLoadExcel() throws Exception {
		BaseIbatisDao mensqldao = (BaseIbatisDao) findBean("mensqldao");
		mensqldao.startBatch();

		FileOutputStream out = new FileOutputStream("d:\\temp\\xxx.csv");
		OutputStreamWriter osw = new OutputStreamWriter(out);
		final BufferedWriter bw = new BufferedWriter(osw);

		new SelectBatch(mensqldao.getSqlSessionFactory()) {
			@Override
			public void processRecord(Object row) throws Exception {
				Map<String, Object> data = (Map<String, Object>) row;
				for (Entry<String, Object> entry : data.entrySet()) {
					String str = entry.getValue().toString();
					bw.append(str).append(",");
				}
				bw.append("\r");
			}
		}.executeSelect("mem.query_derive1");

		bw.close();
		osw.close();
		out.close();
	}

	private static WebApplicationContext context;

	public Object findBean(String beanName) {
		if (context == null) {
			context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		}
		return context.getBean(beanName);
	}
	
	  
}
