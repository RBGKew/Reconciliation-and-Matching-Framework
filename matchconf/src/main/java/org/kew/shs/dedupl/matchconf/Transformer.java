package org.kew.shs.dedupl.matchconf;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;


@RooJavaBean
@RooToString
@RooJpaActiveRecord()
public class Transformer extends Bot {
	
    @ManyToMany(cascade = CascadeType.ALL)
    @Sort(type=SortType.NATURAL)
    private List<Transformer> composedBy = new ArrayList<Transformer>();

}
