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

import com.aeonium.javafx.validation.annotations.FXNotNull;
import com.aeonium.javafx.validation.annotations.FXRequired;
import com.aeonium.javafx.validation.annotations.FXString;
import com.aeonium.javafx.validation.annotations.FXValidationChecked;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * An example controller that illustrates the use of the aeFXValidation
 * framework.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class FXMLDocumentController implements Initializable {

  @FXML
  @FXRequired(required = true, message = "bitte eingeben!")
  private TextField tf1;

  /**
   * This annotation uses a key from the resource bundle as message text.
   */
  @FXML
  @FXRequired(message = "validation.messages.required")
  private TextField tf2;

  @FXML
  @FXString(minLength = 2, maxLength = 5)
  private TextField tf3;

  @FXML
  private TextField tf4;

  @FXML
  @FXString(minLength = 2, maxLength = 5)
  private TextField tfVisible2;

  @FXML
  private Label label;

  @FXML
  private CheckBox cbEnableTf5;

  @FXML
  private CheckBox cbToggleVisibilityTf2;

  /**
   * Example: Message ID from the resource bundle gets picked up if present.
   */
  @FXML
  @FXRequired(message = "validation.messages.required")
  private TextField tf5;

  @FXML
  @FXNotNull
  private ChoiceBox chb1;
  @FXML
  @FXNotNull
  private ComboBox cmb1;
  @FXML
  private DatePicker dp1;

  /**
   * This button is the "submit" button of the form - therefore it must be
   * constrained to be enabled only when all validation constraints have been
   * checked successfully.
   */
  @FXML
  @FXValidationChecked
  private Button button;

  /**
   * Alternatively to binding a submit button directly to the validation
   * results, you may bind a boolean property to them, that is used to bind
   * several control's "disable" properties to.
   */
  @FXValidationChecked
  private BooleanProperty isOK = new SimpleBooleanProperty(false);

  @FXML
  private CheckBox checkedBox;
  @FXML
  private Button checkedButton;

  @FXML
  private void handleButtonAction(ActionEvent event) {
    System.out.println("You clicked me!");
    label.setText("Hello World!");
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {

    checkedBox.selectedProperty().bind(isOK);
    checkedButton.disableProperty().bind(isOK.not());

    tf5.disableProperty().bind(cbEnableTf5.selectedProperty().not());
    tfVisible2.visibleProperty().bind(cbToggleVisibilityTf2.selectedProperty().not());

    System.out.println("FXMLDocumentController.initialize OK.");
  }

}
