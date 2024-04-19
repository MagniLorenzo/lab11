package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * 
     * @param nthread
     *            no. of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int rows;
        private final int cols;
        private final int matrixElems;
        private final int startelem;
        private final int nelem;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *                  the matrix to sum
         * @param startelem
         *                  the initial position for this worker
         * @param nelem
         *                  the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int startelem, final int nelem) {
            super();
            this.matrix = matrix;
            this.rows = matrix.length;
            this.cols = matrix[0].length;
            this.matrixElems = rows * cols;
            this.startelem = startelem;
            this.nelem = nelem;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            System.out.println("Working from position: (" + startelem / cols + ", " + startelem % cols
                    + ") to position: (" + (startelem + nelem - 1) / cols + ", " + (startelem + nelem - 1) % cols + ")");
            for (int i = startelem; i < matrixElems && i < startelem + nelem; i++) {
                this.res += this.matrix[i / this.cols][i % this.cols];
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }

    }

    @Override
    public double sum(final double[][] matrix) {
        final int matrixElems = matrix.length * matrix[0].length;
        final int size = matrixElems % nthread + matrixElems / nthread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrixElems; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        /*
         * Start them
         */
        for (final Worker w : workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }
}
