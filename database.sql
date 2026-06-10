-- Script SQL – Gestion Scolaire (PostgreSQL)
-- Exécuter avant de lancer l'application : psql -U postgres -f database.sql

\connect postgres

DROP DATABASE IF EXISTS gestion_scolaire;
CREATE DATABASE gestion_scolaire
    WITH ENCODING = 'UTF8'
    LC_COLLATE = 'French_France.1252'
    LC_CTYPE   = 'French_France.1252'
    TEMPLATE   = template0;

\connect gestion_scolaire

CREATE TABLE etudiants (
    id             SERIAL PRIMARY KEY,
    nom            VARCHAR(100) NOT NULL,
    prenom         VARCHAR(100) NOT NULL,
    email          VARCHAR(150) UNIQUE NOT NULL,
    telephone      VARCHAR(20),
    date_naissance DATE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cours (
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(20) UNIQUE NOT NULL,
    intitule    VARCHAR(200) NOT NULL,
    description TEXT,
    coefficient NUMERIC(3,1) DEFAULT 1.0,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- un étudiant ne peut avoir qu'une seule note par cours
CREATE TABLE notes (
    id              SERIAL PRIMARY KEY,
    etudiant_id     INTEGER NOT NULL REFERENCES etudiants(id) ON DELETE CASCADE,
    cours_id        INTEGER NOT NULL REFERENCES cours(id)     ON DELETE CASCADE,
    note            NUMERIC(4,2) CHECK (note >= 0 AND note <= 20),
    date_evaluation DATE DEFAULT CURRENT_DATE,
    UNIQUE (etudiant_id, cours_id)
);

INSERT INTO etudiants (nom, prenom, email, telephone, date_naissance) VALUES
('Alaoui',    'Youssef',  'youssef.alaoui@supmti.ma',   '0661001001', '2002-03-15'),
('Benali',    'Fatima',   'fatima.benali@supmti.ma',    '0662002002', '2001-07-22'),
('Chraibi',   'Omar',     'omar.chraibi@supmti.ma',     '0663003003', '2003-01-09'),
('Doukkali',  'Sanaa',    'sanaa.doukkali@supmti.ma',   '0664004004', '2002-11-30'),
('El Fassi',  'Mehdi',    'mehdi.elfassi@supmti.ma',    '0665005005', '2001-05-18');

INSERT INTO cours (code, intitule, description, coefficient) VALUES
('INFO101', 'Algorithmique & Structures de Données', 'Bases de l''algorithmique et complexité', 3.0),
('INFO102', 'Programmation Java',                    'POO, collections, interfaces, JDBC',      3.0),
('INFO103', 'Bases de Données',                      'SQL, modélisation relationnelle',          2.5),
('MATH101', 'Analyse Mathématique',                  'Suites, séries, intégrales',              2.0),
('MATH102', 'Algèbre Linéaire',                      'Espaces vectoriels, matrices',            2.0),
('WEB101',  'Développement Web',                     'HTML, CSS, JavaScript, REST',             2.0);

INSERT INTO notes (etudiant_id, cours_id, note, date_evaluation) VALUES
(1,1,15.50,'2025-12-10'), (1,2,17.00,'2025-12-11'), (1,3,14.25,'2025-12-12'),
(1,4,12.00,'2025-12-13'), (1,5,13.50,'2025-12-14'),
(2,1,18.00,'2025-12-10'), (2,2,16.50,'2025-12-11'), (2,3,19.00,'2025-12-12'),
(2,4,14.75,'2025-12-13'), (2,6,15.00,'2025-12-15'),
(3,1,11.00,'2025-12-10'), (3,2,13.00,'2025-12-11'), (3,3,10.50,'2025-12-12'),
(4,2,14.00,'2025-12-11'), (4,3,16.00,'2025-12-12'), (4,6,17.50,'2025-12-15'),
(5,1,09.50,'2025-12-10'), (5,2,12.00,'2025-12-11'), (5,4,11.00,'2025-12-13');

-- vue bulletin de notes
CREATE VIEW v_bulletin AS
SELECT
    e.id AS etudiant_id,
    e.nom || ' ' || e.prenom AS etudiant,
    c.code AS code_cours,
    c.intitule AS cours,
    c.coefficient,
    n.note,
    n.date_evaluation
FROM notes n
JOIN etudiants e ON e.id = n.etudiant_id
JOIN cours     c ON c.id = n.cours_id
ORDER BY e.nom, e.prenom, c.code;
