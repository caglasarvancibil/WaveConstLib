package MOEAClasses;


import FirstGenerationWaveletConstruction.UCRootVariable;
import WaveletPackage.Wavelet;
import org.moeaframework.core.Variable;

public class RootsVariable implements Variable {
    private Wavelet<Double> wavelet;
    private UCRootVariable ucRootVariable;

    public RootsVariable(UCRootVariable ucRootVariable) {
        this.ucRootVariable = ucRootVariable;
    }

    public UCRootVariable getUcRootVariable() {
        return ucRootVariable;
    }

    public Wavelet<Double> getWavelet() {
        return wavelet;
    }

    public void setWavelet(Wavelet<Double> wavelet) {
        this.wavelet = wavelet;
    }

    @Override
    public String toString() {
        return ucRootVariable.toString();
    }

    @Override
    public Variable copy() {
        return null;
    }
    @Override
    public void randomize() {}

    @Override
    public String encode() {
        return null;
    }

    @Override
    public void decode(String s) {

    }
}
