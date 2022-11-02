package org.luke.mex.app.utils.fileutils;

public class PathUtils {
	private PathUtils() {}
	
	public static String resolveValueWithEnvVars(String value) {
		StringBuilder sb = new StringBuilder();

		StringBuilder replace = new StringBuilder();

		boolean opened = false;
		for (char c : value.toCharArray()) {
			if (c == '%') {
				if (opened) {
					sb.append(System.getenv(replace.toString()));
					replace = new StringBuilder();

					opened = false;
				} else {
					opened = true;
				}
			} else {
				if (opened) {
					replace.append(c);
				} else {
					sb.append(c);
				}
			}
		}

		return sb.toString();
	}
}
