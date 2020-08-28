/*
 * Copyright (C) 2020 Aeonium Software Systems.
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
package de.aeoniumsystems.fx.validation.ui;

import de.aeoniumsystems.fx.validation.FXValidatorService;
import java.util.ResourceBundle;
import org.aeonium.fxunit.FX;
import org.aeonium.fxunit.FXUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author robert
 */
public class ValidationsTest {
  
  @BeforeAll
  public static void setUpClass() {
    FXUnit.init();
  }
  
  @AfterAll
  public static void tearDownClass() {
    FXUnit.closeStage();
  }
  
  @BeforeEach
  public void setUp() {
    FXUnit.show(ValidationsTest.class.getResource("/fxml/FXMLDocument.fxml"), ResourceBundle.getBundle("i18n.text"));
  }
  
  @AfterEach
  public void tearDown() {
  }
  
  @Test
  public void Test_InstantValidation_onBlur(){
    FX.lookup("#tf1")
            .hasNotStyleClass(FXValidatorService.AEFX_VALIDATION_ERROR)
            .focus()
            .hasNotStyleClass(FXValidatorService.AEFX_VALIDATION_ERROR);
  }
  
}
