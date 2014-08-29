package com.orm.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileUtil {
	
	public static boolean writeStreamToFile(InputStream from, File to_file) {
		OutputStream to = null; // Stream to write to destination
		try {
			to = new BufferedOutputStream(new FileOutputStream(to_file)); // Create output stream
			byte[] buffer = new byte[4096]; // To hold file contents
			int bytes_read; // How many bytes in buffer

			while ((bytes_read = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytes_read); // write
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// Always close the streams, even if exceptions were thrown
		finally {
			if (from != null) {
				try {
					from.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			if (to != null) {
				try {
					to.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
}
