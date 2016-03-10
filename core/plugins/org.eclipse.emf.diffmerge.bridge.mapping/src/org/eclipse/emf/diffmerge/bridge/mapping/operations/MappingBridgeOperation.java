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
package org.eclipse.emf.diffmerge.bridge.mapping.operations;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.diffmerge.bridge.api.IBridge;
import org.eclipse.emf.diffmerge.bridge.api.IBridgeExecution;
import org.eclipse.emf.diffmerge.bridge.mapping.api.IMappingBridge;
import org.eclipse.emf.diffmerge.bridge.mapping.api.IQuery;
import org.eclipse.emf.diffmerge.bridge.mapping.api.IQueryIdentifier;
import org.eclipse.emf.diffmerge.bridge.mapping.api.IRule;
import org.eclipse.emf.diffmerge.bridge.mapping.impl.MappingCause;
import org.eclipse.emf.diffmerge.bridge.mapping.impl.MappingExecution;
import org.eclipse.emf.diffmerge.bridge.mapping.impl.QueryExecution;
import org.eclipse.emf.diffmerge.bridge.mapping.impl.MappingExecution.PendingDefinition;
import org.eclipse.emf.diffmerge.bridge.operations.AbstractBridgeOperation;


/**
 * An operation that executes a mapping bridge between data scopes.
 * @author Olivier Constant
 */
public class MappingBridgeOperation extends AbstractBridgeOperation {
  
