package com.raphaelcollin.academicoiff.controller;

import com.raphaelcollin.academicoiff.model.Linha;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ControllerPrincipal {
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label labelNome;
    @FXML
    private GridPane gridPaneTop;
    @FXML
    private HBox hboxNome;
    @FXML
    private HBox hboxBotao;
    @FXML
    private ComboBox<String> anosComboBox;
    @FXML
    private ComboBox<String> periodosComboBox;

    private ChromeDriver driver;

    private Task<ObservableList<TableView<Linha>>> tarefa;

    private boolean janelaConfigurada = false;

    public void initialize(){

            // Configurando Estilos

        borderPane.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());
        borderPane.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");

    }

    @FXML
    public void handleCarregarNotas() {

        /* Definindo tarefa, usaremos a Classe Select que representa um select em HTML para
           selecionar no select do site o mesmo que está selecionado no combo box da Aplicação.
           Depois, iremos carregar a nova página do boletim e retornar os dados contido na tabela.
           Quando a tarefa se concluir, iremos colocar os novos dados na tela e configurar a janela */

        tarefa = new Task<ObservableList<TableView<Linha>>>() {
            @Override
            protected ObservableList<TableView<Linha>> call() {

                borderPane.getScene().setCursor(Cursor.WAIT);

                Select select = new Select(driver.findElementByName("cmbanos"));
                select.selectByIndex(anosComboBox.getSelectionModel().getSelectedIndex());

                Select select2 = new Select(driver.findElementByName(("cmbperiodos")));
                select2.selectByIndex(periodosComboBox.getSelectionModel().getSelectedIndex());

                driver.findElementByName("Exibir").click();
                return ControllerCarregamento.obterNotasAPartirDoHTML(driver.getPageSource());
            }
        };

        tarefa.setOnSucceeded(event ->{
            colocarDadosNaTabela(tarefa.getValue());
            configurarJanela(tarefa.getValue());
            borderPane.getScene().setCursor(Cursor.DEFAULT);
        });

        // Executando Thread

        new Thread(tarefa).start();

    }


    private void configurarJanela(ObservableList<TableView<Linha>> tables) {

            // Configurando e colocando tamanhos adequados para as colunas

        for (TableView<Linha> table : tables){

            table.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY);
            double tamanhoTabela = 0;

            for (TableColumn column : table.getColumns()){

                Text t = new Text( column.getText() );
                double max = t.getLayoutBounds().getWidth();
                for ( int i = 0; i < table.getItems().size(); i++ ) {

                    // A celula nao pode estar vazia

                    if ( column.getCellData( i ) != null ) {
                        t = new Text( column.getCellData( i ).toString() );
                        double calcwidth = t.getLayoutBounds().getWidth();

                        // guardar o valor na nova largura maximia

                        if ( calcwidth > max ) {
                            max = calcwidth;
                        }
                    }
                }

                // Colocar o tamanho da coluna como sendo a largura máxima somada a mais algum espaço

                if (table.getColumns().indexOf(column) == table.getColumns().size() -1) {
                    column.setPrefWidth(max + 30.0d );
                } else {
                    column.setPrefWidth( max + 25.0d );
                }

                // Configurações da coluna

                column.setResizable(false);
                column.setSortable(false);

                // Colocando os dados a partir da segunda coluna centralizados

                if (table.getColumns().indexOf(column) > 0 && !(table.getColumns().indexOf(column) ==
                        table.getColumns().size() -1)){
                    column.setStyle("-fx-alignment: CENTER;");
                }

                tamanhoTabela += column.getWidth();
            }

                // Configurando Altura da Tabela

            if (table.getItems().size() > 1){
                table.setPrefWidth(tamanhoTabela);
                table.setPrefHeight(table.getItems().size() * 34.5);
            }
            else {
                table.setPrefHeight(69);
            }

                // Configurando para não deixar que o usuário mude as ordens das colunas

            table.widthProperty().addListener((observable, oldValue, newValue) -> {
                TableHeaderRow header = (TableHeaderRow) table.lookup("TableHeaderRow");
                header.reorderingProperty().addListener((observable1, oldValue1, newValue1) ->
                        header.setReordering(false));
            });

        }

            // Se a janela ainda não estiver sido configurada

        if (!janelaConfigurada){

            // Colocando o Nome do Aluno no titulo

            List<WebElement> elements = driver.findElementsByClassName("barraRodape");
            labelNome.setText(elements.get(1).getText());

            // Configurando Titulo e Botao sair

            double tamanhoTabela = 0;

            for (TableColumn tableColumn : tables.get(tables.size() - 1).getColumns()){
                tamanhoTabela += tableColumn.getWidth();
            }

            hboxBotao.setPrefWidth((Toolkit.getDefaultToolkit().getScreenSize().width - tamanhoTabela) / 2);
            gridPaneTop.setPadding(new Insets(0,0,0,
                    (Toolkit.getDefaultToolkit().getScreenSize().width - tamanhoTabela) / 2));
            hboxNome.setPrefWidth(tamanhoTabela);


            // Configurando as combo box ano e periodo

            WebElement element = driver.findElementByName("cmbanos");
            String options = element.getText();
            String [] anos = options.split("\\s");
            ObservableList<String> anos2 = FXCollections.observableArrayList();

            for (String ano : anos){
                if (!ano.isEmpty()){
                    anos2.add(ano);
                }
            }

            anosComboBox.setItems(anos2);
            anosComboBox.getSelectionModel().selectFirst();


            WebElement element2 = driver.findElementByName("cmbperiodos");
            String options2 = element2.getText();
            String [] periodos = options2.split("\\s");
            ObservableList<String> periodos2 = FXCollections.observableArrayList();

            for (String periodo : periodos){
                if (!periodo.isEmpty()){
                    periodos2.add(periodo);
                }
            }

            periodosComboBox.setItems(periodos2);
            periodosComboBox.getSelectionModel().selectFirst();
            janelaConfigurada = true;
        }

    }

    // Nesse método, iremos configurar a janela para colocar a(s) tabela(s)

    void colocarDadosNaTabela(ObservableList<TableView<Linha>> tables){

        if (tables != null){

            VBox box = new VBox();
            box.setSpacing(20);
            box.setAlignment(Pos.TOP_CENTER);

            for (TableView<Linha> table : tables){
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.TOP_CENTER);

                table.getItems().remove(0);
                configurarJanela(tables);

                hbox.getChildren().add(table);

                box.getChildren().add(hbox);
            }

            borderPane.setCenter(box);

            BorderPane.setMargin(box,new Insets(20,0,0,
                    0));
        }
    }

        /* Esse método será executado quando usuário utilizar o botão sair.
           Iremos excluir o arquivo dados.txt caso ele exista para quando o usuário executar a aplicação
           novamente, ele ser redirecionado para a janela de autenticação.
           Depois, iremos finalizar a execução do driver e redirecionar o usuário para
           a janela autenticação */

    public void handleSair() {
        try {
            Files.delete(Paths.get("arquivos/dados.txt"));
        } catch (IOException e){
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {

            driver.findElementByName("Image11").click();

            driver.close();

            Stage stage = (Stage) borderPane.getScene().getWindow();

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/janela_autenticacao.fxml"));
                Parent root = fxmlLoader.load();
                root.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());
                root.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
                stage.getScene().setRoot(root);

                ControllerAutenticacao controllerAutenticacao = fxmlLoader.getController();

                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

                stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
                    if (stage.isMaximized()){
                        BorderPane.setMargin(controllerAutenticacao.getPanel(),new Insets(dimension.height * 0.05,
                                dimension.width * 0.3, dimension.height * 0.4,dimension.width * 0.3));
                    } else {
                        BorderPane.setMargin(controllerAutenticacao.getPanel(),new Insets(20));
                    }
                });


            } catch (IOException e){
                System.err.println("Erro: "+ e.getMessage());
                e.printStackTrace();
            }

        }

    }

    // Seter

    void setDriver(ChromeDriver driver) {
        this.driver = driver;
    }

}
