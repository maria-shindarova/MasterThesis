
package diplomnadb;

/**
 *
 * @author Maria Shindarova
 */
public class Trie {
    private TrieNode root =new TrieNode();
    
    public void addWord(String word,int baseWordId){
        TrieNode tmp= root;
        
        if((word==null)||(word=="")){
            return;
        }
        for(int i =0;i<word.length();i++){
           tmp=tmp.addNext(getNumberOfChar(word.charAt(i)));
        }
        tmp.setBaseWordId(baseWordId);
        tmp.setEndOfWord();
        //tmp.s
    }
    public int containsWord(String word){
        TrieNode tmp= root;
        
        if((word==null)||(word=="")){
            return -1;
        }
        for(int i =0;i<word.length();i++){
           tmp=tmp.getNext(getNumberOfChar(word.charAt(i)));
        }
        if(tmp.getEndOfWord()){
            return tmp.getBaseWordId();
        }else{
            return -1;
        }
    }    
    private int getNumberOfChar(char currChar){
        int numChar = 0;
        if(((int)currChar>=1072)&&((int)currChar<=1103)){
            numChar = currChar-1072;
        }else if(((int)currChar>=1040)&&((int)currChar<=1071)){
            numChar = currChar-1040;
        }else{
            return -1;
        }
        if((numChar == 28)||(numChar == 30)){
            return -1;
        }else{
            return numChar;
        }
    }
}

class TrieNode{
    private TrieNode [] children = new TrieNode[32];
    private boolean endOfWord=false;
    private int baseWordId=-1;
    private int countAppiarance=0;
    
    public TrieNode addNext(int atPos){
        if(children[atPos]==null){
            children[atPos]=new TrieNode();
        }
        return children[atPos];
    }
    public TrieNode getNext(int atPos){
        if(children[atPos]==null){
            return null;
        }else{
            return children[atPos];
        }
    }    
    public TrieNode getChild(int atPos) {
        return children[atPos];
    }

    public void setChild(int atPos) {
        this.children[atPos] = new TrieNode();
    }
    public boolean isEndOfWord() {
        return endOfWord;
    }

    public void setEndOfWord() {
        this.endOfWord = true;
    }
    public boolean getEndOfWord() {
        return this.endOfWord;
    }
    public int getBaseWordId() {
        return baseWordId;
    }

    public void setBaseWordId(int baseWordId) {
        this.baseWordId = baseWordId;
    }

    public int getCountAppiarance() {
        return countAppiarance;
    }
    public void increaseCountAppiarance() {
         countAppiarance++;
    }
}