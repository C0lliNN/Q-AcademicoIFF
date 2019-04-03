package com.raphaelcollin.academicoiff.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.scene.layout.Panel;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.awt.*;
import java.io.*;

public class ControllerAutenticacao {

    @FXML
    private BorderPane borderPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Panel panel;
    @FXML
    private TextField matriculaField;
    @FXML
    private PasswordField senhaField;
    @FXML
    private CheckBox checkBox;

    private ChromeOptions options;

    private ChromeDriver driver;

    static final byte[] chave = "MATRICIFFRC0lliN".getBytes();

    private Stage stage;


    public void initialize(){

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        // Configurando Margens

        BorderPane.setMargin(panel,new Insets(dimension.height * 0.05,dimension.width * 0.3,
                dimension.height * 0.4,dimension.width * 0.3));

        GridPane.setMargin(matriculaField,new Insets(dimension.height * 0.05,0,0,0));

            // Image View

        imageView.setImage(new Image("file:arquivos/titulo.png"));
        imageView.setFitWidth(dimension.width * 0.36);
        imageView.setFitHeight(dimension.width * 0.1);

            // Inicializando o Web Driver no Background para acelerar o processo

        new Thread(() -> {
            options = new ChromeOptions();
            options.addArguments("--headless");

            System.setProperty("webdriver.chrome.driver", "arquivos/chromedriver.exe");
            driver = new ChromeDriver(options);

            driver.get("https://academico.iff.edu.br/qacademico/index.asp?t=1001");
        }).start();

    }

    public void handleLogin(){

            // Validação dos Dados

        String erro = "";
        boolean erroEncontrado = false;


        if (matriculaField.getText().isEmpty() || senhaField.getText().isEmpty()){
            erroEncontrado = true;
            erro = "Preencha todos os Campos";
        }

        if (!erroEncontrado && !matriculaField.getText().matches("\\d{12}")){
            erroEncontrado = true;
            erro = "Informe uma matrícula válida";
        }

        if (erroEncontrado){
            exibirAlert("Erro no Processamento dos dados",erro);

        } else {

                // Iniciando Janela de Carregamento

            ControllerCarregamento controllerCarregamento = initializarJanelaCarregamento();

                // Configurando Janela de Carregamento para o processamento dos dados

            if (controllerCarregamento != null){
                controllerCarregamento.setDriver(driver);
                controllerCarregamento.setMatricula(matriculaField.getText());
                controllerCarregamento.setSenha(senhaField.getText());
                controllerCarregamento.setSalvarDados(checkBox.isSelected());
                controllerCarregamento.processar();

            }

        }
    }

        // Método que exibirá um alert

    void exibirAlert(String cabecalho, String texto){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Acadêmico IFF");
        alert.initOwner(stage);
        alert.setHeaderText(cabecalho);
        alert.setContentText(texto);
        alert.show();

    }

    // Método que carregará a janela carregamento e retornará seu controller

    private ControllerCarregamento initializarJanelaCarregamento(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/janela_carregamento.fxml"));

        stage = (Stage) borderPane.getScene().getWindow();

        try {
            Parent root = fxmlLoader.load();
            root.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
            root.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());
            stage.getScene().setRoot(root);
            return fxmlLoader.getController();

        } catch (IOException e){
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

     // Getter

    public Panel getPanel() {
        return panel;
    }
}
