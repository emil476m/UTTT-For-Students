package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import javax.swing.plaf.nimbus.State;
import java.util.List;
import java.util.Random;

public class TestBOTEmil implements IBot{
    private static final String BOTNAME="Based Bot";
    private Random rand = new Random();

    @Override
    public IMove doMove(IGameState state) {
        //state.getField().getMacroboard().
        List<IMove> moves = state.getField().getAvailableMoves();
        IMove moveToTie = testNearWin(state);
        //System.out.println("The not random Move " + moveToTie);
        if (moveToTie != null) {
            //System.out.println("Not Random");
            return moveToTie;
        }
        else {
            if (moves.size() > 0) {
                //System.out.println("Random Move");
                return moves.get(rand.nextInt(moves.size())); /* get random move from available moves */
            }
        }
        return null;
    }


    private IMove testNearWin(IGameState state){
        List<IMove> moves =  state.getField().getAvailableMoves();
        //System.out.println("All available Moves: " + moves.toString());

        int winV0 = 0;
        int winV1 = 0;
        int winV2 = 0;
        int winH0 = 0;
        int winH1 = 0;
        int winH2 = 0;
        int winS0 = 0;
        int winS1 = 0;

        int xMod = 0;
        int yMod = 0;

        IMove move00 = null;
        IMove move01 = null;
        IMove move02 = null;
        IMove move10 = null;
        IMove move11 = null;
        IMove move12 = null;
        IMove move20 = null;
        IMove move21 = null;
        IMove move22 = null;

        for (IMove move: moves){
            if (move.getX() <=2){
                if(move.getY() <= 5 && move.getY()>2){
                    yMod = 3;
                }
                else {
                    yMod = 6;
                }
            }
            else if (move.getX() <=5 && move.getX() >2){
                xMod = 3;
                if(move.getY() <= 5 && move.getY()>2){
                    yMod = 3;
                }
                else {
                    yMod = 6;
                }
            }
            else{
                xMod = 6;
                if(move.getY() <= 5 && move.getY()>2){
                    yMod = 3;
                }
                else {
                    yMod = 6;
                }
            }
        }




        for (IMove move: moves){
            if (move.getX() == 0+xMod && move.getY() == 0+yMod){
                winV0++;
                winH0++;
                winS0++;
                move00 = move;
            }
            else if (move.getX() == 0+xMod  && move.getY() == 1+yMod){
                winV0++;
                winH1++;
                move01 = move;
            }
            else if (move.getX() == 0+xMod  && move.getY() == 2+yMod){
                winV0++;
                winH2++;
                winS1++;
                move02 = move;
            }
            else if (move.getX() == 1+xMod  && move.getY() == 0+yMod){
                winV1++;
                winH0++;
                move10 = move;
            }
            else if (move.getX() == 1+xMod  && move.getY() == 1+yMod){
                winV1++;
                winH1++;
                winS0++;
                winS1++;
                move11 = move;
            }
            else if (move.getX() == 1+xMod  && move.getY() == 2+yMod){
                winV1++;
                winH2++;
                move12 = move;
            }
            else if (move.getX() == 2+xMod  && move.getY() == 0+yMod){
                winV2++;
                winH0++;
                winS1++;
                move20 = move;
            }
            else if (move.getX() == 2+xMod  && move.getY() == 1+yMod){
                winV2++;
                winH1++;
                move21 = move;
            }
            else if (move.getX() == 2+xMod  && move.getY() == 2+yMod){
                winV2++;
                winH2++;
                winS0++;
                move22 = move;
            }
        }

        if(winV0 == 1){
            if (move00 != null)
                return  move00;
            if (move01 != null)
                return  move01;
            if (move02 != null)
                return  move02;
        }
        if(winV1 == 1){
            if (move10 != null)
                return  move10;
            if (move11 != null)
                return  move11;
            if (move12 != null)
                return  move12;
        }
        if(winV2 == 1){
            if (move20 != null)
                return  move20;
            if (move21 != null)
                return  move21;
            if (move22 != null)
                return  move22;
        }
        if(winH0 == 1){
            if (move00 != null)
                return  move00;
            if (move10 != null)
                return  move10;
            if (move20 != null)
                return  move20;
        }
        if(winH1 == 1){
            if (move01 != null)
                return  move01;
            if (move11 != null)
                return  move11;
            if (move21 != null)
                return  move21;
        }
        if(winH2 == 1){
            if (move02 != null)
                return  move02;
            if (move12 != null)
                return  move12;
            if (move22 != null)
                return  move22;
        }
        if(winS0 == 1){
            if (move00 != null)
                return  move00;
            if (move11 != null)
                return  move11;
            if (move22 != null)
                return  move22;
        }
        if(winS1 == 1){
            if (move20 != null)
                return  move20;
            if (move11 != null)
                return  move11;
            if (move02 != null)
                return  move02;
        }


        //System.out.println("winV0: " + winV0 + " winV1 " +winV1 + " winV2 " + winV2 + " winH0 " + winH0 + " winH1 " +
                //winH1 + " winH2 " + winH2 + " winS0 " + winS0 + " winS1 "+ winS1);
        return null;
    }


    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
