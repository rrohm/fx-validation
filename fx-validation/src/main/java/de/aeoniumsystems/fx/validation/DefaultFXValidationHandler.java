/*
 * Copyright (C) 2024 Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;.
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
package de.aeoniumsystems.fx.validation;

import de.aeoniumsystems.fx.validation.exceptions.ValidationException;
import com.aeonium.javafx.actions.FXActionManager;
import com.aeonium.javafx.actions.annotations.AnnotationHandler;
import de.aeoniumsystems.fx.validation.utils.LabelService;
import de.aeoniumsystems.fx.validation.annotations.FXNotNull;
import de.aeoniumsystems.fx.validation.annotations.FXNumber;
import de.aeoniumsystems.fx.validation.annotations.FXRequired;
import de.aeoniumsystems.fx.validation.annotations.FXString;
import de.aeoniumsystems.fx.validation.annotations.FXValidation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

/**
 * Handler for the following validation constraints (i.e., annotations):
 * <ul>
 * <li>FXRequired,</li>
 * <li>FXString, </li>
 * <li>FXNumber, </li>
 * <li>FXNotNull. </li>
 * </ul>
 * It by default also appends an asterisk postfix to all Labels of constrained
 * controls.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class DefaultFXValidationHandler implements AnnotationHandler<Annotation> {

  private static final Logger LOG = Logger.getLogger(DefaultFXValidationHandler.class.getName());

  private FXActionManager manager;

  /**
   * The postfix for the labels of constrained fields.
   */
  private String postfix = "*";

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
   * <li>Query the actual control (the field value) from the controller.</li>
   * <li>Get the validation handler ("validator") from the annotation of the
   * field, then create an instance of the validation handler.</li>
   * <li>Register validator with the control in the FXValidatorService</li>
   * <li>Register controller with the control in the FXValidatorService</li>
   * <li>Add event listeners that call the validate() method of the validation
   * handler.</li>
   * </ol>
   *
   * @param controller The controller
   * @param field The field referencing the UI control
   * @param validation The annotation.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void handle(Object controller, Field field, Annotation validation) {
    String name = null;

    try {
      name = this.getClassName(validation);
      if (name == null) {
        return;
      }

      Control control = (Control) field.get(controller);
      if (control == null) {
        throw new NullPointerException("JavaFX Control for field " + field.getName() + " not found.");
      }

      List<Label> labelsFor = LabelService.getLabelsFor(control);
      if (labelsFor != null) {
        for (Label label : labelsFor) {
          String text = label.getText();
          if (!text.endsWith(this.postfix)) {
            label.setText(text.concat(this.postfix));
          }
        }
      }

      FXAbstractValidator validator;
      final Class<?> classForName = Class.forName(name);
      if (classForName.getEnclosingClass() == controller.getClass()) {
        Constructor<?> constructor =  classForName.getDeclaredConstructor(controller.getClass());
        validator = (FXAbstractValidator) constructor.newInstance(controller);
        
      } else {
        Constructor<?> constructor =  classForName.getConstructor();
        validator = (FXAbstractValidator) constructor.newInstance();
      }
      
      validator.setAnnotation(validation);
      validator.setControl(control);

      // Registering control and validator - necessary for later lookups
      FXValidatorService.registerValidator(control, validator);

      // Registering control and controller - necessary for later binding
      FXValidatorService.registerValidatedControl(controller, control);

      List<EventType<?>> eventTypes = validator.getEventTypes();

      // Common validation triggers: 
      control.disabledProperty().addListener((observable, oldValue, newValue) -> {
        doValidate(validator, control, validation);
      });

      control.visibleProperty().addListener((observable, oldValue, newValue) -> {
        doValidate(validator, control, validation);
      });

      control.focusedProperty().addListener((observable, oldValue, newValue) -> {
        // if control aquires focus: get out, only validate if focus lost.
        if (newValue) {
          return;
        }
        doValidate(validator, control, validation);
      });

      // Specific validation triggers
      // Text-Input: use change events and focus changes
      if (control instanceof TextInputControl textInputControl) {

        textInputControl.textProperty().addListener((observable, oldValue, newValue) -> {
          doValidate(validator, control, validation);
        });

      } else if (control instanceof ChoiceBox<?> choiceBox) {
        choiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
          doValidate(validator, control, validation);
        });
        choiceBox.showingProperty().addListener((observable, oldValue, newValue) -> {
          if (!newValue) {
            doValidate(validator, control, validation);
          }
        });

      } else if (control instanceof ComboBoxBase<?> c) {
        c.valueProperty().addListener((observable, oldValue, newValue) -> {
          doValidate(validator, control, validation);
        });
        c.showingProperty().addListener((observable, oldValue, newValue) -> {
          if (!newValue) {
            doValidate(validator, control, validation);
          }
        });
      }  

      // Custom validation triggers, defined by the validators 
      // TODO cleanup - do some actions belong outside the loop?!
      for (EventType eventType : eventTypes) {
        // not a text input control: use key events from handlers list
        // TODO: validate concept - is this really reasonable/needed?
        control.addEventHandler(eventType, (KeyEvent event) -> {
          doValidate(validator, control, validation);
        });
      }

      // pre-set validation to OK for disabled controls:
      if (control.isDisabled()) {
        doValidate(validator, control, validation);
      }

    } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
      Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NoSuchMethodException ex) {
      Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SecurityException ex) {
      Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Mark the given control according to the validation success as validated or
   * failed. Currently, this is done by adding or removing stye classes.
   *
   * @param control The UI control
   * @param valid Whether the state of the control is valid or not.
   * @param errormessage The error message to display if the control state is
   * not valid.
   */
  public static void mark(Control control, boolean valid, String errormessage) {
    // TODO Make shure this runs on FX thread
    if (valid) {
      if (control.getStyleClass().contains(FXValidatorService.AEFX_VALIDATION_ERROR)) {
        control.getStyleClass().remove(FXValidatorService.AEFX_VALIDATION_ERROR);
      }

      List<Label> labels = LabelService.getLabelsFor(control);

      // If there are no labels: warn, because there would be no chance to 
      // display a control-specific message (at least not in this version)
      if (labels == null) {
        LOG.log(Level.WARNING, "There are no labels for the control {0}. \nYou need to add a Label and set its labelFor property to the control.", control.getId());
      } else {
        for (Label label : labels) {
          if (label.getStyleClass().contains(FXValidatorService.AEFX_VALIDATION_MSG)) {
            label.setVisible(false);
            label.setManaged(false);
          } else {
            label.getStyleClass().remove(FXValidatorService.AEFX_VALIDATION_ERROR);
          }
        }
      }

    } else {
      if (!control.getStyleClass().contains(FXValidatorService.AEFX_VALIDATION_ERROR)) {
        control.getStyleClass().add(FXValidatorService.AEFX_VALIDATION_ERROR);
      }

      List<Label> labels = LabelService.getLabelsFor(control);
      
      if (labels == null) {
        LOG.log(Level.WARNING, "There are no labels for the control {0}. \nYou need to add a Label and set its labelFor property to the control.", control.getId());
      } else {
        for (Label label : labels) {
          if (errormessage != null && label.getStyleClass().contains(FXValidatorService.AEFX_VALIDATION_MSG)) {
            label.setVisible(true);
            label.setManaged(true);
            label.setText(errormessage);
          } else if (!label.getStyleClass().contains(FXValidatorService.AEFX_VALIDATION_ERROR)) {
            label.getStyleClass().add(FXValidatorService.AEFX_VALIDATION_ERROR);
          }
        }
      }
    }
  }

  /**
   * Trigger the actual validation of a control with a given validator in the
   * way described in the fxValidation annotation.
   *
   * @param validator The validator
   * @param control The control
   * @param annotation The annotation
   */
  private void doValidate(FXAbstractValidator validator, Control control, Annotation annotation) {
//    System.out.println("doValidate " + control);
    try {
      validator.validate(control, annotation);
      mark(control, true, null);
//      FXValidatorService.hideHint(control, fxValidation);
    } catch (ValidationException ex) {
      String message = ex.getMessage();
      if (FXValidatorService.getBundle() != null) {
        ResourceBundle bundle = FXValidatorService.getBundle();
        if (bundle.containsKey(message)) {
          message = bundle.getString(message);
        }
      }
      mark(control, false, message);
//      FXValidatorService.showHint(control, fxValidation);
    }
  }

  public String getPostfix() {
    return postfix;
  }

  public void setPostfix(String postfix) {
    this.postfix = postfix;
  }

  private String getClassName(Annotation validation) throws IllegalAccessException {
    String name = null;
    if (validation instanceof FXRequired fXRequired) {
      try {
        Method method = fXRequired.getClass().getMethod("validation");
        Class<?> result = (Class) method.invoke(fXRequired, (Object[]) null);
        name = result.getName();
      } catch (NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
      }

    } else if (validation instanceof FXString fXString) {
      try {
        Method method = fXString.getClass().getMethod("validation");
        Class<?> result = (Class) method.invoke(fXString, (Object[]) null);
        name = result.getName();
      } catch (NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
      }

    } else if (validation instanceof FXNotNull fXNotNull) {
      try {
        Method method = fXNotNull.getClass().getMethod("validation");
        Class<?> result = (Class) method.invoke(fXNotNull, (Object[]) null);
        name = result.getName();
      } catch (NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
      }

    } else if (validation instanceof FXNumber fXNumber) {
      try {
        Method method = fXNumber.getClass().getMethod("validation");
        Class<?> result = (Class) method.invoke(fXNumber, (Object[]) null);
        name = result.getName();
      } catch (NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
      }

    } else if (validation instanceof FXValidation fXValidation) {
      try {
        Method method = fXValidation.getClass().getMethod("validation");
        Class<?> result = (Class) method.invoke(fXValidation, (Object[]) null);
        name = result.getName();
      } catch (NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        Logger.getLogger(DefaultFXValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return name;
  }
}
