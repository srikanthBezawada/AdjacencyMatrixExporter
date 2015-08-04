package org.cytoscape.adj.serializer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.cytoscape.model.CyNetwork;

/**
 * @author SrikanthB
 */ 

// not using this class as of now
public class AdjNetworkSerializer implements Serializable{
    transient private CyNetwork cuurentNetwork;
    private void writeObject(ObjectOutputStream os) throws IOException{
        
        
        
    }
    
}
