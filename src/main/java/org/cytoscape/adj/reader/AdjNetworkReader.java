/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cytoscape.adj.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.cytoscape.adj.reader.visual.ReaderView;
import org.cytoscape.adj.writer.AdjNetworkWriter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fshaik
 */
public class AdjNetworkReader extends AbstractCyNetworkReader  {
    
    private static final Logger logger = LoggerFactory.getLogger(AdjFileFilter.class);
    private static final String NODE_NAME_PREFIX = "Node ";
    private final InputStream inputStream;
    private final String inputName;
    private final CyApplicationManager appMgr;
    private final CyNetworkManager networkMgr;
    private final CyTableFactory tableFactory;
    private final CyTableManager tableMgr;
    private final VisualMappingManager visMgr;
    private final CyEventHelper eventHelper;
    private final CyNetworkViewManager networkViewMgr;
    private final CyNetworkFactory networkFactory;
    private final CyNetworkViewFactory networkViewFactory;

    public AdjNetworkReader(InputStream inputStream, String inputName,
            CyApplicationManager appMgr, CyNetworkViewFactory networkViewFactory,
            CyNetworkFactory networkFactory, CyNetworkManager networkMgr,
            CyNetworkViewManager networkViewMgr, CyRootNetworkManager rootNetworkMgr, 
            CyTableFactory tableFactory, CyTableManager tableMgr, 
            VisualMappingManager visMgr, CyEventHelper eventHelper) {
        super(inputStream, networkViewFactory, networkFactory, networkMgr, rootNetworkMgr);
        this.inputStream = inputStream;
        this.inputName = inputName;
        this.appMgr = appMgr;
        this.networkMgr = networkMgr;
        this.networkViewMgr = networkViewMgr;
        this.tableFactory = tableFactory;
        this.tableMgr = tableMgr;
        this.visMgr = visMgr;
        this.eventHelper = eventHelper;
        this.networkFactory = networkFactory;
        this.networkViewFactory = networkViewFactory;
    }

    @Override
    public void run(TaskMonitor tm) throws Exception {
        tm.setTitle("Adjacency Matrix import");
        tm.setStatusMessage("Adjacency Matrix import");
        
        CyNetwork n = readAdjFormat(inputStream, tm);
        this.networks = new CyNetwork[]{n};
        networkMgr.addNetwork(n);
//        GraphsWithValidation gv = checkSchema(inputStream, graphReader);
//        Graph[] graphs = gv.getGraphs();
//        tm.setProgress(0.50);
//
//        tm.setStatusMessage(format("Creating %d networks from \"%s\".", graphs.length, inputName));
//        this.networks = mapNetworks(graphs);
//        for (CyNetwork n : this.networks)
//            networkMgr.addNetwork(n);
//
//        // set up evidence table if we have cytoscape networks
//        if (this.networks.length > 0) {
//            CyTable table = getOrCreateEvidenceTable();
//            mapGraphsToEvidenceTable(graphs, table);
//            tableMgr.addTable(table);
//        }
//        m.setProgress(1.0);
    }

    @Override
    public CyNetworkView buildCyNetworkView(CyNetwork cn) {
        CyNetworkView networkView = this.networkViewFactory.createNetworkView(cn);
        networkViewMgr.addNetworkView(networkView);
        ReaderView.updateView(networkView, "grid");
        
        return networkView;
    }
    
    public CyNetwork readAdjFormat(InputStream inputStream, TaskMonitor tm){
        Scanner s = new Scanner(inputStream).useDelimiter(AdjNetworkWriter.DELIMETER);
        String startingString ;
        if(s.hasNext()){
            startingString = s.next();
            if(startingString.equals(AdjNetworkWriter.NODE_NAME_HEADER))
                return readNodeNameFormat(s, tm);
            else{
                return readMatrixFormat(s, tm, startingString);
            }
        }
        // TODO: throw exception saying no input in file
        return null;
    }
    
    public CyNetwork readNodeNameFormat(Scanner s, TaskMonitor tm){
        CyNetwork network;
        // Create a new network
        network = networkFactory.createNetwork();

        // Set name for network
        // TODO: is this necessary?
        network.getRow(network).set(CyNetwork.NAME, inputName);
        
        // Add nodes to the network
        List<CyNode> nodesInNewNetwork = new ArrayList<CyNode>();
        while(s.hasNext()){
            String strtemp = s.next();
            if( strtemp.startsWith(System.getProperty("line.separator")))
                break;
            CyNode temp = network.addNode();
            nodesInNewNetwork.add(temp);
            // set node name;
            network.getRow(temp).set(CyNetwork.NAME, strtemp);
        }
        
        // now add edges by iterating through the matrix
        for(int i=0 ; s.hasNext() ; i++){
            for(int k=0 ; k<i ; k++)
                s.next();
            for(int j=i ; ; j++){
                String strtemp = s.next();
                if( strtemp.startsWith(System.getProperty("line.separator")) )
                    break;
                if( Integer.parseInt(strtemp) == 1 ){
                    // create undirected edge!!
                    network.addEdge(nodesInNewNetwork.get(i), nodesInNewNetwork.get(j), false);
                }
            }
        }
        return network;
    }
    
    public CyNetwork readMatrixFormat(Scanner s, TaskMonitor tm, String startingString){
        CyNetwork network;
        // Create a new network
        network = networkFactory.createNetwork();

        // Set name for network
        // TODO: is this necessary?
        network.getRow(network).set(CyNetwork.NAME, inputName);
        
        // Add first node
        List<CyNode> nodesInNewNetwork = new ArrayList<CyNode>();
        CyNode firstNode = network.addNode();
        nodesInNewNetwork.add(firstNode);
        if( Integer.parseInt(startingString) == 1 )
            // create undirected edge!!
            network.addEdge(firstNode, firstNode, false);
        
        // read first line and add nodes & edges (first line only)
        for( int i=1 ; s.hasNext() ; i++ ){
            String strtemp = s.next();
            if( strtemp.startsWith(System.getProperty("line.separator")) )
                break;
            nodesInNewNetwork.add(network.addNode());
            if( Integer.parseInt(strtemp) == 1 )
                // create undirected edge!!
                network.addEdge(firstNode, nodesInNewNetwork.get(i), false);
        }
        // now add edges by iterating through rest of the matrix
        for(int i=1 ; s.hasNext() ; i++){
            for(int k=1 ; k<i ; k++)
                s.next();
            for(int j=i ; ; j++){
                String strtemp = s.next();
                if( strtemp.startsWith(System.getProperty("line.separator")) )
                    break;
                if( Integer.parseInt(strtemp) == 1 ){
                    // create undirected edge!!
                    network.addEdge(nodesInNewNetwork.get(i), nodesInNewNetwork.get(j), false);
                }
            }
        }
        
        // set node names;
        for(int i=1 ; i<=nodesInNewNetwork.size() ; i++){
            CyNode node = nodesInNewNetwork.get(i);
            network.getRow(node).set(CyNetwork.NAME, NODE_NAME_PREFIX+i);
        }
        
        return network;
    }
    
}
