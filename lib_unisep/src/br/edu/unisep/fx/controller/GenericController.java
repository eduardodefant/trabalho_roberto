package br.edu.unisep.fx.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import br.edu.unisep.fx.annotation.FXBinding;
import br.edu.unisep.fx.annotation.FXColumn;
import br.edu.unisep.fx.annotation.FXUserData;
import br.edu.unisep.utils.MsgUtils;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public abstract class GenericController implements Initializable {

	private static final String PROPERTY = "Property";

	protected URL location;
	protected ResourceBundle resources;

	private GenericController parentController;
	private Window window;

	private Map<String, ChangeListener<Toggle>> toggleListeners;

	@SuppressWarnings("rawtypes")
	private Map<String, ChangeListener> changeListeners;

	protected void doBind() {
		executeBind(true);
	}

	protected void doUnbind() {
		executeBind(false);

		toggleListeners.clear();
		changeListeners.clear();
	}

	private void executeBind(boolean bindOn) {
		for (Field f : getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(FXBinding.class)) {
				try {
					f.setAccessible(true);
					Object temp = f.get(this);

					FXBinding fxb = f.getAnnotation(FXBinding.class);

					Object bean = getBeanValue(fxb.property());
					String propertyName = getPropertyName(fxb.property());

					Method m = bean.getClass().getMethod(propertyName, (Class<?>[]) null);
					Property<?> prop = (Property<?>) m.invoke(bean, (Object[]) null);

					if (bindOn) {
						bindField(temp, prop);
					} else {
						unbindField(temp, prop);
					}

				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Não foi possível executar o bind! " + e.getMessage());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void bindColumns() {
		for (Field f : getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(FXColumn.class)) {
				try {
					f.setAccessible(true);

					FXColumn fxb = f.getAnnotation(FXColumn.class);

					TableColumn<?, ?> col = (TableColumn<?, ?>) f.get(this);

					double w = fxb.percentWidth();

					if (w != -1) {
						col.setMaxWidth(Integer.MAX_VALUE * w);
					}

					if (!fxb.dateFormat().equals("")) {
						TableColumn<Object, LocalDate> dtCol = (TableColumn<Object, LocalDate>) col;
						dtCol.setCellValueFactory((param) -> {
							try {
								LocalDate value = (LocalDate) getBeanValue(param.getValue(), fxb.property());
								return new SimpleObjectProperty<LocalDate>(value);
							} catch (Exception e) {
								e.printStackTrace();
								throw new RuntimeException("Erro ao configurar coluna! Propriedade: " + fxb.property());
							}
						});

						dtCol.setCellFactory(column -> {
							return new TableCell<Object, LocalDate>() {
								@Override
								protected void updateItem(LocalDate item, boolean empty) {
									super.updateItem(item, empty);

									DateTimeFormatter fmt = DateTimeFormatter.ofPattern(fxb.dateFormat());

									if (item == null || empty) {
										setText(null);
									} else {
										setText(fmt.format(item));
										setAlignment(fxb.align());
									}

								}
							};
						});
					} else if (!fxb.numberFormat().equals("")) {

						TableColumn<Object, Number> nrCol = (TableColumn<Object, Number>) col;
						nrCol.setCellValueFactory((param) -> {
							try {
								Number value = (Number) getBeanValue(param.getValue(), fxb.property());
								return new SimpleObjectProperty<Number>(value);
							} catch (Exception e) {
								e.printStackTrace();
								throw new RuntimeException("Erro ao configurar coluna! Propriedade: " + fxb.property());
							}
						});

						nrCol.setCellFactory(column -> {
							return new TableCell<Object, Number>() {
								@Override
								protected void updateItem(Number item, boolean empty) {
									super.updateItem(item, empty);

									DecimalFormat df = new DecimalFormat(fxb.numberFormat());

									if (item == null || empty) {
										setText(null);
									} else {
										setText(df.format(item));
										setAlignment(fxb.align());
									}
								}
							};
						});
					} else {
						TableColumn<Object, String> strCol = (TableColumn<Object, String>) col;
						strCol.setCellValueFactory((param) -> {
							try {
								Object value = getBeanValue(param.getValue(), fxb.property());

								if (value != null) {
									return new SimpleStringProperty(value.toString());
								} else {
									return null;
								}

							} catch (Exception e) {
								e.printStackTrace();
								throw new RuntimeException("Erro ao configurar coluna! Propriedade: " + fxb.property());
							}
						});

						strCol.setCellFactory(column -> {
							return new TableCell<Object, String>() {
								@Override
								protected void updateItem(String item, boolean empty) {
									super.updateItem(item, empty);

									if (item == null || empty) {
										setText(null);
									} else {
										setText(item);
										setAlignment(fxb.align());
									}
								}
							};
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Não foi possível configurar as colunas! " + e.getMessage());
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void bindField(Object temp, Property prop) {
		if (temp instanceof TextInputControl) {
			TextInputControl txt = (TextInputControl) temp;

			if (prop instanceof SimpleStringProperty) {
				txt.textProperty().bindBidirectional(prop);
			} else if (prop instanceof SimpleIntegerProperty) {
				txt.textProperty().bindBidirectional(prop, NumberFormat.getIntegerInstance());
			} else if (prop instanceof SimpleDoubleProperty) {
				txt.textProperty().bindBidirectional(prop, DecimalFormat.getInstance());
			}

		} else if (temp instanceof ComboBoxBase<?>) {
			ComboBoxBase fld = (ComboBoxBase) temp;
			fld.valueProperty().bindBidirectional(prop);

		} else if (temp instanceof ChoiceBox<?>) {
			ChoiceBox fld = (ChoiceBox) temp;
			fld.valueProperty().bindBidirectional(prop);

		} else if (temp instanceof ToggleGroup) {
			handleToggleBind(temp, prop);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void handleToggleBind(Object temp, Property prop) {
		ToggleGroup tg = (ToggleGroup) temp;
		prop.setValue(tg.getSelectedToggle().getUserData());

		ChangeListener<Toggle> listener = (observable, oldValue, newValue) -> {
			if (!prop.getValue().equals(newValue.getUserData())) {
				prop.setValue(newValue.getUserData());
			}
		};

		ChangeListener<Object> cl = (ov, oldValue, newValue) -> {
			for (Toggle t : tg.getToggles()) {
				if (t.getUserData().equals(newValue)) {
					t.setSelected(true);
				}
			}
		};

		prop.addListener(cl);
		tg.selectedToggleProperty().addListener(listener);

		toggleListeners.put(prop.getName(), listener);
		changeListeners.put(prop.getName(), cl);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void unbindField(Object temp, Property prop) {
		if (temp instanceof TextInputControl) {
			TextInputControl txt = (TextInputControl) temp;
			txt.textProperty().unbindBidirectional(prop);

		} else if (temp instanceof ComboBoxBase<?>) {
			ComboBoxBase ctrl = (ComboBoxBase) temp;
			ctrl.valueProperty().unbindBidirectional(prop);

		} else if (temp instanceof ChoiceBox<?>) {
			ChoiceBox ctrl = (ChoiceBox) temp;
			ctrl.valueProperty().unbindBidirectional(prop);

		} else if (temp instanceof ToggleGroup) {
			ToggleGroup tg = (ToggleGroup) temp;

			ChangeListener<Toggle> listener = toggleListeners.get(prop.getName());
			tg.selectedToggleProperty().removeListener(listener);

			ChangeListener cl = changeListeners.get(prop.getName());
			prop.removeListener(cl);
		}
	}

	protected void rebind() {
		doUnbind();
		doBind();
	}

	private Object getBeanValue(String property) throws Exception {

		String[] path = property.split("\\.");

		Object obj = this;

		for (int i = 0; i < path.length - 1; i++) {
			String prop = path[i];

			Field f = obj.getClass().getDeclaredField(prop);
			f.setAccessible(true);

			obj = f.get(obj);
		}

		return obj;
	}

	private Object getBeanValue(Object src, String property) throws Exception {

		String[] path = property.split("\\.");

		Object obj = src;

		for (int i = 0; i < path.length; i++) {
			String prop = path[i];

			Field f = obj.getClass().getDeclaredField(prop);
			f.setAccessible(true);

			obj = f.get(obj);
		}

		return obj;
	}

	private String getPropertyName(String property) {
		String[] path = property.split("\\.");

		String prop = path[path.length - 1];
		return prop + PROPERTY;
	}

	private void bindRadioUserData() {
		for (Field f : getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(FXUserData.class)) {
				try {
					f.setAccessible(true);
					FXUserData fxb = f.getAnnotation(FXUserData.class);

					Toggle tg = (Toggle) f.get(this);

					if (!fxb.stringValue().equals("")) {
						tg.setUserData(fxb.stringValue());
					} else if (fxb.intValue() != Integer.MIN_VALUE) {
						tg.setUserData(fxb.intValue());
					}

				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Não foi possível configurar user data! " + e.getMessage());
				}
			}
		}
	}

	public GenericController getParentController() {
		return parentController;
	}

	public Window getWindow() {
		return window;
	}

	public void openModal(String fxml) {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

		try {
			Pane root = (Pane) loader.load();

			GenericController ctrl = (GenericController) loader.getController();
			ctrl.parentController = this;
			ctrl.window = stage;

			Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initStyle(StageStyle.UTILITY);
			stage.initModality(Modality.APPLICATION_MODAL);

			ctrl.onWindowOpen();

			stage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			MsgUtils.exibirAlerta("Erro ao abrir modal: " + e.getMessage());
		}
	}

	public void openScene(AnchorPane parent, String fxml) {
		try {
			Pane tela = FXMLLoader.load(getClass().getResource(fxml));

			tela.setPrefWidth(parent.getPrefWidth());
			tela.setPrefHeight(parent.getPrefHeight());

			AnchorPane.setTopAnchor(tela, 0d);
			AnchorPane.setRightAnchor(tela, 0d);
			AnchorPane.setLeftAnchor(tela, 0d);
			AnchorPane.setBottomAnchor(tela, 0d);

			parent.getChildren().clear();
			parent.getChildren().add(tela);
		} catch (IOException e) {
			e.printStackTrace();
			MsgUtils.exibirErro("Não foi possível carregar o FXML!");
		}
	}

	public void closeModal() {
		Stage st = (Stage) getWindow();
		st.close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.location = location;
		this.resources = resources;

		this.toggleListeners = new HashMap<>();
		this.changeListeners = new HashMap<>();

		this.bindRadioUserData();
		this.bindColumns();

		this.onControllerStart();
	}

	protected void onControllerStart() {
	}

	protected void onWindowOpen() {
	}
}