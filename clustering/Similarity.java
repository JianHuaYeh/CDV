package clustering;

public class Similarity {

    public static double similarity(double[] vec0, double[] vec1, int method) {
        switch (method) {
            case 0: return eucledianSimilarity(vec0, vec1);
            case 1: return pearsonSimilarity(vec0, vec1);
            case 2:
            default: return cosineSimilarity(vec0, vec1);
        }
    }

    public static double minKowskiDistance(double[] vec0, double[] vec1, int order) {
        double sum = 0.0;
        for (int i=0; i<vec0.length; i++) {
            double score = vec0[i];
            double score2 = vec1[i];
            sum += Math.pow(Math.abs(score-score2), order);
        }
        double d = Math.pow(sum, 1.0/order);
        return d;
    }
    
    public static double eucledianDistance(double[] vec0, double[] vec1) {
        double sum = 0.0;
        for (int i=0; i<vec0.length; i++) {
            double score = vec0[i];
            double score2 = vec1[i];
            sum += (score-score2)*(score-score2);
        }
        double d = Math.sqrt(sum);
        return d;
    }
    
    public static double eucledianSimilarity(double[] vec0, double[] vec1) {
        return 1.0/(eucledianDistance(vec0, vec1)+1);
    }

    public static double pearsonSimilarity(double[] vec0, double[] vec1) {
        double sum1=0.0, sum2=0.0;
        double sum1sq=0.0, sum2sq=0.0;
        double psum=0.0;
        int n = vec0.length;
        for (int i=0; i<n; i++) {
            double r1 = vec0[i];
            double r2 = vec1[i];
            sum1 += r1;
            sum2 += r2;
            sum1sq += r1*r1;
            sum2sq += r2*r2;
            psum += r1*r2;
        }
        double num = psum-(sum1*sum2)/n;
        double den=Math.sqrt((sum1sq-sum1*sum1/n)*(sum2sq-sum2*sum2/n));
        if (den == 0.0) return 0.0;
        return num/den;
    }

    public static double cosineSimilarity(double[] vec0, double[] vec1) {
        double sum1sq=0.0, sum2sq=0.0;
        double psum=0.0;
        int n = vec0.length;
        for (int i=0; i<n; i++) {
            double r1 = vec0[i];
            double r2 = vec1[i];
            sum1sq += r1*r1;
            sum2sq += r2*r2;
            psum += r1*r2;
        }
        double den=Math.sqrt(sum1sq)*Math.sqrt(sum2sq);
        if (den == 0.0) return 0.0;
        return psum/den;
    }
    
    
}
