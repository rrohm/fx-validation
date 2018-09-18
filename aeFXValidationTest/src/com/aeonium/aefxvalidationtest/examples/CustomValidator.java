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
import com.aeonium.javafx.validation.annotations.FXValidation;
import com.aeonium.javafx.validation.exceptions.ValidationException;
import java.util.Arrays;
import java.util.List;
import javafx.scene.control.TextInputControl;

/**
 * This is a minimalistic example illustrating how to implement custom
 * validation rules: This class validates text input simply in a customized 
 * {@link #validate() } implementation. The contract of this method
   * is:
   * <ul>
   * <li>First, evaluate the input, according to the validator logic.</li>
   * <li>Then, set the {@link #isValid} property to the validation result (true
   * or false).</li>
   * <li>At last, if the {@link #isValid} property is false, throw a
   * {@link ValidationException}.</li>
   * </ul>
 *
 * @author Robert Rohm&lt;r.rohm@aeonium-systems.de&gt;
 */
public class CustomValidator extends FXAbstractValidator<TextInputControl, FXValidation> {

  /**
   * Implement your custom validation logic. The contract of this method
   * is:
   * <ul>
   * <li>First, evaluate the input, according to the validator logic.</li>
   * <li>Then, set the {@link #isValid} property to the validation result (true
   * or false).</li>
   * <li>At last, if the {@link #isValid} property is false, throw a
   * {@link ValidationException}.</li>
   * </ul>
   * 
   * @param control The validated JavaFX control.
   * @param annotation The annotation applied to the field that references the control.
   * @throws ValidationException Throw a {@link ValidationException} if the input is invalid.
   */
  @Override
  public void validate(TextInputControl control, FXValidation annotation) throws ValidationException {
    List<String> validInput = Arrays.asList("Apples", "Peas", "Bananas");

    // Step 1 - Evaluate the input and set the isValid property accordingly:
    this.isValid.set(validInput.contains(control.getText()));

    // Step 2 - Tell the validation framework if the input is invalid - throw a 
    // ValidationException with a proper message:
    if (!this.isValid.get()) {
      throw new ValidationException("Only Apples, Peas or Bananas!");
    }
  }

}
