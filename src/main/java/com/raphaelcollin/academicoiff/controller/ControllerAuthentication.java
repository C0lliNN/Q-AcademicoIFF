package com.raphaelcollin.academicoiff.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro8.JMetro;
import jfxtras.styles.jmetro8.JMetro.Style;

import java.io.IOException;

public class ControllerAuthentication {

    // Controles

    @FXML
    private AnchorPane root;
    @FXML
    private HBox hBoxImageView;
    @FXML
    private ImageView imageView;
    @FXML
    private Rectangle rectangle;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label tituloLabel;
    @FXML
    private TextField matriculaField;
    @FXML
    private PasswordField senhaField;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Button entrarButton;

    private static final String REGULAR_EXPRESSION_MATRICULA = "\\d{12}";
    private static final String URL_IMAGEVIEW = "file:files/titulo.png";
    private static final String URL_JANELA_CARREGAMENTO = "/loading_view.fxml";
    private static final String ID_ANCHORPANE_CSS = "login-container";
    private static final String ID_TEXTFIELD_MATRICULA_CSS = "matricula-field";
    private static final String ID_PASSWORDFIELD_SENHA_CSS = "senha-field";
    private static final String ID_CHECKBOX_MANTER_CONECTADO = "check";
    private static final String ID_BUTTON_ENTRAR = "entrar-button";

    public void initialize(){

            // JMetro

        JMetro metro = new JMetro(Style.LIGHT);
        metro.applyTheme(root);


        Rectangle2D tamanhoTela = Screen.getPrimary().getBounds();
        double width = tamanhoTela.getWidth() * 0.15;
        double height = width * 1.8;

        anchorPane.setPrefSize(width,height);
        anchorPane.setMinSize(width,height);
        anchorPane.setMaxSize(width,height);

        tituloLabel.setStyle("-fx-font-size: " + width * 0.17 + "px");
        matriculaField.setStyle("-fx-font-size: " + width * 0.08 + "px");
        senhaField.setStyle("-fx-font-size: " + width * 0.08 + "px");
        checkBox.setStyle("-fx-font-size: " + width * 0.07 + "px");
        entrarButton.setStyle("-fx-font-size: " + width * 0.1 + "px");

        imageView.setImage(new Image(URL_IMAGEVIEW));
        imageView.setFitWidth(tamanhoTela.getWidth() * 0.36);
        imageView.setFitHeight(tamanhoTela.getWidth() * 0.36 * 0.27);

        rectangle.setFill(Color.rgb(119,252,3,0.25));
        rectangle.setArcWidth(width * 0.4);
        rectangle.setArcHeight(width * 0.4);
        rectangle.setWidth(width * 2);
        rectangle.setHeight(height);
        rectangle.setStrokeWidth(width * 0.00173);
        rectangle.setStroke(Color.rgb(119,252,3,1));

        AnchorPane.setTopAnchor(hBoxImageView, tamanhoTela.getHeight() * 0.04);
        AnchorPane.setLeftAnchor(hBoxImageView,0.0);
        AnchorPane.setRightAnchor(hBoxImageView,0.0);

        AnchorPane.setTopAnchor(rectangle,height * 0.6);
        AnchorPane.setLeftAnchor(rectangle,width * 2.33);
        AnchorPane.setRightAnchor(rectangle,width * 2.33);

        AnchorPane.setTopAnchor(anchorPane,height * 0.6);
        AnchorPane.setLeftAnchor(anchorPane,width * 2.33);
        AnchorPane.setRightAnchor(anchorPane,width * 2.33);

        AnchorPane.setTopAnchor(tituloLabel, height * 0.05);
        AnchorPane.setLeftAnchor(tituloLabel,width * 0.8);

        AnchorPane.setTopAnchor(matriculaField, height * 0.25);
        AnchorPane.setLeftAnchor(matriculaField,width * 0.4);
        AnchorPane.setRightAnchor(matriculaField,width * 0.4);

        AnchorPane.setTopAnchor(senhaField, height * 0.42);
        AnchorPane.setLeftAnchor(senhaField,width * 0.4);
        AnchorPane.setRightAnchor(senhaField,width * 0.4);

        AnchorPane.setTopAnchor(checkBox, height * 0.6);
        AnchorPane.setLeftAnchor(checkBox, width * 0.4);

        AnchorPane.setTopAnchor(entrarButton, height * 0.73);
        AnchorPane.setLeftAnchor(entrarButton, width * 0.72);

        anchorPane.setId(ID_ANCHORPANE_CSS);
        matriculaField.setId(ID_TEXTFIELD_MATRICULA_CSS);
        senhaField.setId(ID_PASSWORDFIELD_SENHA_CSS);
        checkBox.setId(ID_CHECKBOX_MANTER_CONECTADO);
        entrarButton.setId(ID_BUTTON_ENTRAR);

    }

    public void handleLogin(){

        String erro = "";
        boolean erroEncontrado = false;


        if (matriculaField.getText().isEmpty() || senhaField.getText().isEmpty()){
            erroEncontrado = true;
            erro = "Preencha todos os Campos";
        }

        if (!erroEncontrado && !matriculaField.getText().matches(REGULAR_EXPRESSION_MATRICULA)){
            erroEncontrado = true;
            erro = "Informe uma matrícula válida";
        }

        if (erroEncontrado){
            showAlert("Erro no Processamento dos dados",erro);

        } else {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(URL_JANELA_CARREGAMENTO));

            Stage stage = (Stage) root.getScene().getWindow();

            try {
                Parent root = fxmlLoader.load();
                stage.getScene().setRoot(root);
                ControllerLoading controllerLoading = fxmlLoader.getController();

                if (controllerLoading != null){
                    controllerLoading.setMatricula(matriculaField.getText());
                    controllerLoading.setSenha(senhaField.getText());
                    controllerLoading.setSaveLoginData(checkBox.isSelected());
                    controllerLoading.processar();
                }

            } catch (IOException e){
                System.err.println("Erro: " + e.getMessage());

            }
        }
    }

    void showAlert(String headerText, String contentText){
        Stage stage = (Stage) root.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(stage.getTitle());
        alert.initOwner(stage);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.show();
    }

}
