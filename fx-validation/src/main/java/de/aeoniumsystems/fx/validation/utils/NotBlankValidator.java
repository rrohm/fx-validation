/*
 * Copyright (C) 2024 Robert Rohm&lt;r.rohm@aeonium-systems.de&gt;.
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
package de.aeoniumsystems.fx.validation.utils;

import de.aeoniumsystems.fx.validation.*;
import de.aeoniumsystems.fx.validation.exceptions.ValidationException;
import de.aeoniumsystems.fx.validation.exceptions.FXValidatorException;
import de.aeoniumsystems.fx.validation.annotations.FXNotNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * Checks (currently only) TextFields and TeextAreas whether they have a text property value that is not null, not empty
 * and not blank. Validation gets skipped if the control is either disabled or invisible.
 *
 * @author Robert Rohm&lt;r.rohm@aeonium-systems.de&gt;
 */
public class NotBlankValidator extends FXAbstractValidator<Control, FXNotNull> {

  private static final Logger LOG = Logger.getLogger(NotBlankValidator.class.getName());

  public NotBlankValidator() {
    super();
    this.eventTypes.add(KeyEvent.KEY_RELEASED);
  }

  public NotBlankValidator(Control control, FXNotNull annotation) {
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

    if (control instanceof TextField) {
      TextField textField = (TextField) control;
      valid = textField.getText() != null && !textField.getText().isBlank();

    } else if (control instanceof TextArea) {
      TextArea textArea = (TextArea) control;
      valid = textArea.getText() != null && !textArea.getText().isBlank();

    } else {
      LOG.log(Level.WARNING, "{0} is applied to an unsupported control type: {1}", new Object[]{this.getClass().getSimpleName(), control.getClass().getName()});
    }

    this.isValid.set(valid);

    if (!valid) {
      throw new ValidationException(annotation.message());
    }
  }

}
