package Dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;

public class WebServiceInfo {
	private List<Advertisement> advertisement = new ArrayList<>();

	@XmlElementWrapper(name = "commercials")
	public List<Advertisement> getAdvertisement() {
		return advertisement;
	}

	public void setAdvertisement(List<Advertisement> advertisement) {
		this.advertisement = advertisement;
	}

}
