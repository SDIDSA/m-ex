package org.luke.mex;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class FPAutoSpace extends Application {
	private double nodeWidth = 100;
	@Override
	public void start(Stage ps) throws Exception {
		FlowPane root = new FlowPane();
		root.setVgap(10);

		for (int i = 0; i < 20; i++) {
			root.getChildren().add(new Rectangle(nodeWidth, nodeWidth, Color.GRAY));
		}

		root.needsLayoutProperty().addListener((obs, ov, nv) -> {					
			int colCount = (int) (root.getWidth() / nodeWidth);
			double occupiedWidth = nodeWidth * colCount + 4;
			double hGap = (root.getWidth() - occupiedWidth) / (colCount - 1);
			root.setHgap(hGap);
		});

		StackPane preRoot = new StackPane(root);
		preRoot.setPadding(new Insets(10));
		preRoot.setAlignment(Pos.TOP_LEFT);
		ps.setScene(new Scene(preRoot, 600, 600));
		ps.show();
	}
}
