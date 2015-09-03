package org.cytoscape.adj.internal;

import static org.cytoscape.work.ServiceProperties.ID;

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.adj.writer.AdjWriterFactory;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.util.StreamUtil;

import org.osgi.framework.BundleContext;

/**
 * @author SrikanthB
 */ 

public class CyActivator extends AbstractCyActivator {
    public CyActivator() {
            super();
    }

    public void start(BundleContext bc) {
            final StreamUtil streamUtil = getService(bc, StreamUtil.class);
            final BasicCyFileFilter adjFilter = new BasicCyFileFilter(new String[] { "txt" },
                            new String[] { "application" }, "Adjacency matrix WITHOUT node names", DataCategory.NETWORK, streamUtil);
            final BasicCyFileFilter adjFilterWithNodes = new BasicCyFileFilter(new String[] { "txt" },
                            new String[] { "application" }, "Adjacency matrix WITH node names", DataCategory.NETWORK, streamUtil);
            
            boolean needNodes;
            needNodes=false;
            final AdjWriterFactory adjWriterFactory = new AdjWriterFactory(adjFilter, needNodes);
            final Properties adjWriterFactoryProperties = new Properties();
            adjWriterFactoryProperties.put(ID, "adjWriterFactory");
            registerAllServices(bc, adjWriterFactory, adjWriterFactoryProperties);
            
            needNodes = true;
            final AdjWriterFactory adjWriterFactoryWithNodes = new AdjWriterFactory(adjFilterWithNodes, needNodes);
            final Properties adjWriterFactoryPropertiesWithNodes = new Properties();
            adjWriterFactoryPropertiesWithNodes.put(ID, "adjWriterFactoryWithNodes");
            registerAllServices(bc, adjWriterFactoryWithNodes, adjWriterFactoryPropertiesWithNodes);
            
    }
}