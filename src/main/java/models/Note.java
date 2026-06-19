package models;

import java.time.LocalDate;

// représente une note d'un étudiant pour un cours donné
public class Note {

    private int       id;
    private int       etudiantId;
    private int       coursId;
    private double    note;
    private LocalDate dateEvaluation;

    // champs récupérés par jointure pour l'affichage dans la table
    private String etudiantNom;
    private String coursCode;
    private String coursIntitule;

    public Note(int id, int etudiantId, int coursId,
                double note, LocalDate dateEvaluation,
                String etudiantNom, String coursCode, String coursIntitule) {
        this.id             = id;
        this.etudiantId     = etudiantId;
        this.coursId        = coursId;
        this.note           = note;
        this.dateEvaluation = dateEvaluation;
        this.etudiantNom    = etudiantNom;
        this.coursCode      = coursCode;
        this.coursIntitule  = coursIntitule;
    }

    public int getId()                         { return id; }
    public void setId(int id)                  { this.id = id; }

    public int getEtudiantId()                 { return etudiantId; }
    public void setEtudiantId(int v)           { this.etudiantId = v; }

    public int getCoursId()                    { return coursId; }
    public void setCoursId(int v)              { this.coursId = v; }

    public double getNote()                    { return note; }
    public void setNote(double note)           { this.note = note; }

    public LocalDate getDateEvaluation()       { return dateEvaluation; }
    public void setDateEvaluation(LocalDate d) { this.dateEvaluation = d; }

    public String getEtudiantNom()             { return etudiantNom; }
    public void setEtudiantNom(String v)       { this.etudiantNom = v; }

    public String getCoursCode()               { return coursCode; }
    public void setCoursCode(String v)         { this.coursCode = v; }

    public String getCoursIntitule()           { return coursIntitule; }
    public void setCoursIntitule(String v)     { this.coursIntitule = v; }

    public String getMention() {
        if (note >= 16)      return "Très bien";
        else if (note >= 14) return "Bien";
        else if (note >= 12) return "Assez bien";
        else if (note >= 10) return "Passable";
        else                 return "Insuffisant";
    }
}
