package MOEAClasses;

import Data.DataPaths;
import WaveletPackage.Wavelet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SaveConstructedWaveletsAsTxt {
    public void saveFilterCoefficients( Wavelet wavelet) throws FileNotFoundException, UnsupportedEncodingException {
        fileWithDirectoryAssurance(DataPaths.WaveletFunctions);
        PrintWriter writer =new PrintWriter(
                DataPaths.WaveletFunctions+wavelet.getName()+".txt", "UTF-8");
        writer.print(4+" ;"+wavelet.getHiR().getColumnLength()+" ;");
        writer.println();
        writer.print(wavelet.toTxtFilter());
        writer.close();
    }
    private static File fileWithDirectoryAssurance(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return new File(directory);
    }
}
