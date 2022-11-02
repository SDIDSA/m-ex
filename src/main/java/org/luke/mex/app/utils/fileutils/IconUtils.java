package org.luke.mex.app.utils.fileutils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.luke.mex.gui.controls.image.ImageProxy;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class IconUtils {
	private IconUtils() {
	}

	private static String userHome = System.getProperty("user.home");

	public static final String THIS_PC = "This PC";

	private static final String DESKTOP_FOLDER = userHome + "/Desktop";
	private static final String DOCUMENTS_FOLDER = userHome + "/Documents";
	private static final String DOWNLOADS_FOLDER = userHome + "/Downloads";
	private static final String MUSIC_FOLDER = userHome + "/Music";
	private static final String PICTURES_FOLDER = userHome + "/Pictures";
	private static final String VIDEOS_FOLDER = userHome + "/Videos";

	private static final String APPLICATION = "application";
	private static final String SHORTCUT = "shortcut";
	private static final String LINK = "link";

	private static final String IMAGE = "image";
	private static final String VIDEO = "video";
	private static final String AUDIO = "audio";
	private static final String ARCHIVE = "archive";
	private static final String DOCUMENT = "document";
	private static final String RTF = "rtf";
	private static final String CODE = "code";
	private static final String PDF = "pdf";
	private static final String VECTOR = "vector";
	private static final String DLL = "dll";

	public static final String[] systemFolders = new String[] { DESKTOP_FOLDER, DOCUMENTS_FOLDER, DOWNLOADS_FOLDER,
			MUSIC_FOLDER, PICTURES_FOLDER, VIDEOS_FOLDER };

	static HashMap<String, String> typeMap = new HashMap<>();

	static {
		typeMap.put("exe", APPLICATION);

		typeMap.put("lnk", SHORTCUT);

		typeMap.put("url", LINK);

		typeMap.put("png", IMAGE);
		typeMap.put("jpg", IMAGE);
		typeMap.put("jpeg", IMAGE);
		typeMap.put("ico", IMAGE);
		typeMap.put("webp", IMAGE);

		typeMap.put("mp4", VIDEO);
		typeMap.put("mkv", VIDEO);
		typeMap.put("m4v", VIDEO);
		typeMap.put("webm", VIDEO);

		typeMap.put("mp3", AUDIO);
		typeMap.put("m4a", AUDIO);
		typeMap.put("aac", AUDIO);
		typeMap.put("wav", AUDIO);

		typeMap.put("doc", DOCUMENT);
		typeMap.put("txt", DOCUMENT);
		typeMap.put("log", DOCUMENT);

		typeMap.put("docx", RTF);

		typeMap.put("zip", ARCHIVE);
		typeMap.put("7z", ARCHIVE);
		typeMap.put("rar", ARCHIVE);
		typeMap.put("gz", ARCHIVE);
		typeMap.put("bz2", ARCHIVE);

		typeMap.put("html", CODE);
		typeMap.put("htm", CODE);
		typeMap.put("js", CODE);
		typeMap.put("css", CODE);
		typeMap.put("java", CODE);

		typeMap.put("pdf", PDF);

		typeMap.put("svg", VECTOR);

		typeMap.put("dll", DLL);
	}

	private static String resolve(File file) {
		if (file.equals(new File(THIS_PC))) {
			return "monitor";
		}

		if (file.isDirectory()) {
			return resolveDir(file);
		} else {
			String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();

			String type = typeMap.get(ext);
			return type == null ? "unknown" : type;
		}
	}
	
	private static String resolveDir(File dir) {
		if (dir.getParent() == null) {
			if (dir.getAbsolutePath().replace("\\", "").equalsIgnoreCase(System.getenv("SystemDrive"))) {
				return "system-drive";
			} else {
				return "drive";
			}
		}

		for (String sf : systemFolders) {
			if (dir.equals(new File(sf))) {
				return dir.getName().toLowerCase();
			}
		}
		return "directory";
	}

	private static int[] sizes = new int[] { 24, 32, 64, 128, 256 };

	private static HashMap<String, Image> cache = new HashMap<>();

	public static Image typeIcon(File file, int size) {
		String type = IconUtils.resolve(file);
		Image found = null;
		if (type.equals(SHORTCUT)) {
			found = cache.get(file.getName() + "_" + size);
			if (found == null) {
				File targetFile = file;

				Icon icon = FileSystemView.getFileSystemView().getSystemIcon(targetFile, 256, 256);
				BufferedImage bImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
						BufferedImage.TYPE_INT_ARGB);

				Graphics2D graphics = bImg.createGraphics();
				graphics.drawImage(((ImageIcon) icon).getImage(), 0, 0, null);
				graphics.dispose();

				found = ImageProxy.resize(SwingFXUtils.toFXImage(bImg, null), size);

				cache.put(file.getName() + "_" + size, found);
			}
		} else {
			found = cache.get(type + "_" + size);
			if (found == null) {
				int s = size;
				for (int ts : sizes) {
					if (ts >= s) {
						s = ts;
						break;
					}
				}
				found = ImageProxy.loadResize(type, s, size);
				cache.put(type + "_" + size, found);
			}
		}

		return found;
	}
	
	public static Image systemIcon(File file, int size) {
		Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file, 256, 256);
		BufferedImage bImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = bImg.createGraphics();
		graphics.drawImage(((ImageIcon) icon).getImage(), 0, 0, null);
		graphics.dispose();

		return ImageProxy.resize(SwingFXUtils.toFXImage(bImg, null), size);
	}
}
