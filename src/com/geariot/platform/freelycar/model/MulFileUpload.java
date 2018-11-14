/**
 * 
 */
package com.geariot.platform.freelycar.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author mxy940127
 *
 */
public class MulFileUpload {
	
	private List<MultipartFile> files;
	
	public List<MultipartFile> getFiles() {
		return files;
	}
	
	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}
}
