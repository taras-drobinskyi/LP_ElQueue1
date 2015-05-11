package Dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Advertisement {
	@XmlElement
	private String value;

	@XmlAttribute
	private String contentType;

	public String getContentType() {
		return contentType;
	}

	@XmlTransient
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getValue() {
		return value;
	}

	@XmlTransient
	public void setValue(String value) {
		this.value = value;
	}
}
