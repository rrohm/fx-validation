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
package com.aeonium.javafx.validation;

import com.aeonium.javafx.validation.exceptions.ValidationException;
import com.aeonium.javafx.validation.annotations.FXRequired;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

/**
 * Validate ttext input controls as "required", i.e., they must not be empty.
 * Validation gets skipped if the control is either disabled or invisible.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class RequiredValidator extends FXAbstractValidator<TextInputControl, FXRequired> {

  public RequiredValidator() {
    super();
    this.eventTypes.add(KeyEvent.KEY_RELEASED);
  }

  public RequiredValidator(TextInputControl control, FXRequired annotation) {
    super(control, annotation);
    this.eventTypes.add(KeyEvent.KEY_RELEASED);
  }

  /**
   * Validate a text input control - validation gets skipped if the control is
   * either disabled or invisible.
   *
   * @param control The text input control
   * @throws com.aeonium.javafx.validation.exceptions.ValidationException The
   * exception to be thrown when the control input is not valid.
   */
  @Override
  public void validate(TextInputControl control, FXRequired annotation) throws ValidationException {
    // shortcut: do not check if disabled.
    if (control.isDisabled()) {
      this.isValid.set(true);
      return;
    }
    if (!control.isVisible()) {
      this.isValid.set(true);
      return;
    }

    boolean valid = control.getText().length() > 0;
    this.isValid.set(valid);

    if (!valid) {
      throw new ValidationException(annotation.message());
    }
  }
}
