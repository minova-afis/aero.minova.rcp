package aero.minova.rcp.rcp.widgets;

public interface IMinovaCustomContentProposalListener2 {

	/*******************************************************************************
	 * Copyright (c) 2006, 2015 IBM Corporation and others. This program and the accompanying materials are made available under the terms of the Eclipse Public
	 * License 2.0 which accompanies this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/ SPDX-License-Identifier: EPL-2.0
	 * Contributors: IBM Corporation - initial API and implementation
	 *******************************************************************************/

	/**
	 * This interface is used to listen to additional notifications from a {@link ContentProposalAdapter}.
	 *
	 * @since 3.3
	 */
		/**
		 * A content proposal popup has been opened for content proposal assistance.
		 *
		 * @param adapter
		 *            the ContentProposalAdapter which is providing content proposal behavior to a control
		 */
		public void proposalPopupOpened(MinovaCustomContentProposalAdapter adapter);

		/**
		 * A content proposal popup has been closed.
		 *
		 * @param adapter
		 *            the ContentProposalAdapter which is providing content proposal behavior to a control
		 */
		public void proposalPopupClosed(MinovaCustomContentProposalAdapter adapter);
}
