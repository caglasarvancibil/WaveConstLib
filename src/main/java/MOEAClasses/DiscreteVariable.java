package MOEAClasses;


import FirstGenerationWaveletConstruction.DiscreteSampleVariable;
import WaveletPackage.Wavelet;
import org.moeaframework.core.Variable;

public class DiscreteVariable implements Variable {


    private DiscreteSampleVariable discreteSampleVariable;
    private Wavelet<Double> wavelet;


    public DiscreteVariable(DiscreteSampleVariable discreteSampleVariable) {
       this.discreteSampleVariable=discreteSampleVariable;
    }

    public DiscreteSampleVariable getDiscreteSampleVariable() {
        return discreteSampleVariable;
    }

    public Wavelet<Double> getWavelet() {
        return wavelet;
    }

    public void setWavelet(Wavelet<Double> wavelet) {
        this.wavelet = wavelet;
    }

    @Override
    public Variable copy() {
        return null;
    }

    @Override
    public void randomize() {

    }

    @Override
    public String encode() {
        return null;
    }

    @Override
    public void decode(String s) {

    }

    @Override
    public String toString() {
        return discreteSampleVariable.toString();
    }
}
