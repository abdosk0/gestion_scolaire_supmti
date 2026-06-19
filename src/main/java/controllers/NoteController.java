package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.*;
import utils.InputValidators;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class NoteController implements Initializable {

    @FXML private TableView<Note>             tableNotes;
    @FXML private TableColumn<Note,Integer>   colId;
    @FXML private TableColumn<Note,String>    colEtudiant;
    @FXML private TableColumn<Note,String>    colCours;
    @FXML private TableColumn<Note,Double>    colNote;
    @FXML private TableColumn<Note,String>    colMention;
    @FXML private TableColumn<Note,LocalDate> colDate;

    @FXML private ComboBox<Etudiant> cmbEtudiant;
    @FXML private ComboBox<Cours>    cmbCours;
    @FXML private TextField          txtNote;
    @FXML private Label              lblMoyenne;

    private final NoteDAO     noteDAO     = new NoteDAO();
    private final EtudiantDAO etudiantDAO = new EtudiantDAO();
    private final CoursDAO    coursDAO    = new CoursDAO();

    private final ObservableList<Note> data = FXCollections.observableArrayList();
    private Runnable onDataChanged;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEtudiant.setCellValueFactory(new PropertyValueFactory<>("etudiantNom"));
        colCours.setCellValueFactory(new PropertyValueFactory<>("coursCode"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colMention.setCellValueFactory(new PropertyValueFactory<>("mention"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEvaluation"));

        // colorier la note en vert si >= 10 et rouge sinon
        colNote.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); setStyle(""); return; }
                setText(String.format("%.2f", val));
                setStyle(val >= 10
                        ? "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"
                        : "-fx-text-fill: #c62828; -fx-font-weight: bold;");
            }
        });

        InputValidators.decimal(txtNote);

        tableNotes.setItems(data);

        cmbEtudiant.setItems(FXCollections.observableArrayList(etudiantDAO.findAll()));
        cmbCours.setItems(FXCollections.observableArrayList(coursDAO.findAll()));

        loadData();

        tableNotes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> { if (sel != null) fillForm(sel); });

        cmbEtudiant.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> updateMoyenne(sel));
    }

    public void setOnDataChanged(Runnable r) { this.onDataChanged = r; }

    private void loadData() {
        data.setAll(noteDAO.findAll());
        if (onDataChanged != null) onDataChanged.run();
    }

    public void reloadCombos() {
        cmbEtudiant.setItems(FXCollections.observableArrayList(etudiantDAO.findAll()));
        cmbCours.setItems(FXCollections.observableArrayList(coursDAO.findAll()));
        loadData();
    }

    @FXML
    private void handleEnregistrer() {
        if (!validerFormulaire()) return;

        Etudiant e = cmbEtudiant.getValue();
        Cours    c = cmbCours.getValue();
        double   n = parseNote();

        if (noteDAO.upsert(e.getId(), c.getId(), n)) {
            showInfo("Note enregistrée.");
            loadData();
            updateMoyenne(e);
            viderFormulaire();
        } else {
            showError("Erreur lors de l'enregistrement.");
        }
    }

    @FXML
    private void handleSupprimer() {
        Note sel = tableNotes.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Sélectionnez une note à supprimer."); return; }

        Optional<ButtonType> res = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer la note de " + sel.getEtudiantNom() + " en " + sel.getCoursCode() + " ?",
                ButtonType.YES, ButtonType.NO).showAndWait();

        if (res.isPresent() && res.get() == ButtonType.YES) {
            if (noteDAO.delete(sel.getId())) {
                showInfo("Note supprimée.");
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
        tableNotes.getSelectionModel().clearSelection();
    }

    private void fillForm(Note n) {
        cmbEtudiant.getItems().stream()
                .filter(e -> e.getId() == n.getEtudiantId())
                .findFirst().ifPresent(cmbEtudiant::setValue);
        cmbCours.getItems().stream()
                .filter(c -> c.getId() == n.getCoursId())
                .findFirst().ifPresent(cmbCours::setValue);
        txtNote.setText(String.valueOf(n.getNote()));
    }

    private void viderFormulaire() {
        cmbEtudiant.setValue(null);
        cmbCours.setValue(null);
        txtNote.clear();
        lblMoyenne.setText("—");
    }

    private void updateMoyenne(Etudiant e) {
        if (e == null) { lblMoyenne.setText("—"); return; }
        double moy = noteDAO.getMoyenneEtudiant(e.getId());
        if (moy < 0) {
            lblMoyenne.setText("Pas encore de notes");
        } else {
            String mention = moy >= 16 ? "Très bien"
                           : moy >= 14 ? "Bien"
                           : moy >= 12 ? "Assez bien"
                           : moy >= 10 ? "Passable" : "Insuffisant";
            lblMoyenne.setText(String.format("%.2f / 20   (%s)", moy, mention));
            lblMoyenne.setStyle(moy >= 10
                    ? "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"
                    : "-fx-text-fill: #c62828; -fx-font-weight: bold;");
        }
    }

    private boolean validerFormulaire() {
        if (cmbEtudiant.getValue() == null) { showError("Sélectionnez un étudiant."); return false; }
        if (cmbCours.getValue() == null)    { showError("Sélectionnez un cours.");    return false; }
        if (parseNote() < 0)               { showError("Note invalide (0–20).");     return false; }
        return true;
    }

    private double parseNote() {
        try {
            double v = Double.parseDouble(txtNote.getText().trim().replace(',', '.'));
            return (v >= 0 && v <= 20) ? v : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void showInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void showError(String msg) { new Alert(Alert.AlertType.ERROR,       msg, ButtonType.OK).showAndWait(); }
}
