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

public class ControllerCarregamento {

    // Controles

    @FXML
    private GridPane root;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label label;
    @FXML
    private Button button;

    // Atributos cujos valores virão da janela autenticação ou do arquivo dados.txt

    private String matricula;

    private String senha;

    private boolean salvarDados;

    // Constantes

    private static final String ID_PROGRESS_INDICATOR_CSS = "progress-indicator";
    private static final String ID_BUTTON_SAIR_CSS = "voltar-button";
    private static final int TIMEOUT = 10000; // 10 Segundos
    private static final String URL_PAGINA_LOGIN_HTML = "https://academico.iff.edu.br/qacademico/index.asp?t=1001";
    private static final String NAME_FORM_HTML = "frmLogin";
    private static final String NAME_INPUT_MATRICULA_HTML = "LOGIN";
    private static final String NAME_INPUT_SENHA_HTML = "SENHA";
    private static final String NAME_INPUT_SUBMIT_HTML = "Submit";
    private static final String XPATH_LINK_BOLETIM_HTML = "//a[contains(@href,'/qacademico/alunos/boletim/index.asp')]";
    private static final String URL_JANELA_PRINCIPAL = "/dashboard_view.fxml";
    private static final String URL_JANELA_AUTENTICACAO = "/janela_autenticacao.fxml";
    private static final String XPATH_TABLE_HTML = "//table[contains(@width,'95%')]";
    private static final String URL_ARQUIVO_LOGIN = "arquivos/dados.txt";

    // Variáveis de Controle

    private boolean erroLogin = false;

    private boolean erroTimeout = false;

    private boolean erroInesperado = false;

    // Services

        /* Fará a autenticação, retirada e o carregamento dos dados do boletim */
    private Service<ObservableList<TableView<ObservableList<SimpleStringProperty>>>> serviceObterNotas;

        /* Salvará em um arquivo as informações de login do usuário(Matricula e Senha) caso o mesmo tenha selecionado essa
        *  opção na janela de autenticação */

    private Service serviceSalvarLogin;

    // HTMLPAGE
    private HtmlPage pagina;

