# fx-validation

JavaFX is a great toolkit for quickly building UIs that look and feel good. But if you have implemente UIs with JSF or JQuery etc, you might be used to a simple way of defining validation rules on UI input elements.
fx-validation is an extension to fx-actions that lets you do just that: declaratively define validation rules on UI controls.

## Validation Rules

At present, fx-validation supports these validation rules: 

- @FXRequired
- @FXNumber(min=?, max=?)
- @FXString(minLength=?, maxLength=?)
- @FXNotNull
- @FXValidation // for custom validator classes


## How it works

The validation rules are applied to the fields in the controller. A sample controller might look like this one from the aeFXValidationTest demo application: 

```java 
/**
 * An example controller that illustrates the use of the aeFXValidation
 * framework.
 *
 * @author Robert Rohm &lt;r.rohm@aeonium-systems.de&gt;
 */
public class FXMLDocumentController implements Initializable {

  /**
   * This textfield requires any input. It is provided a custom message.
   */
  @FXML
  @FXRequired(required = true, message = "bitte eingeben!")
  private TextField tf1;

  /**
   * If you need internationalized messages, provide a resource bundle to the
   * ValidatorService: This annotation uses a key from the resource bundle as
   * message text.
   */
  @FXML
  @FXRequired(message = "validation.messages.required")
  private TextField tf2;

  /**
   * This textfield uses string validation with a minimum and a maximum length.
   */
  @FXML
  @FXString(minLength = 2, maxLength = 5)
  private TextField tf3;

  /**
   * This textfield is validated against a regular expresion and uses the
   * <code>messagePattern</code> attribute.
   */
  @FXML
  @FXString(pattern = "[a-z]*", messagePattern = "Lowercase only ;-)")
  private TextField tf14;

  /**
   * This textfield requires input that can be parsed as a integer or float
   * value. The range of <code>double</code> is supported.
   */
  @FXML
  @FXNumber
  private TextField tf4;

  /**
   * This textfield requires the input to be a number within a given range. You
   * may specify minima and maxima also as double values.
   */
  @FXML
  @FXNumber(min = 5, max = 15)
  private TextField tf6;

  /**
   * The visibility of this textfield can be toggled, so it is only validated
   * conditionally when it is visible.
   */
  @FXML
  @FXString(minLength = 2, maxLength = 5)
  private TextField tfVisible2;

  @FXML
  private Label label;

  @FXML
  private CheckBox cbEnableTf5;

  @FXML
  private CheckBox cbToggleVisibilityTf2;

  /**
   * Example: Message ID from the resource bundle gets picked up if present.
   */
  @FXML
  @FXRequired(message = "validation.messages.required")
  private TextField tf5;

  /**
   * "NotNull" Validation for ChoiceBoxes is supported.
   */
  @FXML
  @FXNotNull
  private ChoiceBox chb1;

  /**
   * "NotNull" Validation for ComboBoxes is supported.
   */
  @FXML
  @FXNotNull
  private ComboBox cmb1;

  /**
   * "NotNull" Validation for DatePickers is supported.
   */
  @FXML
  @FXNotNull
  private DatePicker dp1;

  /**
   * You can also apply custom validators to a control, and implement your own
   * validation logic.
   */
  @FXML
  @FXValidation(validation = CustomValidator.class)
  private TextField tfCustom;

  /**
   * This button is the "submit" button of the form - therefore it must be
   * constrained to be enabled only when all validation constraints have been
   * checked successfully.
   */
  @FXML
  @FXValidationChecked
  private Button button;

  /**
   * Alternatively to binding a submit button directly to the validation
   * results, you may bind a boolean property to them, that is used to bind
   * several control's "disable" properties to.
   */
  @FXValidationChecked
  private BooleanProperty isOK = new SimpleBooleanProperty(false);
```


## Bootstrapping

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
