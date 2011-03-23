package is.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lopes
 */
public class URLCaller {
	private URL flickUrl;
	private URLConnection connection;

	public URLCaller(String urlIn) throws IOException
	{
		// Query Flcikr, allocating a BufferedReader for the resultign page
		flickUrl = new URL(urlIn);
		connection = flickUrl.openConnection();
		connection.setRequestProperty("User-Agent", "");
	}

	public void print() throws IOException
	{
		BufferedReader br =	new BufferedReader(new InputStreamReader(connection.getInputStream()));
		// Printout the resulting page
		String line = null;
		do
		{
			line = br.readLine();
			if (line != null)
				System.out.println(line);
		} while (line != null);
		br.close();

	}

	public String getPage() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		BufferedReader br =	new BufferedReader(new InputStreamReader(connection.getInputStream()));
		// Printout the resulting page
		String line = null;
		do
		{
			line = br.readLine();
			if (line != null)
				sb.append(line);
		} while (line != null);
		br.close();

		return sb.toString();

	}

}
