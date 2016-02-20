/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author ricky
 */
public enum Action {

    BET,
    CALL,
    CALL_ALL_IN,
    CHECK,
    FOLD,
    RAISE,
    RAISE_ALL_IN,
    RAISE_INFERIOR_TO_LAST_RAISE;

    public boolean isBet() {
        return this.equals(BET);
    }

    public boolean isCall() {
        return this.equals(CALL);
    }

    public boolean isCallAllIn() {
        return this.equals(CALL_ALL_IN);
    }

    public boolean isCheck() {
        return this.equals(CHECK);
    }

    public boolean isFold() {
        return this.equals(FOLD);
    }

    public boolean isRaise() {
        return this.equals(RAISE);
    }

    public boolean isRaiseAllIn() {
        return this.equals(RAISE_ALL_IN);
    }

    public boolean isRaiseInferiorToLastRaise() {
        return this.equals(RAISE_INFERIOR_TO_LAST_RAISE);
    }
}