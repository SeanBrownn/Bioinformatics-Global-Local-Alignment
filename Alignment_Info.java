import java.util.List;

public class Alignment_Info {

    enum pointer{
        DIAG,
        LEFT,
        UP
    }

    private final int[][] best_score;
    private final List<pointer>[][] pointers;

    public Alignment_Info(int[][] best_score, List<pointer>[][] pointers) {
        this.best_score = best_score;
        this.pointers = pointers;
    }

    public int[][] best_score() {
        return best_score;
    }

    public List<pointer>[][] pointers() {
        return pointers;
    }

    public void print_info(boolean global)
    {
        int[][] best_score=this.best_score;
        int max_score=Integer.MIN_VALUE;
        for (int[] ints : best_score) {
            for (int col = 0; col < best_score[0].length; col++) {
                if (ints[col] > max_score) {
                    max_score = ints[col];
                }
            }
        }

        if(global)
        {
            System.out.println("Optimal score: " + best_score[best_score.length-1][best_score[0].length-1] + "\n");
        }
        else
        {
            System.out.println("Optimal score: " + max_score + "\n");
        }
    }
}
