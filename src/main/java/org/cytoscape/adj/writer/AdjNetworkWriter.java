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
        private double[][] adjMat;
	public AdjNetworkWriter(final OutputStream outputStream, final CyNetwork network) {
		super(network);
		this.outputStream = outputStream;	
                this.adjMat = createAdjMatrix(network);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (taskMonitor != null) {
			taskMonitor.setTitle("Writing to Adjacency matrix format");
			taskMonitor.setStatusMessage("Writing network adjacency matrix structure...");
			taskMonitor.setProgress(-1.0);
		}
                //AdjNetworkSerializer adjSerializer = new AdjNetworkSerializer();
                OutputStreamWriter osWriter = new OutputStreamWriter(outputStream, EncodingUtil.getEncoder()); 
                BufferedWriter bWriter = new BufferedWriter(osWriter);
                
                for(double[] row : adjMat){
                    for(double cell : row){
                        bWriter.write(new Double(cell).toString());
                        bWriter.write("\t");
                    }
                    bWriter.newLine();   
                }
               
                bWriter.close();        
                osWriter.close();
		outputStream.close();

		if (taskMonitor != null) {
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