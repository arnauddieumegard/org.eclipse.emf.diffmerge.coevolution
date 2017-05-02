/**
 * <copyright>
 * 
 * Copyright (c) 2014-2016 Thales Global Services S.A.S.
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
package org.eclipse.emf.diffmerge.bridge.impl.emf;

import org.eclipse.emf.diffmerge.bridge.impl.BaseSymbolFunction;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;


/**
 * An implementation of ISymbolFunction for EMF.
 * @author Olivier Constant
 */
public class EMFSymbolFunction extends BaseSymbolFunction {
  
  /** The singleton instance */
  private static final EMFSymbolFunction INSTANCE = new EMFSymbolFunction();
  
  
  /**
   * Constructor
   */
  protected EMFSymbolFunction() {
    // Stateless
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.impl.BaseSymbolFunction#getAtomSymbol(java.lang.Object)
   */
  @Override
  public Object getAtomSymbol(Object object_p) {
    Object result;
    if (object_p instanceof EObject)
      // Assumes that no EClass implements Iterable 
      result = getEObjectSymbol((EObject)object_p);
    else
      result = super.getAtomSymbol(object_p);
    return result;
  }
  
  /** 
   * Return a symbol for the given element, if possible
   * @param element_p a non-null element
   * @return a potentially null object
   */
  protected String getEObjectSymbol(EObject element_p) {
    String result = EcoreUtil.getID(element_p);
    if (result == null && element_p.eResource() != null)
      result = element_p.eResource().getURIFragment(element_p);
    if (result == null)
      // Valid (if based on object address) for temporary, non-persistent scopes only
      result = Integer.toString(System.identityHashCode(element_p));
    return result;
  }
  
  /**
   * Return the singleton instance of this class
   * @return a non-null object
   */
  public static EMFSymbolFunction getInstance() {
    return INSTANCE;
  }
  
}