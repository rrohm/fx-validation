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
import com.aeonium.javafx.validation.StringValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define constraints for a string input field, e.g., minimum length and/or
 * maximum length.
 *
 * <br>Example:
 * <pre>
 *
 * {@literal @}FXML
 * {@literal @}FXString(minLength = 2, maxLength = 5)
 * private TextField name;
 *
 * </pre>
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXString {

  public Class<? extends FXAbstractValidator> validation() default StringValidator.class;

  /**
   * A required minimum length for the string value of the annotated control.
   * Set it to 0 ignore it, if not needed.
   *
   * @return The required minimum length.
   */
  public int minLength() default 0;

  /**
   * A allowed maximum length for the string value of the annotated control. Set
   * it to 0 ignore it, if not needed.
   *
   * @return The required minimum length.
   */
  public int maxLength() default 0;

  /**
   * Regex pattern for validation.
   *
   * @return The regex validation pattern.
   */
  public String pattern() default "";

  /**
   * Message displayed when minimum length is not reached - you can use %d as
   * placeholder for the actual value.
   *
   * @return The Message
   */
  public String messageMinLength() default "Please enter at least %d characters.";

  /**
   * Message displayed when maximum length is exceeded - you can use %d as
   * placeholder for the actual value.
   *
   * @return The Message
   */
  public String messageMaxLength() default "Please enter max. %d characters.";

  /**
   * Message displayed when the pattern is not mathched.
   *
   * @return The Message
   */
  public String messagePattern() default "Please enter correct Input.";
}
