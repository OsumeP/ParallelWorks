package co.edu.unal.paralela;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public final class ReciprocalArraySum {

    private ReciprocalArraySum() {
    }

    protected static double seqArraySum(final double[] input) {
        double sum = 0;

        for (int i = 0; i < input.length; i++) {
            sum += 1 / input[i];
        }

        return sum;
    }

    private static int getChunkSize(final int nChunks, final int nElements) {
        return (nElements + nChunks - 1) / nChunks;
    }

    private static int getChunkStartInclusive(final int chunk,
            final int nChunks, final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        return chunk * chunkSize;
    }

    private static int getChunkEndExclusive(final int chunk, final int nChunks,
            final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        final int end = (chunk + 1) * chunkSize;
        if (end > nElements) {
            return nElements;
        } else {
            return end;
        }
    }

    private static class ReciprocalArraySumTask extends RecursiveAction{

        private final int startIndexInclusive;

        private final int endIndexExclusive;

        private final double[] input;

        private double value;

        ReciprocalArraySumTask(final int setStartIndexInclusive,
                final int setEndIndexExclusive, final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
        }

        public double getValue() {
            return value;
        }

        @Override
        protected void compute() {
            for(int i = this.startIndexInclusive; i < this.endIndexExclusive; i++) {
           		this.value += 1 / input[i];
           	}
        }
    }

    protected static double parArraySum(final double[] input) {
        assert input.length % 2 == 0;

        double sum = 0;
        int middle = input.length / 2;
        
        ReciprocalArraySumTask left = new ReciprocalArraySumTask(0, middle, input);
        ReciprocalArraySumTask right = new ReciprocalArraySumTask(middle, input.length, input);
        
        left.fork();
        right.compute();
    	left.join();


        sum = left.getValue() + right.getValue();
        
        return sum;
    }

    protected static double parManyTaskArraySum(final double[] input,
            final int numTasks) {
        double sum = 0;
        
        //ForkJoinPool pool = ForkJoinPool.commonPool();
        List<ReciprocalArraySumTask> listTask = new ArrayList<>(numTasks);
        
        for(int i = 0; i < numTasks; i++) {
        	listTask.add(new ReciprocalArraySumTask(getChunkStartInclusive(i, numTasks, input.length),
        			getChunkEndExclusive(i, numTasks, input.length),
        			input));
        }
        
        for(ReciprocalArraySumTask task : listTask) {
        	//pool.execute(task);
        	task.fork();
        }
        
        for(ReciprocalArraySumTask task : listTask) {
        	task.join();
        }


        for (ReciprocalArraySumTask task : listTask) {
            sum += task.getValue();
        }

        return sum;
    }
}
