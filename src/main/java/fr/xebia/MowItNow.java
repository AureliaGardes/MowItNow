package fr.xebia;

import static java.nio.file.Files.readAllLines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class MowItNow {

    public static final String LE_NOM_DU_FICHIER_DOIT_ETRE_PASSE_EN_PARAMETRE = "le nom du fichier doit être passé en paramètre.";

    public static final String FICHIER_VIDE = "Fichier vide.";

    public static final String LE_FICHIER_N_A_PAS_ETE_TROUVE = "le fichier n'a pas été trouvé.";

    public static final String FICHIER_INVALIDE = "Fichier invalide.";

    public static final String FICHIER_INVALIDE_IL_FAUT_AU_MOINS_3_LIGNES_DANS_LE_FICHIER = "Fichier invalide, Il faut au moins 3 lignes dans le fichier.";

    public static final String FICHIER_INVALIDE_LE_NOMBRE_DE_LIGNES_DANS_LE_FICHIER_DOIT_ETRE_IMPAIR = "Fichier invalide, le nombre de lignes dans le fichier doit être impair.";

    public static final String ERREUR_DE_LECTURE_DU_FICHIER_PROGRAMME_INTERROMPU = "Erreur de lecture du fichier, programme interrompu";

    public static final String PREMIERE_LIGNE_INVALIDE = "Première ligne invalide";

    public static final String TAILLE_DE_LA_PELOUSE_INVALIDE_VALEUR_0 = "Taille de la pelouse invalide : {}";

    public static final String ERREUR_DE_CONFIGURATION_DE_LA_PELOUSE_PROGRAMME_INTERROMPU = "Erreur de configuretion de la pelouse, programme interrompu.";

    public static final String ORIENTATION_DE_LA_TONDEUSE_NON_RECONNUE = "Orientation de la tondeuse non reconnue : {}";

    public static final String COMMANDE_NON_RECONNUE = "Commande non reconnue {}";

    public static final String IMPOSSIBLE_DE_DETERMINER_LA_POSITION_DE_LA_TONDEUSE_PROGRAMME_INTERROMPU =
                    "Impossible de déterminer la position de la tondeuse, programme interrompu.";

    public static final String ERREUR_INTERVENUE_DURANT_LE_DEPLACEMENT_DE_LA_TONDEUSE_PROGRAMME_INTERROMPU =
                    "Erreur intervenue durant le déplacement de la tondeuse, programme interrompu.";

    private int maxX; //longueur max de la pelouse

    private int maxY;//largeur max de la pelouse

    int xt; //position de la tondeuse sur la longueur de la pelouse

    int yt; //position de la tondeuse sur la largeur de la pelouse

    int ot; //orientation de la tondeuse

    int mvIdx; //étape de la tonte de la pelouse

    private String nomFichier;

    public MowItNow(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public void run() {

        if (StringUtils.isEmpty(nomFichier)) {
            handleError(LE_NOM_DU_FICHIER_DOIT_ETRE_PASSE_EN_PARAMETRE, null);
            return;
        }

        //lecture du fichier
        List<String> lines = initFile(nomFichier);
        if (lines.isEmpty()) {
            handleError(ERREUR_DE_LECTURE_DU_FICHIER_PROGRAMME_INTERROMPU, null);
            return;
        }

        //debut de la lecture du fichier d'entrée
        int index = 0;
        if (!configPelouse(lines, index)) {
            handleError(ERREUR_DE_CONFIGURATION_DE_LA_PELOUSE_PROGRAMME_INTERROMPU, null);
            return;
        }
        // On a fini de se servir de la ligne
        index++;

        List<Character> directions = Arrays.asList('N', 'E', 'S', 'O');

        while (index < lines.size()) {
            String[] pos = lines.get(index).split(" ");

            //creation tondeuse (position)
            if (!setPositionTondeuse(directions, pos)) {
                handleError(IMPOSSIBLE_DE_DETERMINER_LA_POSITION_DE_LA_TONDEUSE_PROGRAMME_INTERROMPU, null);
                return;
            }

            index++;
            // Je lis les moves
            String lesMoves = lines.get(index);

            //execution des mouvements
            if (!tondre(lesMoves)) {
                handleError(ERREUR_INTERVENUE_DURANT_LE_DEPLACEMENT_DE_LA_TONDEUSE_PROGRAMME_INTERROMPU, null);
                return;
            }
            index++;

            //print dans le résultat de la position finale de la tondeuse
            log.info("{} {} {}", xt, yt, directions.get(ot));
        }

    }

    private boolean tondre(String lesMoves) {
        mvIdx = 0;
        while (mvIdx < lesMoves.length()) {
            char instruction = lesMoves.charAt(mvIdx);
            switch (instruction) {
                case 'A': //on avance la tondeuse
                    avancerTondeuse(ot);
                    break;
                case 'G': //on tourne la tondeuse
                    ot = (ot + 3) % 4;
                    break;
                case 'D':
                    ot = (ot + 1) % 4;
                    break;
                default:
                    handleError(COMMANDE_NON_RECONNUE, String.valueOf(instruction));
                    return false;
            }
            mvIdx++;
        }
        return true;
    }

    private boolean setPositionTondeuse(List<Character> directions, String[] pos) {
        // Je lis la pos tondeuse
        try {
            xt = Integer.parseInt(pos[0]);
            yt = Integer.parseInt(pos[1]);
        } catch (Exception e) {
            log.error("Erreur de parsing : {}", e.getMessage());
        }
        Character orientation = pos[2].charAt(0);

        if (!directions.contains(orientation)) {
            handleError(ORIENTATION_DE_LA_TONDEUSE_NON_RECONNUE,String.valueOf(orientation));
            return false;
        }
        ot = directions.indexOf(orientation); // position de l'orientation dans le tableau Directions si N -> 0, si E -> 1 ....
        return true;
    }

    private void avancerTondeuse(int ot) {
        switch (ot) {
            case 0: // N
                if (yt < maxY) {
                    yt++;
                }
                break;
            case 1: // E
                if (xt < maxX) {
                    xt++;
                }
                break;
            case 2: // S
                if (yt > 0) {
                    yt--;
                }
                break;
            case 3: // O
                if (xt > 0) {
                    xt--;
                }
                break;
            default:
        }
    }

    public boolean configPelouse(List<String> lines, int index) {
        String[] maxs = lines.get(index).split(" ");
        if (maxs.length != 2) {
            handleError(PREMIERE_LIGNE_INVALIDE, null);
            return false;
        }
        try {
            maxX = Integer.parseInt(maxs[0]);
            maxY = Integer.parseInt(maxs[1]);
        } catch (Exception e) {
            log.error("Erreur de parsing : {}", e.getMessage());
            return false;
        }

        if (maxX < 0) {
            handleError(TAILLE_DE_LA_PELOUSE_INVALIDE_VALEUR_0, String.valueOf(maxX));
            return false;
        }
        if (maxY < 0) {
            handleError(TAILLE_DE_LA_PELOUSE_INVALIDE_VALEUR_0, String.valueOf(maxY));
            return false;
        }
        return true;
    }

    public List<String> initFile(String nomFichier) {
        File file;
        try {
            file = ouvertureFichier(nomFichier);
        } catch (FileNotFoundException e) {
            handleError(LE_FICHIER_N_A_PAS_ETE_TROUVE, e.getMessage());
            return new ArrayList<>();
        }
        List<String> lines;
        try {
            lines = readAllLines(Path.of(file.getAbsolutePath()), Charset.defaultCharset());
        } catch (IOException e) {
            handleError(FICHIER_INVALIDE, e.getMessage());
            return new ArrayList<>();
        }

        if (lines.isEmpty()) {
            handleError(FICHIER_VIDE, null);
            return lines;
        }

        if (lines.size() < 3) {
            handleError(FICHIER_INVALIDE_IL_FAUT_AU_MOINS_3_LIGNES_DANS_LE_FICHIER, null);
            return new ArrayList<>();
        }

        if (lines.size() % 2 == 0) {
            handleError(FICHIER_INVALIDE_LE_NOMBRE_DE_LIGNES_DANS_LE_FICHIER_DOIT_ETRE_IMPAIR, null);
            return new ArrayList<>();
        }

        return lines;
    }

    public File ouvertureFichier(String path) throws FileNotFoundException {
        File file = new File("src\\main\\resources\\" + path);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }

    public void handleError(String error, String data) {
        if (data.isBlank()) {
            log.error(error, data);
            return;
        }
        log.error(error);
    }
}
