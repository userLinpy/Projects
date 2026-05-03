package com.ghibli.todolist;

import java.io.File;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {
    // 1. La liste "Master" qui stocke toutes les tâches en mémoire
    private ObservableList<Tache> masterData = FXCollections.observableArrayList();

    // Composants passés en variables d'instance pour être accessibles par la méthode deselectionnerTout()
    private TextField titleInput = new TextField();
    private TextArea descInput = new TextArea();
    private DatePicker dateFinPicker = new DatePicker();
    private ComboBox<String> priorityInput = new ComboBox<>();
    private Slider AvancSlider = new Slider(0, 100, 0);

    private Button addBtn = new Button("Ajouter");
    private Button updateBtn = new Button("Save Modifs");
    private Button clearBtn = new Button("Effacer");
    private Button deleteBtn = new Button("Supprimer");
    private Button finishedBtn = new Button("Terminer");

    // Création d'un filtre filteredData
    // On ne laisse passer que les objects de types Tache qui respectent une condition (ici on accepte tout)
    private FilteredList<Tache> filteredData = new FilteredList<>(masterData, p -> true);
    // On ne verra que les taches filtrées
    // On connecte la ListView à la liste FILTRÉE, et non plus à la liste masterData.
    // Ainsi, si on change le filtre plus tard, la liste sur la fenêtre se mettra à jour toute seule.
    private ListView<Tache> listView = new ListView<>(filteredData);

    // Création de la boite de filtrage (Terminées, En Cours, Toutes)
    private ComboBox<String> filterState = new ComboBox<>();

    // Création d'un objet image
    private ImageView noiraudeBasse = new ImageView();

    @Override
    public void start(Stage stage) {

        // Le titre de la fenêtre 
        stage.setTitle("Gestionnaire de Tâches Ghibli");

        // On charge les données s'il y en a 
        try {
            masterData.clear(); // On s'assure qu'elle est vide avant de charger
            chargerDonnees(); 
        } catch (Exception e) {
            System.err.println("Erreur de chargement, on démarre à vide.");
        }

        // Champ pour le titre 
        // on utilise TextField car le titre ne tient que sur une seule ligne
        titleInput.setPromptText("Titre de la tâche (Obligatoire)");

        // Champ pour la description
        descInput.setPromptText("Description détaillée...");
        descInput.setPrefRowCount(3);

        // Date de Fin (Obligatoire)
        dateFinPicker.setPromptText("Date d'échéance (Obligatoire).");

        // On crée un menu déroulant pour la priorité
        priorityInput.getItems().addAll("Urgent", "Important", "Secondaire");
        priorityInput.setPromptText("Choisir la priorité");
        priorityInput.setValue("Secondaire"); // Valeur par défaut

        // Avancement
        Label labelAvancement = new Label("Avancement : 0%");
        // Création du Slider d'avancement
        AvancSlider.setShowTickLabels(true); // Affiche les chiffres (0, 10, 20...)
        AvancSlider.setMajorTickUnit(25);    // Marque tous les 25%

        // Mettre à jour le label quand on bouge le curseur
        AvancSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            labelAvancement.setText("Avancement : " + newVal.intValue() + "%");
        });


        // Le setCellFactory définit comment transformer un objet "Tache" en un élément graphique (une ligne).
        // JavaFX appelle cette "fabrique" pour chaque cellule visible à l'écran.
        listView.setCellFactory(param -> new ListCell<Tache>() {
            @Override
            protected void updateItem(Tache item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(""); // On réinitialise le style
                } else {
                    // Distinguer les tâches terminées
                    // On crée un objet Text (car String n'a pas l'option barré)
                    javafx.scene.text.Text ligne = new javafx.scene.text.Text(item.toString());

                    if (item.getAvancement() == 100.0) {
                        // On force le barré ET on change la couleur/style
                        ligne.setStrikethrough(true); 
                        ligne.setStyle("-fx-fill: gray; -fx-font-style: italic;"); 
                    } else {
                        ligne.setStrikethrough(false);
                        ligne.setStyle("-fx-fill: black; -fx-font-weight: bold;");
                    }
                    setGraphic(ligne);
                }
            }
        });

        // On crée la ComboBox avec les trois options de filtrage
        filterState.getItems().addAll("Toutes", "En Cours", "Terminées");
        filterState.setValue("Toutes"); // Valeur par défaut au démarrage
        // On lui donne une largeur fixe ou on la laisse s'adapter
        filterState.setMaxWidth(200);

        // Boutons Désactivé par défaut
        updateBtn.setDisable(true); 
        deleteBtn.setDisable(true);
        finishedBtn.setDisable(true);

        // Organisation des boutons
        HBox buttonBox = new HBox(10, addBtn, updateBtn, clearBtn, deleteBtn, finishedBtn);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // On va charger les images de noiraudes ghibli
        // charge une image dans l'objet en appelant la méthode creerNoiraude définie en fin de fichier App.java
        noiraudeBasse = creerNoiraude("noiraudes_nothing.png");

        // On créé un objet qui prend l'espace qui reste sur la fenetre
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS); // Ce composant va manger tout l'espace vide disponible

        // On va séparer la fenêtre en deux cotés
        // Colonne gauche (formulaire)
        VBox gSide = new VBox(10, new Label("Nouvelle Tâche"), titleInput, descInput, dateFinPicker, new Label("Priorité : "), priorityInput, labelAvancement, AvancSlider, buttonBox, spacer, noiraudeBasse);
        gSide.setPadding(new Insets(15)); // Marge intérieure de 15 pixels de tous les côtés

        // Colonne droite (listView)
        VBox dSide = new VBox(10, new Label("Vos Tâches"), filterState, listView);
        dSide.setPadding(new Insets(15));

        // Configuration de la taille des composants
        gSide.setPrefWidth(350);
        gSide.setMinWidth(350);
        
        // On autorise les deux côtés à grandir proportionnellement
        HBox.setHgrow(gSide, Priority.ALWAYS);
        HBox.setHgrow(dSide, Priority.ALWAYS);
        // On autorise les boutons à grandir proportionnellement
        HBox.setHgrow(addBtn, Priority.ALWAYS);
        HBox.setHgrow(clearBtn, Priority.ALWAYS);
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);
        HBox.setHgrow(updateBtn, Priority.ALWAYS);

        // On lie la hauteur de la listView à son parent pour éviter les bugs de Double.MAX_VALUE
        listView.prefHeightProperty().bind(dSide.heightProperty());
        
        // On donne une largeur maximale raisonnable aux champs
        titleInput.setMaxWidth(1000);
        descInput.setMaxWidth(1000);
        dateFinPicker.setMaxWidth(1000);
        priorityInput.setMaxWidth(1000);
        // On donne la taille maximale aux boutons
        addBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setMaxWidth(Double.MAX_VALUE);

        // Fonctionnalités

        // On appelle notre méthode qui active le filtre
        filterState.valueProperty().addListener((obs, oldVal, newVal) -> filterRefresh());

        addBtn.setOnAction(e -> {
            if (titleInput.getText().trim().isEmpty() || dateFinPicker.getValue() == null) {
                new Alert(Alert.AlertType.ERROR, "Le titre et la date sont obligatoires !").show();
            } else {
                Tache t = new Tache(titleInput.getText(), descInput.getText(), dateFinPicker.getValue(), priorityInput.getValue());
                t.setAvancement(AvancSlider.getValue());
                masterData.add(t);
                viderTout();
                changerImageNoiraude("noiraudes_rocher.png");
            }
        });

        clearBtn.setOnAction(e -> {viderTout(); changerImageNoiraude("noiraudes_nothing.png");}); // Ici il suffit d'appeler la méthode qu'on a créé 

        deleteBtn.setOnAction(e -> {
            Tache selectionnee = listView.getSelectionModel().getSelectedItem();
            if (selectionnee != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette tâche ?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        masterData.remove(selectionnee);
                        viderTout();
                        changerImageNoiraude("noiraudes_sat.png");
                    }
                });
            }
            listView.getSelectionModel().clearSelection(); // On désélectionne la tâche 
        });

        updateBtn.setOnAction(e -> {
            Tache t = listView.getSelectionModel().getSelectedItem();
            if (t != null) {
                t.setTitre(titleInput.getText());
                t.setDescription(descInput.getText());
                t.setDateFinTache(dateFinPicker.getValue());
                t.setPriorite(priorityInput.getValue());
                t.setAvancement(AvancSlider.getValue());
                viderTout();
                listView.getSelectionModel().clearSelection(); // On désélectionne la tâche 
                changerImageNoiraude("noiraudes_rocher.png");
                filterRefresh();
            }
        });

        finishedBtn.setOnAction(e -> {
            Tache t = listView.getSelectionModel().getSelectedItem();
            if (t != null) {
                t.setAvancement(100);
                viderTout();
                listView.getSelectionModel().clearSelection();
                changerImageNoiraude("noiraudes_stars.png");
                filterRefresh();
            }
        });

        // On remplit le formulaire quand on sélectionne une tâche
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                titleInput.setText(newV.getTitre());
                descInput.setText(newV.getDescription());
                dateFinPicker.setValue(newV.getDateFinTache());
                priorityInput.setValue(newV.getPriorite());
                AvancSlider.setValue(newV.getAvancement());
                updateBtn.setDisable(false);
                addBtn.setDisable(true);
                deleteBtn.setDisable(false);
                clearBtn.setDisable(true);
                finishedBtn.setDisable(false);
                changerImageNoiraude("stone_running.png");
            }
        });

        // On concatène les deux cotés horizonatelemnt
        HBox mainLayout = new HBox(gSide, dSide);

        // Gestion du clic pour tout désélectionner, rénitialiser les boutons, et les champs
        gSide.setOnMouseClicked(event -> {
            if (event.getTarget() == gSide) { deselectionnerTout();}
        });

        mainLayout.setOnMouseClicked(event -> {
            if (event.getTarget() == mainLayout) { deselectionnerTout(); }
        });

        spacer.setOnMouseClicked(event -> {
            if (event.getTarget() == spacer) { deselectionnerTout();}
        });

        // Affichage des composants dans la fenêtre
        Scene scene = new Scene(mainLayout, 1000, 600);
        // On récupère le fichier CSS 
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    // Méthode pour vider les champs et rénitialiser les boutons
    private void viderTout() {
        titleInput.clear();
        descInput.clear();
        dateFinPicker.setValue(null);
        priorityInput.setValue("Secondaire");
        AvancSlider.setValue(0);
        deleteBtn.setDisable(true);
        updateBtn.setDisable(true);
        addBtn.setDisable(false);
        finishedBtn.setDisable(true);
        sauvegarderDonnees();
        listView.refresh();
    }

    // permet de quitter une tâche si on clique autrepart
    private void deselectionnerTout() {
        // On vérifie s'il y a une sélection en cours
        Tache selectionnee = listView.getSelectionModel().getSelectedItem();
        // On désélectionne la liste dans tous les cas
        listView.getSelectionModel().clearSelection();
        // SECURITÉ : On ne vide les champs QUE si on était en train de modifier une tâche
        // Si selectionnee est null, cela veut dire que l'utilisateur rédige une NOUVELLE tâche.
        // On part du principe que perdre ce qu'on a écrit lors d'une modif d'une par un missclick est moins grave que si c'était la création d'une nouvelle tâche
        if (selectionnee != null) { viderTout(); }
        changerImageNoiraude("noiraudes_nothing.png");
    }

    private void filterRefresh() {
        // On définit la règle de filtrage dans une fonction réutilisable
        filteredData.setPredicate(tache -> {
            String newVal = filterState.getValue(); // On récupère la valeur actuelle choisie
            if (newVal == null || newVal.equals("Toutes")) return true;
            if (newVal.equals("En Cours")) return tache.getAvancement() < 100.0;
            if (newVal.equals("Terminées")) return tache.getAvancement() == 100.0;
            return true; /// valeur par défaut et/ou si il y a un imprévu
        });
        listView.refresh();
    }

    // On définit un chemin universel vers le dossier utilisateur
    // La raison pour laquelle on crée cette méthode est pour éviter que Windows bloque l'accès à la création du fichier csv ou à son accès
    private String getCheminSauvegarde() {
        // Récupère le dossier AppData/Roaming (standard pour les sauvegardes)
        String chemin = System.getenv("APPDATA");
        
        // Si AppData n'existe pas, on se replie sur le dossier utilisateur
        if (chemin == null) {
            chemin = System.getProperty("user.home");
        }

        // On crée un sous-dossier pour notre application
        File dossier = new File(chemin + File.separator + "GhibliTodo");
        if (!dossier.exists()) {
            dossier.mkdirs(); // Crée le dossier s'il n'existe pas
        }

        return dossier.getAbsolutePath() + File.separator + "taches.csv";
    }

    // On va créer une fonction pour sauvegarder les données dans un fichier csv
    // Même méthodologie que sur pandas en python, donc assez simple
    private void sauvegarderDonnees() {
        // On utilise le chemin universel
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(getCheminSauvegarde()))) {
            for (Tache t : masterData) {
                writer.println(t.getTitre() + ";" + 
                               t.getDescription().replace(";", ",") + ";" + 
                               t.getDateFinTache() + ";" + 
                               t.getPriorite() + ";" + 
                               t.getAvancement());
            }
            System.out.println("Sauvegardé dans : " + getCheminSauvegarde());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    // On va créer une fonction/méthode pour charger les données depuis un fichier csv
    // Même méthodologie que sur python avec pandas
    private void chargerDonnees() {
        java.io.File fichier = new java.io.File(getCheminSauvegarde());
        if (!fichier.exists()) return;
        
        try (java.util.Scanner scanner = new java.util.Scanner(fichier)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                try {
                    String[] data = line.split(";");
                    if (data.length == 5) {
                        Tache t = new Tache(data[0], data[1], java.time.LocalDate.parse(data[2]), data[3]);
                        t.setAvancement(Double.parseDouble(data[4]));
                        masterData.add(t);
                    }
                } catch (Exception e) {
                    // Si UNE ligne est corrompue, on l'ignore et on passe à la suivante
                    System.err.println("Ligne ignorée (format invalide) : " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // on va créer une méthode qui charge une image spécifique de noiraudes ghibli
    private ImageView creerNoiraude(String nomFichier) {
        // On charge l'image depuis les ressources
        Image img = new Image(getClass().getResourceAsStream("/" + nomFichier));
        ImageView iv = new ImageView(img);
        
        // On définit une largeur fixe 
        iv.setFitWidth(120);
        iv.setPreserveRatio(true);
        
        // Lisser l'image pour éviter les pixels si elle est agrandie
        iv.setSmooth(true);
        return iv;
    }
    // On va créer une méthode pour changer l'image du noiraude en cours de route
    private void changerImageNoiraude(String nomFichier) {
        Image nouvelleImg = new Image(getClass().getResourceAsStream("/" + nomFichier));
        noiraudeBasse.setImage(nouvelleImg);
    }

    public static void main(String[] args) { launch(); }
}