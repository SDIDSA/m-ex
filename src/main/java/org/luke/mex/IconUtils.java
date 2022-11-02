package org.luke.mex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.luke.mex.gui.controls.image.ImageProxy;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class IconUtils extends Application {

	@Override
	public void start(Stage ps) throws Exception {

		Button add = new Button("Add");

		Button gen = new Button("Generate");

		VBox root = new VBox(10, add, gen);
		root.setPadding(new Insets(10));

		add.prefWidthProperty().bind(root.widthProperty().subtract(20));
		gen.prefWidthProperty().bind(root.widthProperty().subtract(20));
		gen.setDisable(true);

		Scene scene = new Scene(root, 300, 80);

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("Image file", "*.png"));
		add.setOnAction(e -> {
			List<File> files = fc.showOpenMultipleDialog(ps);
			if (files != null) {
				files.forEach(file -> {
					root.getChildren().add(1, new SourceImage(file));
					
				});
			}
		});
		
		root.getChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(Change<? extends Node> c) {
				ps.setHeight(root.getChildren().size() * 35.0 + 51);
				
				if(root.getChildren().size() <= 2) {
					gen.setDisable(true);
				}else {
					gen.setDisable(false);
				}
			}
		});
		
		int[] sizes = new int[] {24,32,64,128,256};
		gen.setOnAction(e -> {
			root.getChildren().forEach(child -> {
				if(child instanceof SourceImage sourceImage) {
					try {
						Image source = new Image(new FileInputStream(sourceImage.getSource()));
						for(int size : sizes) {
							Image sized = ImageProxy.resize(source, size);
							
							File saveTo = new File("D:\\perso\\m-ex\\src\\main\\resources\\images\\icons\\" + sourceImage.getName() + "_" + size + ".png");
							
							ImageIO.write(SwingFXUtils.fromFXImage(sized, null), "png", saveTo);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		});

		ps.setScene(scene);
		ps.show();
	}

	private static class SourceImage extends HBox {
		private File source;
		private ImageView preview;
		private TextField name;

		public SourceImage(File source) {
			super(10);
			setAlignment(Pos.CENTER);
			
			this.source = source;
			
			preview = new ImageView();

			try {
				preview.setImage(ImageProxy.resize(new Image(new FileInputStream(source)), 20));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			name = new TextField(source.getName().substring(0, source.getName().lastIndexOf(".")));
			name.setPromptText("Icon name...");

			Button remove = new Button("remove");
			remove.setMinWidth(100);

			remove.setOnAction(e -> ((VBox) getParent()).getChildren().remove(this));

			getChildren().addAll(preview, name, remove);
		}

		public File getSource() {
			return source;
		}
		
		public String getName() {
			return name.getText();
		}
	}

}
