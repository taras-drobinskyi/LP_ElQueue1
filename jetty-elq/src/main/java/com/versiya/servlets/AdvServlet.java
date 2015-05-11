package com.versiya.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;

import Dto.Advertisement;
import Dto.WebServiceInfo;

public class AdvServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	File uploads = new File("uploads");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		uploads.mkdir();
		Map<String, String[]> parameters = req.getParameterMap();
		WebServiceInfo wsi = JAXBmarshal(parameters);

		req.setAttribute("message", "Sorry this Servlet only handles file upload request");
		req.getRequestDispatcher("/index.jsp").forward(req, resp);

	}

	private WebServiceInfo JAXBmarshal(Map<String, String[]> parameters) throws IOException {
		WebServiceInfo wsi = new WebServiceInfo();

		for (Entry<String, String[]> entry : parameters.entrySet()) {

			String key = entry.getKey();
			String[] value = entry.getValue();
			System.out.println("Key is " + key);
			for (int i = 0; i < value.length; i++) {
				Advertisement adv = new Advertisement();
				adv.setContentType(key);
				adv.setValue(value[i]);
				System.out.println("Value is " + value[i]);
				wsi.getAdvertisement().add(adv);
			}
		}

		try {
			JAXB.marshal(wsi, new FileOutputStream(uploads.getCanonicalPath() + File.separator + "Ads.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return wsi;
	}

}
