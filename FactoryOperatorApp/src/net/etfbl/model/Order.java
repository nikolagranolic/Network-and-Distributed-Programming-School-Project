package net.etfbl.model;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "order")
@XmlAccessorType(XmlAccessType.FIELD)
public class Order {
    @XmlAttribute
    public  String emailAddress;
    @XmlElementWrapper(name = "items")
    @XmlElement(name = "product")
    public Map<Product,Integer> items = new HashMap<>();
    
    public Order(String emailAddress, HashMap<Product, Integer> items) {
        super();
        this.emailAddress = emailAddress;
        this.items = items;
    }
    
	@Override
	public String toString() {
	    return "Order [emailAddress=" + emailAddress + ", items=" + items + "]";
	}
}


