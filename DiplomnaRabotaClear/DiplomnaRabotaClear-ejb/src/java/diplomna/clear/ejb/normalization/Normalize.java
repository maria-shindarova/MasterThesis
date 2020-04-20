/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diplomna.clear.ejb.normalization;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *@author Maria Shindarova
 */
@Entity
@Table(name = "normalize", catalog = "diplomna", schema = "")
@XmlRootElement
//@NamedQueries({
//    @NamedQuery(name = "Normalize.findAll", query = "SELECT n FROM n diplomna.normalize")
//    , @NamedQuery(name = "Normalize.findByWord", query = "SELECT n FROM diplomna.normalize n WHERE n.word = :word")
//    , @NamedQuery(name = "Normalize.findByMeaning", query = "SELECT n FROM diplomna.normalize n WHERE n.meaning = :meaning")})
public class Normalize implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 236)
    @Column(name = "word")
    private String word;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 236)
    @Column(name = "meaning")
    private String meaning;

    public Normalize() {
    }

    public Normalize(String word) {
        this.word = word;
    }

    public Normalize(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (word != null ? word.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Normalize)) {
            return false;
        }
        Normalize other = (Normalize) object;
        if ((this.word == null && other.word != null) || (this.word != null && !this.word.equals(other.word))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "diplomna.clear.ejb.normalization.Normalize[ word=" + word + " ]";
    }
    
}
