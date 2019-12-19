package com.raphaelcollin.academicoiff.controller;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro8.JMetro;
import jfxtras.styles.jmetro8.JMetro.Style;

import java.io.IOException;
import java.io.PrintWriter;

public class ControllerDashboard {

    @FXML
    private BorderPane root;
    @FXML
    private GridPane gridPaneNomeAluno;
    @FXML
    private HBox hboxNome;
    @FXML
    private Label labelNome;
    @FXML
    private HBox hBoxSairButton;
    @FXML
    private Button sairButton;
    @FXML
    private GridPane gridPaneAnoPeriodo;
    @FXML
    private Label anoLabel;
    @FXML
    private ComboBox<String> anoComboBox;
    @FXML
    private Label periodoLabel;
    @FXML
    private ComboBox<String> periodoComboBox;
    @FXML
    private Button carregarButton;

    private HtmlPage pagina;

    private Service<ObservableList<TableView<ObservableList<SimpleStringProperty>>>> serviceAtualizarNotas;

    private static final String NAME_SELECT_ANO_HTML = "cmbanos";
    private static final String NAME_SELECT_PERIODO_HTML = "cmbperiodos";
    private static final String NAME_INPUT_SUBMIT_EXIBIR_HTML = "Exibir";
    private static final String ID_BUTTON_SAIR_CSS = "sair-button";
    private static final String ID_BUTTON_CARREGAR_CSS = "carregar-button";
    private static final String XPATH_TD_NOME_ALUNO_HTML = "//td[contains(@class,'barraRodape')]";
    private static final String ID_PROGRESS_INDICATOR_CSS = "progress-indicator";
    private static final String ID_TABELA_CSS = "tabela";
    private static final String ID_ULTIMA_COLUNA_CSS = "ultimacoluna";
    private static final String URL_ARQUIVO_LOGIN = "arquivos/dados.txt";
    private static final String URL_JANELA_AUTENTICACAO = "/authentication_view.fxml";

    private Rectangle2D screenSize = Screen.getPrimary().getBounds();

    public void initialize(){

        // JMetro

        JMetro metro = new JMetro(Style.LIGHT);
        metro.applyTheme(root);

        serviceAtualizarNotas = new Service<ObservableList<TableView<ObservableList<SimpleStringProperty>>>>() {
            @Override
            protected Task<ObservableList<TableView<ObservableList<SimpleStringProperty>>>> createTask() {
                return new Task<ObservableList<TableView<ObservableList<SimpleStringProperty>>>>() {
                    @Override
                    protected ObservableList<TableView<ObservableList<SimpleStringProperty>>> call() throws Exception {

                        HtmlSelect selectAno = (HtmlSelect) pagina.getElementsByName(NAME_SELECT_ANO_HTML).get(0);
                        selectAno.setSelectedIndex(anoComboBox.getSelectionModel().getSelectedIndex());

                        HtmlSelect selectPeriodo = (HtmlSelect) pagina.getElementsByName(NAME_SELECT_PERIODO_HTML).get(0);
                        selectPeriodo.setSelectedIndex(periodoComboBox.getSelectionModel().getSelectedIndex());

                        pagina = pagina.getElementsByName(NAME_INPUT_SUBMIT_EXIBIR_HTML).get(0).click();
                        return ControllerLoading.obterDados(pagina);

                    }

                };
            }
        };

        serviceAtualizarNotas.setOnSucceeded(event -> colocarDadosNaTabela(serviceAtualizarNotas.getValue()));

        root.setPadding(new Insets(screenSize.getWidth() * 0.0104,0,0,0));
        ((GridPane) root.getTop()).setVgap(screenSize.getHeight() * 0.0277);
        gridPaneNomeAluno.setPadding(new Insets(0,0, screenSize.getHeight() * 0.00925,0));

        gridPaneAnoPeriodo.setHgap(screenSize.getWidth() * 0.04166);

        for (Node node : gridPaneAnoPeriodo.getChildren()) {
            HBox hBox = (HBox) node;
            hBox.setSpacing(screenSize.getWidth() * 0.0078);
        }

        labelNome.setStyle("-fx-font-size: " + screenSize.getWidth() *  0.02083 + "px");
        sairButton.setStyle("-fx-font-size: " + screenSize.getWidth() *  0.01145 + "px");

        periodoLabel.setStyle("-fx-font-size: " + screenSize.getWidth() * 0.0125 + "px");
        periodoComboBox.setStyle("-fx-font-size: " + screenSize.getWidth() * 0.009375 + "px");

        anoLabel.setStyle("-fx-font-size: " + screenSize.getWidth() * 0.0125 + "px");
        anoComboBox.setStyle("-fx-font-size: " + screenSize.getWidth() * 0.009375 + "px");

        carregarButton.setStyle("-fx-font-size: " + screenSize.getWidth() *  0.01145 + "px");

        sairButton.setId(ID_BUTTON_SAIR_CSS);
        carregarButton.setId(ID_BUTTON_CARREGAR_CSS);
    }

