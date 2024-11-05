package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.apache.commons.math3.distribution.TDistribution;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main extends JFrame {

    public Main(String title) {
        super(title);

        // Параметри завдання
        int groupNumber = 15;
        int n = 350;
        double aX = groupNumber - 10;
        double aY = groupNumber + 5;
        double sigmaX = 3 + groupNumber / 10.0;
        double sigmaY = 5 + groupNumber / 5.0;

        // 1. Генерація вибірок для X і Y
        System.out.println("1) Генерація вибірок для X і Y:");
        double[] X = new double[n];
        double[] Y = new double[n];
        generateSamples(X, Y, n, aX, sigmaX, aY, sigmaY);

        for (int i = 0; i < n; i++) {
            System.out.printf("X[%d] = %.2f, Y[%d] = %.2f\n", i, X[i], i, Y[i]);
        }

        // 2. Побудова кореляційного поля з лінією регресії
        System.out.println("\n2) Побудова кореляційного поля з лінією регресії:");
        JFreeChart scatterPlot = createChartWithRegressionLine(X, Y);
        ChartPanel chartPanel = new ChartPanel(scatterPlot);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);

        // 3. Вибіркові оцінки характеристик розподілів
        System.out.println("\n3) Вибіркові оцінки характеристик розподілів:");
        double meanX = calculateSampleMean(X);
        double meanY = calculateSampleMean(Y);
        double varianceX = calculateSampleVariance(X, meanX);
        double varianceY = calculateSampleVariance(Y, meanY);
        double stddevX = calculateSampleRootMeanSquareDeviation(X);
        double stddevY = calculateSampleRootMeanSquareDeviation(Y);
        double correlationCoefficient = calculateSampleCorrelationCoefficient(X, Y);

        System.out.printf("Вибіркове середнє X: %.2f\n", meanX);
        System.out.printf("Вибіркове середнє Y: %.2f\n", meanY);
        System.out.printf("Вибіркова дисперсія X: %.2f\n", varianceX);
        System.out.printf("Вибіркова дисперсія Y: %.2f\n", varianceY);
        System.out.printf("Середньоквадратичне відхилення X: %.2f\n", stddevX);
        System.out.printf("Середньоквадратичне відхилення Y: %.2f\n", stddevY);
        System.out.printf("Вибірковий коефіцієнт кореляції: %.2f\n", correlationCoefficient);

        // 4. Перевірка гіпотези про значущість вибіркового коефіцієнта кореляції
        System.out.println("\n4) Перевірка гіпотези про значущість вибіркового коефіцієнта кореляції:");
        checkCorrelationSignificance(correlationCoefficient, n);
    }

    // Генерація вибірок X і Y (використовуємо нормальний розподіл)
    public static void generateSamples(double[] X, double[] Y, int n, double aX, double sigmaX, double aY, double sigmaY) {
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            X[i] = aX + sigmaX * random.nextGaussian();
            Y[i] = aY + sigmaY * random.nextGaussian();
        }
    }

    // Вибіркове середнє
    public static double calculateSampleMean(double[] data) {
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.length;
    }

    // Вибіркова дисперсія
    public static double calculateSampleVariance(double[] data, double mean) {
        double sum = 0;
        for (double number : data) {
            sum += Math.pow((number - mean), 2);
        }
        return sum / (data.length);
    }

    // Середньоквадратичне відхилення
    public static double calculateSampleRootMeanSquareDeviation(double[] data) {
        double mean = calculateSampleMean(data);
        return Math.sqrt(calculateSampleVariance(data, mean)); // Квадратний корінь з дисперсії
    }

    // Вибірковий коефіцієнт кореляції
    public static double calculateSampleCorrelationCoefficient(double[] dataX, double[] dataY) {
        double stddevX = calculateSampleRootMeanSquareDeviation(dataX);
        double stddevY = calculateSampleRootMeanSquareDeviation(dataY);
        double meanX = calculateSampleMean(dataX);
        double meanY = calculateSampleMean(dataY);

        double sum = 0;
        for (int i = 0; i < dataX.length; i++) {
            sum += (dataX[i] - meanX) * (dataY[i] - meanY);
        }
        return sum / (dataX.length * stddevX * stddevY); // Формула для вибіркового коефіцієнта кореляції
    }

    // Перевірка гіпотези про значущість коефіцієнта кореляції (T-критерій Стьюдента)
    public static void checkCorrelationSignificance(double r, int n) {
        // Обчислення T-критерію
        double T = r * Math.sqrt((n - 2) / (1 - r * r));

        // Обчислення критичного значення t-критерію Стьюдента для рівня значимості 0.05
        TDistribution tDist = new TDistribution(n - 2); // Розподіл Стьюдента
        double gamma =0.95;
        double alpha = 1 - gamma;
        double tCritical = tDist.inverseCumulativeProbability(1 - alpha / 2);

        System.out.printf("Значення T-критерію: %.2f\n", T);
        System.out.printf("Критичне значення : %.2f\n", tCritical);

        if (Math.abs(T) < tCritical) {
            System.out.println("Немає підстав відкидати нульову гіпотезу.");
        } else {
            System.out.println("Нульову гіпотезу відхилено.");
        }
    }

    // Обчислення лінії регресії (нахил і перетин)
    public static double[] calculateRegressionLine(double[] X, double[] Y) {
        double meanX = calculateSampleMean(X);
        double meanY = calculateSampleMean(Y);
        double sumXY = 0;
        double sumXSquare = 0;

        for (int i = 0; i < X.length; i++) {
            sumXY += (X[i] - meanX) * (Y[i] - meanY);
            sumXSquare += Math.pow(X[i] - meanX, 2);
        }

        double slope = sumXY / sumXSquare;
        double intercept = meanY - slope * meanX;

        return new double[]{slope, intercept};
    }

    // Створення графіка кореляційного поля з лінією регресії
    private JFreeChart createChartWithRegressionLine(double[] X, double[] Y) {
        XYSeries series = new XYSeries("Кореляційне поле");
        XYSeries regressionLine = new XYSeries("Лінія регресії");

        // Додавання точок кореляційного поля
        for (int i = 0; i < X.length; i++) {
            series.add(X[i], Y[i]);
        }

        // Обчислення коефіцієнтів лінії регресії
        double[] regressionCoefficients = calculateRegressionLine(X, Y);
        double slope = regressionCoefficients[0];
        double intercept = regressionCoefficients[1];

        // Додавання точок для лінії регресії
        for (double x : X) {
            double y = slope * x + intercept;
            regressionLine.add(x, y);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);          // Додаємо кореляційне поле
        dataset.addSeries(regressionLine);  // Додаємо лінію регресії

        JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                "Кореляційне поле з лінією регресії", // Заголовок
                "X", // Мітка осі X
                "Y", // Мітка осі Y
                dataset, // Дані
                PlotOrientation.VERTICAL,
                true, // Легенда
                true, // Інструментальні підказки
                false // URL
        );

        XYPlot plot = (XYPlot) scatterPlot.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Налаштування рендерера: крапки для кореляційного поля і лінія для регресії
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, false);

        plot.setRenderer(renderer);

        return scatterPlot;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main chart = new Main("Статистичний аналіз");
            chart.pack();
            chart.setLocationRelativeTo(null); // Центрувати вікно на екрані
            chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Правильне завершення програми
            chart.setVisible(true);
        });
    }
}
