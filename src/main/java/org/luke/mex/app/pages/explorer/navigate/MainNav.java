package org.luke.mex.app.pages.explorer.navigate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.stream.Stream;

import org.luke.mex.app.pages.explorer.navigate.top.CategoryHeader;
import org.luke.mex.app.utils.fileutils.IconUtils;
import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.label.MultiText;
import org.luke.mex.gui.controls.scroll.ScrollBar;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class MainNav extends StackPane implements Styleable {
	private DoubleProperty iconSize;

	private Window window;
	private FlowPane content;

	private Consumer<File> load;

	private ScrollBar scrollBar;

	private DoubleConsumer setProgress;
	private Consumer<File> updatePathDisp;

	private static HashMap<File, Double> scrollCache = new HashMap<>();

	public MainNav(Window window) {
		this.window = window;

		iconSize = new SimpleDoubleProperty(46);

		iconSize.addListener((obs, ov, nv) -> content.getChildren().forEach(child -> {
			if (child instanceof FileEntry fe) {
				fe.updateSize(nv.doubleValue());
			}
		}));

		setAlignment(Pos.TOP_LEFT);

		content = new FlowPane();
		content.setPadding(new Insets(10));

		Runnable updateHgap = () -> {
			double paneWidth = content.getWidth() - 20;
			double nodeWidth = iconSize.get() + 40;
			int colCount = (int) (paneWidth / nodeWidth);
			double occupiedWidth = nodeWidth * colCount + 8;
			double hGap = (paneWidth - occupiedWidth) / (colCount - 1);
			content.setHgap(hGap);
		};

		content.needsLayoutProperty().addListener((obs, ov, nv) -> updateHgap.run());
		needsLayoutProperty().addListener((obs, ov, nv) -> updateHgap.run());
		content.getChildren().addListener((ListChangeListener<? super Node>) c -> updateHgap.run());

		content.minWidthProperty().bind(widthProperty());
		content.maxWidthProperty().bind(widthProperty());

		scrollBar = new ScrollBar(16, 5);
		scrollBar.install(this, content);

		scrollBar.positionProperty().addListener((obs, ov, nv) -> {
			if (loaded != null) {
				scrollCache.put(loaded, nv.doubleValue());
			}
		});

		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(widthProperty());
		clip.heightProperty().bind(heightProperty());

		setClip(clip);

		getChildren().addAll(content, scrollBar);

		scrollBar.positionProperty().addListener(e -> checkVisibility());
		content.needsLayoutProperty().addListener(e -> checkVisibility());

		applyStyle(window.getStyl());
	}
	
	private void checkVisibility() {
		double min = -content.getTranslateY() - iconSize.get() * 3;
		double max = min + getHeight() + iconSize.get() * 6;

		double minR = min / content.getHeight();
		double maxR = max / content.getHeight();

		int from = (int) (content.getChildren().size() * minR);
		int to = (int) (content.getChildren().size() * maxR);

		from = Math.max(0, from);
		to = Math.min(content.getChildren().size() - 1, to);

		for(int i = 0; i< from; i++) {
			if (content.getChildren().get(i) instanceof FileEntry fe) {
				fe.hide();
			}
		}
		
		for (int i = from; i <= to; i++) {
			if (content.getChildren().get(i) instanceof FileEntry fe) {
				fe.checkVisibiltiy(this);
			}
		}

		for(int i = to + 1; i< content.getChildren().size(); i++) {
			if (content.getChildren().get(i) instanceof FileEntry fe) {
				fe.hide();
			}
		}
	}

	public void setSetProgress(DoubleConsumer setProgress) {
		this.setProgress = setProgress;
	}

	public void setUpdatePathDisp(Consumer<File> updatePathDisp) {
		this.updatePathDisp = updatePathDisp;
	}

	private void setProgress(double progress) {
		if (setProgress != null) {
			setProgress.accept(progress);
		}
	}

	private void updatePathDisp(File file) {
		if (updatePathDisp != null) {
			updatePathDisp.accept(file);
		}
	}

	public void setIconSize(double size) {
		iconSize.set(size);
	}

	public void setLoad(Consumer<File> load) {
		this.load = load;
	}

	private File loaded;

	public File getLoaded() {
		return loaded;
	}

	private static MultiText emptyFolder;

	Thread th;

	public void load(File dir) {
		loaded = null;
		if (th != null && th.isAlive()) {
			th.interrupt();
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}

		setProgress(0);
		updatePathDisp(dir);

		scrollBar.positionProperty().set(0);
		content.getChildren().clear();
		setDisable(true);

		loaded = dir;

		th = new Thread() {
			@Override
			public void run() {

				if (dir.equals(new File(IconUtils.THIS_PC))) {
					ArrayList<FileEntry> folders = new ArrayList<>();
					ArrayList<FileEntry> drives = new ArrayList<>();

					for (String sf : IconUtils.systemFolders) {
						folders.add(createFileEntry(new File(sf)));
					}

					for (File root : File.listRoots()) {
						drives.add(createFileEntry(root));
					}

					Platform.runLater(() -> content.getChildren().add(new CategoryHeader(window, "Folders")));
					folders.forEach(fe -> Platform.runLater(() -> content.getChildren().add(fe)));

					CategoryHeader ch = new CategoryHeader(window, "Devices and drives");
					FlowPane.setMargin(ch, new Insets(10, 0, 0, 0));
					Platform.runLater(() -> content.getChildren().add(ch));

					drives.forEach(fe -> Platform.runLater(() -> content.getChildren().add(fe)));
				} else {
					loadFolder(dir);
				}

				Platform.runLater(() -> {
					setDisable(false);
					setProgress(0);
					checkVisibility();
				});
			}
		};

		th.start();
	}

	public void refresh() {
		load(loaded);
	}

	private void loadFolder(File dir) {
		setProgress(.05);
		List<File> files = dir.list() == null ? new ArrayList<>() : Stream.of(dir.listFiles()).sorted((o1, o2) -> {
			if (o1.isDirectory() && o2.isFile()) {
				return -1;
			}
			if (o1.isFile() && o2.isDirectory()) {
				return 1;
			}
			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		}).filter(file -> !file.getName().equalsIgnoreCase("desktop.ini")).toList();

		if (files.isEmpty()) {
			if (emptyFolder == null) {
				emptyFolder = new MultiText(window, "This folder appears to be empty...", new Font(12));
				emptyFolder.center();

				emptyFolder.minWidthProperty().bind(content.widthProperty().subtract(10));
				emptyFolder.maxWidthProperty().bind(content.widthProperty().subtract(10));

				emptyFolder.setPadding(new Insets(16, 0, 0, 0));

				emptyFolder.setFill(window.getStyl().get().getTextNormal());
			}
			Platform.runLater(() -> content.getChildren().add(emptyFolder));
		}

		int[] count = new int[] { 0 };
		files.forEach(file -> {
			try {
				FileEntry fe = createFileEntry(file);
				count[0]++;
				Platform.runLater(() -> {
					content.getChildren().add(fe);
					setProgress(((double) count[0] / files.size()) * .95 + .05);
				});
			} catch (NullPointerException | IllegalArgumentException x) {
				// DISCARD
			}
		});

		if (scrollCache.containsKey(dir)) {
			Platform.runLater(() -> scrollBar.positionProperty().set(scrollCache.get(dir)));
		}
	}

	private FileEntry createFileEntry(File file) {
		FileEntry fe = FileEntry.load(window, file);
		fe.updateSize(iconSize.get());
		fe.setLoadFolder(load);
		return fe;
	}

	@Override
	public void applyStyle(Style style) {
		scrollBar.setThumbFill(style.getBackgroundFloating());
		scrollBar.setTrackFill(style.getScrollbarAutoTrack());

		if (emptyFolder != null) {
			emptyFolder.setFill(style.getTextNormal());
		}
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}
