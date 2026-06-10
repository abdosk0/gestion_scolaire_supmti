package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import models.CoursDAO;
import models.EtudiantDAO;
import models.NoteDAO;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private Label   lblNbEtudiants;
    @FXML private Label   lblNbCours;
    @FXML private Label   lblNbNotes;

    @FXML private EtudiantController etudiantViewController;
    @FXML private CoursController    coursViewController;
    @FXML private NoteController     noteViewController;

    private final EtudiantDAO etudiantDAO = new EtudiantDAO();
    private final CoursDAO    coursDAO    = new CoursDAO();
    private final NoteDAO     noteDAO     = new NoteDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        etudiantViewController.setOnDataChanged(this::refreshStats);
        coursViewController.setOnDataChanged(this::refreshStats);
        noteViewController.setOnDataChanged(this::refreshStats);

        refreshStats();
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    refreshStats();
                    noteViewController.reloadCombos();
                }
        );
    }

    public void refreshStats() {
        lblNbEtudiants.setText(String.valueOf(etudiantDAO.findAll().size()));
        lblNbCours.setText(String.valueOf(coursDAO.findAll().size()));
        lblNbNotes.setText(String.valueOf(noteDAO.findAll().size()));
    }
}
