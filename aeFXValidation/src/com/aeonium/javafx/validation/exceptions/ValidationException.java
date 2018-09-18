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
package com.aeonium.javafx.validation.exceptions;

import com.aeonium.javafx.validation.FXAbstractValidator;

/**
 * Base exception for signalling validation errors – a validator derived from
 * {@link FXAbstractValidator} must throw this exception in it's 
 * {@link FXAbstractValidator#validate(javafx.scene.control.Control, java.lang.annotation.Annotation)
 * } method, if the validation failed and the property
 * {@link FXAbstractValidator#isValid} has been set to <code>false</code>.
 *
 * @author Robert Rohm&lt;r.rohm@aeonium-systems.de&gt;
 */
public class ValidationException extends Exception {

  /**
   * Construct a validation exception with a given message.
   *
   * @param msg The message.
   */
  public ValidationException(String msg) {
    super(msg);
  }

}
