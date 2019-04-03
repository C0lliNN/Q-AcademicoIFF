package com.raphaelcollin.academicoiff.controller;

import com.raphaelcollin.academicoiff.model.Linha;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerCarregamento {
    @FXML
    private GridPane gridPane;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label label;

    private ChromeDriver driver;

    private String matricula;

    private String senha;

    private boolean salvarDados;

    private Task<ObservableList<TableView<Linha>>> tarefa;

    public void initialize(){
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

            // Configurando o tamanho do progressIndicator

        progressIndicator.setPrefWidth(dimension.width * 0.7);
        progressIndicator.setPrefHeight(dimension.height * 0.08);

            // Definindo a Task

        tarefa = new Task<ObservableList<TableView<Linha>>>() {
            @Override
            protected ObservableList<TableView<Linha>> call(){

                if (driver == null){
                    iniciarChromeDriver();
                }

                String html = obterArquivoHTML(matricula,senha,salvarDados);

                if (html == null){
                    return null;
                }

               return obterNotasAPartirDoHTML(html);

            }
        };

            // Definindo o que acontecerá quanto a Task terminar

        tarefa.setOnSucceeded(event -> {

            FXMLLoader fxmlLoader;
            Parent root = null;
            ControllerAutenticacao controllerAutenticacao = null;
            try {

                if (tarefa.getValue() == null){

                    fxmlLoader = new FXMLLoader(getClass().getResource("/janela_autenticacao.fxml"));
                    root = fxmlLoader.load();

                    controllerAutenticacao = fxmlLoader.getController();

                    driver.close();

                } else {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/janela_principal.fxml"));
                    root = fxmlLoader.load();

                    ControllerPrincipal controllerPrincipal = fxmlLoader.getController();
                    controllerPrincipal.setDriver(driver);
                    controllerPrincipal.colocarDadosNaTabela(tarefa.getValue());

                }

            } catch (IOException e){
                System.err.println("Erro: " + e.getMessage());
            } finally {

                if (root != null){

                    root.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());
                    root.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");

                    Stage stage = (Stage) gridPane.getScene().getWindow();


                    stage.getScene().setRoot(root);

                    if (controllerAutenticacao != null){
                        controllerAutenticacao.exibirAlert("Erro no Login","Matrícula ou senha inválido(a)");
                    }


                }

            }

        });

            // Bindando progressIndicator com a Task

        progressIndicator.progressProperty().bind(tarefa.progressProperty());

    }

    public void processar(){

            // Íncio da Execução da Thread

        new Thread(tarefa).start();
    }

        // Método que Inicia o ChromeDriver

    private void iniciarChromeDriver(){

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        System.setProperty("webdriver.chrome.driver", "arquivos/chromedriver.exe");

        driver = new ChromeDriver(options);

        driver.get("https://academico.iff.edu.br/qacademico/index.asp?t=1001");

    }

        /* Método que recebera a matricula, senha, e uma variável que indicará se é para
           salvar os dados ou não. Com esses dados o driver vai entrar no site no Acadêmico e
           tentar fazer login com esses dados. Caso consiga ele vai na página do boletim e retornará
           o arquivo html do página boletim em formato de String. */

    private String obterArquivoHTML(String matricula, String senha, boolean salvarDados){
        WebElement elementoLogin = driver.findElementByName("LOGIN");
        elementoLogin.sendKeys(matricula);

        WebElement elementoSenha = driver.findElementByName("SENHA");
        elementoSenha.sendKeys(senha);
        elementoSenha.submit();

        if (!driver.getCurrentUrl().equals("https://academico.iff.edu.br/qacademico/index.asp?t=2000")){
            return null;
        }

        WebElement element = driver.findElementByLinkText("Boletim");
        element.click();

        /* Agora que o login foi efetuado com sucesso, caso a variável salvarDados for igual a true
           iremos salvar o login e a senha encryptada no arquivo dados.txt .*/

        if (salvarDados){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("arquivos/dados.txt"))) {

                // Encriptando senha que será guardado no arquivo dados.txt...

                byte[] senhaEncrypt = senha.getBytes();

                Key secretKey = new SecretKeySpec(ControllerAutenticacao.chave,"AES");

                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] cipher = c.doFinal(senhaEncrypt);

                String senhaEncrypted = new BASE64Encoder().encode(cipher);

                // Escrevendo dados

                writer.write(matricula);
                writer.newLine();
                writer.write(senhaEncrypted);


            } catch (Exception e){
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return driver.getPageSource();
    }

    /* Método que receberá o arquivo fonte da do boletim em forma de String e retornará
       uma lista com uma ou mais tabelas de notas. No acadêmico, em determinados períodos, o aluno pode
       cursar diferentes grupos de disciplinas dando origem a mais de uma tabela. Os dados serão retirados
       da tabela usando expressões regulares. */

    static ObservableList<TableView<Linha>> obterNotasAPartirDoHTML(String html){

        html = html.replaceAll("\n",""); // Corrigir bug

        Pattern pattern = Pattern.compile(".*?<table width=\"95%\".*?>(.*?)</table>.*?");

        Matcher matcher = pattern.matcher(html);

        ObservableList<TableView<Linha>> tables = FXCollections.observableArrayList();

        while (matcher.find()){
            String table = matcher.group(1);

                // Tratamento dos dados

            table = table.replaceAll(",[^0-9]","");
            table = table.replaceAll("<tr.*?>","<tr>");
            table = table.replaceAll("<td.*?>","<td>");

            Pattern trPatter = Pattern.compile(".*?(<tr>.*?</tr>).*?");
            Matcher trMatcher = trPatter.matcher(table);

            int count = 0;

            List<String> trs = new ArrayList<>();

            while (trMatcher.find()){

                if (count > 0){

                        // Tratamento dos dados

                    String tr = trMatcher.group(1);
                    tr = tr.replaceAll(" *<","<");
                    tr = tr.replaceAll("> *",">");
                    tr = tr.replaceAll("<div.*?>|</div>|<a.*?>|</a>|<q_latente.*?>|</q_latente>|[\n\t]|","");
                    tr = tr.replaceAll("<tr>","\t<tr>\n\t\t");
                    tr = tr.replaceAll("</tr>","\n\t</tr>");
                    tr = tr.replaceAll("</td><td>","</td>\n\t\t<td>");
                    tr = tr.replaceAll("<td></td>","<td>&nbsp;</td>");
                    trs.add(tr);
                }

                count++;
            }


            ObservableList<Linha> linhas = FXCollections.observableArrayList();

            /* Essa variável guardará o número de colunas de cada tabela. O máximo é 22
               Essa variável será útil na hora da criação da Table View */

            int numeroColunas = 0;

            for (String tr : trs){

                // Criando lista de dados

                Pattern tdPatter = Pattern.compile("<td>(.*?)</td>");
                Matcher tdMatcher = tdPatter.matcher(tr);

                String c1 = "";
                String c2 = "";
                String c3 = "";
                String c4 = "";
                String c5 = "";
                String c6 = "";
                String c7 = "";
                String c8 = "";
                String c9 = "";
                String c10 = "";
                String c11 = "";
                String c12 = "";
                String c13 = "";
                String c14 = "";
                String c15 = "";
                String c16 = "";
                String c17 = "";
                String c18 = "";
                String c19 = "";
                String c20 = "";
                String c21 = "";
                String c22 = "";
                String c23 = "";

                numeroColunas = 0;

                if(tdMatcher.find()){
                    c1 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }

                if(tdMatcher.find()){
                    c2 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }

                if(tdMatcher.find()){
                    c3 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c4 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c5 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c6 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c7 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c8 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c9 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c10 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c11 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c12 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c13 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c14 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c15 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c16 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c17 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c18 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c19 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c20 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c21 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c22 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }
                if(tdMatcher.find()){
                    c23 = tdMatcher.group(1).replaceAll("&nbsp;","");
                    numeroColunas++;
                }

                // Adicionando linha a lista

                linhas.add(new Linha(c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c11,c12,
                        c13,c14,c15,c16, c17,c18,c19,c20,c21,c22,c23));

            }

                // Criando Table View

            TableView<Linha> tableView = new TableView<>();

                // Criando as Colunas

            for (int c = 0; c < numeroColunas; c++){
                TableColumn<Linha, SimpleStringProperty> tableColumn = new TableColumn<>();

                    // Colocando Titulo da Coluna

                tableColumn.setText(linhas.get(0).get(c+1));

                    // Colocando CellFactory

                tableColumn.setCellValueFactory(new PropertyValueFactory<>("c"+(c+1)));

                    // Adicionando Colunas

                tableView.getColumns().add(tableColumn);
            }

            tableView.setItems(linhas);
            tables.add(tableView);
        }

        return tables;
    }

        // Getters e Setters

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setSalvarDados(boolean salvarDados) {
        this.salvarDados = salvarDados;
    }

    void setDriver(ChromeDriver driver) {
        this.driver = driver;
    }

    public Label getLabel() {
        return label;
    }
}
