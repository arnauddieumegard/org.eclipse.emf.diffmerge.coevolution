/*********************************************************************
 * Copyright (c) 2014-2018 Thales Global Services S.A.S.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Thales Global Services S.A.S. - initial API and implementation
 **********************************************************************/
package org.eclipse.emf.diffmerge.bridge.util.structures;

import org.eclipse.emf.diffmerge.bridge.api.ISymbolFunction;
import org.eclipse.emf.diffmerge.bridge.api.ISymbolProvider;


/**
 * Fields for structs.
 * @param <T> the type of the possible values for the field
 * @author Olivier Constant
 */
public interface IField<T> extends ISymbolProvider {
  
  /**
   * @see org.eclipse.emf.diffmerge.bridge.api.ISymbolProvider#getSymbol(org.eclipse.emf.diffmerge.bridge.api.ISymbolFunction)
   */
  String getSymbol(ISymbolFunction function_p);
  
}