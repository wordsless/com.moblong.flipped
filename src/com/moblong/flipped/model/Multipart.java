package com.moblong.flipped.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * @author www.codejava.net
 *
 */
public class Multipart {
	
	private final String boundary;
	private static final String LINE_FEED = "\r\n";
	//private ByteArrayOutputStream bout;
	private OutputStream output;
	private PrintWriter writer;

	/**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     * @param requestURL
     * @param charset
     * @throws IOException
     */
	public Multipart(final OutputStream output, final String boundary) throws IOException {
		// creates a unique boundary based on time stamp
		this.output = output;
		this.boundary = boundary;
		//bout     = new ByteArrayOutputStream();
		writer   = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
	}
	
	public final String getBoundary() {
		return boundary;
	}
	
	/**
	 * adds a form field to the request
     * @param name field name
     * @param value field value
     */
	public void addFormField(String name, String value) {
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
		writer.append("Content-Type: text/plain; charset=" + "UTF-8").append(LINE_FEED);
		writer.append(LINE_FEED);
		writer.append(value).append(LINE_FEED);
		writer.flush();
	}

	/**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
	public void addFilePart(String fieldName, File file, String mine) throws IOException {
		String fileName = file.getName();
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
		writer.append("Content-Type: "+ mine).append(LINE_FEED);
		writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		writer.append(LINE_FEED);
		writer.flush();
		
		InputStream input = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
		output.flush();
		input.close();

		writer.append(LINE_FEED);
		writer.flush();
	}
	
	/**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
	public void addHeaderField(String name, String value) {
		writer.append(name + ": " + value).append(LINE_FEED);
		writer.flush();
	}

	/**
     * Completes the request and receives response from the server.
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
	public void commit() throws IOException {
		//writer.append(LINE_FEED);
		writer.append("--" + boundary + "--").append(LINE_FEED);
		writer.flush();
		writer.close();
		writer = null;
	}
}