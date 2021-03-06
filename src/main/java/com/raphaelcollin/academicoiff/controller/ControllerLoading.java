package com.raphaelcollin.academicoiff.controller;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.raphaelcollin.academicoiff.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro8.JMetro;
import jfxtras.styles.jmetro8.JMetro.Style;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.util.List;

public class ControllerLoading {

    @FXML
    private GridPane root;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label label;
    @FXML
    private Button button;

    private String matricula;

    private String senha;

    private boolean saveLoginData;

    private static final String ID_PROGRESS_INDICATOR_CSS = "progress-indicator";
    private static final String ID_BUTTON_SAIR_CSS = "voltar-button";
    private static final int TIMEOUT = 10000;
    private static final String URL_PAGINA_LOGIN_HTML = "https://academico.iff.edu.br/qacademico/index.asp?t=1001";
    private static final String NAME_FORM_HTML = "frmLogin";
    private static final String NAME_INPUT_MATRICULA_HTML = "LOGIN";
    private static final String NAME_INPUT_SENHA_HTML = "SENHA";
    private static final String NAME_INPUT_SUBMIT_HTML = "Submit";
    private static final String XPATH_LINK_BOLETIM_HTML = "//a[contains(@href,'/qacademico/alunos/boletim/index.asp')]";
    private static final String URL_JANELA_PRINCIPAL = "/dashboard_view.fxml";
    private static final String URL_JANELA_AUTENTICACAO = "/authentication_view.fxml";
    private static final String XPATH_TABLE_HTML = "//table[contains(@width,'95%')]";
    private static final String URL_ARQUIVO_LOGIN = "files/dados.txt";

    private boolean erroLogin = false;
    private boolean erroTimeout = false;
    private boolean erroInesperado = false;

    private Service<ObservableList<TableView<ObservableList<SimpleStringProperty>>>> serviceObterNotas;

    private Service serviceSalvarLogin;

    private HtmlPage page;

    public void initialize(){

        JMetro metro = new JMetro(Style.LIGHT);
        metro.applyTheme(root);

        Rectangle2D screenSize = Screen.getPrimary().getBounds();

        root.setVgap(screenSize.getHeight() * 0.037);

        progressIndicator.setPrefWidth(screenSize.getWidth() * 0.7);
        progressIndicator.setPrefHeight(screenSize.getHeight() * 0.08);

        label.setStyle("-fx-font-size: " + screenSize.getWidth() * 0.026 + "px");
        button.setStyle("-fx-font-size: " + screenSize.getWidth() * 0.015 + "px");

        progressIndicator.setId(ID_PROGRESS_INDICATOR_CSS);
        button.setId(ID_BUTTON_SAIR_CSS);

        serviceObterNotas = new Service<ObservableList<TableView<ObservableList<SimpleStringProperty>>>>() {
            @Override
            protected Task<ObservableList<TableView<ObservableList<SimpleStringProperty>>>> createTask() {
                return new Task<ObservableList<TableView<ObservableList<SimpleStringProperty>>>>() {
                    @Override
                    protected ObservableList<TableView<ObservableList<SimpleStringProperty>>> call() {

                        updateMessage("Conectando...");

                        WebClient client = new WebClient(); // HTMLUnit Client

                        client.getOptions().setCssEnabled(false);
                        client.getOptions().setDownloadImages(false);
                        client.getOptions().setThrowExceptionOnScriptError(false);
                        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
                        client.getOptions().setUseInsecureSSL(true);
                        client.getOptions().setTimeout(TIMEOUT); //

                        try {
                            try {
                                page = client.getPage(URL_PAGINA_LOGIN_HTML); // Acessando página de login
                            } catch (IOException e) {
                                System.out.println("Erro: " + e.getMessage());
                                erroInesperado = true;
                                cancel();
                            }

                            updateMessage("Autenticando...");

                            HtmlForm formulario;
                            HtmlInput matriculaInput;
                            HtmlInput senhaInput ;
                            HtmlInput okSubmitInput = null;

                            try {
                                formulario = page.getFormByName(NAME_FORM_HTML);
                                matriculaInput = formulario.getInputByName(NAME_INPUT_MATRICULA_HTML);
                                senhaInput = formulario.getInputByName(NAME_INPUT_SENHA_HTML);
                                okSubmitInput = formulario.getInputByName(NAME_INPUT_SUBMIT_HTML);
                                matriculaInput.setValueAttribute(matricula);
                                senhaInput.setValueAttribute(senha);
                            } catch (Exception e){
                                System.out.println("Erro: " + e.getMessage());
                                cancel();
                            }

                            try {
                                page = okSubmitInput.click();
                            } catch (IOException e) {
                                System.out.println("Erro: " + e.getMessage());
                                erroInesperado = true;
                                cancel();
                            }

                            updateMessage("Obtendo Notas...");
                            HtmlAnchor link = null;


                            try {
                                link = (HtmlAnchor) page.getByXPath(XPATH_LINK_BOLETIM_HTML).get(0);
                            } catch (Exception e) {
                                System.out.println("Erro: " + e.getMessage());
                                erroLogin = true;
                                cancel();
                            }

                            try {
                                page = link.click();
                            } catch (IOException e) {
                                System.out.println("Erro: " + e.getMessage());
                                erroInesperado = true;
                                cancel();
                            }

                            return obterDados(page);

                        } catch (ScriptException exception) {
                            erroTimeout = true;
                            cancel();
                            return null;
                        }
                    }
                };
            }
        };

        serviceObterNotas.setOnSucceeded(event -> {

            FXMLLoader fxmlLoader;
            try {

                if (serviceObterNotas.getValue() != null){

                    fxmlLoader = new FXMLLoader(getClass().getResource(URL_JANELA_PRINCIPAL));
                    Parent root = fxmlLoader.load();

                    ControllerDashboard controllerDashboard = fxmlLoader.getController();
                    controllerDashboard.setPagina(page);
                    controllerDashboard.colocarDadosNaTabela(serviceObterNotas.getValue());
                    controllerDashboard.configurarCabecalho();

                    Stage stage = (Stage) this.root.getScene().getWindow();
                    stage.getScene().setRoot(root);


                    if (saveLoginData) {
                        serviceSalvarLogin.start();
                    }

                } else {
                    serviceObterNotas.cancel();
                }

            } catch (IOException e){
                System.err.println("Erro: " + e.getMessage());
            }

        });

        serviceObterNotas.setOnCancelled(event -> {
            Stage stage = (Stage) root.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(URL_JANELA_AUTENTICACAO));
            try {
                Parent root = fxmlLoader.load();
                stage.getScene().setRoot(root);

                ControllerAuthentication controllerAuthentication = fxmlLoader.getController();

                if (erroLogin) {
                    controllerAuthentication.showAlert("Erro no Login","Matrícula ou senha inválido(a)");
                } else if (erroTimeout) {
                    controllerAuthentication.showAlert("Tempo Excedido","Verifique sua conexão com a Internet");
                } else if (erroInesperado) {
                    controllerAuthentication.showAlert("Erro no Login","Ocorreu um erro inesperado! Tente novamente mais tarde");
                }

            } catch (IOException e) {
                System.out.println("Erro: " + e.getMessage());
            }

        });

