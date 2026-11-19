package application;

import java.io.IOException;

import java.net.URL;

import java.sql.Connection;

import java.sql.Date;

import java.sql.DriverManager;

import java.sql.PreparedStatement;

import java.sql.SQLException;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import java.time.format.DateTimeParseException;

import java.time.format.ResolverStyle;

import java.util.ResourceBundle;

import java.util.UUID;

import java.util.function.UnaryOperator;



import javafx.application.Platform;

import javafx.collections.FXCollections;

import javafx.collections.ObservableList;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;

import javafx.fxml.Initializable;
<<<<<<< HEAD

=======
import javafx.scene.Node;
>>>>>>> branch 'master' of https://github.com/DaviFurtadoTeixeira/Mini_StockWatch.git
import javafx.scene.Parent;

import javafx.scene.Scene;

import javafx.scene.control.Alert;

import javafx.scene.control.Button;

import javafx.scene.control.ComboBox;

import javafx.scene.control.Label;

import javafx.scene.control.TextField;

import javafx.scene.control.TextFormatter;

import javafx.scene.image.ImageView;

import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;

import java.util.Random;

public class tela_controle_cadastro_produto implements Initializable {



   @FXML

   private AnchorPane anchor_produto;



   @FXML

   private ImageView background_tela_produto;



   @FXML

   private Button button_cadastrar;



   @FXML

   private TextField campo_fornecedor;



   @FXML

   private TextField campo_nome;



   @FXML

   private TextField campo_quantidade;



   @FXML

   private TextField campo_validade;



   @FXML

   private ComboBox<String> combobox_tipo_produto;



   @FXML

   private ComboBox<String> combobox_setor;



   @FXML

   private Label label_fornecedor;



   @FXML

   private Label label_nome;



   @FXML

   private Label label_setor;



   @FXML

   private Label label_quantidade;



   @FXML

   private Label label_tipo;



   @FXML

   private Label label_titulo;



   @FXML

   private Label label_validade;



   @FXML

   private ImageView logo_stockwatch;



   @FXML

   private ImageView logo_stockwatch1;



   // Flag para evitar loop infinito ao atualizar o texto programaticamente no listener

   private boolean formatandoData = false;
   private boolean filtrandoCombo = false;
   private boolean sincronizandoCombos = false;
   private boolean tipoSelecionadoManualmente = false;



   // Lista base de tipos para o autocompletar

   private final ObservableList<String> listaTipos = FXCollections.observableArrayList(

   	    "Arroz, Feijão e Farinha",

   	    "Massas e Macarrão",

   	    "Vinagres e Enlatados",

   	    "Temperos e Especiarias",

   	    "Guloseimas e Doces",

   	    "Chocolates e Cereais",

   	    "Bebidas",

   	    "Biscoitos e Bolachas",

   	    "Café e Açúcar",

   	    "Utensílios",

   	    "Produtos de Limpeza",

   	    "Higiene"

   	);



   // Lista de setores S001 a S012
   private final ObservableList<String> listaSetores = FXCollections.observableArrayList(
       "S001", "S002", "S003", "S004", "S005", "S006",
       "S007", "S008", "S009", "S010", "S011", "S012"
   );

   private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {

       Alert alert = new Alert(tipo);

       alert.setTitle(titulo);

       alert.setHeaderText(null);

       alert.setContentText(mensagem);

       alert.showAndWait();

   }



   private void limparCampos() {

	    if (campo_nome != null) {
	        campo_nome.clear();
	        campo_nome.setStyle("");
	    }

	    if (campo_fornecedor != null) {
	        campo_fornecedor.clear();
	        campo_fornecedor.setStyle("");
	    }

	    if (combobox_setor != null) {
	        combobox_setor.setDisable(false);
	        combobox_setor.setValue(null);
	        combobox_setor.getItems().setAll(listaSetores);
	    }

	    tipoSelecionadoManualmente = false;

	    if (combobox_tipo_produto != null) {
	        filtrandoCombo = true;

	        combobox_tipo_produto.setValue(null);
	        combobox_tipo_produto.getItems().setAll(listaTipos);

	        if (combobox_tipo_produto.getEditor() != null) {
	            combobox_tipo_produto.getEditor().clear();
	        }

	        filtrandoCombo = false;

	        aplicarEstiloCombo("");
	    }

	    if (campo_quantidade != null) {
	        campo_quantidade.clear();
	        campo_quantidade.setStyle("");
	    }

	    if (campo_validade != null) {
	        campo_validade.clear();
	        campo_validade.setStyle("");
	    }
	}



