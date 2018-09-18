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
import com.aeonium.javafx.validation.annotations.FXString;
import java.util.regex.Pattern;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

/**
 * Check string input, e.g., whether it fits into min and max lengths.
 * Validation gets skipped if the control is either disabled or invisible.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class StringValidator extends FXAbstractValidator<TextInputControl, FXString> {

  private Pattern pattern;

  public StringValidator() {
    super();
    this.pattern = null;
    this.eventTypes.add(KeyEvent.KEY_RELEASED);
  }

  public StringValidator(TextInputControl control, FXString annotation) {
    super(control, annotation);
    this.pattern = null;
    this.eventTypes.add(KeyEvent.KEY_RELEASED);
    
    if (annotation.pattern().length() > 0) {
      this.createRegex(annotation);
    }
  }

  private void createRegex(FXString annotation) {
    this.pattern = Pattern.compile(annotation.pattern());
  }

  @Override
  public void validate(TextInputControl control, FXString annotation) throws ValidationException {
    // shortcut: do not check if disabled.
    if (control.isDisabled()) {
      this.isValid.set(true);
      return;
    }
    if (!control.isVisible()) {
      this.isValid.set(true);
      return;
    }

    boolean valid = true;

    // 1. minLength?
    if (annotation.minLength() > 0) {
      valid = valid && control.getText().length() >= annotation.minLength();
      
      this.isValid.set(valid);
      if (!valid) {
        String msg = annotation.messageMinLength();
        if (annotation.messageMinLength().contains("%d")) {
          msg = String.format(annotation.messageMinLength(), annotation.minLength());
        }
        throw new ValidationException(msg);
      }
    }

    // 2. maxLength?
    if (annotation.maxLength() > 0) {
      valid = valid && control.getText().length() <= annotation.maxLength();
      this.isValid.set(valid);
      if (!valid) {
        String msg = annotation.messageMaxLength();
        if (annotation.messageMaxLength().contains("%d")) {
          msg = String.format(annotation.messageMaxLength(), annotation.maxLength());
        }
        throw new ValidationException(msg);
      }
    }

    // 3. pattern?
    if (annotation.pattern().length() > 0) {
      // check for necessary lazy initialization: 
      if (this.pattern == null) {
        this.createRegex(annotation);
      }
      
      valid = valid && this.pattern.matcher(control.getText()).matches();
      this.isValid.set(valid);
      if (!valid) {
        String msg = annotation.messagePattern();
        throw new ValidationException(msg);
      }
    }
  }

}
