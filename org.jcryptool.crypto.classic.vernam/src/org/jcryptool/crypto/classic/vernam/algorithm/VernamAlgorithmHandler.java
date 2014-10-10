//-----BEGIN DISCLAIMER-----
/*******************************************************************************
* Copyright (c) 2011, 2014 JCrypTool Team and Contributors
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
//-----END DISCLAIMER-----
package org.jcryptool.crypto.classic.vernam.algorithm;

import java.io.ByteArrayInputStream;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.jcryptool.core.logging.utils.LogUtil;
import org.jcryptool.core.operations.algorithm.AbstractAlgorithm;
import org.jcryptool.core.operations.algorithm.AbstractAlgorithmHandler;
import org.jcryptool.core.operations.algorithm.classic.AbstractClassicAlgorithm;
import org.jcryptool.core.operations.dataobject.IDataObject;
import org.jcryptool.core.operations.dataobject.classic.ClassicDataObject;
import org.jcryptool.crypto.classic.vernam.VernamPlugin;
import org.jcryptool.crypto.classic.vernam.ui.VernamWizard;
/**
 * The VernamAlgorithmHandler is a specific implementation of
 * AbstractAlgorithmHandler.
 *
 * @see org.jcryptool.core.operations.algorithm.AbstractAlgorithmHandler
 *
 * @author Michael Sommer (M1S)
 * @author Holger Friedrich (migration from Actions to Commands)
 * @version 0.0.2
 *
 */
public class VernamAlgorithmHandler extends AbstractAlgorithmHandler {
	/**
	 * Constructor
	 */
	public VernamAlgorithmHandler()
	{
		super();
	}

	public Object execute(ExecutionEvent event)
	{
		final VernamWizard wizard = new VernamWizard("Vernam");
		final AbstractClassicAlgorithm algorithm = new VernamAlgorithm();
		WizardDialog dialog = new WizardDialog(getActiveWorkbenchWindow().getShell(), wizard);
        dialog.setHelpAvailable(true);
        if( dialog.open() == Window.OK )
        {
        	Job job = new Job("ERSTER JOB")
        	{
				public IStatus run(IProgressMonitor monitor)
				{
					String jobTitle = "JOB TITLE";
					try
					{
						if (!(wizard.encrypt()))
						{
							jobTitle = "Nix";
						}

						monitor.beginTask(jobTitle, 4);

						if (monitor.isCanceled())
						{
	                           return Status.CANCEL_STATUS;
	                    }
						char[] key = wizard.getKey().toCharArray();

						monitor.worked(1);

						ByteArrayInputStream is = null;
						is = new ByteArrayInputStream( wizard.getMyT().getBytes() );
						if (wizard.encrypt()) {
							// explicit encrypt
							algorithm.init(AbstractAlgorithm.ENCRYPT_MODE,
									wizard.getMyT(),
									null,
									key,
									null
									);
						}
						if( wizard.decrypt() ) {
							// implicit decrypt
							algorithm.init(AbstractAlgorithm.DECRYPT_MODE,
									wizard.getMyT(),
									null,
									key,
									null
									);
						}


						monitor.worked(2);

						VernamAlgorithmHandler.super.finalizeRun( algorithm );
					}
					catch( final Exception ex )
					{
						LogUtil.logError( VernamPlugin.PLUGIN_ID, ex );
					}
					finally
					{

					}
					return Status.OK_STATUS;
				}

        	};
        	job.setUser(true);
            job.schedule();
        }
        return null;
	}

	@Override
	public void run(IDataObject dataobject)
	{
		AbstractClassicAlgorithm algorithm = new VernamAlgorithm();
		algorithm.init((ClassicDataObject) dataobject);
		super.finalizeRun(algorithm);
	}

}