  /**
   * Constructor
   * @param sourceDataSet_p the non-null source data set
   * @param targetDataSet_p the non-null target data set
   * @param bridge_p the non-null bridge to execute
   * @param execution_p a non-null execution for the bridge
   */
  public MappingBridgeOperation(Object sourceDataSet_p, Object targetDataSet_p,
      IMappingBridge<?,?> bridge_p, IBridgeExecution execution_p) {
    super(sourceDataSet_p, targetDataSet_p, bridge_p, execution_p);
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.operations.AbstractBridgeOperation#getBridge()
   */
  @Override
  public IMappingBridge<?,?> getBridge() {
    return (IMappingBridge<?,?>)super.getBridge();
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.operations.AbstractBridgeOperation#getBridgeExecution()
   */
  @Override
  public MappingExecution getBridgeExecution() {
    return (MappingExecution)super.getBridgeExecution();
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.impl.helpers.AbstractExpensiveOperation#getWorkAmount()
   */
  @Override
  @SuppressWarnings("unchecked")
  protected int getWorkAmount() {
    return ((IBridge<Object, Object>)getBridge()).getWorkAmount(
        getSourceDataSet(), getTargetDataSet());
  }
  
  /**
   * Execute the given bridge based on the given execution and the given
   * source and target data sets
   * @param bridge_p a non-null object
   * @param execution_p a non-null object
   * @param sourceDataSet_p a non-null object
   * @param targetDataSet_p a non-null object
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void handleBridge(IMappingBridge<?,?> bridge_p, MappingExecution execution_p,
      Object sourceDataSet_p, Object targetDataSet_p) {
    // Root query execution definition
    QueryExecution rootQueryEnv = createQueryExecution();
    // First iteration: target creations
    handleQueriesForTargetCreationRec(bridge_p.getQueries(), bridge_p,
        sourceDataSet_p, targetDataSet_p, rootQueryEnv, execution_p);
    ((IMappingBridge)bridge_p).targetsCreated(targetDataSet_p);
    // Second iteration: target definitions
    handleTargetDefinitions(execution_p);
    ((IMappingBridge)bridge_p).targetsDefined(targetDataSet_p);
    execution_p.setStatus(Status.OK_STATUS);
  }

  /**
   * Create a new query execution
   * 
   * @return a new query execution
   */
	protected QueryExecution createQueryExecution() {
		QueryExecution rootQueryEnv = new QueryExecution();
		return rootQueryEnv;
	}
  
  /**
   * Execute the given queries for target creation based on the given query and rule
   * executions, recursively encompassing sub-queries, on the given source data
   * for the given target data sets
   * @param queries_p a non-null, potentially empty collection
   * @param source_p a non-null object
   * @param targetDataSet_p a non-null object
   * @param queryExecution_p a non-null object
   * @param execution_p a non-null object
   */
  protected void handleQueriesForTargetCreationRec(
      Collection<? extends IQuery<?,?>> queries_p, IBridge<?,?> bridge_p,
      Object source_p, Object targetDataSet_p, QueryExecution queryExecution_p,
      MappingExecution execution_p) {
    // Handling of each query
    for (IQuery<?,?> query : queries_p) {
      handleQueryForTargetCreationRec(
          query, bridge_p, source_p, targetDataSet_p, queryExecution_p, execution_p);
    }
  }
  
  /**
   * Execute the given query for target creation based on the given query and rule
   * executions, recursively encompassing sub-queries, on the given source data
   * for the given target data set
   * @param query_p a non-null object
   * @param source_p a non-null object
   * @param targetDataSet_p a non-null object
   * @param queryExecution_p a non-null object
   * @param execution_p a non-null object
   */
  protected void handleQueryForTargetCreationRec(
      IQuery<?,?> query_p, IBridge<?,?> bridge_p, Object source_p,
      Object targetDataSet_p, QueryExecution queryExecution_p, MappingExecution execution_p) {
    checkProgress();
    // Query execution
    @SuppressWarnings("unchecked")
    Iterator<?> queryResultIterator =
      ((IQuery<Object, Object>)query_p).evaluate(source_p, queryExecution_p);
    if (queryResultIterator.hasNext()) {
      @SuppressWarnings("unchecked")
      IQueryIdentifier<Object> queryID = (IQueryIdentifier<Object>)query_p.getID();
      while (queryResultIterator.hasNext()) {
        // Extension of query execution
        Object queryResult = queryResultIterator.next();
        QueryExecution newQueryEnv = queryExecution_p.newWith(queryID, queryResult);
        // Rule execution: target creation
        handleQueryForTargetCreation(
            query_p, bridge_p, queryResult, targetDataSet_p, newQueryEnv, execution_p);
        // Handling of sub-queries
        handleQueriesForTargetCreationRec(
            query_p.getQueries(), bridge_p, queryResult, targetDataSet_p, newQueryEnv, execution_p);
      }
    }
  }
  
  /**
   * Execute the given query for target creation based on the given query and rule
   * executions, on the given source data for the given target data set
   * @param query_p a non-null object
   * @param source_p a non-null object
   * @param targetDataSet_p a non-null object
   * @param queryExecution_p a non-null object
   * @param execution_p a non-null object
   */
  protected void handleQueryForTargetCreation(IQuery<?,?> query_p,
      IBridge<?,?> bridge_p, Object source_p, Object targetDataSet_p,
      QueryExecution queryExecution_p, MappingExecution execution_p) {
    // Execution of local rules
    for (IRule<?, ?> rule : query_p.getRules()) {
      handleRuleForTargetCreation(rule, bridge_p, source_p, targetDataSet_p, queryExecution_p, execution_p);
    }
    getMonitor().worked(1);
  }
  
  /**
   * Execute the given rule for target creation only, based on the given source data,
   * query execution and bridge execution, for the given target data set
   * @param rule_p a non-null object
   * @param bridge_p a non-null object
   * @param source_p a non-null object
   * @param targetDataSet_p a non-null object
   * @param queryExecution_p a non-null object
   * @param execution_p a non-null object
   */
  @SuppressWarnings("unchecked")
  protected void handleRuleForTargetCreation(IRule<?,?> rule_p, IBridge<?,?> bridge_p,
      Object source_p, Object targetDataSet_p, QueryExecution queryExecution_p,
      MappingExecution execution_p) {
    checkProgress();
    MappingCause<Object,Object> cause = new MappingCause<Object,Object>(
        queryExecution_p, source_p, (IRule<Object, Object>)rule_p);
    if (!execution_p.isTolerantToDuplicates() || !execution_p.isRegistered(cause)) {
      // Target creation
      Object target = ((IRule<Object, Object>)rule_p).createTarget(source_p, queryExecution_p);
      // Target addition to scope
      ((IMappingBridge<Object, Object>)bridge_p).addTarget(targetDataSet_p, target);
      // Target registration in bridge execution
      execution_p.put(cause, target);
    }
  }
  
  /**
   * Execute definitions for the pending target definitions of the given bridge execution
   * @param execution_p a non-null object
   */
  protected void handleTargetDefinitions(MappingExecution execution_p) {
    Set<Object> pendingSources = execution_p.getPendingSources();
    // Handle all pending rules
    for (Object source : pendingSources) {
      handleRuleForTargetDefinitions(source, execution_p);
    }
    // Handle progress for non-executed rules
    int nbPendingSources = pendingSources.size();
    int remainingProgress = Math.max(0, getBridge().getNbRules() - nbPendingSources);
    getMonitor().worked(remainingProgress);
  }
  
  /**
   * Execute the pending target definitions for the given source, based on the given
   * bridge execution
   * @param source_p a non-null object
   * @param execution_p a non-null object
   */
  protected void handleRuleForTargetDefinitions(Object source_p,
      MappingExecution execution_p) {
    Map<IRule<?,?>, PendingDefinition> pendingDefinitions =
        execution_p.getPendingDefinitions(source_p);
    // Handle all pending definitions
    for (Entry<IRule<?,?>, PendingDefinition> entry : pendingDefinitions.entrySet()) {
      handleRuleForTargetDefinition(
          entry.getKey(), source_p, entry.getValue(), execution_p);
    }
  }
  
  /**
   * Execute the given rule for target definition, based on the given query and rule
   * executions
   * @param rule_p a non-null object
   * @param source_p a non-null object
   * @param pendingDef_p a non-null object
   * @param execution_p a non-null object
   */
  @SuppressWarnings("unchecked")
  protected void handleRuleForTargetDefinition(IRule<?,?> rule_p,
      Object source_p, PendingDefinition pendingDef_p, MappingExecution execution_p) {
    checkProgress();
    ((IRule<Object,Object>)rule_p).defineTarget(
        source_p,
        pendingDef_p.getTarget(),
        pendingDef_p.getQueryEnvironment(),
        execution_p);
    getMonitor().worked(1);
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.util.IExpensiveOperation#run()
   */
  public IStatus run() {
    getMonitor().worked(1);
    IStatus result;
    try {
      handleBridge(getBridge(), getBridgeExecution(), getSourceDataSet(), getTargetDataSet());
      result = Status.OK_STATUS;
    } catch (OperationCanceledException e) {
      result = Status.CANCEL_STATUS;
    }
    return result;
  }
  
}