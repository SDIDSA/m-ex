package org.luke.mex.app.utils.fileutils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import org.luke.mex.app.utils.fileutils.ApplicationsUtils.Opener;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class TypeUtils {
	private TypeUtils() {
	}

	private static HashMap<String, List<Opener>> openWithCache = new HashMap<>();

	public static List<Opener> canOpen(File file) {
		String ext = ext(file.getName());
		List<Opener> found = openWithCache.get(ext);

		if (found == null) {
			ArrayList<Opener> res = new ArrayList<>();

			try {
				String regPath = "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\."
						+ ext(file.getName()) + "\\OpenWithList\\";
				TreeMap<String, Object> values = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, regPath);

				System.out.println(values);

				values.keySet().forEach(key -> {
					if (key.length() == 1) {
						Opener exec = ApplicationsUtils.getOpener((String) values.get(key));
						if (exec != null)
							res.add(exec);
					}
				});
			} catch (Exception x) {
				System.out.println(x.getClass().getSimpleName());
			}

			found = res;
			openWithCache.put(ext, found);
		}

		return found;
	}

	private static String ext(String filename) {
		Optional<String> opt = Optional.ofNullable(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));

		return opt.isPresent() ? opt.get() : "";
	}
}
