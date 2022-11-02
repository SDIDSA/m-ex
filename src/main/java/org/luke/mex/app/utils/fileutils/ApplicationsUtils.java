package org.luke.mex.app.utils.fileutils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.luke.mex.app.utils.Command;
import org.luke.mex.app.utils.scriptutils.FileInfo;
import org.luke.mex.gui.controls.image.ImageProxy;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ApplicationsUtils {
	private ApplicationsUtils() {
	}

	private static HashMap<String, Opener> applications = new HashMap<>();

	public static void init() {
		new Thread(() -> {
			try {
				String appPaths = "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\App Paths";
				String[] appPathKeys = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, appPaths);

				for (String key : appPathKeys) {

					String[] possibles = new String[] { "Path", "App Path", "" };

					String preLoc = null;

					for (String possible : possibles) {
						try {
							String valPath = "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\App Paths\\"
									+ key;
							preLoc = PathUtils.resolveValueWithEnvVars(
									Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, valPath, possible));
							String location = preLoc + (preLoc.endsWith(".exe") ? "" : ("\\" + key));

							applications.put(key.toLowerCase(), new Opener(new File(location)));

							break;
						} catch (Exception x) {
							// IGNORE
						}
					}
				}

				String uninstallPath = "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall";
				String[] uninstallKeys = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, uninstallPath);

				for (String key : uninstallKeys) {

					try {

						String valPath = "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key;
						String val = PathUtils.resolveValueWithEnvVars(Advapi32Util
								.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, valPath, "UninstallString"));
						File parent = new File(
								PathUtils.resolveValueWithEnvVars(val.replace("\"", "")).split(".exe")[0] + ".exe");
						parent = parent.getParentFile();
						getExes(parent).forEach(exe -> {
							applications.put(exe.getName().toLowerCase(), new Opener(exe));
						});
					} catch (Exception x) {
						// IGNORE
					}
				}
			} catch (Exception x) {
				System.out.println(x.getClass().getSimpleName());
			}

			File r = new File("").getParentFile();
			try {
				new Command(location -> {
					if (location.contains("InstallLocation") || location.contains("-") || location.isBlank()) {
						return;
					}
					String name = location.substring(location.lastIndexOf("\\") + 1).split("_")[0];
					File manifest = new File(location.trim() + "\\AppxManifest.xml");
					applications.put(name, new Opener(name, manifest));

				}, "powershell.exe", "/c", "Get-AppxPackage | Select InstallLocation").execute(r).waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}

			applications.forEach((key, value) -> {
				System.out.println(key + " -> " + value.manifest);
			});
		}).start();
	}

	private static List<File> getExes(File dir) {
		ArrayList<File> result = new ArrayList<>();

		for (File f : dir.listFiles()) {
			if (f.getName().toLowerCase().endsWith(".exe")) {
				result.add(f);
			}
		}

		return result;
	}

	public static Opener getOpener(String name) {
		System.out.println("getting opener for " + name);
		for (Entry<String, Opener> key : applications.entrySet()) {
			if (key.getValue().match(name)) {
				System.out.println("found at " + key.getKey());
				return key.getValue();
			}
		}
		return null;
	}

	public static class Opener {
		private String id;
		private String description;
		private Image icon;
		private Consumer<File> open;

		private File exe;
		private File manifest;

		public Opener(String id, String description, Image icon, Consumer<File> open) {
			this.id = id;
			this.description = description;
			this.icon = icon;
			this.open = open;
		}

		public Opener(File exe) {
			this.id = exe.getName().toLowerCase();
			this.exe = exe;
		}

		public Opener(String id, File manifest) {
			this.id = id;
			this.manifest = manifest;
		}

		public Image getIcon() {
			return icon;
		}

		public void open(File file) {
			open.accept(file);
		}

		public String getDescription() {
			return description;
		}

		public boolean match(String name) {
			return name.toLowerCase().contains(id.toLowerCase());
		}

		public void prepare() {
			if (description == null) {
				if (exe != null) {
					prepareFromExe();
				}

				if (manifest != null) {
					prepareFromManifest();
				}
			}
		}

		private void prepareFromExe() {
			icon = IconUtils.systemIcon(exe, 16);
			FileInfo info = FileInfo.get(exe);
			String desc = info.get("File Description");
			this.description = desc == null ? id : desc;
			open = file -> new Command("cmd", "/c", exe.getName() + " \"" + file.getAbsolutePath() + "\"")
					.execute(exe.getParentFile());
		}

		private void prepareFromManifest() {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();

				Document document = builder.parse(manifest);

				String desc = document.getElementsByTagName("DisplayName").item(0).getTextContent();

				description = desc.contains("ms-resource:") ? id.substring(id.lastIndexOf(".") + 1) : desc;

				String icon = document.getElementsByTagName("Logo").item(0).getTextContent();

				String iconName = icon.substring(icon.indexOf("\\") + 1, icon.lastIndexOf("."));
				String iconFolder = icon.substring(0, icon.lastIndexOf("\\"));

				File imageParent = new File(manifest.getParentFile() + "\\" + iconFolder);

				File ico = Stream.of(imageParent.listFiles())
						.filter(f -> f.getName().startsWith(iconName) && f.getName().contains("scale-200"))
						.sorted((o1, o2) -> Integer.compare(o1.getName().length(), o2.getName().length())).toList()
						.get(0);

				this.icon = ImageProxy.resize(SwingFXUtils.toFXImage(autoCrop(ImageIO.read(ico)), null), 16);

				String[] parts = manifest.getParent().split("_");
				String appId = parts[parts.length - 1];

				open = file -> new Command("powershell.exe", "/c",
						"start \"" + file.getAbsolutePath() + "\" \"shell:AppsFolder\\" + id + "_" + appId + "!App\"")
						.execute(file.getParentFile());

			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static BufferedImage autoCrop(BufferedImage sourceImage) {
		int left = 0;
		int right = 0;
		int top = 0;
		int bottom = 0;
		boolean firstFind = true;
		for (int x = 0; x < sourceImage.getWidth(); x++) {
			for (int y = 0; y < sourceImage.getWidth(); y++) {
				Color c = new Color(sourceImage.getRGB(x, y), true);
				if (c.getAlpha() != 0) {
					// we walk from left to right, thus x can be applied as left on first finding
					if (firstFind) {
						left = x;
					}

					// update right on each finding, because x can grow only
					right = x;

					// on first find apply y as top
					if (firstFind) {
						top = y;
					} else {
						// on each further find apply y to top only if a lower has been found
						top = Math.min(top, y);
					}

					// on first find apply y as bottom
					if (bottom == 0) {
						bottom = y;
					} else {
						// on each further find apply y to bottom only if a higher has been found
						bottom = Math.max(bottom, y);
					}
					firstFind = false;
				}
			}
		}

		return sourceImage.getSubimage(left, top, right - left, bottom - top);
	}

	public static void open(File file, AppxPackage app) {
		//DO THE MAGIC !!
	}

	static class AppxPackage {
		private String appName;
		private String packageFamilyName;
		private String appId;

		public AppxPackage(String appName, String packageFamilyName, String appId) {
			this.appName = appName;
			this.packageFamilyName = packageFamilyName;
			this.appId = appId;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getPackageFamilyName() {
			return packageFamilyName;
		}

		public void setPackageFamilyName(String packageFamilyName) {
			this.packageFamilyName = packageFamilyName;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}
	}
}
