import java.util.*;
import net.sf.javabdd.*;

public class QueenLogic implements IQueensLogic{
    private int size;
    private int[][] board;
    public BDDFactory factory;
    private BDD rules;
    private BDD restricted;
    private Set<Integer> queenPos;

    public QueenLogic(){
        this.factory = JFactory.init(2000000, 200000);
        this.rules = factory.one();
        this.restricted = factory.one();
        this.queenPos = new HashSet<>();
    }

    public void initializeBoard(int size) {
        this.size = size;
        this.board = new int[size][size];
        createRules(size);
    }

    public int[][] getBoard() {
        return board;
    }

    private void createRules(int size) {
        int nVar = size * size;
        factory.setVarNum(nVar);
        for(int i = 0; i < nVar; i++) {
            rules.andWith(makeRuleBDD(i, excludedVars(i)));
        }
        rules.andWith(oneQueenRule());
    }

    private Iterable<Integer> excludedVars(int var) {
        List<Integer> result = horizontal(var);
        result.addAll(vertical(var));
        result.addAll(diagonal(var));
        return result;
    }

    public BDD makeRuleBDD(int i, Iterable<Integer> excludedVars) {
        BDD rule = factory.one();
        for (int excluded : excludedVars) {
            rule.andWith(factory.nithVar(excluded));
        }
        return factory.ithVar(i).imp(rule);
    }

    public BDD oneQueenRule() {
        BDD rule = factory.one();
        for (int row = 0; row < size; row++) {
            BDD innerRule = factory.zero();
            for (int col = 0; col < size; col++) {
                innerRule.orWith(factory.ithVar(row * size + col));
            }
            rule.andWith(innerRule);
        }
        return rule;
    }

    public List<Integer> vertical(int i) {
        List<Integer> result = new ArrayList<>();
        int col = i % size;
        for(int row = 0; row < size; row++) {
            int n = col + row * size;
            if(n == i) continue;
            result.add(n);
        }
        return result;
    }

    public List<Integer> horizontal(int i){
        List<Integer> temp = new ArrayList<>();
        for(int col = 0; col < size; col++) {
            int n = col + size * (i / size);
            if (n == i) continue;
            temp.add(n);
        }
        return temp;
    }

    public List<Integer> diagonal(int i){
        List<Integer> temp = new ArrayList<>();
        int[][] directions = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
        for (int[] vector : directions) {
            int row = i / size + vector[0];
            int col = i % size + vector[1];
            while (row >= 0 && row < size && col >= 0 && col < size){
                temp.add(row * size + col);
                row += vector[0];
                col += vector[1];
            }
        }
        return temp;
    }

    private void addQueenPos(int var) {
        queenPos.add(var);
        updateRestrictions();
    }

    private void updateRestrictions() {
        BDD temp = factory.one();
        for (int pos : queenPos) {
            temp.andWith(factory.ithVar(pos));
        }
        restricted = rules.restrict(temp);
    }

    public void insertQueen(int column, int row) {
        if (board[column][row] == -1) {
            board[column][row] = -1;
        } else if (board[column][row] == 1) {
            board[column][row] = 1;
        } else if((board[column][row] == 0)){
            board[column][row] = 1;
            addQueenPos(row * size + column);
        }

        for(int r = 0; r < size; r++) {
            for(int c = 0; c < size; c++) {
                if(board[c][r] == 1) continue;
                if(restricted.restrict(factory.ithVar(r * size + c)).isZero()) {
                    board[c][r] = -1;
                } else if (restricted.pathCount() == 1) {
                    board[c][r] = 1;
                    addQueenPos(r * size + c);
                } else {
                    board[c][r] = 0;
                }
            }
        }
    }
}
