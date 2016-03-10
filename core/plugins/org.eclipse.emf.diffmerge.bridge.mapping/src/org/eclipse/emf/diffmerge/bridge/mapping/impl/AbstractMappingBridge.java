/**
 * <copyright>
 * 
 * Copyright (c) 2014 Thales Global Services S.A.S.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thales Global Services S.A.S. - initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.emf.diffmerge.bridge.mapping.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.diffmerge.bridge.api.IBridgeExecution;
import org.eclipse.emf.diffmerge.bridge.api.IBridgeTrace;
import org.eclipse.emf.diffmerge.bridge.impl.AbstractBridge;
import org.eclipse.emf.diffmerge.bridge.mapping.api.IMappingBridge;
import org.eclipse.emf.diffmerge.bridge.mapping.api.IMappingExecution;
import org.eclipse.emf.diffmerge.bridge.mapping.api.IQuery;
import org.eclipse.emf.diffmerge.bridge.mapping.api.IQueryHolder;
import org.eclipse.emf.diffmerge.bridge.mapping.operations.MappingBridgeOperation;


/**
 * A base implementation of IMappingBridge.
 * @param <SD> the type of the source data set
 * @param <TD> the type of the target data set
 * @see IMappingBridge
 * @author Olivier Constant
 */
public abstract class AbstractMappingBridge<SD, TD> extends AbstractBridge<SD, TD>
implements IMappingBridge<SD, TD> {
  
  /** The non-null ordered set of queries */
  private final Set<IQuery<? super SD, ?>> _rootQueries;
  
  
  /**
   * Constructor
   */
  public AbstractMappingBridge() {
    super();
    _rootQueries = new LinkedHashSet<IQuery<? super SD, ?>>();
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.mapping.api.IQueryHolder#accept(org.eclipse.emf.diffmerge.bridge.mapping.api.IQuery)
   */
  public void accept(IQuery<? super SD, ?> query_p) {
    _rootQueries.add(query_p);
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.api.IBridge#createExecution(org.eclipse.emf.diffmerge.bridge.api.IBridgeTrace.Editable)
   */
  public MappingExecution createExecution(IBridgeTrace.Editable trace_p) {
    return new MappingExecution(trace_p);
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.api.IBridge#executeOn(java.lang.Object, java.lang.Object, org.eclipse.emf.diffmerge.bridge.api.IBridgeExecution, org.eclipse.core.runtime.IProgressMonitor)
   */
  public IMappingExecution executeOn(SD sourceDataSet_p, TD targetDataSet_p,
      IBridgeExecution execution_p, IProgressMonitor monitor_p) {
    MappingExecution execution;
    if (execution_p instanceof MappingExecution)
      execution = (MappingExecution)execution_p;
    else
      execution = createExecution(null);
    MappingBridgeOperation operation = createMappingOperation(sourceDataSet_p, targetDataSet_p, execution);
    operation.run(monitor_p);
    return operation.getBridgeExecution();
  }
  
/**
 * Creates a new mapping bridge operation
 * 
 * @param sourceDataSet_p the source data set
 * @param targetDataSet_p the target data set
 * @param execution the mapping execution
 * @return a mapping bridge operation
 */
protected MappingBridgeOperation createMappingOperation(SD sourceDataSet_p, TD targetDataSet_p,
		MappingExecution execution) {
	MappingBridgeOperation operation =
        new MappingBridgeOperation(sourceDataSet_p, targetDataSet_p, this, execution);
	return operation;
}
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.mapping.api.IMappingBridge#getNbQueries()
   */
  public int getNbQueries() {
    return getNbQueriesRec(this);
  }
  
  /**
   * Return the total number of queries corresponding to the given holder,
   * i.e., the holder itself if a query plus all its sub-queries, recursively
   * @param holder_p a non-null query holder
   * @return a strictly positive int
   */
  private int getNbQueriesRec(IQueryHolder<?> holder_p) {
    int result = holder_p instanceof IQuery<?,?>? 1: 0;
    for (IQuery<?,?> subQuery: holder_p.getQueries()) {
      result += getNbQueriesRec(subQuery);
    }
    return result;
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.mapping.api.IMappingBridge#getNbRules()
   */
  public int getNbRules() {
    int result = 0;
    for (IQuery<?,?> query: getQueries()) {
      result += getNbRulesRec(query);
    }
    return result;
  }
  
  /**
   * Return the total number of rules corresponding to the given query,
   * i.e., on the query itself plus all its sub-queries, recursively
   * @param query_p a non-null query
   * @return a positive int
   */
  private int getNbRulesRec(IQuery<?,?> query_p) {
    int result = query_p.getRules().size();
    for (IQuery<?,?> subQuery: query_p.getQueries()) {
      result += getNbRulesRec(subQuery);
    }
    return result;
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.mapping.api.IQueryHolder#getQueries()
   */
  public Collection<? extends IQuery<? super SD, ?>> getQueries() {
    return Collections.unmodifiableCollection(_rootQueries);
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.api.IBridge#getWorkAmount(java.lang.Object, java.lang.Object)
   */
  public int getWorkAmount(SD sourceDataSet_p, TD targetDataSet_p) {
    return 1 + getNbQueries() + getNbRules();
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.mapping.api.IMappingBridge#targetsCreated(java.lang.Object)
   */
  public void targetsCreated(TD targetSet_p) {
    // Do nothing
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.mapping.api.IMappingBridge#targetsDefined(java.lang.Object)
   */
  public void targetsDefined(TD targetSet_p) {
    // Do nothing
  }
  
}
