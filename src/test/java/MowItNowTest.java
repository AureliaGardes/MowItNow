import static fr.xebia.MowItNow.LE_FICHIER_N_A_PAS_ETE_TROUVE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.xebia.MowItNow;

@RunWith(MockitoJUnitRunner.class)
public class MowItNowTest {


    @Mock
    MowItNow mowItNow = new MowItNow("tondeuse.txt");



    @Test(expected = FileNotFoundException.class)
    public void ouvertureFichierTestKo() throws FileNotFoundException {
        doCallRealMethod().when(mowItNow).ouvertureFichier(any());
        mowItNow.ouvertureFichier("inconu");
    }

    @Test
    public void ouvertureFichierTestOk() throws FileNotFoundException {
        doCallRealMethod().when(mowItNow).ouvertureFichier(any());
        assertNotNull(mowItNow.ouvertureFichier("tondeuse.txt"));
    }

    @Test
    public void initFileKoFileNotFoundTest() throws FileNotFoundException {

        doCallRealMethod().when(mowItNow).ouvertureFichier(any());
        doThrow(FileNotFoundException.class).when(mowItNow).ouvertureFichier(any());

        assertTrue(mowItNow.initFile("tondeuseTest.txt").isEmpty());
        verify(mowItNow).handleError(LE_FICHIER_N_A_PAS_ETE_TROUVE, anyString());
    }





}
