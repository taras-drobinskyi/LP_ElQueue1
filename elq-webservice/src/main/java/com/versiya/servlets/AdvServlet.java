package com.versiya.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		uploads.mkdir();
		Map<String, String[]> parameters = req.getParameterMap();
		List<String> resultList = JAXBmarshal(parameters);

		req.setAttribute("message", resultList);
		req.getRequestDispatcher("/result.jsp").forward(req, resp);

	}

	private List<String> JAXBmarshal(Map<String, String[]> parameters) throws IOException {
		WebServiceInfo wsi = new WebServiceInfo();
		List<String> resultList = new ArrayList<>();
		for (Entry<String, String[]> entry : parameters.entrySet()) {
			
			String key = entry.getKey();
			String[] value = entry.getValue();
			for (int i = 0; i < value.length; i++) {
				if (!value[i].isEmpty()) {
				Advertisement adv = new Advertisement();
				adv.setContentType(key);
				adv.setValue(value[i]);
				wsi.getAdvertisement().add(adv);
				resultList.add(value[i]);
				}
			}
		}
		if (!wsi.getAdvertisement().isEmpty()) {
			FileOutputStream fos = new FileOutputStream(uploads.getCanonicalPath() + File.separator + "Ads.xml");

			JAXB.marshal(wsi, fos);
			fos.flush();
			fos.close();

		}
		return resultList;
	}

}
