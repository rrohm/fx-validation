/*
 * Copyright (C) 2014 Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;.
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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventType;
import javafx.scene.control.Control;

/**
 * Base class for the validator classes: Each validation case for a control
 * gets one instance of a validator.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 * @param <T> The control base type
 * @param <A> The annotation type
 */
public abstract class FXAbstractValidator<T extends Control, A extends Annotation> {


  protected T control;

  protected A annotation;

  public FXAbstractValidator() {
  }

  public FXAbstractValidator(T control, A annotation) {
    this.control = control;
    this.annotation = annotation;
  }

  /**
   * Property for the state of validation - initially, it needs to be set to
   * false.
   */
  protected final BooleanProperty isValid = new SimpleBooleanProperty(false);


  /**
   * Every validation class must define which events shall trigger the
   * validation.
   *
   */
  protected List<EventType> eventTypes = new ArrayList<>();

  /**
   * Validate the current data and/or state of control according to the
   * validation constraints annotated at the field. This method is meant to
   * set the value of the isValid property - also before it throws an exception.
   *
   * @param control The control
   * @param annotation The annotation
   * @throws ValidationException Throws an exception when validation fails. The
   *                             message of the exception should be specific
   *                             to the reason of failure.
   */
  public abstract void validate(T control, A annotation) throws ValidationException;

  /**
   * An overloaded version of <code>validate(T control, A annotation)</code>
   * that uses the referenced control and annotation - use this as default
   * method to trigger validation.
   *
   * @throws ValidationException Throws an exception when validation fails. The
   *                             message of the exception should be specific
   *                             to the reason of failure.
   */
  public void validate() throws ValidationException {
    this.validate(this.control, this.annotation);
  }


  public List<EventType> getEventTypes() {
    return eventTypes;
  }


  public BooleanProperty isValidProperty() {
    return isValid;
  }

  public T getControl() {
    return control;
  }

  public void setControl(T control) {
    this.control = control;
  }

  public A getAnnotation() {
    return annotation;
  }

  public void setAnnotation(A annotation) {
    this.annotation = annotation;
  }
}
