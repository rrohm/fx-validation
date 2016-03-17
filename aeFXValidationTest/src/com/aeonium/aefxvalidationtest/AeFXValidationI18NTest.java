/*
 * Copyright (C) 2016 Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.aeonium.aefxvalidationtest;

import com.aeonium.javafx.actions.FXActionManager;
import com.aeonium.javafx.utils.LabelService;
import com.aeonium.javafx.validation.ValidatorService;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * An application class, illustrating validation with localized messages from a
 * resource bundle.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class AeFXValidationI18NTest extends Application {

  private FXActionManager myActionControllerFactory;

  @Override
  public void start(Stage stage) throws Exception {
    // Get the pre-configurated controller factory:
    myActionControllerFactory = ValidatorService.createActionManager();

    // Load the FXMl with the controller factory
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("FXMLDocument.fxml"));
    fxmlLoader.setResources(ResourceBundle.getBundle("com.aeonium.aefxvalidationtest.text"));
    fxmlLoader.setControllerFactory(myActionControllerFactory);
    
    Parent root = (Parent) fxmlLoader.load();
    Scene scene = new Scene(root);

    // Add the validation stylesheet - if you have not done so in the FXML
    scene.getStylesheets().add("/com/aeonium/javafx/validation/aeFXValidation.css");

    // Load and set the resource bundle
    ValidatorService.setBundle(ResourceBundle.getBundle("com.aeonium.aefxvalidationtest.messages"));

    // Register all Label instances with the LabelService
    LabelService.initialize(scene);
    myActionControllerFactory.initActions();
    ValidatorService.initialize(fxmlLoader.getController());

    stage.setTitle("fx-validation I18N Test");
    stage.setScene(scene);
    stage.show();
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

}
