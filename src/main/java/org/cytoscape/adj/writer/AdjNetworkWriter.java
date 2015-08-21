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
        private static final String DELIMETER = "\t";
        
	public AdjNetworkWriter(final OutputStream outputStream, final CyNetwork network) {
		super(network);
		this.outputStream = outputStream;	
                this.network = network;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (taskMonitor != null) {
			taskMonitor.setTitle("Writing to Adjacency matrix format");
			taskMonitor.setStatusMessage("Writing network adjacency matrix structure...");
			taskMonitor.setProgress(-1.0);
		}
                // create adjacency matrix from the network
                this.adjMat = createAdjMatrix(this.network);
                //AdjNetworkSerializer adjSerializer = new AdjNetworkSerializer();
                OutputStreamWriter osWriter = new OutputStreamWriter(outputStream, EncodingUtil.getEncoder()); 
                BufferedWriter bWriter = new BufferedWriter(osWriter);
                List<CyNode> nodeList = network.getNodeList();
                
                // Write the first line ( node names )
                bWriter.write("NName");
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

		if (taskMonitor != null) {
                        taskMonitor.setTitle("Successfully written to the file!");
			taskMonitor.setStatusMessage("Success.");
			taskMonitor.setProgress(1.0);
		}
	}
        
        public static double[][] createAdjMatrix(CyNetwork network) {
            //make an adjacencymatrix for the current network
            int totalnodecount = network.getNodeList().size();
            List<CyNode> nodeList = network.getNodeList();
            CyTable edgeTable = network.getDefaultEdgeTable();
            double[][] adjacencyMatrixOfNetwork = new double[totalnodecount][totalnodecount];
            CyRow row;
            int k = 0;
            for (CyNode root : nodeList) {
                List<CyNode> neighbors = network.getNeighborList(root, CyEdge.Type.ANY);
                for (CyNode neighbor : neighbors) {
                    List<CyEdge> edges = network.getConnectingEdgeList(root, neighbor, CyEdge.Type.ANY);
                    if (edges.size() > 0) {
                        row = edgeTable.getRow(edges.get(0).getSUID());
                        try {
                            adjacencyMatrixOfNetwork[k][nodeList.indexOf(neighbor)] = 1;
                        } catch (Exception ex) {
                        }
                    }
                }
                k++;
            }
            return adjacencyMatrixOfNetwork;
        }
}