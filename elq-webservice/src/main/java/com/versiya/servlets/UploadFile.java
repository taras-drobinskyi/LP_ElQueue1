package com.versiya.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	File uploads = new File("uploads");
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		uploads.mkdir();

		// process only if its multipart content
		if (ServletFileUpload.isMultipartContent(req)) {
			try {
				List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);

				for (FileItem item : multiparts) {
					if (!item.isFormField()) {
						String name = new File(item.getName()).getName();
						item.write(new File(uploads.getCanonicalPath() + File.separator + name));
						
					}
				}

				// File uploaded successfully
				req.setAttribute("message", "File Uploaded Successfully");
			} catch (Exception ex) {
				req.setAttribute("message", "File Upload Failed due to " + ex);
			}

		} else {
			req.setAttribute("message", "Sorry this Servlet only handles file upload request");
		}

		req.getRequestDispatcher("/index.jsp").forward(req, resp);

	}

}
