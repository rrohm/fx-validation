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

import com.aeonium.javafx.validation.annotations.FXNumber;
import javafx.scene.control.TextInputControl;

/**
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class NumberValidator extends FXAbstractValidator<TextInputControl, FXNumber> {

  @Override
  public void validate(TextInputControl control, FXNumber annotation) throws Exception {
    // shortcut: do not check if disabled.
    if (control.isDisabled()) {
      this.isValid.set(true);
      return;
    }
    
    boolean valid = false;

    try {
      Number n = Double.parseDouble(control.getText());
      if (null != n) {
        valid = true;
      }
    } catch (Exception e) {
      // nothing to do, validator remains invalid.
    }
    this.isValid.set(valid);

    if (!valid) {
      throw new Exception(annotation.message());
    }
  }

}
