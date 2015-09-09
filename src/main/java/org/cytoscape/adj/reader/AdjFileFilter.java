package org.cytoscape.adj.reader;

import java.io.InputStream;
import org.cytoscape.adj.internal.CyActivator;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smd.faizan@gmail.com
 */
public class AdjFileFilter extends BasicCyFileFilter {

    private static final Logger logger = LoggerFactory.getLogger(AdjFileFilter.class);
    
    public AdjFileFilter(StreamUtil streamUtil) {
        super(new String[] {CyActivator.FILE_EXTENSION},
              new String[] {"application/"+CyActivator.FILE_EXTENSION},
              "Adjacency Matrix Format",
              DataCategory.NETWORK, streamUtil);
    }
    
    @Override
    public boolean accepts(final InputStream stream, final DataCategory category) {
        // TODO: Can the stream be processed? introduce a check
        logger.debug("Importing using AdjReader");
        return true;
    }
    
}
