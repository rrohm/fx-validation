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

import com.aeonium.javafx.actions.annotations.AnnotationHandler;
import com.aeonium.javafx.validation.annotations.FXValidationChecked;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

/**
 * Default handler for @FXValidationChecked annotations. 
 * 
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class DefaultFXValidationCheckedHandler  implements AnnotationHandler<FXValidationChecked >{

  @Override
  public void handle(Object controller, Field field, FXValidationChecked annotation) {
    try {
      final Object fieldContent = field.get(controller);
      if (fieldContent instanceof Control) {
        Control control = (Control) fieldContent;
        ValidatorService.registerCheckedControl(controller, control);

        if (control instanceof ButtonBase) {
          ButtonBase buttonBase = (ButtonBase) control;
          buttonBase.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
            try {
              ValidatorService.validate(controller);

            } catch (Exception ex) {
              event.consume();
              Logger.getLogger(DefaultFXValidationCheckedHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
          });
        }

      } else if (fieldContent instanceof BooleanProperty) {
        final BooleanProperty booleanProperty = (BooleanProperty) fieldContent;
        ValidatorService.registerCheckedProperty(controller, booleanProperty);

      } else {
        throw new UnsupportedOperationException("FXValidationChecked annotation is not supported for fields of type " + field.getClass().getCanonicalName());
      }

    } catch (IllegalArgumentException | IllegalAccessException ex) {
      Logger.getLogger(DefaultFXValidationCheckedHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
