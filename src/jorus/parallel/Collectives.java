package jorus.parallel;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Collectives<T> {
	private static final Logger logger = LoggerFactory
			.getLogger(Collectives.class);

    protected final Broadcast<T> broadcast;
    protected final Scatter<T> scatter;
    protected final Gather<T> gather;
    protected final AllGather<T> allGather;
    protected final ReduceToRoot<T> reduceToRoot;
    protected final ReduceToAll<T> reduceToAll;
 
    public Collectives(Properties p, PxSystem s, Class<T> c) throws Exception { 
     
    	if(logger.isDebugEnabled()) {
    		logger.debug("Loading collectives for: " + c.getName());
    	}
        
        String type = p.getProperty("jorus.broadcast", "Flat");
        String name = "jorus.parallel.collectives." + type + "Broadcast";
        if(logger.isDebugEnabled()) {
        	logger.debug("     using " + type + " broadcast for " + c.getName());
        }
        broadcast = (Broadcast<T>) Collective.loadImplementation(name, s, c);
        
        type = p.getProperty("jorus.scatter", "Flat");
        name = "jorus.parallel.collectives." + type + "Scatter";
        if(logger.isDebugEnabled()) {
        	logger.debug("     using " + type + " scatter for " + c.getName());
        }
        scatter = (Scatter<T>) Collective.loadImplementation(name, s, c);
        
        type = p.getProperty("jorus.gather", "Flat");
        name = "jorus.parallel.collectives." + type + "Gather";
        if(logger.isDebugEnabled()) {
        	logger.debug("     using " + type + " gather for " + c.getName());
        }
        gather = (Gather<T>) Collective.loadImplementation(name, s, c);
        
        type = p.getProperty("jorus.allGather", "Ring");
        name = "jorus.parallel.collectives." + type + "AllGather";
        if(logger.isDebugEnabled()) {
        	logger.debug("     using " + type + " allGather for " + c.getName());
        }
        allGather = (AllGather<T>) Collective.loadImplementation(name, s, c);
        
        type = p.getProperty("jorus.reduceToRoot", "Flat");
        name = "jorus.parallel.collectives." + type + "ReduceToRoot";
        if(logger.isDebugEnabled()) {
        	logger.debug("     using " + type + " reduceToRoot for " + c.getName());
        }
        reduceToRoot = (ReduceToRoot<T>) Collective.loadImplementation(name, s, c);
       
        type = p.getProperty("jorus.reduceToAll", "MPICH");
        name = "jorus.parallel.collectives." + type + "ReduceToAll";
        if(logger.isDebugEnabled()) {
        	logger.debug("     using " + type + " reduceToAll for " + c.getName());
        }
        reduceToAll = (ReduceToAll<T>) Collective.loadImplementation(name, s, c);
    }
}
