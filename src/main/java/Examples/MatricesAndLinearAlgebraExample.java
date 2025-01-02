package Examples;

import LinearAlgebra.*;
import Symbolic.ParseStringsOperations;

/**
 * Consist usage examples of Linear Algebra Package.
 * @see Matrix
 * @see MatrixOperations
 * @see LinearSolver
 * @see LUDecomposition
 */

public class MatricesAndLinearAlgebraExample {
    public static void main(String[] args) {

       //Matrice Operations and Properties
        System.out.println("Example of Matrice Operations and Properties =");
        Matrix<Double> matrix1=new DoubleMatrix(new Double[][]{{2.0,3.0,0.0},{12.0,3.0,5.0},{2.0,1.0,2.0}});
        Matrix<Double> matrix2=new DoubleMatrix(new Double[][]{{2.0},{3.0},{1.0}});
        matrix2.transpose().getReverse().print();

        Matrix<Double> mat1= DoubleMatrixOperations.getInstance().add(matrix1,matrix1);
        mat1.print();


        DoubleMatrixOperations.getInstance().mul(matrix1,matrix2).print();
        DoubleMatrixOperations.getInstance().mul(matrix1,matrix1).print();
        DoubleMatrixOperations.getInstance().sub(matrix1,matrix1).print();
        DoubleMatrixOperations.getInstance().dotProduct(matrix2,matrix2).print();
        matrix1.getRow(1).print();

        Matrix<Double> matrix3=matrix1.copy();
        matrix3.swapRows(0,1);

        matrix1.print();
        matrix3.print();
        matrix2.transpose().print();
        matrix2.print();
        matrix1.getDiagonals().print();
        matrix1.setDiagonals(matrix2);
        matrix1.print();

        DoubleMatrixOperations.getInstance().identity(4).print();
        DoubleMatrixOperations.getInstance().zeros(2,5).print();
        DoubleMatrixOperations.getInstance().ones(5,2).print();
        DoubleMatrixOperations.getInstance().concatenate(matrix1,matrix1, direction.VERTICAL).print();

       //LU decomposition Example
        System.out.println();
        System.out.println("LU Decomposition Example =");
        Matrix<Double> A=new DoubleMatrix(new Double[][]{{10.0,-7.0,0.0},{-3.0,2.0,6.0},{5.0,-1.0,5.0}});
        System.out.println("A= "+A);
        DoubleLUDecomposition.getInstance().LUDecomposition(A);
        System.out.println(DoubleLUDecomposition.getInstance().toString());


        //Linear Solver Example with Symbolic Package
        System.out.println();
        System.out.println("Linear Solver Example :");
        Matrix<Double> coeficientMatrix=new DoubleMatrix(new Double[][]{{2.0,1.0,1.0},{-1.0,1.0,-1.0},{1.0,2.0,3.0}});
        Matrix<Double> rigthPart =new DoubleMatrix(new Double[][]{{2.0,3.0,-10.0}});
        Matrix<String> variables= StringMatrixOperations.getInstance().symbolicVariables("x",3);
        Matrix<String> stringCoefMatrix=new StringMatrix(StringMatrixOperations.getInstance().convertString(coeficientMatrix).getMatrix());
        Matrix<String> equations=StringMatrixOperations.getInstance().mul(stringCoefMatrix,variables.transpose());
        Matrix<String> smartEquations = new StringMatrix(new String[1][3] );
        System.out.println("Symbolic equations =");
        equations.print();

        for (int i = 0; i < 3; i++) {
            String smartliteral = ParseStringsOperations.smartLiteral(ParseStringsOperations.parseStringValue(equations.transpose().getIndex(0, i)));
            smartEquations.setIndex(0, i, smartliteral);
        }
        System.out.println();
        System.out.println("Symbolic equations after smart literal operation =");
        smartEquations.transpose().print();

        DoubleLinearSolver.getInstance().solve(smartEquations,variables,rigthPart);
        Matrix<Double> solutions= DoubleLinearSolver.getInstance().getLinearSolution();
        System.out.println();
        System.out.println("Solutions =");
        for (int i = 0; i < solutions.getColumnLength(); i++) {
            System.out.print(variables.getIndex(0,i)+"= "+solutions.getIndex(0,i)+" ; ");

        }



    }
}
