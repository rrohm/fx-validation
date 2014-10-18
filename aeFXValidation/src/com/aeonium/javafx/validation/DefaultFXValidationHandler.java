/*
 * Copyright (C) 2014 Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;.
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
package com.aeonium.javafx.validation;

import com.aeonium.javafx.actions.FXActionManager;
import com.aeonium.javafx.actions.annotations.AnnotationHandler;
import com.aeonium.javafx.utils.LabelService;
import com.aeonium.javafx.validation.annotations.FXRequired;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

/**
 * TODO: Make this class generic, not only working with FXRequired
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class DefaultFXValidationHandler implements AnnotationHandler<FXRequired> {

  private FXActionManager manager;

  public DefaultFXValidationHandler() {
  }

  DefaultFXValidationHandler(FXActionManager manager) {
    this.manager = manager;
  }

  /**
   * The default validation handling method takes a controller object, a field
   * and the annotation, and does the following things:
   *
   * <ol>
   *   <li>Query the actual control (the field value) from the controller.</li>
   *   <li>Get the validation handler ("validator") from the annotation of the
   *       field, then create an instance of the validation handler.</li>
   *   <li>Register validator with the control in the ValidatorService</li>
   *   <li>Register controller with the control in the ValidatorService</li>
   *   <li>Add event listeners that call the validate() method of the validation
   *       handler.</li>
   * </ol>
   *
   * @param controller
   * @param field
   * @param validation The annotation - currently not used here.
   */
  @Override
  public void handle(Object controller, Field field, FXRequired validation) {
    System.out.println("DefaultFXValidationHandler " + controller + ", " + field.getName() + " " + validation.getClass().getName());
    try {

      FXRequired fxValidation = field.getAnnotation(FXRequired.class);
      String name = fxValidation.validation().getName();
      Control control = (Control) field.get(controller);

      FXAbstractValidator validator = (FXAbstractValidator) Class.forName(name).newInstance();
      validator.setAnnotation(validation);
      validator.setControl(control);

      // Registering control and validator - necessary for later lookups
      ValidatorService.registerValidator(control, validator);

      // Registering control and controller - necessary for later binding
      ValidatorService.registerValidatedControl(controller, control);


      List<EventType> eventTypes = validator.getEventTypes();

      for (EventType eventType : eventTypes) {

        // Text-Input: use change events and focus changes
        if (control instanceof TextInputControl) {
          TextInputControl textInputControl = (TextInputControl) control;

          textInputControl.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            doValidate(validator, control, fxValidation);
          });
          textInputControl.disabledProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            doValidate(validator, control, fxValidation);
          });


          textInputControl.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            // if control aquires focus: get out, only validate if focus lost.
            if (newValue == true) {
              return;
            }

            doValidate(validator, control, fxValidation);
          });


          // not a text input control: use key events from handlers list
          // TODO: validate concept - is this really reasonable/needed?
        } else {

          control.addEventHandler(eventType, (KeyEvent event) -> {
            doValidate(validator, control, fxValidation);
          });
        }

      }

    } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | InstantiationException ex) {
      Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Mark the given control according to the validation success as validated or
   * failed. Currently, this is done by adding or removing stye classes.
   *
   * @param control
   * @param valid
   */
  public static void mark(Control control, boolean valid, String errormessage){
    if (valid) {
      if (control.getStyleClass().contains(ValidatorService.AEFX_VALIDATION_ERROR)) {
        control.getStyleClass().remove(ValidatorService.AEFX_VALIDATION_ERROR);
      }

      List<Label> labels = LabelService.getLabelsFor(control);
      for (Label label : labels) {
        if (label.getStyleClass().contains(ValidatorService.AEFX_VALIDATION_MSG)) {
          label.setVisible(false);
          label.setManaged(false);
        } else {
          label.getStyleClass().remove(ValidatorService.AEFX_VALIDATION_ERROR);
        }
      }
    } else {
      if (!control.getStyleClass().contains(ValidatorService.AEFX_VALIDATION_ERROR)) {
        control.getStyleClass().add(ValidatorService.AEFX_VALIDATION_ERROR);
      }

      List<Label> labels = LabelService.getLabelsFor(control);
      for (Label label : labels) {
        if (label != null) {
          if (errormessage != null && label.getStyleClass().contains(ValidatorService.AEFX_VALIDATION_MSG)) {
            label.setVisible(true);
            label.setManaged(true);
            label.setText(errormessage);
          } else {
            if (!label.getStyleClass().contains(ValidatorService.AEFX_VALIDATION_ERROR)) {
              label.getStyleClass().add(ValidatorService.AEFX_VALIDATION_ERROR);
            }
          }
        }
      }
      //            Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void doValidate(FXAbstractValidator validator, Control control, Annotation fxValidation) {
    try {
      validator.validate(control, fxValidation);
      mark(control, true, null);
//      ValidatorService.hideHint(control, fxValidation);
    } catch (Exception ex) {
      mark(control, false, ex.getMessage());
//      ValidatorService.showHint(control, fxValidation);
    }
  }
}