    public void initialize(){

        // JMetro

        JMetro metro = new JMetro(Style.LIGHT);
        metro.applyTheme(root);

        Rectangle2D tamanhoTela = Screen.getPrimary().getBounds();

            // Configurando o tamanho dos controles

        root.setVgap(tamanhoTela.getHeight() * 0.037);

        progressIndicator.setPrefWidth(tamanhoTela.getWidth() * 0.7);
        progressIndicator.setPrefHeight(tamanhoTela.getHeight() * 0.08);

            // CSS

        label.setStyle("-fx-font-size: " + tamanhoTela.getWidth() * 0.026 + "px");
        button.setStyle("-fx-font-size: " + tamanhoTela.getWidth() * 0.015 + "px");

            // Definindo ID

        progressIndicator.setId(ID_PROGRESS_INDICATOR_CSS);
        button.setId(ID_BUTTON_SAIR_CSS);

            // Definindo o Service Obter Nota

        serviceObterNotas = new Service<ObservableList<TableView<ObservableList<SimpleStringProperty>>>>() {
            @Override
            protected Task<ObservableList<TableView<ObservableList<SimpleStringProperty>>>> createTask() {
                return new Task<ObservableList<TableView<ObservableList<SimpleStringProperty>>>>() {
                    @Override
                    protected ObservableList<TableView<ObservableList<SimpleStringProperty>>> call() {

                        updateMessage("Conectando...");

                        WebClient cliente = new WebClient(); // Client do HTMLUnit

                        // Definindo configurações do navegador para conferir-lo maior eficiência

                        cliente.getOptions().setCssEnabled(false);
                        cliente.getOptions().setDownloadImages(false);
                        cliente.getOptions().setThrowExceptionOnScriptError(false);
                        cliente.getOptions().setThrowExceptionOnFailingStatusCode(false);
                        cliente.getOptions().setUseInsecureSSL(true);
                        cliente.getOptions().setTimeout(TIMEOUT); // Tempo máximo de Execução da Thread

                        /* Todos esses blocos try-catch são necessário para caso haja ocorra algum erro, a thread seja cancelada
                           e a aplicação retorne para janela de autenticação */

                        try {
                            try {
                                pagina = cliente.getPage(URL_PAGINA_LOGIN_HTML); // Acessando página de login
                            } catch (IOException e) {
                                System.out.println("Erro: " + e.getMessage());
                                erroInesperado = true;
                                cancel();
                            }

                            updateMessage("Autenticando...");

                            // Preenchendo o Formulário

                            HtmlForm formulario;
                            HtmlInput matriculaInput;
                            HtmlInput senhaInput ;
                            HtmlInput okSubmitInput = null;

                            try {
                                formulario = pagina.getFormByName(NAME_FORM_HTML);
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
                                pagina = okSubmitInput.click(); // Mudando de Página
                            } catch (IOException e) {
                                System.out.println("Erro: " + e.getMessage());
                                erroInesperado = true;
                                cancel();
                            }

                            updateMessage("Obtendo Notas...");
                            HtmlAnchor link = null; // Obtendo link para página de Boletim

                            /* Caso não tenha sido possível obter o link, será sinal de que o logn não foi efetuado com sucesso.
                             *  Nessa situação, será gerada uma Exception que indica que houve erro no login */

                            try {
                                link = (HtmlAnchor) pagina.getByXPath(XPATH_LINK_BOLETIM_HTML).get(0);
                            } catch (Exception e) {
                                System.out.println("Erro: " + e.getMessage());
                                erroLogin = true;
                                cancel();
                            }

                            try {
                                pagina = link.click(); // Mudando de Página
                            } catch (IOException e) {
                                System.out.println("Erro: " + e.getMessage());
                                erroInesperado = true;
                                cancel();
                            }

                            return obterDados(pagina);

                        } catch (ScriptException exception) {
                            erroTimeout = true;
                            cancel();
                            return null;
                        }
                    }
                };
            }
        };

            /* Caso o Service tenha sido executado com sucesso, a janela principal contendo os dados em forma de tabela(s)
            *  será exibida  para o usuário */

        serviceObterNotas.setOnSucceeded(event -> {

            FXMLLoader fxmlLoader;
            try {

                if (serviceObterNotas.getValue() != null){

                    fxmlLoader = new FXMLLoader(getClass().getResource(URL_JANELA_PRINCIPAL));
                    Parent root = fxmlLoader.load();

                    ControllerPrincipal controllerPrincipal = fxmlLoader.getController();
                    controllerPrincipal.setPagina(pagina);
                    controllerPrincipal.colocarDadosNaTabela(serviceObterNotas.getValue());
                    controllerPrincipal.configurarCabecalho();

                    Stage stage = (Stage) this.root.getScene().getWindow();
                    stage.getScene().setRoot(root);

                    /* Se o usuário tiver marcado o CheckBox "Mantenha-me conectado" da janela autenticação, deve-se salvar
                       os dados no arquivo dados.txt o que será feito pelo serviceSalvarLogin */

                    if (salvarDados) {
                        serviceSalvarLogin.start();
                    }

                } else {
                    serviceObterNotas.cancel(); // Executa no Evento abaixo que recarrega a janela autenticação, exibindo um alert de erro
                }

            } catch (IOException e){
                System.err.println("Erro: " + e.getMessage());
            }

        });

        /* Caso tenha ocorrido algum erro na obtenção dos dados, a janela autenticação será novamente carregada, exibindo um alert
        *  com o erro ocorrido. */

        serviceObterNotas.setOnCancelled(event -> {
            Stage stage = (Stage) root.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(URL_JANELA_AUTENTICACAO));
            try {
                Parent root = fxmlLoader.load();
                stage.getScene().setRoot(root);

                ControllerAutenticacao controllerAutenticacao = fxmlLoader.getController();

                if (erroLogin) {
                    controllerAutenticacao.exibirAlert("Erro no Login","Matrícula ou senha inválido(a)");
                } else if (erroTimeout) {
                    controllerAutenticacao.exibirAlert("Tempo Excedido","Verifique sua conexão com a Internet");
                } else if (erroInesperado) {
                    controllerAutenticacao.exibirAlert("Erro no Login","Ocorreu um erro inesperado! Tente novamente mais tarde");
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

                    // Escrevendo dados

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

            // Bindando progressIndicator e Label com o Service de Obtençao das notas

        progressIndicator.progressProperty().bind(serviceObterNotas.progressProperty());
        label.textProperty().bind(serviceObterNotas.messageProperty());

    }

    public void processar(){

            // �?ncio da Execução da Thread

        serviceObterNotas.start();
    }

        /* Método que recebera a matricula, senha, e uma variável que indicará se é para
           salvar os dados ou não. Com esses dados o driver vai entrar no site no Acadêmico e
           tentar fazer login com esses dados. Caso consiga ele vai na página do boletim e retornará
           o arquivo html do página boletim em formato de String. */

    static ObservableList<TableView<ObservableList<SimpleStringProperty>>> obterDados(HtmlPage pagina){

        // Lista que contem todas as tabelas de notas em formato HTML

        List<HtmlTable> tabelasHtml = pagina.getByXPath(XPATH_TABLE_HTML);

        // Lista que conterá todas as tabelas como objetos da classe TableView configuradas e preenchidas

        ObservableList<TableView<ObservableList<SimpleStringProperty>>> tablesView =
                FXCollections.observableArrayList();

        for (HtmlTable tabelaHtml : tabelasHtml) { // Percorrendo Todas as Tabelas encontradas

            TableView<ObservableList<SimpleStringProperty>> tableView = new TableView<>(); // Criando TableView

            for (int i = 0; i < tabelaHtml.getRow(1).getCells().size(); i++) {

                // Criando, configurando e adicionando as colunas da Tabela

                /* Como o número de colunas é muito variável, dependendo do curso do aluno. Não foi possível
                 *  usar objetos estáticos para preencher a tabela. Os dados são alocados dinamicamente usando
                 *  uma ObservableList<SimpleStringProperty> */

                TableColumn<ObservableList<SimpleStringProperty>, String> coluna =
                        new TableColumn<>(tabelaHtml.getRow(1).getCells().get(i).getTextContent().trim());
                int posicaoAtual = i;
                coluna.setCellValueFactory(celula -> celula.getValue().get(posicaoAtual));
                tableView.getColumns().add(coluna);
            }

            // Criando Lista que conterá todos os dados da tela, ou seja, as disciplinas e notas

            ObservableList<ObservableList<SimpleStringProperty>> dados = FXCollections.observableArrayList();

                                    /* Começa-se a partir da terceira linha pois a primeira e segunda linha da tabela do documento
                                       HTML referem-se respectivamente ao Título e cabeçalho da Tabela. */

            for (HtmlTableRow linha : tabelaHtml.getRows().subList(2, tabelaHtml.getRowCount())) {

                // Lista que conterá os dados de uma linha, ou seja, de uma disciplina

                ObservableList<SimpleStringProperty> linhaDados = FXCollections.observableArrayList();

                for (HtmlTableCell celula : linha.getCells()) {
                    // Adicionado células a linha de dados
                    linhaDados.add(new SimpleStringProperty(celula.getTextContent().trim()));
                }
                dados.add(linhaDados);
            }

            tableView.setItems(dados);
            tablesView.add(tableView);

        }

        return tablesView;

    }

    // Cancela a execução da Thread e exclui o login salvo caso o mesmo exista

    @FXML
    public void handleVoltar() {
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

    public void setSalvarDados(boolean salvarDados) {
        this.salvarDados = salvarDados;
    }

}
