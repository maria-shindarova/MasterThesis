/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomnadb;

/**
 *
 * @@author Maria Shindarova
 */
public class QueueDiplomnaOmograph {
    private NodeQueue first;
    private NodeQueue last;
    private int cnt=0;
    private final int MAX_NODES=6;
    
    public void addElement(String current){
        if(cnt==0){
            first = new NodeQueue(current);
            last=first;
            first.setNext(last);
            
        }else if(cnt<MAX_NODES){
            NodeQueue tmp;
            last.setNext(new NodeQueue(current));
            tmp=last;
            last=last.getNext();
            last.setPrev(tmp);
            
        }else{
            NodeQueue tmp;
            tmp = first;
            first=first.getNext();
            
            tmp.setValue(current);
            tmp.setPrev(last);
            last.setNext(tmp);
            last=tmp;
            tmp.setNext(last);
        }
        cnt++;
    }
    
    public String[] getAll(){
        String[] result= new String[cnt];
        NodeQueue tmp=first;
        
        for(int i =0;i<cnt;i++){
            result[i]=tmp.getValue();
            tmp=tmp.getNext();
        }
        
        return result;
    }
}
class NodeQueue{
    NodeQueue next=null;
    NodeQueue prev=null;
    String value="";
    
    public NodeQueue(String newVal){
        value = newVal;
    }
    public NodeQueue getPrev() {
        return prev;
    }

    public void setPrev(NodeQueue prev) {
        this.prev = prev;
    }

    public NodeQueue getNext() {
        return next;
    }

    public void setNext(NodeQueue next) {
        this.next = next;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}