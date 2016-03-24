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
package com.aeonium.aefxvalidationtest.examples;

import com.aeonium.javafx.validation.FXAbstractValidator;
import com.aeonium.javafx.validation.ValidationException;
import com.aeonium.javafx.validation.annotations.FXValidation;
import java.util.Arrays;
import java.util.List;
import javafx.scene.control.TextInputControl;

/**
 *
 * @author Robert Rohm&lt;r.rohm@aeonium-systems.de&gt;
 */
public class CustomValidator extends FXAbstractValidator<TextInputControl, FXValidation>{

  /**
   * 
   * @param control
   * @param annotation
   * @throws ValidationException 
   */
  @Override
  public void validate(TextInputControl control, FXValidation annotation) throws ValidationException {
    List<String> validInput = Arrays.asList("Apples", "Peas", "Bananas");
    
    this.isValid.set(validInput.contains(control.getText()));
    
    if (!this.isValid.get()) {
      throw new ValidationException("Only Apples, Peas or Bananas!");
    }
  }
  
}
