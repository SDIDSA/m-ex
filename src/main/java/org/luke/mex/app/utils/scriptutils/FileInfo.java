package org.luke.mex.app.utils.scriptutils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import org.luke.mex.app.utils.Command;

public class FileInfo {
	private static HashMap<File, FileInfo> metaCache = new HashMap<>();
	
	public static FileInfo get(File file) {
		FileInfo found = metaCache.get(file);
		
		if(found == null) {
			found = new FileInfo(file);
			metaCache.put(file, found);
		}
		
		return found;
	}
	
	private HashMap<String, String> data;

	private FileInfo(File file) {
		data = new HashMap<>();

		try {
			File script = new File(URLDecoder.decode(getClass().getResource("/scripts/meta.exe").getFile(), "utf-8"));

			new Command(line -> {
				String[] parts = line.split(":");
				data.put(parts[0].trim(), parts[1].trim());
			}, "cmd.exe", "/C", "meta \"" + file.getAbsolutePath() + "\"").execute(script.getParentFile()).waitFor();
		} catch (UnsupportedEncodingException | InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
	
	public String get(String key) {
		return data.get(key);
	}

}
