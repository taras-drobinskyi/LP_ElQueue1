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

import main.XMLMediaContent;

import org.apache.log4j.Logger;

import Dto.Advertisement;
import Dto.WebServiceInfo;

public class AdvServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	File uploads = new File("uploads");
	Logger log = Logger.getLogger(AdvServlet.class.getName());


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Map<String, String[]> parameters = req.getParameterMap();
		String[] parameterValues = req.getParameterValues("textInfo");

		List<String> resultList = generateXML(parameterValues);
		// List<String> resultList = JAXBmarshal(parameters);
		if (!resultList.isEmpty()) {
			resp.setHeader("ticker", "true");
		} else {
			resp.setHeader("ticker", "false");

		}

		req.setAttribute("message", resultList);
		req.getRequestDispatcher("/result.jsp").forward(req, resp);

	}

	/**
	 * generates list of input messages and saves it to XML
	 * 
	 * @param parameterValues
	 * @return
	 * @throws IOException
	 */
	private List<String> generateXML(String[] parameterValues) throws IOException {

		XMLMediaContent dataMediaContent = new XMLMediaContent();
		dataMediaContent.getMessageList();
		List<String> messagesList = new ArrayList<String>();

		for (int i = 0; i < parameterValues.length; i++) {
			if (!parameterValues[i].isEmpty()) {
				messagesList.add(parameterValues[i]);
			}
		}
		dataMediaContent.setMessageList(messagesList);
		dataMediaContent.saveXMLDocument();
		if (!messagesList.isEmpty()) {
			log.debug(messagesList.size() + " new ticker(s)");
		} else {
			log.debug("No new tickers");
		}

		return messagesList;



	}

	/**
	 * generates XML file using JAXB
	 * 
	 * @param parameters
	 * @return
	 * @throws IOException
	 */
	private List<String> JAXBmarshal(Map<String, String[]> parameters) throws IOException {

		uploads.mkdir();
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
			log.debug("Xml file was generated using JAXB");

			JAXB.marshal(wsi, fos);
			fos.flush();
			fos.close();

		}
		return resultList;
	}

}
