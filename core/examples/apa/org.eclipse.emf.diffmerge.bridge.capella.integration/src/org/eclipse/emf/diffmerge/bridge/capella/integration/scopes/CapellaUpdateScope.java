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

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.emf.diffmerge.api.scopes.IEditableModelScope;
import org.eclipse.emf.diffmerge.impl.scopes.RootedModelScope;
import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.core.data.capellamodeller.Project;

/**
 * A model scope rooted at the Capella project to be used in incremental bridges
 *
 */
public class CapellaUpdateScope extends RootedModelScope {
  
  /**
   * The label of the already persisted target scope as it will appear in the
   * diff/merge UI.
   */
  private static final String TARGET_ORIGINATOR_LABEL = "Resulting Model"; //$NON-NLS-1$
 
   /**
     * The Capella model root
     */
    private final Project _project;

    /**
     * The unique entry point to this scope
     * 
     * @param project_p the (non-null) Capella project
     */
    public CapellaUpdateScope(final Project project_p) {
      super(new ArrayList<EObject>(Arrays.asList(project_p)), true); 
      this._project = project_p;
    }

  /**
   * @see org.eclipse.emf.diffmerge.impl.scopes.RootedModelScope#add(org.eclipse.emf.ecore.EObject)
   */
  @Override
  public boolean add(final EObject element_p) {
    return super.add(element_p);
  }

  /**
   * @see org.eclipse.emf.diffmerge.impl.scopes.AbstractModelScope#getOriginator()
   */
  @Override
  public Object getOriginator() {
    return TARGET_ORIGINATOR_LABEL;
  }

  /**
   * Expose the root to outside.
   * 
   * @return the Capella model root
   */
  public Project getProject() {
    return _project;
  }    
  
  /**
   * @return the target data set
   */
  public IEditableModelScope getTargetDataSet() {
    return this;
  }
}