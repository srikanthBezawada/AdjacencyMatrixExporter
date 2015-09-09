package org.cytoscape.adj.internal;


import java.util.Properties;
import org.cytoscape.adj.reader.AdjFileFilter;
import org.cytoscape.adj.reader.AdjNetworkReaderFactory;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.adj.writer.AdjWriterFactory;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import static org.cytoscape.work.ServiceProperties.ID;

import org.osgi.framework.BundleContext;

/**
 * @author SrikanthB
 */ 

public class CyActivator extends AbstractCyActivator {
    private static CyAppAdapter appAdapter;
    public static final String FILE_EXTENSION = "adj";
    
    public CyActivator() {
            super();
    }

    public void start(BundleContext bc) {
            final StreamUtil streamUtil = getService(bc, StreamUtil.class);
            final BasicCyFileFilter adjFilter = new BasicCyFileFilter(new String[] { FILE_EXTENSION },
                            new String[] { "application" }, "Adjacency matrix WITHOUT node names", DataCategory.NETWORK, streamUtil);
            final BasicCyFileFilter adjFilterWithNodes = new BasicCyFileFilter(new String[] { FILE_EXTENSION },
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
            // TODO: Does it need to be changed to registerService()
            registerAllServices(bc, adjWriterFactoryWithNodes, adjWriterFactoryPropertiesWithNodes);
            
            // get services for readers
            this.appAdapter = getService(bc, CyAppAdapter.class);
            final CyApplicationManager appMgr = getService(bc, CyApplicationManager.class);
            final CyNetworkViewFactory cyNetworkViewFactory = getService(bc, CyNetworkViewFactory.class);
            final CyNetworkFactory cyNetworkFactory = getService(bc, CyNetworkFactory.class);
            final CyNetworkManager cyNetworkManager = getService(bc, CyNetworkManager.class);
            final CyNetworkViewManager cyNetworkViewManager = getService(bc, CyNetworkViewManager.class);
            final CyRootNetworkManager cyRootNetworkManager = getService(bc, CyRootNetworkManager.class);
            final CyTableManager cyTableManager = getService(bc, CyTableManager.class);
            final CyTableFactory cyTableFactory = getService(bc, CyTableFactory.class);
            final VisualMappingManager visMgr = getService(bc, VisualMappingManager.class);
            final CyEventHelper eventHelper = getService(bc, CyEventHelper.class);
            // register readers
            final CyFileFilter jgfReaderFilter = new AdjFileFilter(streamUtil);
            final AdjNetworkReaderFactory jgfReaderFactory = new AdjNetworkReaderFactory(
                jgfReaderFilter, appMgr, cyNetworkViewFactory, cyNetworkFactory,
                cyNetworkManager, cyNetworkViewManager, cyRootNetworkManager, cyTableFactory,
                cyTableManager, visMgr, eventHelper);
            final Properties jgfNetworkReaderFactoryProps = new Properties();
            jgfNetworkReaderFactoryProps.put(ID, "JGFNetworkReaderFactory");
            registerService(bc, jgfReaderFactory, InputStreamTaskFactory.class, jgfNetworkReaderFactoryProps);
            
    }
    
    public static CyAppAdapter getCyAppAdapter(){
        return appAdapter;
    }
}