package com.cib.main.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns={"/download"}) 
public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	
	private static final String DOWNLOAD_DIRECTORY = "download";

	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		OutputStream outputStrem = null;
		InputStream inputStream = null;
		try {
			String downloadPath = getServletContext().getRealPath("") + File.separator + DOWNLOAD_DIRECTORY;
			String fileName = "迁徙报表";
			File file = new File(downloadPath + File.separator + fileName );
			if(!file.exists())
				file.createNewFile();
			inputStream = new FileInputStream(file);

			response.setBufferSize(8192);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/zip;charset=UTF-8");//打包下载
//			String fileName = encodeFileName(downloadedFile.getDisplayName(),request);

			response.addHeader("Content-disposition", "attachment;filename="+fileName);
			outputStrem = response.getOutputStream();
			byte[] buffer = new byte[8192];
			int len;
			while ((len = inputStream.read(buffer)) > 0) {
				outputStrem.write(buffer, 0, len);
			}
			outputStrem.flush();
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			if (outputStrem != null) {
				try {
					outputStrem.close();
				} catch (IOException e) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		super.doGet(request, response);
	}
	
//	private String encodeFileName(String fileName,HttpServletRequest request) throws UnsupportedEncodingException {
//		String agent = request.getHeader("USER-AGENT");
//		if (null != agent && -1 != agent.indexOf("MSIE")) {
//			return URLEncoder.encode(fileName, "UTF-8");
//		} else {
//			return fileName;
//		}
//	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}