package org.luke.mex.app.pages.explorer.navigate;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import org.luke.mex.app.utils.fileutils.DeleteUtils;
import org.luke.mex.app.utils.fileutils.RenameUtils;
import org.luke.mex.app.utils.fileutils.TypeUtils;
import org.luke.mex.app.utils.fileutils.IconUtils;
import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.popup.Direction;
import org.luke.mex.gui.controls.popup.context.ContextMenu;
import org.luke.mex.gui.controls.popup.context.items.MenuItem;
import org.luke.mex.gui.controls.popup.context.items.MenuMenuItem;
import org.luke.mex.gui.controls.popup.tooltip.Tooltip;
import org.luke.mex.gui.controls.space.FixedHSpace;
import org.luke.mex.gui.factory.Backgrounds;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FileEntry extends VBox implements Styleable {
//	private static HashMap<File, FileEntry> cache = new HashMap<>();

	public static FileEntry load(Window window, File file) {
//		FileEntry found = cache.get(file);
//
//		if (found == null) {
//			found = new FileEntry(window, file);
//			cache.put(file, found);
//		}

		return new FileEntry(window, file);
	}

	private static ContextMenu cm;
	private static MenuItem open;
	private static MenuMenuItem openWith;
	private static MenuItem cut;
	private static MenuItem copy;
	private static MenuItem rename;
	private static MenuItem delete;
	private static MenuItem format;
	private static MenuItem properties;

	private Window window;
	private File file;
	private ImageView fileIcon;
	private Label name;
	private Tooltip ttip;
	private Consumer<File> loadFolder;

	private FileEntry(Window window, File file) {
		super(8);
		setAlignment(Pos.CENTER);
		setPadding(new Insets(10));

		this.window = window;
		this.file = file;

		fileIcon = new ImageView();

		setOnContextMenuRequested(e -> showMenu());

		setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case ENTER: {
				open();
				break;
			}
			case DELETE: {
				delete();
				break;
			}
			default:
				break;
			}
		});

		name = new Label();
		name.setFont(new Font(12).getFont());

		getChildren().addAll(fileIcon, name);

		fileIcon.opacityProperty().bind(Bindings.when(disabledProperty()).then(.5).otherwise(1));

		setOnMouseClicked(e -> {
			if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
				open();
			}
		});

		ttip = new Tooltip(window, "", Direction.UP);
		ttip.setFont(new Font(12));
		ttip.setOffset(-10);

		updateName();

		Tooltip.install(this, ttip);

		parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> obs, Parent ov, Parent nv) {
				if (ov != null && nv == null) {
					Tooltip.uninstall(FileEntry.this, ttip);

					parentProperty().removeListener(this);
				}
			}
		});

		setFocusTraversable(true);
		setOnMousePressed(e -> requestFocus());

		applyStyle(window.getStyl());
	}

	private void open() {
		if (file.isDirectory()) {
			if (loadFolder != null) {
				loadFolder.accept(file);
			}
		} else {
			try {
				Desktop.getDesktop().open(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void rename() {
		RenameUtils.rename(window, file, newFile -> {
			this.file = newFile;
			updateSize(fileIcon.getFitWidth());
			updateName();
		});
	}
	
	private void showMenu() {
		if (cm == null) {
			cm = new ContextMenu(window);
			open = new MenuItem(cm, "Open");
			openWith = new MenuMenuItem(cm, "Open with");
			cut = new MenuItem(cm, "Cut");
			copy = new MenuItem(cm, "Copy");
			rename = new MenuItem(cm, "Rename");
			delete = new MenuItem(cm, "Delete");
			format = new MenuItem(cm, "Format");
			properties = new MenuItem(cm, "Properties");

			cm.addMenuItem(open);
			cm.addMenuItem(openWith);
			cm.separate();
			cm.addMenuItem(cut);
			cm.addMenuItem(copy);
			cm.separate();
			cm.addMenuItem(rename);
			cm.addMenuItem(delete);
			cm.addMenuItem(format);
			cm.addMenuItem(properties);
		}

		if (file.isDirectory()) {
			cm.disable(openWith);

			if (file.getParent() == null) {
				cm.disable(delete);
				cm.disable(cut);

				cm.enableBefore(format, rename);
			} else {
				cm.enableBefore(cut, copy);
				cm.enableBefore(delete, properties);

				cm.disable(format);
			}
		} else {
			cm.enableAfter(openWith, open);
			cm.enableBefore(cut, copy);
			cm.enableBefore(delete, properties);

			cm.disable(format);

			openWith.clear();

			TypeUtils.canOpen(file).forEach(opener -> {
				new Thread(() -> {
					opener.prepare();
					MenuItem item = new MenuItem(openWith.getSubMenu(), opener.getDescription());
					item.setAction(() -> opener.open(file));
					
					item.getChildren().add(0, new FixedHSpace(10));
					item.getChildren().add(0, new ImageView(opener.getIcon()));

					Platform.runLater(() -> openWith.addMenuItem(item));
				}).start();
			});
		}

		open.setAction(this::open);
		rename.setAction(this::rename);
		delete.setAction(this::delete);
		requestFocus();

		cm.showPop(this, Direction.RIGHT_DOWN, -5);
	}

	private void delete() {
		DeleteUtils.delete(window, this.file, () -> ((FlowPane) getParent()).getChildren().remove(this));
	}

	private String displayName() {
		boolean nullParent = file.getParent() == null;
		boolean isFile = (file.getName().indexOf(".") > 1 && file.isFile());
		String nameString = null;

		if (nullParent) {
			nameString = "Local Disk (" + file.getAbsolutePath().replace("\\", "") + ")";
		} else if (isFile) {
			nameString = file.getName().substring(0, file.getName().lastIndexOf("."));
		} else {
			nameString = file.getName();
		}

		return nameString;
	}

	public void setLoadFolder(Consumer<File> loadFolder) {
		this.loadFolder = loadFolder;
	}

	public void updateSize(double size) {
		if (fileIcon.getFitWidth() == size && fileIcon.getFitHeight() == size) {
			return;
		}

		fileIcon.setFitWidth(size);
		fileIcon.setFitHeight(size);

		setMaxWidth(size + 40);
		setMinWidth(size + 40);
		setMaxHeight(size + 40);
		setMinHeight(size + 40);

		new Thread() {
			@Override
			public void run() {
				Image img = IconUtils.typeIcon(file, (int) size);
				Platform.runLater(() -> fileIcon.setImage(img));
			}
		}.start();
	}

	public boolean checkVisibiltiy(StackPane parent) {
		Bounds paneBounds = parent.localToScene(parent.getBoundsInLocal());
		Bounds nodeBounds = localToScene(getBoundsInLocal());

		boolean visible = paneBounds.intersects(nodeBounds);

		if (visible) {
			show();
		} else {
			hide();
		}

		return visible;
	}

	public void hide() {
		setVisible(false);
		getChildren().clear();
	}

	public void show() {
		setVisible(true);
		getChildren().setAll(fileIcon, name);
	}

	private void updateName() {
		String nameString = displayName();
		name.setText(nameString);
		ttip.setText(file.getParent() == null ? nameString : file.getName());
	}

	public File getFile() {
		return file;
	}

	@Override
	public void applyStyle(Style style) {
		name.setTextFill(style.getTextNormal());

		backgroundProperty().bind(Bindings.when(pressedProperty())
				.then(Backgrounds.make(style.getBackgroundModifierActive(), 10))
				.otherwise(Bindings.when(hoverProperty()).then(Backgrounds.make(style.getBackgroundModifierHover(), 10))
						.otherwise(Bindings.when(focusedProperty())
								.then(Backgrounds.make(style.getBackgroundModifierSelected(), 10))
								.otherwise(Background.EMPTY))));
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}
