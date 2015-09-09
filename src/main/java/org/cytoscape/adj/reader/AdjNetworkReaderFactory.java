/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cytoscape.adj.reader;

import java.io.InputStream;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;

/**
 *
 * @author smd.faizan@gmail.com
 */
public class AdjNetworkReaderFactory implements InputStreamTaskFactory  {

    private final CyFileFilter adjFileFilter;
    private final CyApplicationManager appMgr;
    private final CyNetworkFactory cyNetworkFactory;
    private final CyNetworkViewFactory cyNetworkViewFactory;
    private final CyNetworkManager cyNetworkManager;
    private final CyNetworkViewManager cyNetworkViewManager;
    private final CyRootNetworkManager cyRootNetworkManager;
    private final CyTableFactory cyTableFactory;
    private final CyTableManager cyTableManager;
    private final VisualMappingManager visMgr;
    private final CyEventHelper eventHelper;
    
    public AdjNetworkReaderFactory(final CyFileFilter adjFileFilter, final CyApplicationManager appMgr,
            final CyNetworkViewFactory cyNetworkViewFactory, final CyNetworkFactory cyNetworkFactory,
            final CyNetworkManager cyNetworkManager, final CyNetworkViewManager cyNetworkViewManager,
            final CyRootNetworkManager cyRootNetworkManager, final CyTableFactory cyTableFactory,
            final CyTableManager cyTableManager, final VisualMappingManager visMgr,
            final CyEventHelper eventHelper) {
        this.adjFileFilter = adjFileFilter;
        this.appMgr = appMgr;
        this.cyNetworkFactory = cyNetworkFactory;
        this.cyNetworkViewFactory = cyNetworkViewFactory;
        this.cyNetworkManager = cyNetworkManager;
        this.cyNetworkViewManager = cyNetworkViewManager;
        this.cyRootNetworkManager = cyRootNetworkManager;
        this.cyTableFactory = cyTableFactory;
        this.cyTableManager = cyTableManager;
        this.visMgr = visMgr;
        this.eventHelper = eventHelper;
    }
    
    @Override
    public TaskIterator createTaskIterator(InputStream in, String string) {
       return new TaskIterator(
                new AdjNetworkReader(in, string, appMgr, cyNetworkViewFactory,
                                     cyNetworkFactory, cyNetworkManager, cyNetworkViewManager,
                                     cyRootNetworkManager, cyTableFactory, cyTableManager,
                                     visMgr, eventHelper));
    }

    @Override
    public boolean isReady(InputStream in, String string) {
        return true;
    }

    @Override
    public CyFileFilter getFileFilter() {
        return adjFileFilter;
    }
    
}
