/*
 * Copyright (C) 2020 Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;.
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

import com.aeonium.javafx.validation.exceptions.ValidationException;
import com.aeonium.javafx.actions.FXActionManager;
import com.aeonium.javafx.actions.annotations.AnnotationHandler;
import com.aeonium.javafx.utils.LabelService;
import com.aeonium.javafx.validation.annotations.FXNotNull;
import com.aeonium.javafx.validation.annotations.FXNumber;
import com.aeonium.javafx.validation.annotations.FXRequired;
import com.aeonium.javafx.validation.annotations.FXString;
import com.aeonium.javafx.validation.annotations.FXValidation;
import com.aeonium.javafx.validation.annotations.FXValidationChecked;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;

/**
 * This class serves as a registry for validator objects and provides methods
 * for looking up validators of a control.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class FXValidatorService {

  public static final String DEFAULT_STYLESHEET = "/com/aeonium/javafx/validation/aeFXValidation.css";

  private static final Logger LOG = Logger.getLogger(FXValidatorService.class.getName());

  /**
   * CSS class for the erronous controls and corresponding labels.
   */
  public static final String AEFX_VALIDATION_ERROR = "aefx-validation-error";
  /**
   * CSS class for the error message labels.
   */
  public static final String AEFX_VALIDATION_MSG = "aefx-validation-message";

  /**
   * Mapping of multiple validators to it's control.
   */
  private static final Map<Control, List<FXAbstractValidator>> validatorMap;

  /**
   * @deprecated Experimental - not for use!
   */
  private static final Map<Control, Popup> popupMap;

  /**
   * Mapping of multiple validated controls to it's controller.
   */
  private static final Map<Object, List<Control>> validatedControlMap;
  /**
   * Mapping of multiple checked controls to it's controller.
   */
  private static final Map<Object, List<Control>> checkedControlMap;
  private static final Map<Object, List<BooleanProperty>> checkedPropertyMap;

  /**
   * Resource bundle for localized messages - if defined, the validator messages
   * are used as keys for the bundle.
   */
  private static ResourceBundle bundle = null;

  static {
    validatorMap = new WeakHashMap<>();
    validatedControlMap = new WeakHashMap<>();
    checkedControlMap = new WeakHashMap<>();
    checkedPropertyMap = new WeakHashMap<>();
    popupMap = new WeakHashMap<>();
  }

  /**
   * @deprecated Not implemented yet, work in progress.
   * @param targetControl TBD
   * @param validatedControls TBD
   */
  public static void bindValidators(Control targetControl, Control... validatedControls) {
    System.out.println("ValidatorService.bindValidators " + targetControl);
    for (Control vc : validatedControls) {
      List<FXAbstractValidator> list = validatorMap.get(vc);
      if (list == null) {
        continue;
      }

      for (FXAbstractValidator validator : list) {
        System.out.println("bind:  " + targetControl + " -> " + vc + " | " + validator);
      }
    }
  }

  /**
   * Use this factory method to obtain an instance of the FXActionManager as a
   * callback for loading an initializing FXML UIs. Main purpose of this factory
   * method is to initialize the FXActionManager with the default handlers for
   * the validation annotations. If you need to create custom annotations and/or
   * custom handlers, add the handlers in the way this method does:
   * <pre>
   * FXActionManager fxActionManager = new FXActionManager();
   * FXValidatorService.addValidationHandlers(fxActionManager);
   * </pre>
   *
   * @return The newly created and initialized action manager.
   */
  public static FXActionManager createActionManager() {
    FXActionManager fxActionManager = new FXActionManager();

    addValidationHandlers(fxActionManager);

    return fxActionManager;
  }

  /**
   * Use this factory method to obtain an instance of the FXActionManager with 
   * the default validation annotation handlers, extended with your own 
   * additional annotation handlers. 
   * 
   * @param additionalHandlers
   * @return 
   */
  public static FXActionManager createActionManager(Map<Class, AnnotationHandler> additionalHandlers) {
    FXActionManager fxActionManager = new FXActionManager(additionalHandlers);

    addValidationHandlers(fxActionManager);

    return fxActionManager;
  }

  /**
   * Use this method if you have already an instance of FXActionManager and you
   * want to initialize it for using aeFXValidation annotations.
   *
   * @param fxActionManager The action manager instance
   */
  public static void addValidationHandlers(FXActionManager fxActionManager) {
    final DefaultFXValidationHandler defaultFXValidationHandler1 = new DefaultFXValidationHandler();
    fxActionManager.addHandler(FXRequired.class, defaultFXValidationHandler1);
    fxActionManager.addHandler(FXString.class, defaultFXValidationHandler1);
    fxActionManager.addHandler(FXNumber.class, defaultFXValidationHandler1);
    fxActionManager.addHandler(FXNotNull.class, defaultFXValidationHandler1);
    fxActionManager.addHandler(FXValidation.class, defaultFXValidationHandler1);
    fxActionManager.addHandler(FXValidationChecked.class, new DefaultFXValidationCheckedHandler());
  }

  public static List<FXAbstractValidator> getValidators(Control c) {
    return validatorMap.get(c);
  }

  public static List<FXAbstractValidator> getValidators(Control... c) {
    List<FXAbstractValidator> list = new ArrayList<>();

    for (Control control : c) {
      list.addAll(validatorMap.get(control));
    }
    return list;
  }

  /**
   * The convenient way to initialize the node tree <i>and</i> the controller.
   * This method does actually the same like:
   * <pre>
   *
   * FXActionManager actionManager = FXValidatorService.createActionManager();
   *
   * LabelService.initialize(parent);
   * actionManager.initActions(controller);
   * FXValidatorService.initialize(controller);
   *
   *
   * </pre>
   *
   * @param parent The parent node to scan the tree down from.
   * @param controller The controller to initialize.
   */
  public static void initialize(Parent parent, Object controller) {
    LabelService.initialize(parent);
    FXActionManager actionManager = createActionManager();
    actionManager.initActions(controller);
    initialize(controller);
  }

  /**
   * Initialize validation in the given controller object.
   *
   * @param controller The controller to initialize
   */
  public static void initialize(Object controller) {
    List<Control> checkedControls = checkedControlMap.get(controller);
    List<Control> validatedControls = validatedControlMap.get(controller);
    List<BooleanProperty> checkedProperties = checkedPropertyMap.get(controller);

//    System.out.println("initialize " + controller);
    if (validatedControls != null) {
      for (Control control : validatedControls) {
        List<Label> labels = LabelService.getLabelsFor(control);
        if (labels == null) {
          throw new NullPointerException("No labels for control: " + control);
        }
        // hide validation message labels:
        for (Label label : labels) {
          if (label.getStyleClass().contains(AEFX_VALIDATION_MSG)) {
            label.setVisible(false);
            label.setManaged(false);
          }
        }
      }
    }

    initializeCheckedControls(checkedControls, validatedControls);

    initializeCheckedProperties(checkedProperties, validatedControls);
  }

  /**
   * Iterate through a list of "checked" controls, i.e., of controls that have
   * to be controlled by the validation service: their disable property gets
   * bound to the combined result of all validation constraints.
   *
   * @param checkedControls The list of checked controls
   * @param validatedControls The list of validated controls
   * @throws UnsupportedOperationException Thrown if control is not an instance
   * of ButtonBase
   */
  private static void initializeCheckedControls(List<Control> checkedControls, List<Control> validatedControls) throws UnsupportedOperationException {
    if (checkedControls != null) {

      for (Control control : checkedControls) {
        // if we have something like a button, checkbox etc.
        if (control instanceof ButtonBase) {
          ButtonBase buttonBase = (ButtonBase) control;
          List<ObservableBooleanValue> validatorsOK = new ArrayList<>();

          // go through validated controls and bind enabledProperty
          for (Control validatedControl : validatedControls) {
            // ... for each validator - get validators
            List<FXAbstractValidator> validators = validatorMap.get(validatedControl);

            for (FXAbstractValidator validator : validators) {
              validatorsOK.add(validator.isValid);
            }
          }

          // create collective binding for checked controls
          buttonBase.disableProperty().bind(new AllBooleansBinding(validatorsOK).not());
        } else {
          throw new UnsupportedOperationException("ValidatorService supports only descendants of ButtonBase as checked controls.");
        }
      }
    }
  }

  /**
   * Initialize checked boolean properties, i.e., bind their value to the
   * combined result of all validation constraints. This method is failfast,
   * i.e. it throws an exception if you try to use checked properties in an FXML
   * form without any validated controls that the property may get bound to. So,
   * you need at least one control with some validation constraint in your
   * controller.
   *
   * @param checkedProperties The list of checked properties
   * @param validatedControls The list of validated controls
   */
  private static void initializeCheckedProperties(List<BooleanProperty> checkedProperties, List<Control> validatedControls) {
    if (validatedControls == null || validatedControls.isEmpty()) {
      final String message = "Use of checked Property without validated Controls! You may want to check your Controller class.";
      LOG.severe(message);
      throw new RuntimeException(message);
    }
    if (checkedProperties != null) {

      for (BooleanProperty checkedProperty : checkedProperties) {

        List<ObservableBooleanValue> validatorsOK = new ArrayList<>();

        // go through validated controls and bind enabledProperty
        for (Control validatedControl : validatedControls) {
          // ... for each validator - get validators
          List<FXAbstractValidator> validators = validatorMap.get(validatedControl);

          for (FXAbstractValidator validator : validators) {
            validatorsOK.add(validator.isValid);
          }
        }

        checkedProperty.bind(new AllBooleansBinding(validatorsOK));
      }
    }
  }

  /**
   * Process all validators that belong to the given controller - this is
   * failfast, the method quits validation on the first failure with an
   * exception.
   *
   * @param controller The controller
   * @throws ValidationException The Exception signalling a failed validation
   */
  public static void validate(Object controller) throws ValidationException {

    List<Control> validatedControls = validatedControlMap.get(controller);

    for (Control validatedControl : validatedControls) {
      // ... for each validator - get validators
      List<FXAbstractValidator> validators = validatorMap.get(validatedControl);

      for (FXAbstractValidator validator : validators) {
        validator.validate();
      }
    }
  }

  /**
   *
   * @deprecated Experimental - not for use!
   * @param control The control
   * @param annotation The annotation that should provide the hint message
   */
  static void hideHint(Control control, Annotation annotation) {
    Popup popup = popupMap.get(control);
    if (popup != null && popup.isShowing()) {
      popup.hide();
    }
  }

  /**
   * @deprecated Experimental - not for use!
   * @param control The control to show the hint for.
   * @param annotation The annotation providing the text message
   */
  public static void showHint(Control control, Annotation annotation) {
    Popup popup = popupMap.get(control);
    if (popup == null) {
      popup = new Popup();
      popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
      popup.getContent().add(new Label(annotation.toString()));
      popupMap.put(control, popup);
    }
    Point2D localToScene = control.localToScreen(0, 0);
    popup.show(control, localToScene.getX(), localToScene.getY());
  }

  public static void registerValidator(Control c, FXAbstractValidator validator) {
    List<FXAbstractValidator> list = validatorMap.get(c);

    if (list == null) {
      list = new ArrayList<>();
      validatorMap.put(c, list);
    }

    list.add(validator);
  }

  public static void unregisterValidator(Control c, FXAbstractValidator validator) {
    List<FXAbstractValidator> list = validatorMap.get(c);

    if (list != null) {
      list.remove(validator);
    }
  }

  /**
   * Register a validated control with it's controler.
   *
   * @param controller The controller
   * @param control The UI control to register.
   */
  public static void registerValidatedControl(Object controller, Control control) {
    List<Control> list = validatedControlMap.get(controller);

    if (list == null) {
      list = new ArrayList<>();
      validatedControlMap.put(controller, list);
    }

    list.add(control);
  }

  public static void registerCheckedControl(Object controller, Control control) {
    List<Control> list = checkedControlMap.get(controller);

    if (list == null) {
      list = new ArrayList<>();
      checkedControlMap.put(controller, list);
    }

    list.add(control);
  }

  static void registerCheckedProperty(Object controller, BooleanProperty booleanProperty) {
    List<BooleanProperty> list = checkedPropertyMap.get(controller);

    if (list == null) {
      list = new ArrayList<>();
      checkedPropertyMap.put(controller, list);
    }

    list.add(booleanProperty);
  }

  public static ResourceBundle getBundle() {
    return bundle;
  }

  /**
   * Set a resource bundle, if you want to use localized validation messages. In
   * this case, use the key of your localized messages for the
   * <code>message</code> attribute in the annotations.
   *
   * @param aBundle A resource bundle for the validation messages.
   */
  public static void setBundle(ResourceBundle aBundle) {
    bundle = aBundle;
  }

  private static class BooleanValidatorsBinding extends BooleanBinding {

    private final List<ObservableBooleanValue> bools;

    public BooleanValidatorsBinding(List<ObservableBooleanValue> validatorsOK) {
      this.bools = validatorsOK;
      Observable[] bb = {};
      bb = bools.toArray(bb);
      super.bind(bb);
    }

    @Override
    protected boolean computeValue() {
      boolean result = true;
      for (ObservableBooleanValue b : bools) {
        result = result && b.get();
      }
      return !result;
    }
  }

  private static class AllBooleansBinding extends BooleanBinding {

    private final List<ObservableBooleanValue> bools;

    public AllBooleansBinding(List<ObservableBooleanValue> validatorsOK) {
      this.bools = validatorsOK;
      Observable[] bb = {};
      bb = bools.toArray(bb);
      super.bind(bb);
    }

    @Override
    protected boolean computeValue() {
      boolean result = true;
      for (ObservableBooleanValue b : bools) {
        result = result && b.get();
      }
      return result;
    }
  }
}
