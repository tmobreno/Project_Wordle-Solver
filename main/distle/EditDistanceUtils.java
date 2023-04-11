package main.distle;

import java.util.*;

public class EditDistanceUtils {
    
    /**
     * Returns the completed Edit Distance memoization structure, a 2D array
     * of ints representing the number of string manipulations required to minimally
     * turn each subproblem's string into the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return Completed Memoization structure for editDistance(s0, s1)
     */
    public static int[][] getEditDistTable (String s0, String s1) {
        int[][] mainArray = new int[s0.length() + 1][s1.length() + 1];

        for (int i = 0; i <= s0.length(); i++) {
            mainArray[i][0] = i;
        }
     
        for (int j = 0; j <= s1.length(); j++) {
            mainArray[0][j] = j;
        }

        for (int r = 1; r <= s0.length(); r++){
            char lett = s0.charAt(r-1);
            for (int c = 1; c <= s1.length(); c++){
                char lett2 = s1.charAt(c-1);
                if (lett == lett2){
                    mainArray[r][c] = mainArray[r-1][c-1];
                }else{
                    int insert = Integer.MAX_VALUE;
                    int delete = Integer.MAX_VALUE;
                    int replace = Integer.MAX_VALUE;
                    int transposition = Integer.MAX_VALUE;
    
                    if (c >= 1){
                        insert = mainArray[r][c-1] + 1;
                    }
                    if (r >= 1){
                        delete = mainArray[r-1][c] + 1;
                    }
                    if (r >= 1 && c >= 1){
    
                        replace = mainArray[r-1][c-1] + 1;
                    }
                    if (r >= 2 && c >= 2 && s0.charAt(r-2) == s1.charAt(c-1) && s0.charAt(r-1) == s1.charAt(c-2)){
                        transposition = mainArray[r-2][c-2] + 1;
                    }
                    int minimum = Math.min(delete, insert);
                    int minimum2 = Math.min(minimum, replace);
                    int minimum3 = Math.min(minimum2, transposition);
                    mainArray[r][c] = minimum3;
                }
            }
        }
        return mainArray;
    }
    
    /**
     * Returns one possible sequence of transformations that turns String s0
     * into s1. The list is in top-down order (i.e., starting from the largest
     * subproblem in the memoization structure) and consists of Strings representing
     * the String manipulations of:
     * <ol>
     *   <li>"R" = Replacement</li>
     *   <li>"T" = Transposition</li>
     *   <li>"I" = Insertion</li>
     *   <li>"D" = Deletion</li>
     * </ol>
     * In case of multiple minimal edit distance sequences, returns a list with
     * ties in manipulations broken by the order listed above (i.e., replacements
     * preferred over transpositions, which in turn are preferred over insertions, etc.)
     * @param s0 String transforming into other
     * @param s1 Target of transformation
     * @param table Precomputed memoization structure for edit distance between s0, s1
     * @return List that represents a top-down sequence of manipulations required to
     * turn s0 into s1, e.g., ["R", "R", "T", "I"] would be two replacements followed
     * by a transposition, then insertion.
     */
    public static List<String> getTransformationList (String s0, String s1, int[][] table) {
        List<String> finalTrans = new ArrayList<>();
        transformationsList(s0, s1, table, finalTrans, s0.length(), s1.length());
        return finalTrans;  
    }

    public static List<String> transformationsList (String s0, String s1, int[][] table, List<String> finalTrans, int r, int c) {
        if(r == 0 && c == 0){
            return finalTrans;
        }
        char lett = 'a';
        char lett2 = 'b';  
        if (c > 0 && r > 0){
            lett = s0.charAt(r-1);
            lett2 = s1.charAt(c-1);  
        }
        int newR = r;
        int newC = c;

        if (lett == lett2){
            newR = r-1;
            newC = c-1;
        }
        else if (c == 0){
            newR = r-1;
            finalTrans.add("D");
        }
        else if (r == 0){
            newC = c-1;
            finalTrans.add("I");
        }
        else{
            int replace = table[r-1][c-1] - 1;
            int min = replace;
            newR = r-1;
            newC = c-1;

            if (r >= 2 && c >= 2){
                if (table[r-2][c-2] - 1 < min && s0.charAt(r-2) == s1.charAt(c-1) && s0.charAt(r-1) == s1.charAt(c-2)){
                    min = table[r-2][c-2] - 1;
                    newR = r-2;
                    newC = c-2;
                }
            }
            int insert = table[r][c-1] - 1;
            if (insert < min){
                min = insert;
                newR = r;
                newC = c-1;
            }
            int delete = table[r-1][c] - 1;
            if (delete < min){
                min = delete;
                newC = c;
                newR = r-1;
            }
            if (newR == r-1 && newC == c-1){
                finalTrans.add("R");
            }
            if (newR == r-2){
                finalTrans.add("T");
            }
            if (newC == c-1 && newR == r){
                finalTrans.add("I");
            }
            if (newR == r-1 && newC == c){
                finalTrans.add("D");
            }
        }
        return transformationsList(s0, s1, table, finalTrans, newR, newC);
    }
    
    /**
     * Returns the edit distance between the two given strings: an int
     * representing the number of String manipulations (Insertions, Deletions,
     * Replacements, and Transpositions) minimally required to turn one into
     * the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return The minimal number of manipulations required to turn s0 into s1
     */
    public static int editDistance (String s0, String s1) {
        if (s0.equals(s1)) { return 0; }
        return getEditDistTable(s0, s1)[s0.length()][s1.length()];
    }
    
    /**
     * See {@link #getTransformationList(String s0, String s1, int[][] table)}.
     */
    public static List<String> getTransformationList (String s0, String s1) {
        return getTransformationList(s0, s1, getEditDistTable(s0, s1));
    }

}
