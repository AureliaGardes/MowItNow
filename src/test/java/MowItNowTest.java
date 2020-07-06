import static fr.xebia.MowItNow.LE_FICHIER_N_A_PAS_ETE_TROUVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.xebia.MowItNow;

@RunWith(MockitoJUnitRunner.class)
public class MowItNowTest {


    @Mock
    MowItNow mowItNowMock = new MowItNow("tondeuse.txt");

    MowItNow mowItNow = new MowItNow("src\\test\\resources\\tondeuse.txt");

    private final String resourcesPath = "src\\test\\resources\\";



    @Test(expected = FileNotFoundException.class)
    public void ouvertureFichierTestKo() throws FileNotFoundException {
        doCallRealMethod().when(mowItNowMock).ouvertureFichier(any());
        mowItNowMock.ouvertureFichier("inconu");
    }

    @Test
    public void ouvertureFichierTestOk() throws FileNotFoundException {
        doCallRealMethod().when(mowItNowMock).ouvertureFichier(any());
        assertNotNull(mowItNowMock.ouvertureFichier(resourcesPath + "tondeuse.txt"));
    }

    @Test
    public void initFileKoFileNotFoundTest() throws FileNotFoundException {
        doCallRealMethod().when(mowItNowMock).initFile(any());
        doThrow(FileNotFoundException.class).when(mowItNowMock).ouvertureFichier(any());
        assertTrue(mowItNowMock.initFile(resourcesPath + "tondeuseTest.txt").isEmpty());
        verify(mowItNowMock).handleError(LE_FICHIER_N_A_PAS_ETE_TROUVE, null);
    }

    @Test
    public void TondreOkTest(){
    assertEquals("5 1 E", mowItNow.run());
    }

    @Test
    public void TondreKOFichierVideTest() {
        mowItNow.setNomFichier(resourcesPath + "FichierVide.txt");
        assertEquals("Erreur de lecture du fichier, programme interrompu", mowItNow.run());
    }

    @Test
    public void TondreKOFichierInvalideTest() {
        mowItNow.setNomFichier(resourcesPath + "MoinsDe3Lignes.txt");
        assertEquals("Erreur de lecture du fichier, programme interrompu", mowItNow.run());
    }

    @Test
    public void TondreKOFichierLignesPairesTest() {
        mowItNow.setNomFichier(resourcesPath + "lignesPaires.txt");
        assertEquals("Erreur de lecture du fichier, programme interrompu", mowItNow.run());
    }

    @Test
    public void TondreKOpremiereLigneInvalideTest() {
        mowItNow.setNomFichier(resourcesPath + "premiereLigneInvalide.txt");
        assertEquals("Erreur de configuration de la pelouse, programme interrompu.", mowItNow.run());
    }

    @Test
    public void TondreKOTaillePelouseInvalide_1Test() {
        mowItNow.setNomFichier(resourcesPath + "TaillePelouseInvalide_1.txt");
        assertEquals("Erreur de configuration de la pelouse, programme interrompu.", mowItNow.run());
    }

    @Test
    public void TondreKOTaillePelouseInvalide_2Test() {
        mowItNow.setNomFichier(resourcesPath + "TaillePelouseInvalide_2.txt");

        assertEquals("Erreur de configuration de la pelouse, programme interrompu.", mowItNow.run());
    }

    @Test
    public void TondreKOMauvaiseDirectionTest() {
        mowItNow.setNomFichier(resourcesPath + "CommandeNonReconnue.txt");

        assertEquals("Erreur intervenue durant le déplacement de la tondeuse, programme interrompu.",  mowItNow.run());
    }

    @Test
    public void TondreKOMauvaiseOrientationTondeuseTest() {
        mowItNow.setNomFichier(resourcesPath + "MauvaiseOrientationTondeuse.txt");
        assertEquals("Impossible de déterminer la position de la tondeuse, programme interrompu.", mowItNow.run());
    }

}
