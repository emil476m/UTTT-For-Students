package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameState;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.*;

public class testBot implements IBot{
    private int xMod = 0;
    private int yMod = 0;

    private int winV0 = 0;
    private int winV1 = 0;
    private int winV2 = 0;
    private int winH0 = 0;
    private int winH1 = 0;
    private int winH2 = 0;
    private int winS0 = 0;
    private int winS1 = 0;

    private IMove move00 = null;
    private IMove move01 = null;
    private IMove move02 = null;
    private IMove move10 = null;
    private IMove move11 = null;
    private IMove move12 = null;
    private IMove move20 = null;
    private IMove move21 = null;
    private IMove move22 = null;
    final int moveTimeMs = 1000;
    private List<IMove> winWithMove = new ArrayList<>();
    String botName = "TestBot";

    private GameSim createSimulator(IGameState state) {
        GameSim simulator = new GameSim(new GameState());
        simulator.setGameOver(GameOverState.Active);
        simulator.setCurrentPlayer(state.getMoveNumber() % 2);
        simulator.getCurrentState().setRoundNumber(state.getRoundNumber());
        simulator.getCurrentState().setMoveNumber(state.getMoveNumber());
        simulator.getCurrentState().getField().setBoard(state.getField().getBoard());
        simulator.getCurrentState().getField().setMacroboard(state.getField().getMacroboard());
        return simulator;
    }

        @Override
    public IMove doMove(IGameState state) {


        return calculatebestMoves(state, moveTimeMs);

    }
    private IMove calculatebestMoves(IGameState state, int moveTimeMs) {
        //testNearWin(state); //checks for winning move
        long time = System.currentTimeMillis();
        Random rand = new Random();

        while (System.currentTimeMillis() < time + moveTimeMs) {
            GameSim sim = createSimulator(state);
            IGameState gs = sim.getCurrentState();
            List<IMove> moves = gs.getField().getAvailableMoves();
            IMove randomMovePlayer = moves.get(rand.nextInt(moves.size()));
            IMove bestMove = randomMovePlayer;
            while (sim.getGameOver() == GameOverState.Active) {
                sim.updateGame(randomMovePlayer);

                if (sim.getGameOver() == GameOverState.Active) {
                    moves = gs.getField().getAvailableMoves();
                    IMove randomMoveOpponent = moves.get(rand.nextInt(moves.size()));
                    sim.updateGame(randomMoveOpponent);
                }

                if (sim.getGameOver() == GameOverState.Active) {
                    moves = gs.getField().getAvailableMoves();
                    randomMovePlayer = moves.get(rand.nextInt(moves.size()));
                }
            }
            if (sim.getGameOver() == GameOverState.Win) {
                winWithMove.add(bestMove);
            }
        }

       return checkHowManyWins(winWithMove, state);
    }

    private IMove checkHowManyWins(List<IMove> winWithMove, IGameState state)
    {
        List<IMove> moves = winWithMove;
        HashMap<IMove,Integer> times = new HashMap<>();
        int max = 0;
        IMove move = null;
        for (IMove m: moves)
        {
            if(!times.containsKey(m))
            {
                times.put(m,1);
            }
            else
            {
                times.put(m,times.get(m)+1);
            }
        }
        moves.stream().distinct();
        for (IMove m:moves) {
            if(times.get(m) > max)
            {
                max = times.get(m);
                move = m;
            }
        }
        IMove bestMove = checkLegalListMove(state);
        moves.clear();
        if (bestMove != null){
            System.out.println(bestMove.toString() + " this is best move");
            return bestMove;
        }
        else {
            return move;
        }
    }

    private IMove checkLegalListMove(IGameState state){


        List<IMove> moves = state.getField().getAvailableMoves();
        List<IMove> blockOrWin = getWinMove(state);
        /*if (blockOrWin != null){
            System.out.println(blockOrWin + ""+'\n');
            return blockOrWin.get(0);
        }*/
        System.out.println(blockOrWin + ""+'\n');
        if (blockOrWin != null){

            for (IMove move:moves){
                for (IMove m2 :blockOrWin){
                    if (move.getX() == m2.getX() && move.getY() == m2.getY()){
                        return move;
                    }
                }
            }
        }
        return null;
    }
    @Override
    public String getBotName() {
        return botName;
    }

    public enum GameOverState {
        Active,
        Win,
        Tie
    }

    public class Move implements IMove {
        int x = 0;
        int y = 0;

