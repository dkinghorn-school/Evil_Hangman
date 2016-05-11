package cs240.byu.edu.evilhangman_android.StudentPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by devonkinghorn on 5/10/16.
 */
public class EvilHangmanGameController implements StudentEvilHangmanGameController{
  Set<String> dictionary;
  Set<Character> guesses;
  int numGuessesRemaining;
  int wordLength;
  GAME_STATUS status;
  StringBuilder word;
  public void startGame(InputStreamReader dictionaryFile, int length){
    word = new StringBuilder();
    for(int i = 0; i < length; i++){
      word.append('-');
    }
    BufferedReader br = new BufferedReader(dictionaryFile);
    dictionary = new TreeSet();
    guesses = new TreeSet();
    wordLength = length;
    status = GAME_STATUS.NORMAL;
    String line;
    try {
      while ((line = br.readLine()) != null) {
        String[] words = line.split(" ");
        for (String s : words) {
          if(s.length() == wordLength)
            dictionary.add(s);
        }
      }
    }catch(Exception e){

    }
  }
  private boolean rightMost(String first, String second){
    int string1Score = 0;
    int string2Score = 0;
    for(int i = wordLength-1; i >= 0; i--){
      if(first.charAt(i) != ' '){
        string1Score++;
      }
      if(second.charAt(i) != ' '){
        string2Score++;
      }
      if(string1Score != string2Score){
        if(string1Score > string2Score) {
          return true;
        } else {
          return false;
        }
      }
    }
    return true;
  }

  // returns true if first is better than second
  private boolean isBetter(String first, String second, Map<String, Set<String>> map){
    if(map.get(first).size() > map.get(second).size())
      return true;
    if(map.get(first).size() < map.get(second).size())
      return false;
    int string1Score = 0;
    int string2Score = 0;
    for(int i = 0; i < wordLength; i++){
      if(first.charAt(i) != ' '){
        string1Score++;
      }
      if(second.charAt(i) != ' '){
        string2Score++;
      }
    }
    if(string1Score < string2Score)
      return true;
    if(string1Score > string2Score)
      return false;

    return rightMost(first,second);
  }

  private Set<String> findBestSet(Map<String, Set<String>> map){
    String[] keys = map.keySet().toArray(new String[0]);
    if(keys.length == 0){
      return null;
    }
    String bestSetKey = keys[0];

    for(int i = 1; i < keys.length; i++){
      if(!isBetter(bestSetKey, keys[i],map)){
        bestSetKey = keys[i];
      }
    }
    for(int i = 0; i < wordLength; i++){
      if(bestSetKey.charAt(i) != ' '){
        word.deleteCharAt(i);
        word.insert(i,bestSetKey.charAt(i));
      }
    }
    return map.get(bestSetKey);
  }
  private boolean checkIfWon(){
    boolean toReturn = true;
    for(int i = 0; i < wordLength; i++){
      if(word.charAt(i) == '-')
        toReturn = false;
    }
    return toReturn;
  }
  public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException{
    if(!guesses.add((guess))){
      throw new GuessAlreadyMadeException();
    }
    numGuessesRemaining--;
    Map<String,Set<String>> categories = new HashMap();
    StringBuilder hash;
    for(String str : dictionary){
      hash = new StringBuilder();
      for(int i = 0; i < wordLength; i++){
        if(str.charAt(i) == guess){
          hash.append(guess);
        }else{
          hash.append(" ");
        }
      }
      if(!categories.containsKey(hash.toString())){
        categories.put(hash.toString(),new TreeSet<String>());
      }
      Set temp = categories.get(hash.toString());
      temp.add(str);
    }
    dictionary = findBestSet(categories);
    if(numGuessesRemaining == 0)
      status = GAME_STATUS.PLAYER_LOST;
    if(checkIfWon())
      status = GAME_STATUS.PLAYER_WON;
    return dictionary;
  }

  public GAME_STATUS getGameStatus(){
    return status;

  }
  public int getNumberOfGuessesLeft(){
    return numGuessesRemaining;
  }
  public String getCurrentWord(){
    return word.toString();
  }
  public Set<Character> getUsedLetters(){
    return guesses;
  }
  public void setNumberOfGuesses(int numberOfGuessesToStart){
    numGuessesRemaining = numberOfGuessesToStart;
  }

}
