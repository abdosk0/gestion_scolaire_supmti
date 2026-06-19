package models;

import java.time.LocalDate;

public class Etudiant {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateNaissance;

    public Etudiant(int id, String nom, String prenom,
                    String email, String telephone, LocalDate dateNaissance) {
        this.id            = id;
        this.nom           = nom;
        this.prenom        = prenom;
        this.email         = email;
        this.telephone     = telephone;
        this.dateNaissance = dateNaissance;
    }

    // constructeur sans id pour les insertions
    public Etudiant(String nom, String prenom,
                    String email, String telephone, LocalDate dateNaissance) {
        this(0, nom, prenom, email, telephone, dateNaissance);
    }

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getNom()                { return nom; }
    public void setNom(String nom)        { this.nom = nom; }

    public String getPrenom()             { return prenom; }
    public void setPrenom(String prenom)  { this.prenom = prenom; }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    public String getTelephone()          { return telephone; }
    public void setTelephone(String t)    { this.telephone = t; }

    public LocalDate getDateNaissance()           { return dateNaissance; }
    public void setDateNaissance(LocalDate d)     { this.dateNaissance = d; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
}
