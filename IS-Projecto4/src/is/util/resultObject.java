/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package is.util;

import java.io.Serializable;

/**
 *
 * @author lopes
 */
public class resultObject implements Serializable{

	String xml, html;

	public resultObject(String xml, String html) {
		this.xml = xml;
		this.html = html;
	}

	public String getHtml() {
		return html;
	}

	public String getXml() {
		return xml;
	}

}
