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
package com.aeonium.javafx.validation.annotations;

import com.aeonium.javafx.validation.FXAbstractValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javafx.scene.control.Control;

/**
 * Use this annotation for applying custom validation to a control: Implement a
 * custom validator class (derived from {@link FXAbstractValidator}) and assign
 * it's class with the {@link FXValidation#validation()} attibute of this
 * annotation.
 * <p>
 * You may use the attribute {@link FXValidation#applicableFor()} to limit the
 * use of your validator to a number of control classes.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXValidation {

  public Class<? extends FXAbstractValidator> validation();

  /**
   * Array containing the set of controls that this validation may be applied
   * to. Use an empty array for "any control". This is valid, since interpreting
   * an empty array as "this validation cannot be applied to any control" would
   * not make sense.
   *
   * @return List of all aplicable Control subtypes
   */
  public Class<? extends Control>[] applicableFor() default {};

  public String message() default "Validation failed";
}
