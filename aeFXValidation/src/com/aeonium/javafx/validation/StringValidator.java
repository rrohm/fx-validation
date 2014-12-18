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

import com.aeonium.javafx.validation.annotations.FXString;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class StringValidator extends FXAbstractValidator<TextInputControl, FXString> {

  public StringValidator() {
    super();
    this.eventTypes.add(KeyEvent.KEY_RELEASED);
  }

  public StringValidator(TextInputControl control, FXString annotation) {
    super(control, annotation);
    this.eventTypes.add(KeyEvent.KEY_RELEASED);
  }

  @Override
  public void validate(TextInputControl control, FXString annotation) throws ValidationException {
//    System.out.println("StringValidator.validate " + annotation);
    // shortcut: do not check if disabled.
    if (control.isDisabled()) {
      this.isValid.set(true);
      return;
    }

    boolean valid = false;

    if (annotation.minLength() > 0) {
      valid = control.getText().length() >= annotation.minLength();
    }
    this.isValid.set(valid);
    if (!valid) {
      String msg = annotation.messageMinLength();
      if (annotation.messageMinLength().contains("%d")) {
        msg = String.format(annotation.messageMinLength(), annotation.minLength());
      }
      throw new ValidationException(msg);
    }

    if (annotation.maxLength() > 0) {
      valid = valid && control.getText().length() <= annotation.maxLength();
    }
    this.isValid.set(valid);
    if (!valid) {
      String msg = annotation.messageMaxLength();
      if (annotation.messageMaxLength().contains("%d")) {
        msg = String.format(annotation.messageMaxLength(), annotation.maxLength());
      }
      throw new ValidationException(msg);
    }

    if (annotation.pattern().length() > 0) {
      throw new UnsupportedOperationException("Pattern not yet supported.");
    }
    this.isValid.set(valid);

  }

}
