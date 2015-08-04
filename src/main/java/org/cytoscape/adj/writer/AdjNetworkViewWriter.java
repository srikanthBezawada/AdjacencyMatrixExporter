package org.cytoscape.adj.writer;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static org.cytoscape.adj.writer.AdjNetworkWriter.createAdjMatrix;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;

// TODO : update this class

/**
 * @author SrikanthB
 */ 

/*
public class AdjNetworkViewWriter extends AbstractNetworkViewTask implements CyWriter {
    private final OutputStream outputStream;
        private double[][] adjMat;
        
	public AdjNetworkViewWriter(final OutputStream outputStream, final CyNetworkView networkView) {
		this.outputStream = outputStream;	
                this.adjMat = createAdjMatrix(networkView.getModel());
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
    
}
*/