   // Método centralizado para aplicar o estilo unificado (fundo, bordas arredondadas e setinha transparente)

   private void aplicarEstiloCombo(String corBorda) {

       String estiloBase = "-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-background-color: white;";

       if (!corBorda.isEmpty()) {

           combobox_tipo_produto.setStyle(estiloBase + " -fx-border-color: " + corBorda + "; -fx-border-width: 2px;");

       } else {

           combobox_tipo_produto.setStyle(estiloBase);

       }



       if (combobox_tipo_produto.getEditor() != null) {

           combobox_tipo_produto.getEditor().setStyle("-fx-background-color: transparent;");

       }



       // Força a setinha a acompanhar o arredondamento perfeito sem sobras quadradas

       SetNodeStyleInline(combobox_tipo_produto, ".arrow-button", "-fx-background-color: transparent; -fx-background-radius: 0 15px 15px 0;");

   }



   private void SetNodeStyleInline(ComboBox<?> combo, String selector, String style) {

       javafx.scene.Node node = combo.lookup(selector);

       if (node != null) {

           node.setStyle(style);

       }

   }



   @Override
   public void initialize(URL location, ResourceBundle resources) {

       aplicarFiltrosEspeciais();
       configurarValidadoresVisuais();

       // Retira o foco automático do campo nome ao iniciar
       Platform.runLater(() -> {
           if (anchor_produto != null) {
               anchor_produto.requestFocus();
           }
       });

       // =====================================================
       // INICIALIZAÇÃO DA COMBOBOX SETOR
       // =====================================================

       if (combobox_setor != null) {

           // Carrega os 12 setores
           combobox_setor.getItems().setAll(listaSetores);

           combobox_setor.setValue(null);

           combobox_setor.setDisable(false);
       }

       // =====================================================
       // INICIALIZAÇÃO DA COMBOBOX TIPO DE PRODUTO
       // =====================================================

       if (combobox_tipo_produto != null) {

           combobox_tipo_produto.setEditable(true);

           filtrandoCombo = true;

           combobox_tipo_produto.getItems().setAll(listaTipos);
           combobox_tipo_produto.setValue(null);

           filtrandoCombo = false;

           Platform.runLater(() -> aplicarEstiloCombo(""));

           // -------------------------------------------------
           // ESTILO DA COMBOBOX
           // -------------------------------------------------

           combobox_tipo_produto.valueProperty().addListener((obs, oldValue, newValue) -> {

               if (filtrandoCombo || sincronizandoCombos) {
                   return;
               }

               Platform.runLater(() -> {

                   if (newValue != null && !newValue.trim().isEmpty()) {
                       aplicarEstiloCombo("#2ecc71");

                   } else if (combobox_tipo_produto.isFocused()) {
                       aplicarEstiloCombo("red");

                   } else {
                       aplicarEstiloCombo("");
                   }

               });

           });

           // -------------------------------------------------
           // AUTOCOMPLETE
           // -------------------------------------------------

           combobox_tipo_produto.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {

               if (filtrandoCombo || sincronizandoCombos) {
                   return;
               }

               Platform.runLater(() -> {

                   if (newValue == null || newValue.isEmpty()) {

                       filtrandoCombo = true;

                       combobox_tipo_produto.setValue(null);
                       combobox_tipo_produto.getItems().setAll(listaTipos);

                       aplicarEstiloCombo(
                           combobox_tipo_produto.isFocused() ? "red" : ""
                       );

                       filtrandoCombo = false;

                       return;
                   }

                   String corAtual =
                       (combobox_tipo_produto.getValue() != null
                       && !combobox_tipo_produto.getValue().trim().isEmpty())
                       ? "#2ecc71"
                       : (combobox_tipo_produto.isFocused() ? "red" : "");

                   aplicarEstiloCombo(corAtual);

                   // Se o texto já for um tipo válido, não filtra
                   if (listaTipos.contains(newValue)) {
                       return;
                   }

                   String filtro = newValue.toLowerCase();

                   ObservableList<String> filtrados =
                       FXCollections.observableArrayList();

                   for (String tipo : listaTipos) {

                       if (tipo.toLowerCase().contains(filtro)) {
                           filtrados.add(tipo);
                       }
                   }

                   filtrandoCombo = true;

                   combobox_tipo_produto.getItems().setAll(filtrados);

                   filtrandoCombo = false;

                   if (!combobox_tipo_produto.isShowing()
                           && !filtrados.isEmpty()) {

                       combobox_tipo_produto.show();
                   }

               });

           });

       }