        serviceSalvarLogin = new Service() {
            @Override
            protected Task createTask() {

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(URL_ARQUIVO_LOGIN))) {

                    // Encriptando matricula e senha que serão guardados no arquivo dados.txt

                    Key secretKey = new SecretKeySpec(Main.CHAVE_ENCRYPT.getBytes(), Main.ALGORITIMO_ECRYPT);

                    Cipher c = Cipher.getInstance(Main.ALGORITIMO_ECRYPT);
                    c.init(Cipher.ENCRYPT_MODE, secretKey);

                    byte[] cipher = c.doFinal(matricula.getBytes());
                    String matriculaEncrypted = new BASE64Encoder().encode(cipher);

                    byte[] cipher2 = c.doFinal(senha.getBytes());
                    String senhaEncrypted = new BASE64Encoder().encode(cipher2);

                    writer.write(matriculaEncrypted);
                    writer.newLine();
                    writer.write(senhaEncrypted);


                } catch (Exception e) {
                    System.err.println("Erro: " + e.getMessage());
                    e.printStackTrace();
                }


                return null;
            }

        };

        progressIndicator.progressProperty().bind(serviceObterNotas.progressProperty());
        label.textProperty().bind(serviceObterNotas.messageProperty());

    }

    public void processar(){

        serviceObterNotas.start();
    }

    static ObservableList<TableView<ObservableList<SimpleStringProperty>>> obterDados(HtmlPage pagina){

        List<HtmlTable> tabelasHtml = pagina.getByXPath(XPATH_TABLE_HTML);

        ObservableList<TableView<ObservableList<SimpleStringProperty>>> tablesView =
                FXCollections.observableArrayList();

        for (HtmlTable tabelaHtml : tabelasHtml) {

            TableView<ObservableList<SimpleStringProperty>> tableView = new TableView<>();

            for (int i = 0; i < tabelaHtml.getRow(1).getCells().size(); i++) {

                TableColumn<ObservableList<SimpleStringProperty>, String> coluna =
                        new TableColumn<>(tabelaHtml.getRow(1).getCells().get(i).getTextContent().trim());
                int posicaoAtual = i;
                coluna.setCellValueFactory(celula -> celula.getValue().get(posicaoAtual));
                tableView.getColumns().add(coluna);
            }

            ObservableList<ObservableList<SimpleStringProperty>> dados = FXCollections.observableArrayList();

            for (HtmlTableRow linha : tabelaHtml.getRows().subList(2, tabelaHtml.getRowCount())) {

                ObservableList<SimpleStringProperty> linhaDados = FXCollections.observableArrayList();

                for (HtmlTableCell celula : linha.getCells()) {
                    linhaDados.add(new SimpleStringProperty(celula.getTextContent().trim()));
                }
                dados.add(linhaDados);
            }

            tableView.setItems(dados);
            tablesView.add(tableView);

        }

        return tablesView;

    }

    @FXML
    public void handleCancel() {
        try (PrintWriter writer = new PrintWriter(URL_ARQUIVO_LOGIN)) {
            writer.print("");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        serviceObterNotas.cancel();
    }

        // Getters e Setters

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setSaveLoginData(boolean saveLoginData) {
        this.saveLoginData = saveLoginData;
    }

}