    void configurarCabecalho() {

            HtmlTableCell nomeAluno = (HtmlTableCell) pagina.getByXPath(XPATH_TD_NOME_ALUNO_HTML).get(1);
            labelNome.setText(nomeAluno.getTextContent().trim());


            double tamanhoTabela = 0;

            ObservableList<Node> list = ((AnchorPane) root.getCenter()).getChildren();
            TableView<ObservableList<SimpleStringProperty>> tableView = (TableView)
                    ((HBox) list.get(list.size() - 1)).getChildren().get(0);

            for (TableColumn<ObservableList<SimpleStringProperty>, ?> tableColumn : tableView.getColumns()){
                tamanhoTabela += tableColumn.getWidth();
            }

            hBoxSairButton.setPrefWidth((screenSize.getWidth() - tamanhoTabela) / 2);
            gridPaneNomeAluno.setPadding(new Insets(0,0,0, (screenSize.getWidth() - tamanhoTabela) / 2));
            hboxNome.setPrefWidth(tamanhoTabela);

            HtmlSelect selectAno = (HtmlSelect) pagina.getElementsByName(NAME_SELECT_ANO_HTML).get(0);
            ObservableList<String> anos = FXCollections.observableArrayList();

            for (HtmlOption option : selectAno.getOptions()){
                if (!option.getText().isEmpty()){
                    anos.add(option.getText().trim());
                }
            }

            anoComboBox.setItems(anos);
            anoComboBox.getSelectionModel().selectFirst();


            HtmlSelect selectPeriodo = (HtmlSelect) pagina.getElementsByName(NAME_SELECT_PERIODO_HTML).get(0);
            ObservableList<String> periodos = FXCollections.observableArrayList();

            for (HtmlOption option : selectPeriodo.getOptions()){
                if (!option.getText().trim().isEmpty()){
                    periodos.add(option.getText().trim());
                }
            }

            periodoComboBox.setItems(periodos);
            periodoComboBox.getSelectionModel().selectFirst();

    }

