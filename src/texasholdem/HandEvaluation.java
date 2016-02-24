/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author 1907riseyasenga
 */
public class HandEvaluation implements Comparable<HandEvaluation> {

    private final HandType handType;
    private final int value;
    
    public HandEvaluation(HandType handType, int value) {
        this.handType = handType;
        this.value = value;
    }
    
    public String getHandName() {
        return handType.getName();
    }
    
    public int getRank() {
        return handType.getRank();
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "Eval{" + "handType=" + handType + ", value=" + value + '}';
    }

    @Override
    public int compareTo(HandEvaluation right) {
        if(right == null){
            return 1;
        }
        
        if(getRank() == right.getRank()){
            return value - right.value;
        }
        
        return getRank() - right.getRank();
    }
}

