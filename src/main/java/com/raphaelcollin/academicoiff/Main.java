package com.raphaelcollin.academicoiff;

import com.raphaelcollin.academicoiff.controller.ControllerLoading;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sun.misc.BASE64Decoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.Key;

public class Main extends Application {

    /* Caso o usuário já tenha logado ativando a check box 'Mantenha-me conectado' os seus dados
       estarão guardados no arquivo dados.txt. Quando isso acontecer, a aplicação já iniciará
       na tela de carregamento. Caso contrário, a aplicação iniciára na tela de autenticação */

    // OBS: A senha que será guardada no arquivo estará encryptada

    // Constantes

    private static final String URL_ARQUIVO_LOGIN = "files/dados.txt";
    public static final String CHAVE_ENCRYPT = "MATRICIFFRC0lliN"; // Chave de Encrypt
    public static final String ALGORITIMO_ECRYPT = "AES"; // Algoritimo usado na Encryptação
    private static final String URL_JANELA_CARREGAMENTO = "/loading_view.fxml";
    private static final String URL_JANELA_AUTENTICACAO = "/authentication_view.fxml";
    private static final String URL_ESTILO_CSS = "/style.css";
    private static final String TITLE_STAGE = "Academico IFF";
    private static final String URL_ICONE_STAGE = "file:files/icon.png";

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader;
        Parent root = null;

        String matricula;
        String senha;


        try (BufferedReader reader = new BufferedReader(new FileReader(URL_ARQUIVO_LOGIN))) {

            Key secretKey = new SecretKeySpec(CHAVE_ENCRYPT.getBytes(), ALGORITIMO_ECRYPT);

            Cipher c = Cipher.getInstance(ALGORITIMO_ECRYPT);
            c.init(Cipher.DECRYPT_MODE, secretKey);

            String matriculaEncryptada = reader.readLine();

            byte[] byteDecMatricula = new BASE64Decoder().decodeBuffer(matriculaEncryptada);
            byte[] byteDecodificaMatricula = c.doFinal(byteDecMatricula);

            matricula = new String(byteDecodificaMatricula);

            String senhaEncryptada = reader.readLine();

            byte[] byteDecSenha = new BASE64Decoder().decodeBuffer(senhaEncryptada);
            byte[] byteDecodificaSenha = c.doFinal(byteDecSenha);

            senha = new String(byteDecodificaSenha);

            fxmlLoader = new FXMLLoader(getClass().getResource(URL_JANELA_CARREGAMENTO));
            root = fxmlLoader.load();

            ControllerLoading controllerLoading = fxmlLoader.getController();

            controllerLoading.setMatricula(matricula);
            controllerLoading.setSenha(senha);
            controllerLoading.setSaveLoginData(false);
            controllerLoading.processar();

        } catch (Exception e) {

            fxmlLoader = new FXMLLoader(getClass().getResource(URL_JANELA_AUTENTICACAO));
            root = fxmlLoader.load();


        } finally {

            if (root != null){

                Rectangle2D tamanhoTela = Screen.getPrimary().getBounds();
                primaryStage.setX(tamanhoTela.getMinX());
                primaryStage.setY(tamanhoTela.getMinY());

                primaryStage.setMaxWidth(tamanhoTela.getWidth());
                primaryStage.setMinWidth(tamanhoTela.getWidth());

                primaryStage.setMaxHeight(tamanhoTela.getHeight());
                primaryStage.setMinHeight(tamanhoTela.getHeight());

                primaryStage.setScene(new Scene(root));
                primaryStage.getScene().getStylesheets().add(getClass().getResource(URL_ESTILO_CSS).toExternalForm());
            }

            primaryStage.setTitle(TITLE_STAGE);
            primaryStage.getIcons().add(new Image(URL_ICONE_STAGE));
            primaryStage.show();
            primaryStage.setMaximized(true);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
