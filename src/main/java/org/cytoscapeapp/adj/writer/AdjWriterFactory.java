package org.cytoscape.adj.writer;

import java.io.OutputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;

/**
 * @author SrikanthB
 */ 

public class AdjWriterFactory implements CyNetworkViewWriterFactory {
	
	private final CyFileFilter filter;

	public AdjWriterFactory(final CyFileFilter filter) {
		this.filter = filter;
	}

	@Override
	public CyWriter createWriter(OutputStream outputStream, CyNetwork network) {
		return new AdjNetworkWriter(outputStream, network);
	}

	@Override
	public CyFileFilter getFileFilter() {
		return filter;
	}

        @Override
        public CyWriter createWriter(OutputStream out, CyNetworkView cnv) {
                //return new AdjNetworkViewWriter(out, cnv);
                // TODO  : update this
                return null;
        }
}