    void colocarDadosNaTabela(ObservableList<TableView<ObservableList<SimpleStringProperty>>> tables){

        if (tables != null){

            AnchorPane anchorPane = new AnchorPane();

            double position = 0.0;

            for (TableView<ObservableList<SimpleStringProperty>> table : tables){
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.TOP_CENTER);

                table.setId(ID_TABELA_CSS);

                configurarTabela(table);

                hbox.getChildren().add(table);

                anchorPane.getChildren().add(hbox);
                AnchorPane.setTopAnchor(hbox,position);
                position = position + screenSize.getHeight() * 0.09259;
                AnchorPane.setLeftAnchor(hbox,0.0);
                AnchorPane.setRightAnchor(hbox,0.0);
            }

            root.setCenter(anchorPane);

            BorderPane.setMargin(anchorPane,new Insets(screenSize.getHeight() * 0.046296,0,0, 0));
        }
    }


    private void configurarTabela(TableView<ObservableList<SimpleStringProperty>> tabela) {

        tabela.widthProperty().addListener((observable, oldValue, newValue) -> {
            TableHeaderRow header = (TableHeaderRow) tabela.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable1, oldValue1, newValue1) ->
                    header.setReordering(false));
        });

        double tamanhoTabela = 0;

        for (TableColumn column : tabela.getColumns()){

            StringBuilder estilo = new StringBuilder();

            if (tabela.getColumns().indexOf(column) > 0){

                estilo.append("-fx-padding: ").append(screenSize.getHeight() * 0.004629).append(" 0 ").
                append(screenSize.getHeight() * 0.004629).append(" ");

                if (tabela.getColumns().indexOf(column) == tabela.getColumns().size() - 1) {
                    estilo.append(screenSize.getHeight() * 0.004629).append(";");
                    estilo.append("-fx-alignment: LEFT;");

                    column.setId(ID_ULTIMA_COLUNA_CSS);

                } else {
                    estilo.append("0;");
                    estilo.append("-fx-alignment: CENTER;");
                }

            }

            estilo.append("-fx-font-size: ").append(screenSize.getWidth() * 0.008333).append("px");
            column.setStyle(estilo.toString());

            column.setSortable(false);
            column.setResizable(false);

            Text t = new Text(column.getText());
            double tamanhoMaiorCelulaColuna = t.getLayoutBounds().getWidth();
            for ( int i = 0; i < tabela.getItems().size(); i++ ) {

                if (column.getCellData(i) != null) {
                    t = new Text( column.getCellData( i ).toString() );
                    double calcwidth = t.getLayoutBounds().getWidth();

                    if (calcwidth > tamanhoMaiorCelulaColuna ) {
                        tamanhoMaiorCelulaColuna = calcwidth;
                    }
                }
            }

            double faixa1 = 0;
            double faixa2 = 0;

            if (screenSize.getWidth() >= 1600) {
                faixa1 = screenSize.getWidth() * 0.015;
                faixa2 = screenSize.getWidth() * 0.01;
            } else if (screenSize.getWidth() >= 1360) {
                faixa1 = screenSize.getWidth() * 0.004;
                faixa2 = screenSize.getWidth() * 0.003;
            } else if (screenSize.getWidth() >= 1024) {
                faixa1 = screenSize.getWidth() * 0.00006;
                faixa2 = screenSize.getWidth() * 0.00005;
            }

            if (tabela.getColumns().indexOf(column) == tabela.getColumns().size() -1) {
                column.setPrefWidth(tamanhoMaiorCelulaColuna + faixa1);
                column.setMinWidth(tamanhoMaiorCelulaColuna + faixa1);
                column.setMaxWidth(tamanhoMaiorCelulaColuna + faixa1);

            } else {
                column.setPrefWidth(tamanhoMaiorCelulaColuna + faixa2);
                column.setMinWidth(tamanhoMaiorCelulaColuna + faixa2);
                column.setMaxWidth(tamanhoMaiorCelulaColuna + faixa2);
            }

            tamanhoTabela += column.getWidth();

        }

        if (tamanhoTabela > screenSize.getWidth() * 0.95) {
            tabela.setPrefWidth(screenSize.getWidth() * 0.95);
            tabela.setMinWidth(screenSize.getWidth() * 0.95);
            tabela.setMaxWidth(screenSize.getWidth() * 0.95);
            tabela.setPrefWidth(tamanhoTabela);
            tabela.setMinWidth(tamanhoTabela);
            tabela.setMaxWidth(tamanhoTabela);
        }

        if (tabela.getItems().size() > 1){
            tabela.setPrefHeight(tabela.getItems().size() * (screenSize.getHeight() * 0.03287));
            tabela.setMinHeight(tabela.getItems().size() * (screenSize.getHeight() * 0.03287));
            tabela.setMaxHeight(tabela.getItems().size() * (screenSize.getHeight() * 0.03287));
        }
        else {
            tabela.setPrefHeight(screenSize.getHeight() * 0.06018);
            tabela.setMinHeight(screenSize.getHeight() * 0.06018);
            tabela.setMaxHeight(screenSize.getHeight() * 0.06018);
        }



    }


    @FXML
    public void handleCarregarNotas() {

        AnchorPane anchorPane = new AnchorPane();
        VBox vBox = new VBox(screenSize.getHeight() * 0.018518);
        vBox.setAlignment(Pos.CENTER);

        Label label = new Label("Carregando Notas...");
        label.setStyle("-fx-font-size: " + screenSize.getWidth() * 0.026 + "px");

        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setProgress(0);
        indicator.setId(ID_PROGRESS_INDICATOR_CSS);

        indicator.setPrefWidth(screenSize.getWidth() * 0.7);
        indicator.setPrefHeight(screenSize.getHeight() * 0.08);

        indicator.progressProperty().bind(serviceAtualizarNotas.progressProperty());

        vBox.getChildren().setAll(label,indicator);

        anchorPane.getChildren().add(vBox);
        AnchorPane.setTopAnchor(vBox, screenSize.getHeight() * 0.23148);
        AnchorPane.setLeftAnchor(vBox,0.0);
        AnchorPane.setRightAnchor(vBox,0.0);

        root.setCenter(anchorPane);

        serviceAtualizarNotas.restart();

    }

    @FXML
    public void handleSair() {
        try (PrintWriter writer = new PrintWriter(URL_ARQUIVO_LOGIN)) {
            writer.print("");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

        Stage stage = (Stage) root.getScene().getWindow();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(URL_JANELA_AUTENTICACAO));
            Parent root = fxmlLoader.load();
            stage.getScene().setRoot(root);

        } catch (IOException e){
            System.err.println("Erro: "+ e.getMessage());
        }

    }

    void setPagina(HtmlPage pagina) {
        this.pagina = pagina;
    }

}
