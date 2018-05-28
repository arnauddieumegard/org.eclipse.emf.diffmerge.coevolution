/**
 * <copyright>
 * 
 * Copyright (c) 2014-2018 Thales Global Services S.A.S.
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
package org.eclipse.emf.diffmerge.bridge.mapping;

import org.eclipse.osgi.util.NLS;


/**
 * Utility class for the externalization mechanism.
 * @author Olivier Constant
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {
  private static final String BUNDLE_NAME = "org.eclipse.emf.diffmerge.bridge.mapping.messages"; //$NON-NLS-1$
  public static String BridgeLogger_TargetCreationStepMessage;
  public static String BridgeLogger_TargetDefinitionStepMessage;
  public static String BridgeLogger_EmptyQueryExecutionError;
  public static String BridgeLogger_EmptyQueryResultError;
  public static String BridgeLogger_TargetCreationMessageSuffix;
  public static String BridgeLogger_TargetDefinitionMessageSuffix;
  
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
    // Nothing
  }
}
