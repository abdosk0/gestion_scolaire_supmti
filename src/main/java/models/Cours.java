package models;

public class Cours {

    private int    id;
    private String code;
    private String intitule;
    private String description;
    private double coefficient;

    public Cours(int id, String code, String intitule,
                 String description, double coefficient) {
        this.id          = id;
        this.code        = code;
        this.intitule    = intitule;
        this.description = description;
        this.coefficient = coefficient;
    }

    public Cours(String code, String intitule,
                 String description, double coefficient) {
        this(0, code, intitule, description, coefficient);
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }

    public String getCode()              { return code; }
    public void setCode(String code)     { this.code = code; }

    public String getIntitule()          { return intitule; }
    public void setIntitule(String i)    { this.intitule = i; }

    public String getDescription()       { return description; }
    public void setDescription(String d) { this.description = d; }

    public double getCoefficient()       { return coefficient; }
    public void setCoefficient(double c) { this.coefficient = c; }

    @Override
    public String toString() {
        return code + " – " + intitule;
    }
}
