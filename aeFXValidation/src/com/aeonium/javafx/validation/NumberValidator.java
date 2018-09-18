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
import com.aeonium.javafx.validation.annotations.FXNumber;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

/**
 * Checks whether input of a text control may be parsed as a double precision
 * number and optionally whether the value does not violate min an max range
 * constraints. Validation gets skipped if the control is either disabled or
 * invisible.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class NumberValidator extends FXAbstractValidator<TextInputControl, FXNumber> {

  public NumberValidator() {
    super();
    this.eventTypes.add(KeyEvent.KEY_RELEASED);
  }
  
  
  /**
   * Check whether the text input could get parsed as a number - validation gets
   * skipped if the control is either disabled or invisible.
   *
   * @param control The control
   * @param annotation The annotation
   * @throws ValidationException Throws an exception when validation fails. The
   *                             message of the exception should be specific
   *                             to the reason of failure.
   */
  @Override
  public void validate(TextInputControl control, FXNumber annotation) throws ValidationException {
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

    try {
      Number n = Double.parseDouble(control.getText());

      valid = true;
      if (annotation.min() != Double.MIN_VALUE) {
        valid = valid && (n.doubleValue() >= annotation.min());
      }
      if (annotation.max() != Double.MAX_VALUE) {
        valid = valid && (n.doubleValue() <= annotation.max());
      }
    } catch (NumberFormatException e) {
      // nothing to do, validator remains invalid.
    }
    this.isValid.set(valid);

    if (!valid) {
      throw new ValidationException(annotation.message());
    }
  }

}
