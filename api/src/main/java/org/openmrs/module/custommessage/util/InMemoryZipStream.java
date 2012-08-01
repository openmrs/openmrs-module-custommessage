/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.custommessage.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Convenient wrapper for creating in memory zip file which can include files and directories.
 * Actually, all data of zip file will be kept in memory and then can be obtained by invoking
 * <code>inMemoryZipStream.getByteArray()</code> method.
 */
public class InMemoryZipStream {
	
	/** byte array to hold zip data with */
	private ByteArrayOutputStream bos = null;
	
	/** zip output stream to be used for managing zip file structure */
	private ZipOutputStream zos = null;
	
	/**
	 * Creates new in-memory zip stream
	 */
	public InMemoryZipStream() {
		this.bos = new ByteArrayOutputStream();
		this.zos = new ZipOutputStream(this.bos);
	}
	
	/**
	 * Creates new folder inside zip file represented by this stream. Do not forget to call
	 * {@link InMEmoryZipEntry#closeFolder()} to close this directory entry
	 * 
	 * @param folderName the name of folder to be created within zip file
	 * @throws IOException if I/O error occurs
	 */
	public void enterFolder(String folderName) throws IOException {
		this.zos.putNextEntry(new ZipEntry(folderName.concat(File.separator)));
	}
	
	/**
	 * Closes folder open recently within zip file by calling
	 * {@link InMemoryZipStream#enterFolder(String)} method
	 * 
	 * @throws IOException if I/O error occurs
	 */
	public void closeFolder() throws IOException {
		this.zos.closeEntry();
	}
	
	/**
	 * Adds given byte data as file into zip file represented by this stream
	 * 
	 * @param fileName the name of file to be created inside zip file
	 * @param data the data to be stored as file
	 * @throws IOException if I/O error occurs
	 */
	public void add(String fileName, byte[] data) throws IOException {
		this.zos.putNextEntry(new ZipEntry(fileName));
		this.zos.write(data);
		this.zos.closeEntry();
	}
	
	/**
	 * Adds properties as zip entry
	 * 
	 * @param fileName the name of file to hold properties inside zip entry
	 * @param properties the properties to be stored
	 * @throws IOException if I/O error occurs
	 */
	public void add(String fileName, Properties properties) throws IOException {
		this.zos.putNextEntry(new ZipEntry(fileName));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		properties.store(bos, null);
		this.zos.write(bos.toByteArray());
		this.zos.closeEntry();
	}
	
	/**
	 * Closes this in-memory stream
	 * 
	 * @throws IOException if stream closing error occurs
	 */
	public void close() throws IOException {
		zos.flush();
		bos.flush();
        zos.close();
        bos.close();
	}
	
	/**
	 * Converts zip stream to byte array
	 * 
	 * @return array of bytes that represents this in-memory stream
	 */
	public byte[] toByteArray() {
		return this.bos.toByteArray();
	}
}
