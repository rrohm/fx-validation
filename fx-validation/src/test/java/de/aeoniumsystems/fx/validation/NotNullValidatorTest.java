/*
 * Copyright (C) 2024 aeonium software systems UG (haftungsbeschränkt).
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
package de.aeoniumsystems.fx.validation;

import de.aeoniumsystems.fx.validation.annotations.FXNotNull;
import de.aeoniumsystems.fx.validation.exceptions.ValidationException;
import java.lang.annotation.Annotation;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link NotNullValidator}.
 *
 * @author robert rohm
 */
public class NotNullValidatorTest {

  public NotNullValidatorTest() {
  }

  /**
   * Test of validate method, of class NotNullValidator.
   *
   * @throws java.lang.Exception any
   */
//  @Test
//  public void testValidate() throws Exception {
//    System.out.println("validate");
//    Control control = new ChoiceBox<>();
//    FXNotNull annotation = new FXNotNullImpl();
//    NotNullValidator instance = new NotNullValidator();
//    instance.validate(control, annotation);
//  }
//  
  @Test
  public void testValidate_null() throws Exception {
    System.out.println("validate");
    Control control = new ChoiceBox<>();
    FXNotNull annotation = new FXNotNullImpl();
    NotNullValidator instance = new NotNullValidator();
    
    assertThrows(ValidationException.class, () -> {
      instance.validate(control, annotation);
    });
  }

  @Test
  public void testValidate_null_null_throws() throws Exception {
    System.out.println("validate null, null");
    Control control = null;
    FXNotNull annotation = null;
    NotNullValidator instance = new NotNullValidator();

    Assertions.assertThrows(NullPointerException.class, () -> {
      instance.validate(control, annotation);
    });
  }

  @Test
  public void testValidate_annotation_null_throws() throws Exception {
    System.out.println("validate null, null");
    Control control = new TextArea();
    FXNotNull annotation = null;
    NotNullValidator instance = new NotNullValidator();

    Assertions.assertThrows(NullPointerException.class, () -> {
      instance.validate(control, annotation);
    });
  }

  @Test
  public void testValidate_control_null_throws() throws Exception {
    System.out.println("validate control, null");
    Control control = null;
    FXNotNull annotation = new FXNotNullImpl();
    NotNullValidator instance = new NotNullValidator();

    Assertions.assertThrows(NullPointerException.class, () -> {
      instance.validate(control, annotation);
    });
  }

  private static class FXNotNullImpl implements FXNotNull {

    public FXNotNullImpl() {
    }

    @Override
    public Class<? extends FXAbstractValidator<?, ?>> validation() {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Class<? extends Control>[] applicableFor() {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String message() {
      return "Test-Message";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
  }

}
