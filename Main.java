import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static Alignment_Info global_alignment(String x, String y, int match_score, int mismatch_score, int gap_score)
    {
        int[][] best_score=new int[x.length()+1][y.length()+1];
        List<Alignment_Info.pointer>[][] pointers=new ArrayList[x.length()+1][y.length()+1];
        for (int i = 0; i < x.length()+1; i++) {
            for (int j = 0; j < y.length()+1; j++) {
                pointers[i][j] = new ArrayList<>();
            }
        }
        best_score[0][0]=0;
        for(int row=1; row<=x.length(); row++)
        {
            best_score[row][0]=row*gap_score;
            pointers[row][0].add(Alignment_Info.pointer.UP);
        }
        for(int column=1; column<=y.length(); column++)
        {
            best_score[0][column]=column*gap_score;
            pointers[0][column].add(Alignment_Info.pointer.LEFT);
        }
        for(int i=1; i<=x.length(); i++)
        {
            for(int j=1; j<=y.length(); j++)
            {
                int score;
                if(x.charAt(i-1)==y.charAt(j-1))
                {
                    score=match_score;
                }
                else
                {
                    score=mismatch_score;
                }
                int match=best_score[i-1][j-1]+score;
                int deletion=best_score[i-1][j]+gap_score;
                int insertion=best_score[i][j-1]+gap_score;
                int max=Math.max(Math.max(match,deletion), insertion);
                // finds biggest out of match, deletion, insertion
                best_score[i][j]=max;
                if(max==match)
                {
                    pointers[i][j].add(Alignment_Info.pointer.DIAG);
                }
                if(max==deletion)
                {
                    pointers[i][j].add(Alignment_Info.pointer.UP);
                }
                if(max==insertion)
                {
                    pointers[i][j].add(Alignment_Info.pointer.LEFT);
                }
            }
        }
        return new Alignment_Info(best_score, pointers);
    }

    public static Alignment_Info local_alignment(String x, String y, int match_score, int mismatch_score, int gap_score)
    {
        int[][] best_score=new int[x.length()+1][y.length()+1];
        List<Alignment_Info.pointer>[][] pointers=new ArrayList[x.length()+1][y.length()+1];
        for (int i = 0; i < x.length()+1; i++) {
            for (int j = 0; j < y.length()+1; j++) {
                pointers[i][j] = new ArrayList<>();
            }
        }
        for(int row=1; row<=x.length(); row++)
        {
            best_score[row][0]=0;
            pointers[row][0].add(Alignment_Info.pointer.UP);
        }
        for(int column=1; column<=y.length(); column++)
        {
            best_score[0][column]=0;
            pointers[0][column].add(Alignment_Info.pointer.LEFT);
        }
        //don't need to set row 0 or column 0 values to 0 b/c these are the default values
        for(int i=1; i<=x.length(); i++)
        {
            for(int j=1; j<=y.length(); j++)
            {
                int score;
                if(x.charAt(i-1)==y.charAt(j-1))
                {
                    score=match_score;
                }
                else
                {
                    score=mismatch_score;
                }
                int match=best_score[i-1][j-1]+score;
                int deletion=best_score[i-1][j]+gap_score;
                int insertion=best_score[i][j-1]+gap_score;
                int max=Math.max(Math.max(0,match), Math.max(deletion,insertion));
                // finds biggest out of 0, match, deletion, insertion
                best_score[i][j]=max;
                if(max==match && best_score[i-1][j-1]>=0)
                {
                    pointers[i][j].add(Alignment_Info.pointer.DIAG);
                    /* if best_score[i-1][j-1]=0, then this is the first character in the optimal
                    substring, so we shouldn't add any pointers */
                }
                if(max==deletion && best_score[i-1][j]>=0)
                {
                    pointers[i][j].add(Alignment_Info.pointer.UP);
                }
                if(max==insertion && best_score[i][j-1]>=0)
                {
                    pointers[i][j].add(Alignment_Info.pointer.LEFT);
                }
            }
        }
        return new Alignment_Info(best_score, pointers);
    }

    private static List<Alignment> find_alignments_helper(Alignment_Info alignmentInfo,
                                                          String x, String y, int i, int j)
    {
        List<Alignment_Info.pointer>[][] pointers= alignmentInfo.pointers();
        if(alignmentInfo.best_score()[i][j]<=0)
        {
            return List.of(new Alignment(new StringBuilder(), new StringBuilder()));
        }
        List<Alignment> alignments= new ArrayList<>();
        for(Alignment_Info.pointer direction:pointers[i][j])
        {
            if(direction.equals(Alignment_Info.pointer.DIAG))
            {
                List<Alignment> subalignments= find_alignments_helper(alignmentInfo, x, y, i-1, j-1);
                for(Alignment subalignment:subalignments)
                {
                    StringBuilder x_substring=subalignment.x();
                    StringBuilder y_substring=subalignment.y();
                    alignments.add(new Alignment(x_substring.append(x.charAt(i-1)),
                            y_substring.append(y.charAt(j-1))));
                }
            }
            else if(direction.equals(Alignment_Info.pointer.UP))
            {
                List<Alignment> subalignments= find_alignments_helper(alignmentInfo, x, y, i-1, j);
                for(Alignment subalignment:subalignments)
                {
                    StringBuilder x_substring=subalignment.x();
                    StringBuilder y_substring=subalignment.y();
                    alignments.add(new Alignment(x_substring.append(x.charAt(i-1)),
                            y_substring.append("-")));
                }
            }
            else // if direction is left
            {
                List<Alignment> subalignments= find_alignments_helper(alignmentInfo, x, y, i, j-1);
                for(Alignment subalignment:subalignments)
                {
                    StringBuilder x_substring=subalignment.x();
                    StringBuilder y_substring=subalignment.y();
                    alignments.add(new Alignment(x_substring.append("-"),
                            y_substring.append(y.charAt(j-1))));
                }
            }
        }
        return alignments;
    }

    public static List<Alignment> find_global_alignments(Alignment_Info alignment_info,
                                                         String x, String y)
    {
        return find_alignments_helper(alignment_info, x, y, x.length(), y.length());
    }

    public static List<Alignment> find_local_alignments(Alignment_Info alignment_info,
                                                        String x, String y)
    {
        int max_score=Integer.MIN_VALUE;
        int[][] best_score=alignment_info.best_score();
        // finds the best possible local alignment score
        for (int[] ints : best_score) {
            for (int col = 0; col < best_score[0].length; col++) {
                if (ints[col] > max_score) {
                    max_score = ints[col];
                }
            }
        }

        /* finds positions corresponding to the best local alignment score, and returns
            all alignments that lead to the best score */
        List<Alignment> alignments= new ArrayList<>();
        for(int row=0; row<best_score.length; row++)
        {
            for(int col=0; col<best_score[0].length; col++)
            {
                if(best_score[row][col]==max_score)
                {
                    alignments.addAll(find_alignments_helper(alignment_info,x,y,row,col));
                }
            }
        }
        return alignments;
    }

    public static void print_alignments(List<Alignment> alignments)
    {
        System.out.println("Number of solutions: " + alignments.size());
        System.out.println();
        int index=1;
        for(Alignment alignment:alignments)
        {
            System.out.println("Alignment " + index + ":");
            alignment.print();
            System.out.println();
            index++;
        }
    }

    public static void main(String[] args) {
        String fileName = args[0]; // Get the input file name from the command-line argument

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName)) ) {
            String alignmentType = reader.readLine();

            String[] scoreLine = reader.readLine().split(" ");
            int m = Integer.parseInt(scoreLine[0]);
            int s = Integer.parseInt(scoreLine[1]);
            int d = Integer.parseInt(scoreLine[2]);

            String x = reader.readLine();
            String y = reader.readLine();

            if(alignmentType.equals("g"))
            {
                Alignment_Info global_alignment_info=global_alignment(x,y,m,s,d);
                global_alignment_info.print_info(true);
                List<Alignment> global_alignments=
                        find_global_alignments(global_alignment_info, x,y);
                print_alignments(global_alignments);
            }
            else
            {
                Alignment_Info local_alignment_info=local_alignment(x,y,m,s,d);
                local_alignment_info.print_info(false);
                List<Alignment> local_alignments=find_local_alignments(local_alignment_info,x,y);
                print_alignments(local_alignments);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