       // =====================================================
       // SINCRONIZAÇÃO BIDIRECIONAL SETOR <-> TIPO
       // =====================================================

       if (combobox_setor != null && combobox_tipo_produto != null) {

           // -------------------------------------------------
           // QUANDO O SETOR É SELECIONADO
           // -------------------------------------------------

           combobox_setor.valueProperty().addListener((obs, antigo, novo) -> {

               if (sincronizandoCombos) {
                   return;
               }

               // Impede mudança de setor depois de escolher
               // manualmente o tipo de produto
               if (tipoSelecionadoManualmente) {

                   if (antigo != null) {

                       sincronizandoCombos = true;

                       combobox_setor.setValue(antigo);

                       sincronizandoCombos = false;
                   }

                   return;
               }

               sincronizandoCombos = true;

               if (novo == null) {

                   combobox_tipo_produto.setValue(null);

                   filtrandoCombo = true;
                   combobox_tipo_produto.getItems().setAll(listaTipos);
                   filtrandoCombo = false;

               } else {

                   int indice = listaSetores.indexOf(novo);

                   if (indice >= 0 && indice < listaTipos.size()) {

                       filtrandoCombo = true;

                       combobox_tipo_produto.getItems().setAll(listaTipos);
                       combobox_tipo_produto.setValue(listaTipos.get(indice));

                       if (combobox_tipo_produto.getEditor() != null) {
                           combobox_tipo_produto.getEditor()
                               .setText(listaTipos.get(indice));
                       }

                       filtrandoCombo = false;
                   }
               }

               sincronizandoCombos = false;

           });

           // -------------------------------------------------
           // QUANDO O TIPO DE PRODUTO É SELECIONADO
           // -------------------------------------------------

           combobox_tipo_produto.valueProperty().addListener((obs, antigo, novo) -> {

               if (sincronizandoCombos || filtrandoCombo) {
                   return;
               }

               if (novo == null || novo.trim().isEmpty()) {

            	    sincronizandoCombos = true;

            	    tipoSelecionadoManualmente = false;

            	    combobox_setor.setDisable(false);

            	    combobox_setor.setValue(null);

            	    combobox_setor.getItems().setAll(listaSetores);

            	    sincronizandoCombos = false;

            	    return;
            	}

               int indice = listaTipos.indexOf(novo);

               if (indice < 0 || indice >= listaSetores.size()) {
                   return;
               }

               sincronizandoCombos = true;

               // Preenche o setor correspondente
               combobox_setor.setValue(listaSetores.get(indice));

               // Marca que o tipo foi selecionado pelo usuário
               tipoSelecionadoManualmente = true;

               // Bloqueia a alteração do setor
               combobox_setor.setDisable(true);

               sincronizandoCombos = false;

           });

       }

   }



   private void aplicarFiltrosEspeciais() {

	    // 1. FILTRO DO NOME: Apenas letras, espaços, hífens e limite máximo de 255 caracteres

	    UnaryOperator<TextFormatter.Change> filtroNome = change -> {

	        String texto = change.getControlNewText();

	        if (texto.length() <= 255 && texto.matches("[\\p{L}\\s\\-'.]*")) {
	            return change;
	        }

	        return null;
	    };

       campo_nome.setTextFormatter(new TextFormatter<>(filtroNome));



       // 2. FILTRO DO FORNECEDOR: Limite máximo de 255 caracteres

       UnaryOperator<TextFormatter.Change> filtroFornecedor = change -> {

           String texto = change.getControlNewText();

           if (texto.length() <= 255) {

               return change;

           }

           return null;

       };

       campo_fornecedor.setTextFormatter(new TextFormatter<>(filtroFornecedor));



       // 3. FILTRO DA QUANTIDADE: Apenas números inteiros

       UnaryOperator<TextFormatter.Change> filtroQuantidade = change -> {

           String texto = change.getControlNewText();

           if (texto.matches("\\d*")) {

               return change;

           }

           return null;

       };

       campo_quantidade.setTextFormatter(new TextFormatter<>(filtroQuantidade));



       // 4. FILTRO DA VALIDADE (Bloqueio estricto em tempo real de dias > 31 e meses > 12)

       UnaryOperator<TextFormatter.Change> filtroValidade = change -> {

           String novoTexto = change.getControlNewText();



           if (novoTexto.isEmpty()) {

               return change;

           }



           if (novoTexto.length() > 10) {

               return null;

           }



           // Remove barras para analisar apenas os números introduzidos até ao momento

           String numeros = novoTexto.replaceAll("[^0-9]", "");



           // Validações em tempo real à medida que o utilizador digita:

           // - Máximo de 8 dígitos numéricos

           if (numeros.length() > 8) {

               return null;

           }



           // Valida o Dia (primeiros 2 dígitos): não pode começar com > 3, nem ser "00", nem passar de "31"

           if (numeros.length() >= 1) {

               int d1 = numeros.charAt(0) - '0';

               if (d1 > 3) return null;

           }

           if (numeros.length() >= 2) {

               int dia = Integer.parseInt(numeros.substring(0, 2));

               if (dia < 1 || dia > 31) return null;

           }



           // Valida o Mês (seguintes 2 dígitos): não pode começar com > 1, nem ser "00", nem passar de "12"

           if (numeros.length() >= 3) {

               int m1 = numeros.charAt(2) - '0';

               if (m1 > 1) return null;

           }

           if (numeros.length() >= 4) {

               int mes = Integer.parseInt(numeros.substring(2, 4));

               if (mes < 1 || mes > 12) return null;

           }



           return change;

       };



       campo_validade.setTextFormatter(new TextFormatter<>(filtroValidade));

   }



   private void configurarValidadoresVisuais() {

       // --- 1. CAMPO NOME ---

       campo_nome.focusedProperty().addListener((observable, oldValue, newValue) -> {

           if (newValue) {

               if (campo_nome.getText().trim().isEmpty()) {

                   campo_nome.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

               }

           } else {

               if (campo_nome.getText().trim().isEmpty()) {

                   campo_nome.setStyle("");

               }

           }

       });

       campo_nome.textProperty().addListener((observable, oldValue, newValue) -> {

           if (!newValue.trim().isEmpty()) {

               campo_nome.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");

           } else if (campo_nome.isFocused()) {

               campo_nome.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

           } else {

               campo_nome.setStyle("");

           }

       });



       // --- 2. CAMPO FORNECEDOR ---

       campo_fornecedor.focusedProperty().addListener((observable, oldValue, newValue) -> {

           if (newValue) {

               if (campo_fornecedor.getText().trim().isEmpty()) {

                   campo_fornecedor.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

               }

           } else {

               if (campo_fornecedor.getText().trim().isEmpty()) {

                   campo_fornecedor.setStyle("");

               }

           }

       });

       campo_fornecedor.textProperty().addListener((observable, oldValue, newValue) -> {

           if (!newValue.trim().isEmpty()) {

               campo_fornecedor.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");

           } else if (campo_fornecedor.isFocused()) {

               campo_fornecedor.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

           } else {

               campo_fornecedor.setStyle("");

           }

       });



       // --- 3. CAMPO QUANTIDADE ---

       campo_quantidade.focusedProperty().addListener((observable, oldValue, newValue) -> {

           if (newValue) {

               if (campo_quantidade.getText().trim().isEmpty() || !campo_quantidade.getText().matches("\\d+")) {

                   campo_quantidade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

               }

           } else {

               if (campo_quantidade.getText().trim().isEmpty()) {

                   campo_quantidade.setStyle("");

               }

           }

       });

       campo_quantidade.textProperty().addListener((observable, oldValue, newValue) -> {

           if (!newValue.trim().isEmpty() && newValue.matches("\\d+")) {

               campo_quantidade.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");

           } else if (campo_quantidade.isFocused()) {

               campo_quantidade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

           } else {

               campo_quantidade.setStyle("");

           }

       });



       // --- 4. CAMPO VALIDADE (Formatação automática com barras e validação estricta) ---

       campo_validade.focusedProperty().addListener((observable, oldValue, newValue) -> {

           if (newValue) {

               if (campo_validade.getText() == null || campo_validade.getText().trim().length() < 10) {

                   campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

               }

           } else {

               if (campo_validade.getText() == null || campo_validade.getText().trim().isEmpty()) {

                   campo_validade.setStyle("");

               }

           }

       });



       campo_validade.textProperty().addListener((observable, oldValue, newValue) -> {

           if (formatandoData) return;

           if (newValue == null || newValue.isEmpty()) {

               if (campo_validade.isFocused()) {

                   campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

               } else {

                   campo_validade.setStyle("");

               }

               return;

           }



           // Remove tudo o que não for número e limita a 8 dígitos

           String numeros = newValue.replaceAll("[^0-9]", "");

           if (numeros.length() > 8) {

               numeros = numeros.substring(0, 8);

           }



           // Reconstrói a formatação DD/MM/AAAA de forma fluida

           StringBuilder sb = new StringBuilder();

           for (int i = 0; i < numeros.length(); i++) {

               if (i == 2 || i == 4) {

                   sb.append('/');

               }

               sb.append(numeros.charAt(i));

           }



           String formatado = sb.toString();



           if (!formatado.equals(newValue)) {

               formatandoData = true;

               int posicaoCursor = campo_validade.getCaretPosition();

               campo_validade.setText(formatado);

               campo_validade.positionCaret(Math.min(posicaoCursor, formatado.length()));

               formatandoData = false;

           }



           // Validação visual da borda quando atinge 10 caracteres verificando se a data é real e válida

           if (formatado.length() == 10) {

               try {

                   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

                   LocalDate localDate = LocalDate.parse(formatado, formatter);

                   if (localDate.isBefore(LocalDate.now())) {

                       campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

                   } else {

                       campo_validade.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 4px;");

                   }

               } catch (Exception e) {

                   campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

               }

           } else if (campo_validade.isFocused()) {

               campo_validade.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 4px;");

           }

       });



       // --- 5. COMBOBOX TIPO DE PRODUTO ---

       combobox_tipo_produto.focusedProperty().addListener((observable, oldValue, newValue) -> {

           Platform.runLater(() -> {

               if (newValue) {

                   if (combobox_tipo_produto.getValue() == null || combobox_tipo_produto.getValue().trim().isEmpty()) {

                       aplicarEstiloCombo("red");

                   }

               } else {

                   if (combobox_tipo_produto.getValue() == null || combobox_tipo_produto.getValue().trim().isEmpty()) {

                       aplicarEstiloCombo("");

                   }

               }

           });

       });

   }



   private String gerarIdProduto() {

       String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

       Random random = new Random();



       StringBuilder id = new StringBuilder();



       // Gera quatro letras aleatórias

       for (int i = 0; i < 4; i++) {

           id.append(letras.charAt(random.nextInt(letras.length())));

       }



       id.append("-");



       // Gera dois números aleatórios

       for (int i = 0; i < 2; i++) {

           id.append(random.nextInt(10));

       }



       return id.toString();

   }

   @FXML

   void CadastrarProduto(ActionEvent event) {



       if (campo_nome.getText().trim().isEmpty() ||

           campo_quantidade.getText().trim().isEmpty()) {



           exibirAlerta(

               "Atenção",

               "Preencha corretamente os campos obrigatórios (Nome e Quantidade)!",

               Alert.AlertType.WARNING

           );

           return;

       }



       try {

           // Gera ID no padrão XXXX-00

           String idAleatorio = gerarIdProduto();



           String nome = campo_nome.getText().trim();



           String fornecedor = campo_fornecedor.getText() != null

                   ? campo_fornecedor.getText().trim() : "";



           String tipo = combobox_tipo_produto.getValue() != null

                   ? combobox_tipo_produto.getValue().trim() : "";



           int quantidade = Integer.parseInt(

               campo_quantidade.getText().trim()

           );



           Date validade = null;



           String textoValidade = campo_validade.getText().trim();



           // Validação da data de validade

           if (!textoValidade.isEmpty()) {



               if (textoValidade.length() < 10) {

                   exibirAlerta(

                       "Data Incompleta",

                       "Preencha a data inteira no formato DD/MM/AAAA.",

                       Alert.AlertType.ERROR

                   );

                   return;

               }



               try {

                   DateTimeFormatter formatter = DateTimeFormatter

                       .ofPattern("dd/MM/uuuu")

                       .withResolverStyle(ResolverStyle.STRICT);



                   LocalDate localDate = LocalDate.parse(

                       textoValidade, formatter

                   );



                   if (localDate.isBefore(LocalDate.now())) {

                       exibirAlerta(

                           "Data Inválida",

                           "A data de validade não pode ser anterior ao dia de hoje!",

                           Alert.AlertType.ERROR

                       );

                       return;

                   }



                   validade = Date.valueOf(localDate);



               } catch (DateTimeParseException e) {

                   exibirAlerta(

                       "Data Inválida",

                       "A data informada não existe no calendário.",

<<<<<<< HEAD
                       Alert.AlertType.ERROR
=======
    @FXML
    private Button button_visualizar_prateleiras;

    @FXML
    private Button button_cadastrar;
>>>>>>> branch 'master' of https://github.com/DaviFurtadoTeixeira/Mini_StockWatch.git

                   );

                   return;

               }

           }



           // Cadastro utilizando a classe BancoDeDados

           BancoDeDados banco = new BancoDeDados();



           boolean sucesso = banco.inserirProduto(

               idAleatorio,

               nome,

               fornecedor,

               tipo,

               quantidade,

               validade

           );



           if (sucesso) {

               exibirAlerta(

                   "Sucesso",

                   "Produto cadastrado com sucesso!\nID do produto: " + idAleatorio,

                   Alert.AlertType.INFORMATION

               );



               limparCampos();



           } else {

               exibirAlerta(

                   "Erro de Banco de Dados",

                   "Não foi possível cadastrar o produto. Verifique a conexão com o banco de dados.",

                   Alert.AlertType.ERROR

               );

           }



       } catch (NumberFormatException e) {

           exibirAlerta(

               "Erro de Formato",

               "O campo Quantidade deve conter apenas números inteiros.",

               Alert.AlertType.ERROR

           );

       }

   }





   @FXML

   void VisualizarEstoque(ActionEvent event) {

       try {

           // Substitua "tela_visualizacao_produtos.fxml" pelo nome exato do seu arquivo FXML da tabela

           FXMLLoader loader = new FXMLLoader(getClass().getResource("tela_visualizacao.fxml"));

           Parent root = loader.load();



           Stage stage = (Stage) anchor_produto.getScene().getWindow();

           stage.setScene(new Scene(root));

           stage.setTitle("StockWatch - Visualização de Produtos");

           stage.centerOnScreen();

           stage.show();



       } catch (IOException e) {

           e.printStackTrace();

           exibirAlerta("Erro", "Não foi possível carregar a tela de visualização. Verifique o nome do arquivo FXML.", Alert.AlertType.ERROR);

       }

   }

    @FXML
    void VisualizarEstoque(ActionEvent event) {
        try {
            // Substitua "tela_visualizacao_produtos.fxml" pelo nome exato do seu arquivo FXML da tabela
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tela_visualizacao.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) anchor_produto.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("StockWatch - Visualização de Produtos");
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Não foi possível carregar a tela de visualização. Verifique o nome do arquivo FXML.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    void VisualizarPrateleira(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/tela_prateleiras.fxml"));
            Scene scene = new Scene(root, 1920, 1080);
            Stage janela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janela.setScene(scene);
            janela.setTitle("Tela cadastro");
            janela.setMaximized(true);
            janela.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}