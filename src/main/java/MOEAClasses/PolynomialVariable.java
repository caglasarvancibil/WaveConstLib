package MOEAClasses;





import FirstGenerationWaveletConstruction.PiecewisePolynomialVariable;
import WaveletPackage.Wavelet;
import org.moeaframework.core.Variable;

public class PolynomialVariable implements Variable {

    private Wavelet<Double> wavelet;
    private PiecewisePolynomialVariable piecewisePolynomialVariable;

    public PolynomialVariable(PiecewisePolynomialVariable piecewisePolynomialVariable) {
        this.piecewisePolynomialVariable=piecewisePolynomialVariable;
    }

    public PiecewisePolynomialVariable getPiecewisePolynomialVariable() {
        return piecewisePolynomialVariable;
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
    public String toString() {
        return piecewisePolynomialVariable.toString();
    }

    @Override
    public String encode() {
        return null;
    }

    @Override
    public void decode(String s) {

    }


}
