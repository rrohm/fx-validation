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
import com.aeonium.javafx.validation.NotNullValidator;
import com.aeonium.javafx.validation.ValidatorService;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;

/**
 * Marks a selection control, e.g., ChoiceBox, as not allowed to have a null
 * value. This constraint is applicable on all controls that support a value
 * property. Currently that is:
 *
 * <ul>
 * <li>ChoiceBox</li>
 * <li>ComboBoxBase and its descendants</li>
 * </ul>
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXNotNull {

  /**
   * The default validator is {@link NotNullValidator}, you should not need to
   * change this.
   *
   * @return The validator class.
   */
  public Class<? extends FXAbstractValidator> validation() default NotNullValidator.class;

  /**
   * Currently, this annotation and it's validator may be used with
   * javafx.scene.control.ComboBoxBase and it's descendants, and with
   * javafx.scene.control.ChoiceBox. You should not change this values.
   *
   * @return List of all aplicable Control subtypes
   */
  public Class<? extends Control>[] applicableFor() default {ChoiceBox.class, ComboBoxBase.class};

  /**
   * Use this attribute to provide a custom message, or, use a key of your
   * resource bundle, if you initialized the {@link ValidatorService} with a
   * resource bundle (see {@link ValidatorService#setBundle(java.util.ResourceBundle)}.
   *
   * @return The default message or a key for the resource bundle.
   */
  public String message() default "This field must not be null!";
}
