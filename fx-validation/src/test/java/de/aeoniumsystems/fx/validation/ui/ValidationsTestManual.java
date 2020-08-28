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

import com.aeonium.javafx.actions.FXActionManager;
import de.aeoniumsystems.fx.validation.FXValidatorService;
import de.aeoniumsystems.fx.validation.utils.LabelService;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.scene.Scene;
import org.aeonium.fxunit.FXHelper;
import org.aeonium.fxunit.FXUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author robert rohm
 */
public class ValidationsTestManual {

  @BeforeAll
  public static void setUpClass() {
    FXUnit.init();
  }

  @AfterAll
  public static void tearDownClass() {
    FXUnit.closeStage();
  }

  @BeforeEach
  public void setUp() throws Exception {
    FXUnit.show(ValidationsTestManual.class.getResource("/fxml/FXMLDocument.fxml"), ResourceBundle.getBundle("i18n.text"));

    Scene scene = FXUnit.getStage().getScene();
    
    FXHelper.runAndWait(() -> {
      scene.getStylesheets().add(FXValidatorService.DEFAULT_STYLESHEET);
      FXActionManager myActionControllerFactory = FXValidatorService.createActionManager();
      LabelService.initialize(scene);
      myActionControllerFactory.initActions(FXUnit.getController());
      FXValidatorService.initialize(FXUnit.getController());
    });
  }

  @AfterEach
  public void tearDown() {
  }

  @Test
  public void TestInstantValidation() throws Exception {

    Thread.sleep(100000);
  }

}
