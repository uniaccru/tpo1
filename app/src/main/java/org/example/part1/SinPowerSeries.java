package org.example.part1;

public class SinPowerSeries {

    private double factorial(double n){
        double res = 1;
        for (double i = 2; i <= n; i++) {
            res *= i;
        }
        return res;
    }

    public double sin(double x, int n) {

        double res = 0;

        //sin(X) = sum {k=0}-{n} [ (-1)^k * x^(2k+1) / (2k+1)! ]
        for (int k = 0; k <= n; k++) {
            double sign = Math.pow(-1, k);
            double numerator = Math.pow(x, 2 * k + 1);
            double denominator = factorial(2 * k + 1);

            res += sign * numerator / denominator;
        }

        return res;
    }

}
