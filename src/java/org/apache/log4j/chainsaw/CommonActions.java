package org.apache.log4j.chainsaw;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.log4j.chainsaw.help.HelpManager;


/**
 * @author psmith
 *
 */
class CommonActions
{
    private static CommonActions instance = null;
    private static Action SHOW_RELEASE_NOTE; 
    
    
    private void initActions() {
        SHOW_RELEASE_NOTE = new AbstractAction("Release Notes") {

            public void actionPerformed(ActionEvent e)
            {
                HelpManager.getInstance().setHelpURL(ChainsawConstants.RELEASE_NOTES_URL);
                
            }};
    }
    
    static synchronized CommonActions getInstance() {
        if(instance==null) {
            instance = new CommonActions();
        }
        return instance;
    }
    
    /**
     * 
     */
    private CommonActions()
    {
        initActions();
    }

    /**
     * @return
     */
    public Action getShowReleaseNotes()
    {
        return SHOW_RELEASE_NOTE;
    }

}
