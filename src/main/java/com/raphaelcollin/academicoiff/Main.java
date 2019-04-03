package com.raphaelcollin.academicoiff;

import com.raphaelcollin.academicoiff.controller.ControllerAutenticacao;
import com.raphaelcollin.academicoiff.controller.ControllerCarregamento;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sun.misc.BASE64Decoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;

public class Main extends Application {

    /* Caso o usuário já tenha logado ativando a check box 'Mantenha-me conectado' os seus dados
       estarão guardados no arquivo dados.txt. Quando isso acontecer, a aplicação já iniciará
       na tela de carregamento. Caso contrário, a aplicação iniciára na tela de autenticação */

    // OBS: A senha que será guardada no arquivo estará encryptada

    @Override
    public void start(Stage primaryStage) throws Exception{

            // Variáveis que definirão qual janela será executada

        FXMLLoader fxmlLoader;
        Parent root = null;

        String matricula;
        String senha;

        /* So existirá o arquivo dados.txt caso o login já estiver acontecido. Caso contrário, o arquivo
           dados.txt não existira, gerando um catch que carregará a janela autenticação */


        try (BufferedReader reader = new BufferedReader(new FileReader("arquivos/dados.txt"))) {

                // Lendo matricula e senha do arquivo dados.txt

            matricula = reader.readLine();
            String senhaEncryptada = reader.readLine();

                // Decriptando senha

            Key secretKey = new SecretKeySpec("MATRICIFFRC0lliN".getBytes(),"AES");

            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] byteDecmensagem = new BASE64Decoder().decodeBuffer(senhaEncryptada);
            byte[] byteDecodifica = c.doFinal(byteDecmensagem);

            senha = new String(byteDecodifica);

                // Carregando janela carregamento

            fxmlLoader = new FXMLLoader(getClass().getResource("/janela_carregamento.fxml"));
            root = fxmlLoader.load();

            ControllerCarregamento controllerCarregamento = fxmlLoader.getController();

                // Configurando Label

            controllerCarregamento.getLabel().setText("Carregando Notas");

                // Configurando controller para o processamento

            controllerCarregamento.setMatricula(matricula);
            controllerCarregamento.setSenha(senha);
            controllerCarregamento.setSalvarDados(false);

            controllerCarregamento.processar();


        } catch (IOException e) {

                // Carregando janela autenticação

            fxmlLoader = new FXMLLoader(getClass().getResource("/janela_autenticacao.fxml"));
            root = fxmlLoader.load();

            ControllerAutenticacao controllerAutenticacao = fxmlLoader.getController();

            // Configurando margens da janela autenticação.

            primaryStage.maximizedProperty().addListener(((observable, oldValue, newValue) ->{

                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

                if (primaryStage.isMaximized()){
                    BorderPane.setMargin(controllerAutenticacao.getPanel(),new Insets(dimension.height * 0.05,
                            dimension.width * 0.3, dimension.height * 0.4,dimension.width * 0.3));
                } else {
                    BorderPane.setMargin(controllerAutenticacao.getPanel(),new Insets(20));
                }
            }));

        } finally {

                // Configurações gerais da Stage

            if (root != null){
                root.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());
                root.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
                primaryStage.setScene(new Scene(root,800,800));
            }

            primaryStage.setTitle("Acadêmico IFF");
            primaryStage.setMaximized(true);

            primaryStage.getIcons().add(new Image("file:arquivos/icon.png"));
            primaryStage.show();

        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
