/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private static final int PIXEL_ENERGY_AT_BORDER = 1000;

    private int width;
    private int height;

    private int[][] color;
    private double[][] energy;

    private boolean transposed;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }

        this.width = picture.width();
        this.height = picture.height();

        this.color = new int[height][width];
        storeColorForPixel(picture);

        this.energy = new double[height][width];
        storeEnergyForPixel();
    }

    // current picture
    public Picture picture() {
        if (transposed) {
            transpose();
        }

        Picture pic = new Picture(width(), height());
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                pic.set(col, row, new Color(color[row][col]));
            }
        }
        return new Picture(pic);
    }

    // width of current picture
    public int width() {
        return transposed ? height : width;
    }

    // height of current picture
    public int height() {
        return transposed ? width : height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (transposed) {
            validateWithinBounds(y, x);
            return energy[x][y];
        }
        else {
            validateWithinBounds(x, y);
            return energy[y][x];
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!transposed) {
            transpose();
        }
        return findSeam();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (transposed) {
            transpose();
        }
        return findSeam();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!transposed) {
            transpose();
        }
        removeSeam(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (transposed) {
            transpose();
        }
        removeSeam(seam);
    }

    private void removeSeam(int[] seam) {
        if (seam == null || width <= 1 || isSeamInvalid(seam)) {
            throw new IllegalArgumentException();
        }

        int[][] newColor = new int[height][width - 1];
        double[][] newEnergy = new double[height][width - 1];

        for (int row = 0; row < height; row++) {
            int s = seam[row];

            for (int col = 0; col < s; col++) {
                newColor[row][col] = color[row][col];
                newEnergy[row][col] = energy[row][col];
            }

            for (int col = s + 1; col < width; col++) {
                validateWithinBounds(col - 1, row);
                newColor[row][col - 1] = color[row][col];
                newEnergy[row][col - 1] = energy[row][col];
            }
        }

        color = newColor;
        energy = newEnergy;
        width--;

        for (int row = 0; row < height; row++) {
            int s = seam[row];

            if (s == 0) {
                energy[row][s] = pixelEnergyFor(s, row);
            }
            else if (s == width) {
                energy[row][s - 1] = pixelEnergyFor(s - 1, row);
            }
            else {
                energy[row][s] = pixelEnergyFor(s, row);
                energy[row][s - 1] = pixelEnergyFor(s - 1, row);
            }
        }
    }

    private void storeColorForPixel(Picture picture) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                color[row][col] = picture.get(col, row).getRGB();
            }
        }
    }

    private void storeEnergyForPixel() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                energy[row][col] = pixelEnergyFor(col, row);
            }
        }
    }

    private double pixelEnergyFor(int col, int row) {
        validateWithinBounds(col, row);

        if (col == 0 || col == width - 1 || row == 0 || row == height - 1) {
            return PIXEL_ENERGY_AT_BORDER;
        }

        Color up = new Color(color[row - 1][col]);
        Color down = new Color(color[row + 1][col]);
        Color left = new Color(color[row][col - 1]);
        Color right = new Color(color[row][col + 1]);

        return Math.sqrt(pixelEnergy(up, down) + pixelEnergy(left, right));
    }

    private double pixelEnergy(Color a, Color b) {
        return Math.pow(a.getRed() - b.getRed(), 2) +
                Math.pow(a.getGreen() - b.getGreen(), 2) +
                Math.pow(a.getBlue() - b.getBlue(), 2);
    }

    private void transpose() {
        int temp = width;
        width = height;
        height = temp;

        int[][] c = new int[height][width];
        double[][] e = new double[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                c[row][col] = color[col][row];
                e[row][col] = energy[col][row];
            }
        }

        color = c;
        energy = e;
        transposed = !transposed;
    }

    private void validateWithinBounds(int col, int row) {
        if (!withinBounds(col, row)) {
            throw new IllegalArgumentException();
        }
    }

    private boolean withinBounds(int col, int row) {
        return (col >= 0 && col < width && row >= 0 && row < height);
    }

    private int[] findSeam() {
        double[][] distTo = new double[height][width];
        Arrays.fill(distTo[0], 0);
        for (int row = 1; row < height; row++) {
            Arrays.fill(distTo[row], Double.POSITIVE_INFINITY);
        }

        int[][] colTo = new int[height][width];
        for (int row = 0; row < height - 1; row++) {
            topologicalOrderRelaxation(distTo, colTo, row);
        }
        int minEnergyPos = getMinEnergyPos(colTo, distTo[height - 1]);
        Stack<Integer> seams = getSeams(colTo, minEnergyPos);

        int[] seam = new int[height];
        int idx = 0;

        while (!seams.isEmpty()) {
            seam[idx++] = seams.pop();
        }
        return seam;
    }

    private int getMinEnergyPos(int[][] colTo, double[] distTo) {
        int minEnergyPos = 0;
        double minEnergyPath = Double.POSITIVE_INFINITY;
        for (int c = 0; c < width; c++) {
            if (minEnergyPath > distTo[c]) {
                minEnergyPath = distTo[c];
                minEnergyPos = colTo[height - 1][c];
            }
        }
        return minEnergyPos;
    }

    private Stack<Integer> getSeams(int[][] colTo, int col) {
        Stack<Integer> seams = new Stack<>();
        seams.push(col);
        for (int i = height - 1; i > 0; i--) {
            col = colTo[i][col];
            seams.push(col);
        }
        return seams;
    }

    private void topologicalOrderRelaxation(double[][] distTo, int[][] colTo, int row) {
        for (int col = 0; col < width; col++) {
            relax(col, row, distTo, colTo);
        }
    }

    private void relax(int col, int row, double[][] distTo, int[][] colTo) {
        if (withinBounds(col - 1, row + 1) &&
                distTo[row + 1][col - 1] > distTo[row][col] + energy[row + 1][col - 1]) {
            distTo[row + 1][col - 1] = distTo[row][col] + energy[row + 1][col - 1];
            colTo[row + 1][col - 1] = col;
        }

        if (distTo[row + 1][col] > distTo[row][col] + energy[row + 1][col]) {
            distTo[row + 1][col] = distTo[row][col] + energy[row + 1][col];
            colTo[row + 1][col] = col;
        }

        if (withinBounds(col + 1, row + 1) &&
                distTo[row + 1][col + 1] > distTo[row][col] + energy[row + 1][col + 1]) {
            distTo[row + 1][col + 1] = distTo[row][col] + energy[row + 1][col + 1];
            colTo[row + 1][col + 1] = col;
        }
    }

    private boolean isSeamInvalid(int[] seam) {
        if (seam.length != height) {
            return true;
        }

        for (int i = seam.length - 1; i > 0; i--) {
            if (seam[i] >= width || Math.abs(seam[i] - seam[i - 1]) > 1) {
                return true;
            }
        }
        return false;
    }

}