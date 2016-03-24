# fx-validation

JavaFX is a great toolkit for quickly building UIs that look and feel good. But if you have implemente UIs with JSF or JQuery etc, you might be used to a simple way of defining validation rules on UI input elements.
fx-validation is an extension to fx-actions that lets you do just that: declaratively define validation rules on UI controls.

## Validation Rules

At present, fx-validation supports these validation rules: 

- @FXRequired
- @FXNumber(min=?, max=?)
- @FXString(minLength=?, maxLength=?)
- @FXNotNull

## Example

This example show the loading and initialization of a FXML UI with internationalization. If you need I18N support, you might provide separate property bundles, one for the FXML UI and one for the validation messages.

```java
  @Override
  public void start(Stage stage) throws Exception {
    // Get the pre-configured controller factory:
    FXActionManager myActionControllerFactory = ValidatorService.createActionManager();

    // Load the FXMl with the controller factory
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("FXMLDocument.fxml"));
    fxmlLoader.setResources(ResourceBundle.getBundle("com.aeonium.aefxvalidationtest.text"));
    fxmlLoader.setControllerFactory(myActionControllerFactory);
    
    Parent root = (Parent) fxmlLoader.load();
    Scene scene = new Scene(root);

    // Add the validation stylesheet - if you have not done so in the FXML
    scene.getStylesheets().add("/com/aeonium/javafx/validation/aeFXValidation.css");

    // Load and set the resource bundle
    ValidatorService.setBundle(ResourceBundle.getBundle("com.aeonium.aefxvalidationtest.messages"));

    // Register all Label instances with the LabelService
    LabelService.initialize(scene);
    myActionControllerFactory.initActions();
    ValidatorService.initialize(fxmlLoader.getController());

    stage.setTitle("fx-validation I18N Test");
    stage.setScene(scene);
    stage.show();
  }
```
