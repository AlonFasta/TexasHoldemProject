import java.util.List;

public class Round implements PlayerActionService{


    public static enum GameMoves{
        FOLD("F"), BET("B"), CALL("C"), CHECK("K"), RAISE("R");

        private String value;
        GameMoves(String str){this.value = str;}
        public String toString(){return value;}
    }
    private int roundNum;
    private List<Player> playersRef;
    private boolean isBetOn;
    private int currBet;
    private int closeTheRound; //who is the player that closes the round
    private int smallIdx;
    private int bigIdx;
    private int roundCashBox;
    private int currPlayerIndex; //the curr turn
    private boolean isValidGameMove;

    Round(List<Player> playersRef, int roundNum, int roundCashBox){
        this.roundNum = roundNum;
        this.playersRef = playersRef;
        for(Player player : playersRef){
            player.initActionService(this);
        }
        this.roundCashBox = roundCashBox;
        isBetOn = false;
        currBet = 0;
        isValidGameMove = false;
    }

    @Override
    public int updateCashBox(int amount) {
        roundCashBox = roundCashBox + amount;
        return 0;
    }

    @Override
    public void updateCurrBet(int amount) {
        currBet = amount;
    }

    @Override
    public int getCurrBet() {
        return currBet;
    }

    private void findSmallBigIndex(){
        for (Player player:playersRef) {
            String state = player.getState();
            if(state == "S"){
                smallIdx = playersRef.indexOf(player);
            }
            if(state == "B"){
                bigIdx = playersRef.indexOf(player);
            }
        }
    }

    public int getCashBox(){
        return roundCashBox;
    }

    @Override
    public boolean isBetOn() {
        return isBetOn;
    }

    @Override
    public void turnBetOn() {
        this.isBetOn = true;
    }

    public void startRound(){
        this.findSmallBigIndex();
        //run over the players starting the first one in the curr round and get the game moves
        if(roundNum == 1){
            startBlindRound();
        }
        else{
            startRegularRound();
        }
    }

    public void setCurrPlayer(){
        currPlayerIndex = (bigIdx+1)%(playersRef.size());
    }
    public int getCurrPlayer(){
        return currPlayerIndex;
    }

    public void blindBet(){
        boolean result;
        do {result = playersRef.get(smallIdx).Bet(5);}while(!result);  //the game move is the blind small!
        do{ result = playersRef.get(bigIdx).Bet(10);}while (!result); //the game move is the blind big!
    }
    private void startBlindRound(){

        gameMove(playersRef.get(nextTurn(bigIdx)));
        closeTheRound = nextTurn(bigIdx); //the big closes the round and the one after him stops the loop
        int currPlayerIndex = nextTurn(closeTheRound);
        while(currPlayerIndex != closeTheRound){
            boolean needUpdateRoundClose = gameMove(playersRef.get(currPlayerIndex));
            if(needUpdateRoundClose){
                closeTheRound = currPlayerIndex;
            }
            currPlayerIndex = nextTurn(currPlayerIndex);
        }

    }

    private void startRegularRound(){
        boolean result;
        closeTheRound = smallIdx;
        result = gameMove(playersRef.get(smallIdx)); //small starts the round
        if(result){ }//raise an error and start again? need to make sure that not raice and not call here
        int currPlayerIndex = nextTurn(smallIdx);
        while(currPlayerIndex != closeTheRound){
            boolean needUpdateRoundClose = gameMove(playersRef.get(currPlayerIndex));
            if(needUpdateRoundClose){
                closeTheRound = currPlayerIndex;
            }
            currPlayerIndex = nextTurn(currPlayerIndex);
        }
    }
    private int nextTurn(int lastToPlayIndex){
            int theNextPlayerInArray = (lastToPlayIndex + 1)%(playersRef.size());
            while (playersRef.get(theNextPlayerInArray).isQuit()){
                theNextPlayerInArray = (theNextPlayerInArray + 1)%(playersRef.size());
            }
        return  theNextPlayerInArray;
    }

    public boolean gameMove(Round.GameMoves gameMove, int amount){
        switch (gameMove){
            case RAISE:
                if(playersRef.get(currPlayerIndex).Raise(amount)){
                    isValidGameMove = true;
                }
                closeTheRound = currPlayerIndex;

            case CALL:
                if(playersRef.get(currPlayerIndex).call()){
                    isValidGameMove = true;
                }

            case BET:
                if(playersRef.get(currPlayerIndex).Bet(amount)){
                    isValidGameMove = true;
                }
                closeTheRound = currPlayerIndex;

            case CHECK:
                if(playersRef.get(currPlayerIndex).check()){
                    isValidGameMove = true;
                }

            case FOLD:
                playersRef.get(currPlayerIndex).fold();

            default:
                //error

        }

        currPlayerIndex = nextTurn(currPlayerIndex);
        if(currPlayerIndex == closeTheRound)
            return true;
        else return false;
    }

    public boolean isValidGameMove() {
        return isValidGameMove;
    }

}
