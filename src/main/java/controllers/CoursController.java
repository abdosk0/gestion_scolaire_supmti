package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Cours;
import models.CoursDAO;
import utils.InputValidators;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CoursController implements Initializable {

    @FXML private TableView<Cours>           tableCours;
    @FXML private TableColumn<Cours,Integer> colId;
    @FXML private TableColumn<Cours,String>  colCode;
    @FXML private TableColumn<Cours,String>  colIntitule;
    @FXML private TableColumn<Cours,String>  colDescription;
    @FXML private TableColumn<Cours,Double>  colCoef;

    @FXML private TextField txtCode;
    @FXML private TextField txtIntitule;
    @FXML private TextArea  txtDescription;
    @FXML private TextField txtCoef;
    @FXML private TextField txtRecherche;

    private final CoursDAO dao = new CoursDAO();
    private final ObservableList<Cours> data = FXCollections.observableArrayList();
    private Runnable onDataChanged;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colIntitule.setCellValueFactory(new PropertyValueFactory<>("intitule"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCoef.setCellValueFactory(new PropertyValueFactory<>("coefficient"));

        InputValidators.decimal(txtCoef);

        tableCours.setItems(data);
        loadData();

        tableCours.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> { if (sel != null) fillForm(sel); });

        txtRecherche.textProperty().addListener((obs, old, val) -> rechercher(val));
    }

    public void setOnDataChanged(Runnable r) { this.onDataChanged = r; }

    private void loadData() {
        data.setAll(dao.findAll());
        if (onDataChanged != null) onDataChanged.run();
    }

    private void rechercher(String motCle) {
        if (motCle == null || motCle.isBlank()) loadData();
        else data.setAll(dao.search(motCle.trim()));
    }

    @FXML
    private void handleAjouter() {
        if (!validerFormulaire()) return;
        Cours c = new Cours(
                txtCode.getText().trim().toUpperCase(),
                txtIntitule.getText().trim(),
                txtDescription.getText().trim(),
                parseCoef()
        );
        if (dao.create(c)) {
            showInfo("Cours ajouté avec succès.");
            loadData();
            viderFormulaire();
        } else {
            showError("Échec de l'ajout. Le code doit être unique.");
        }
    }

    @FXML
    private void handleModifier() {
        Cours sel = tableCours.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Sélectionnez un cours."); return; }
        if (!validerFormulaire()) return;

        sel.setCode(txtCode.getText().trim().toUpperCase());
        sel.setIntitule(txtIntitule.getText().trim());
        sel.setDescription(txtDescription.getText().trim());
        sel.setCoefficient(parseCoef());

        if (dao.update(sel)) {
            showInfo("Cours mis à jour.");
            loadData();
            viderFormulaire();
        } else {
            showError("Échec de la mise à jour.");
        }
    }

    @FXML
    private void handleSupprimer() {
        Cours sel = tableCours.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Sélectionnez un cours."); return; }

        Optional<ButtonType> res = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le cours « " + sel.getIntitule() + " » ?",
                ButtonType.YES, ButtonType.NO).showAndWait();

        if (res.isPresent() && res.get() == ButtonType.YES) {
            if (dao.delete(sel.getId())) {
                showInfo("Cours supprimé.");
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
        tableCours.getSelectionModel().clearSelection();
    }

    private void fillForm(Cours c) {
        txtCode.setText(c.getCode());
        txtIntitule.setText(c.getIntitule());
        txtDescription.setText(c.getDescription() != null ? c.getDescription() : "");
        txtCoef.setText(String.valueOf(c.getCoefficient()));
    }

    private void viderFormulaire() {
        txtCode.clear();
        txtIntitule.clear();
        txtDescription.clear();
        txtCoef.clear();
        txtRecherche.clear();
    }

    private boolean validerFormulaire() {
        if (txtCode.getText().isBlank())     { showError("Le code est obligatoire.");     return false; }
        if (txtIntitule.getText().isBlank()) { showError("L'intitulé est obligatoire.");  return false; }
        if (parseCoef() < 0)                { showError("Coefficient invalide.");         return false; }
        return true;
    }

    private double parseCoef() {
        try {
            return Double.parseDouble(txtCoef.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void showInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void showError(String msg) { new Alert(Alert.AlertType.ERROR,       msg, ButtonType.OK).showAndWait(); }
}
