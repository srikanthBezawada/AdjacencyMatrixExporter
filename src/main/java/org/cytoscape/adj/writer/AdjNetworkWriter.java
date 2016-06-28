package org.cytoscape.adj.writer;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @author SrikanthB
 */ 

public class AdjNetworkWriter extends AbstractNetworkTask implements CyWriter {

	private final OutputStream outputStream;
        private final CyNetwork network;
        private double[][] adjMat;
        private final boolean isDirected;
        private final boolean needNodes;
        public static final String DELIMETER = "\t";
        public static final String NODE_NAME_HEADER = "NName";
        
	public AdjNetworkWriter(final OutputStream outputStream, final CyNetwork network, final boolean isDirected, final boolean needNodes) {
		super(network);
		this.outputStream = outputStream;	
                this.network = network;
                this.isDirected = isDirected;
                this.needNodes = needNodes;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (taskMonitor != null) {
			taskMonitor.setTitle("Writing to Adjacency matrix format");
			taskMonitor.setStatusMessage("Writing network adjacency matrix structure...");
			taskMonitor.setProgress(-1.0);
		}
                this.adjMat = createAdjMatrix(this.network);
                
                if(needNodes){
                    OutputStreamWriter osWriter = new OutputStreamWriter(outputStream, EncodingUtil.getEncoder()); 
                    BufferedWriter bWriter = new BufferedWriter(osWriter);
                    List<CyNode> nodeList = network.getNodeList();

                    // Write the first line ( node names )
                    bWriter.write(NODE_NAME_HEADER);
                    bWriter.write(DELIMETER);
                    for(CyNode node : nodeList){
                        bWriter.write(network.getRow(node).get(CyNetwork.NAME, String.class));
                        bWriter.write(DELIMETER);
                    }
                    bWriter.newLine();

                    int i=0;
                    for(double[] row : adjMat){
                        bWriter.write(network.getRow(nodeList.get(i)).get(CyNetwork.NAME, String.class));
                        bWriter.write(DELIMETER);
                        for(double cell : row){
                            bWriter.write(String.valueOf(new Double(cell).intValue()));
                            bWriter.write(DELIMETER);
                        }
                        bWriter.newLine();   
                        i++;
                    }

                    bWriter.close();        
                    osWriter.close();
                    outputStream.close();
                } else{
                    OutputStreamWriter osWriter = new OutputStreamWriter(outputStream, EncodingUtil.getEncoder()); 
                    BufferedWriter bWriter = new BufferedWriter(osWriter);

                    for(double[] row : adjMat){
                        for(double cell : row){
                            bWriter.write(String.valueOf(new Double(cell).intValue()));
                            bWriter.write(DELIMETER);
                        }
                        bWriter.newLine();   
                    }

                    bWriter.close();        
                    osWriter.close();
                    outputStream.close();
                }
                

		if (taskMonitor != null) {
                        taskMonitor.setTitle("Successfully written to the file!");
			taskMonitor.setStatusMessage("Success.");
			taskMonitor.setProgress(1.0);
		}
	}
        
        public double[][] createAdjMatrix(CyNetwork network) {
            List<CyNode> nodeList = network.getNodeList();
            int totalnodecount = network.getNodeList().size();
            double[][] adjacencyMatrixOfNetwork = new double[totalnodecount][totalnodecount];
            List<CyEdge> edges = network.getEdgeList();
            
            for(CyEdge e : edges) {
                CyNode a = e.getSource();
                CyNode b = e.getTarget();
                if(isDirected){ // -1 convention for now
                    if(adjacencyMatrixOfNetwork[nodeList.indexOf(a)][nodeList.indexOf(b)] == 0 && adjacencyMatrixOfNetwork[nodeList.indexOf(b)][nodeList.indexOf(a)] == 0) {
                        adjacencyMatrixOfNetwork[nodeList.indexOf(a)][nodeList.indexOf(b)] = 1;
                        adjacencyMatrixOfNetwork[nodeList.indexOf(b)][nodeList.indexOf(a)] = -1;
                    } else {
                        adjacencyMatrixOfNetwork[nodeList.indexOf(a)][nodeList.indexOf(b)] = 1;
                        adjacencyMatrixOfNetwork[nodeList.indexOf(b)][nodeList.indexOf(a)] = 1;
                    }
                    
                } else{
                    adjacencyMatrixOfNetwork[nodeList.indexOf(a)][nodeList.indexOf(b)] = 1;
                    adjacencyMatrixOfNetwork[nodeList.indexOf(b)][nodeList.indexOf(a)] = 1;
                } 
                
            }
            return adjacencyMatrixOfNetwork;
        }
        
}