/*
 * Copyright (C) 2024 Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;.
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


package de.aeoniumsystems.fx.validation.annotations;

import de.aeoniumsystems.fx.validation.FXAbstractValidator;
import de.aeoniumsystems.fx.validation.NumberValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javafx.scene.control.TextInputControl;

/**
 * Marks a text input field as numeric input.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXNumber{

  public Class<? extends FXAbstractValidator<TextInputControl, FXNumber>> validation() default NumberValidator.class;

  public double min() default Double.MIN_VALUE;

  public double max() default Double.MAX_VALUE;

  public String message() default "This field must be a number!";
}
