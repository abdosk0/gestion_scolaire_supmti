package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Etudiant;
import models.EtudiantDAO;
import utils.InputValidators;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EtudiantController implements Initializable {

    @FXML private TableView<Etudiant>          tableEtudiants;
    @FXML private TableColumn<Etudiant,Integer>   colId;
    @FXML private TableColumn<Etudiant,String>    colNom;
    @FXML private TableColumn<Etudiant,String>    colPrenom;
    @FXML private TableColumn<Etudiant,String>    colEmail;
    @FXML private TableColumn<Etudiant,String>    colTel;
    @FXML private TableColumn<Etudiant,LocalDate> colNaissance;

    @FXML private TextField  txtNom;
    @FXML private TextField  txtPrenom;
    @FXML private TextField  txtEmail;
    @FXML private TextField  txtTel;
    @FXML private DatePicker dpNaissance;
    @FXML private TextField  txtRecherche;

    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnVider;

    private final EtudiantDAO dao = new EtudiantDAO();
    private final ObservableList<Etudiant> data = FXCollections.observableArrayList();
    private Runnable onDataChanged;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));

        InputValidators.lettresSeulement(txtNom);
        InputValidators.lettresSeulement(txtPrenom);
        InputValidators.telephone(txtTel);
        dpNaissance.setEditable(false);

        tableEtudiants.setItems(data);
        loadData();

        tableEtudiants.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) fillForm(selected);
                });

        txtRecherche.textProperty().addListener((obs, old, val) -> rechercher(val));
    }

    public void setOnDataChanged(Runnable r) { this.onDataChanged = r; }

    private void loadData() {
        data.setAll(dao.findAll());
        if (onDataChanged != null) onDataChanged.run();
    }

    private void rechercher(String motCle) {
        if (motCle == null || motCle.isBlank()) {
            loadData();
        } else {
            data.setAll(dao.search(motCle.trim()));
        }
    }

    @FXML
    private void handleAjouter() {
        if (!validerFormulaire()) return;

        Etudiant e = new Etudiant(
                txtNom.getText().trim(),
                txtPrenom.getText().trim(),
                txtEmail.getText().trim(),
                txtTel.getText().trim(),
                dpNaissance.getValue()
        );

        if (dao.create(e)) {
            showInfo("Étudiant ajouté avec succès.");
            loadData();
            viderFormulaire();
        } else {
            showError("Échec de l'ajout. Vérifiez que l'e-mail est unique.");
        }
    }

    @FXML
    private void handleModifier() {
        Etudiant selected = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Sélectionnez un étudiant à modifier."); return; }
        if (!validerFormulaire()) return;

        selected.setNom(txtNom.getText().trim());
        selected.setPrenom(txtPrenom.getText().trim());
        selected.setEmail(txtEmail.getText().trim());
        selected.setTelephone(txtTel.getText().trim());
        selected.setDateNaissance(dpNaissance.getValue());

        if (dao.update(selected)) {
            showInfo("Étudiant mis à jour.");
            loadData();
            viderFormulaire();
        } else {
            showError("Échec de la mise à jour.");
        }
    }

    @FXML
    private void handleSupprimer() {
        Etudiant selected = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Sélectionnez un étudiant à supprimer."); return; }

        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + selected.getNomComplet() + " ? (ses notes seront aussi supprimées)",
                ButtonType.YES, ButtonType.NO).showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (dao.delete(selected.getId())) {
                showInfo("Étudiant supprimé.");
                loadData();
                viderFormulaire();
            } else {
                showError("Échec de la suppression.");
            }
        }
    }

    @FXML
    private void handleVider() {
        viderFormulaire();
        tableEtudiants.getSelectionModel().clearSelection();
    }

    private void fillForm(Etudiant e) {
        txtNom.setText(e.getNom());
        txtPrenom.setText(e.getPrenom());
        txtEmail.setText(e.getEmail());
        txtTel.setText(e.getTelephone() != null ? e.getTelephone() : "");
        dpNaissance.setValue(e.getDateNaissance());
    }

    private void viderFormulaire() {
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtTel.clear();
        dpNaissance.setValue(null);
        txtRecherche.clear();
    }

    private boolean validerFormulaire() {
        if (txtNom.getText().isBlank()) {
            showError("Le nom est obligatoire.");
            return false;
        }
        if (txtPrenom.getText().isBlank()) {
            showError("Le prénom est obligatoire.");
            return false;
        }
        if (txtEmail.getText().isBlank()) {
            showError("L'e-mail est obligatoire.");
            return false;
        }
        if (!InputValidators.estEmailValide(txtEmail.getText())) {
            showError("Format d'e-mail invalide (ex. nom@domaine.ma).");
            return false;
        }
        return true;
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
