/*******************************************************************************
 * Copyright (c) 2011 Dominik Schadow - http://www.xml-sicherheit.de All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Dominik Schadow - initial API and implementation
 *               Holger Friedrich - support of Commands
 *******************************************************************************/
package org.jcryptool.crypto.xml.ui.commands;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.jcryptool.core.logging.utils.LogUtil;
import org.jcryptool.core.operations.algorithm.AbstractAlgorithmHandler;
import org.jcryptool.core.operations.dataobject.IDataObject;
import org.jcryptool.crypto.xml.core.verify.VerificationResult;
import org.jcryptool.crypto.xml.core.verify.VerifyDocument;
import org.jcryptool.crypto.xml.ui.XSTUIPlugin;
import org.jcryptool.crypto.xml.ui.verify.SignatureView;

/**
 * <p>
 * Command used to show the <b>XML Signatures</b> view of the XML Security Tools to verify all XML Signatures contained
 * in the selected XML document.
 * </p>
 *
 * @author Dominik Schadow
 * @author Holger Friedrich (support of Commands)
 * @version 0.5.1
 */
public class NewVerificationCommand extends AbstractAlgorithmHandler {
    /** The data to sign. */
    private InputStream data = null;

    private void createVerification() {
        VerifyDocument verify = new VerifyDocument();
        ArrayList<VerificationResult> results = new ArrayList<VerificationResult>();

        try {
            data = getActiveEditorInputStream();

            results = verify.verify(data);

            if (results.isEmpty()) {
                MessageDialog.openInformation(getActiveWorkbenchWindow().getShell(), Messages.NewVerificationCommand_0,
                        Messages.NewVerificationCommand_1);
            }

            // show results
            IViewPart vp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .showView(SignatureView.ID);

            if (vp instanceof SignatureView) {
                ((SignatureView) vp).setInput(results);
            }
        } catch (XMLSignatureException ex) {
            LogUtil.logError(XSTUIPlugin.getId(), Messages.NewVerificationCommand_2, ex, true);
        } catch (KeyResolverException ex) {
            LogUtil.logError(XSTUIPlugin.getId(), Messages.NewVerificationCommand_3, ex, true);
        } catch (Exception ex) {
            LogUtil.logError(XSTUIPlugin.getId(), Messages.NewVerificationCommand_4, ex, true);
        }
    }

    @Override
    public Object execute(ExecutionEvent event) {
        run(null);
        return(null);
    }

    @Override
    public void run(IDataObject dataobject) {
        createVerification();
    }
}
