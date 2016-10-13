/**
 * <copyright>
 * 
 * Copyright (c) 2016 Thales Global Services S.A.S.
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
package org.eclipse.emf.diffmerge.bridge.capella.integration.scopes;

import org.eclipse.emf.diffmerge.api.scopes.IEditableModelScope;
import org.eclipse.emf.diffmerge.bridge.incremental.Messages;

/**
 * A Capella intermediate model scope exposing its target data set
 */
public class CapellaIntermediateUpdateScope extends CapellaUpdateScope {

  /** The optional source data set */
  protected final Object _sourceDataSet;

  /** The optional target data set */
  protected final IEditableModelScope _targetDataSet;

  /**
   * Constructor
   * 
   * @param sourceDataSet_p a (non-null) source data set
   * @param targetDataSet_p a (non-null) target data set
   */
  public CapellaIntermediateUpdateScope(Object sourceDataSet_p,
      CapellaUpdateScope targetDataSet_p) {
    super(targetDataSet_p.getProject());
    _sourceDataSet = sourceDataSet_p;
    _targetDataSet = targetDataSet_p;
  }

  /**
   * @see org.eclipse.emf.diffmerge.impl.scopes.AbstractModelScope#getOriginator()
   */
  @Override
  public Object getOriginator() {
    return Messages.IntermediateModelScope_Originator;
  }

  /**
   * @return the target data set
   */
  @Override
  public IEditableModelScope getTargetDataSet() {
    return _targetDataSet;
  }
}