/*
 * Copyright (C) 2016 Robert Rohm&lt;r.rohm@aeonium-systems.de&gt;.
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
import com.aeonium.javafx.validation.exceptions.FXValidatorException;
import com.aeonium.javafx.validation.annotations.FXNotNull;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.input.KeyEvent;

/**
 * Checks (currently only) choiceboxes and comboboxes whether they have a valid
 * selection value not equaling null. Validation gets skipped if the control is
 * either disabled or invisible.
 *
 * @author Robert Rohm&lt;r.rohm@aeonium-systems.de&gt;
 */
public class NotNullValidator extends FXAbstractValidator<Control, FXNotNull> {

  public NotNullValidator() {
    super();
    this.eventTypes.add(KeyEvent.KEY_RELEASED);
  }

  public NotNullValidator(Control control, FXNotNull annotation) {
    super(control, annotation);
    Class<? extends Control>[] applicableFor = annotation.applicableFor();
    boolean isApplicable = false;
    for (Class<? extends Control> applicableFor1 : applicableFor) {
      if (applicableFor1.isInstance(control)) {
        isApplicable = true;
        break;
      }
    }
    if (!isApplicable) {
      throw new FXValidatorException(annotation.annotationType().getName() + " is not applicable on " + control.getClass().getName());
    }
  }

  @Override
  public void validate(Control control, FXNotNull annotation) throws ValidationException {
// shortcut: do not check if disabled.
    if (control.isDisabled()) {
      this.isValid.set(true);
      return;
    }
    if (!control.isVisible()) {
      this.isValid.set(true);
      return;
    }

    boolean valid = false;

    if (control instanceof ChoiceBox) {
      ChoiceBox choiceBox = (ChoiceBox) control;
      valid = choiceBox.getValue() != null;

    } else if (control instanceof ComboBoxBase) {
      ComboBoxBase comboBoxBase = (ComboBoxBase) control;
      valid = comboBoxBase.getValue() != null;
    }

    this.isValid.set(valid);

    if (!valid) {
      throw new ValidationException(annotation.message());
    }
  }

}