        public Move(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Move move = (Move) o;
            return x == move.x && y == move.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    class GameSim {
        private final IGameState currentState;
        private int currentPlayer = 0; //player0 == 0 && player1 == 1
        private volatile GameOverState gameOver = GameOverState.Active;

        public void setGameOver(GameOverState state) {
            gameOver = state;
        }

        public GameOverState getGameOver() {
            return gameOver;
        }

        public void setCurrentPlayer(int player) {
            currentPlayer = player;
        }

        public IGameState getCurrentState() {
            return currentState;
        }

        public GameSim(IGameState currentState) {
            this.currentState = currentState;
        }

        public Boolean updateGame(IMove move) {
            if (!verifyMoveLegality(move))
                return false;

            updateBoard(move);
            currentPlayer = (currentPlayer + 1) % 2;

            return true;
        }

        private Boolean verifyMoveLegality(IMove move) {
            IField field = currentState.getField();
            boolean isValid = field.isInActiveMicroboard(move.getX(), move.getY());

            if (isValid && (move.getX() < 0 || 9 <= move.getX())) isValid = false;
            if (isValid && (move.getY() < 0 || 9 <= move.getY())) isValid = false;

            if (isValid && !field.getBoard()[move.getX()][move.getY()].equals(IField.EMPTY_FIELD))
                isValid = false;

            return isValid;
        }

        private void updateBoard(IMove move) {
            String[][] board = currentState.getField().getBoard();
            board[move.getX()][move.getY()] = currentPlayer + "";
            currentState.setMoveNumber(currentState.getMoveNumber() + 1);
            if (currentState.getMoveNumber() % 2 == 0) {
                currentState.setRoundNumber(currentState.getRoundNumber() + 1);
            }
            checkAndUpdateIfWin(move);
            updateMacroboard(move);

        }

        private void checkAndUpdateIfWin(IMove move) {
            String[][] macroBoard = currentState.getField().getMacroboard();
            int macroX = move.getX() / 3;
            int macroY = move.getY() / 3;

            if (macroBoard[macroX][macroY].equals(IField.EMPTY_FIELD) ||
                    macroBoard[macroX][macroY].equals(IField.AVAILABLE_FIELD)) {

                String[][] board = getCurrentState().getField().getBoard();

                if (isWin(board, move, "" + currentPlayer))
                    macroBoard[macroX][macroY] = currentPlayer + "";
                else if (isTie(board, move))
                    macroBoard[macroX][macroY] = "TIE";

                //Check macro win
                if (isWin(macroBoard, new Move(macroX, macroY), "" + currentPlayer))
                    gameOver = GameOverState.Win;
                else if (isTie(macroBoard, new Move(macroX, macroY)))
                    gameOver = GameOverState.Tie;
            }

        }

        private boolean isTie(String[][] board, IMove move) {
            int localX = move.getX() % 3;
            int localY = move.getY() % 3;
            int startX = move.getX() - (localX);
            int startY = move.getY() - (localY);

            for (int i = startX; i < startX + 3; i++) {
                for (int k = startY; k < startY + 3; k++) {
                    if (board[i][k].equals(IField.AVAILABLE_FIELD) ||
                            board[i][k].equals(IField.EMPTY_FIELD))
                        return false;
                }
            }
            return true;
        }


        public boolean isWin(String[][] board, IMove move, String currentPlayer) {
            int localX = move.getX() % 3;
            int localY = move.getY() % 3;
            int startX = move.getX() - (localX);
            int startY = move.getY() - (localY);

            //check col
            for (int i = startY; i < startY + 3; i++) {
                if (!board[move.getX()][i].equals(currentPlayer))
                    break;
                if (i == startY + 3 - 1) return true;
            }

            //check row
            for (int i = startX; i < startX + 3; i++) {
                if (!board[i][move.getY()].equals(currentPlayer))
                    break;
                if (i == startX + 3 - 1) return true;
            }

            //check diagonal
            if (localX == localY) {
                //we're on a diagonal
                int y = startY;
                for (int i = startX; i < startX + 3; i++) {
                    if (!board[i][y++].equals(currentPlayer))
                        break;
                    if (i == startX + 3 - 1) return true;
                }
            }

            //check anti diagonal
            if (localX + localY == 3 - 1) {
                int less = 0;
                for (int i = startX; i < startX + 3; i++) {
                    if (!board[i][(startY + 2) - less++].equals(currentPlayer))
                        break;
                    if (i == startX + 3 - 1) return true;
                }
            }
            return false;
        }

        private void updateMacroboard(IMove move) {
            String[][] macroBoard = currentState.getField().getMacroboard();
            for (int i = 0; i < macroBoard.length; i++)
                for (int k = 0; k < macroBoard[i].length; k++) {
                    if (macroBoard[i][k].equals(IField.AVAILABLE_FIELD))
                        macroBoard[i][k] = IField.EMPTY_FIELD;
                }

            int xTrans = move.getX() % 3;
            int yTrans = move.getY() % 3;

            if (macroBoard[xTrans][yTrans].equals(IField.EMPTY_FIELD))
                macroBoard[xTrans][yTrans] = IField.AVAILABLE_FIELD;
            else {
                // Field is already won, set all fields not won to avail.
                for (int i = 0; i < macroBoard.length; i++)
                    for (int k = 0; k < macroBoard[i].length; k++) {
                        if (macroBoard[i][k].equals(IField.EMPTY_FIELD))
                            macroBoard[i][k] = IField.AVAILABLE_FIELD;
                    }
            }
        }
    }

    private void getMod(List<IMove> moves){
        IMove move = moves.get(0);
        if (move.getX() <=2){
            xMod = 0;
            if(move.getY() <= 5 && move.getY()>2){
                    yMod = 3;
            }
            else if(move.getY() > 5){
                    yMod = 6;
            }
            else
                yMod = 0;
        }
        else if (move.getX() <=5 && move.getX() >2){
            xMod = 3;
            if(move.getY() <= 5 && move.getY()>2){
                yMod = 3;
            }
            else if(move.getY() > 5){
                yMod = 6;
            }
            else
                yMod = 0;
        }
        else{
            xMod = 6;
            if(move.getY() <= 5 && move.getY()>2){
                yMod = 3;
            }
            else if(move.getY() > 5){
                yMod = 6;
            }
            else
                yMod = 0;
        }
    }

    private void resetVar(){
        winV0 = 0;
        winV1 = 0;
        winV2 = 0;
        winH0 = 0;
        winH1 = 0;
        winH2 = 0;
        winS0 = 0;
        winS1 = 0;
    }

    private void setAllMoves(){
        move00 = new Move(0 + xMod,0 + yMod);
        move01 = new Move(0 + xMod,1 + yMod);
        move02 = new Move(0 + xMod,2 + yMod);
        move10 = new Move(1 + xMod,0 + yMod);
        move11 = new Move(1 + xMod,1 + yMod);
        move12 = new Move(1 + xMod,2 + yMod);
        move20 = new Move(2 + xMod,0 + yMod);
        move21 = new Move(2 + xMod,1 + yMod);
        move22 = new Move(2 + xMod,2 + yMod);
    }


    private boolean checkPlayerMoveSame(IGameState state, IMove move1, IMove move2){
        if (state.getField().getPlayerId(move1.getX(),move1.getY()).equals(state.getField().getPlayerId(move2.getX(),move2.getY())) ) {
            if (!state.getField().getPlayerId(move1.getX(), move1.getY()).equals(".") && !(state.getField().getPlayerId(move1.getX(), move1.getY()).equals("-1"))){
                return true;
            }
        }
        return false;
    }

    private List<IMove> getWinMove(IGameState state){
        List<IMove> winMoves = new ArrayList<>();
        List<IMove> moves =  state.getField().getAvailableMoves();
        getMod(moves);
        setAllMoves();


        if (checkPlayerMoveSame(state, move01, move02))
            winMoves.add(move00);
        System.out.println(state.getField().getPlayerId(0,0) + " cords");
        System.out.println(checkPlayerMoveSame(state, move01, move02) + " stuff");

        if (checkPlayerMoveSame(state, move00, move02))
            winMoves.add(move01);

        if (checkPlayerMoveSame(state, move00, move01))
            winMoves.add(move02);



        if (checkPlayerMoveSame(state, move11, move12))
            winMoves.add(move10);

        if (checkPlayerMoveSame(state, move12, move10))
            winMoves.add(move11);

        if (checkPlayerMoveSame(state, move11, move10))
            winMoves.add(move12);


        if (checkPlayerMoveSame(state, move21, move22))
            winMoves.add(move20);

        if (checkPlayerMoveSame(state, move20, move22))
            winMoves.add(move21);

        if (checkPlayerMoveSame(state, move21, move20))
            winMoves.add(move22);


        if (checkPlayerMoveSame(state, move10, move20))
            winMoves.add(move00);

        if (checkPlayerMoveSame(state, move00, move20))
            winMoves.add(move10);

        if (checkPlayerMoveSame(state, move10, move00))
            winMoves.add(move20);


        if (checkPlayerMoveSame(state, move11, move21))
            winMoves.add(move01);

        if (checkPlayerMoveSame(state, move01, move21))
            winMoves.add(move11);

        if (checkPlayerMoveSame(state, move11, move01))
            winMoves.add(move21);


        if (checkPlayerMoveSame(state, move12, move22))
            winMoves.add(move02);

        if (checkPlayerMoveSame(state, move22, move02))
            winMoves.add(move12);

        if (checkPlayerMoveSame(state, move12, move02))
            winMoves.add(move22);


        if (checkPlayerMoveSame(state, move11, move22))
            winMoves.add(move00);

        if (checkPlayerMoveSame(state, move00, move22))
            winMoves.add(move11);

        if (checkPlayerMoveSame(state, move11, move00))
            winMoves.add(move22);



        if (checkPlayerMoveSame(state, move11, move02))
            winMoves.add(move20);
        if (checkPlayerMoveSame(state, move02, move20))
            winMoves.add(move11);

        if (checkPlayerMoveSame(state, move11, move20))
            winMoves.add(move02);

        winMoves.stream().distinct();
        if (!winMoves.isEmpty())
            return winMoves;
        else
            return null;
    }
}