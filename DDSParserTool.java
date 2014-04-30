package ddsparsetool;


import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class DDSParserTool extends JFrame {

	// The check boxes for a regular obs report [0] or an ops report plus the RMS data [1]
	//
    private JCheckBox reportCheckBoxes[] = { new JCheckBox(FileAndDirectoryManager.reportNames[0],true),
    		                                 new JCheckBox(FileAndDirectoryManager.reportNames[1]) };

    // Provide a boolean map of the state of the check boxes
    //
    private boolean[] isCheckBoxChecked = new boolean[reportCheckBoxes.length];

    // This is the text field used for specifying the output reports file names
    //
    JTextField filename = new JTextField("DDSReport",20);

    // Button group for the check boxes
    //
    ButtonGroup reportButtonGroup = new ButtonGroup();

    // Input File descriptor
    //
    File theFile = null;

	private static final long serialVersionUID = 1L;

	//*********************************************************

	public DDSParserTool(String title) {

		super(title);

	} // end constructor DDSParserTool

	//*********************************************************

	private void displayFileChooser() {

		// Display File Dialog, so the user can choose a file or directory to process
		//
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setDialogTitle("Daily Data Summary Files");

		fileChooser.setFileFilter(new DDSFilter());

		fileChooser.setFileSelectionMode(
				JFileChooser.FILES_AND_DIRECTORIES);

		int result = fileChooser.showOpenDialog(null);

		// If User clocked Cancel button on dialog, return
		if (result==JFileChooser.CANCEL_OPTION) {
			System.exit(0); // is Canceled
		} // end if

		// Get the selected file or directory
		//
		theFile = fileChooser.getSelectedFile(); // get the selected file

		// Display this window
		//
		setVisible(true);

		// Set the first time flag so the titles can be
		//   outputted to each of the report files
		//
		FileAndDirectoryManager.setIsFirstTime(true);

	}  // end method displayFileChooser

	//*********************************************************

	public Container createContentPane() {

		//Create the content-pane-to-be.
		//
		JPanel contentPane = new JPanel(new FlowLayout());
		contentPane.setOpaque(true);

		// Create the check box panel for all the check boxes
		//
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel,BoxLayout.Y_AXIS));

		// The first two check boxes need to be "radio" button - only one is valid as checked
		//
		reportButtonGroup.add(reportCheckBoxes[0]);
		reportButtonGroup.add(reportCheckBoxes[1]);

		// Add the check boxes to the panel
		//
		checkBoxPanel.add(reportCheckBoxes[0]);
		checkBoxPanel.add(reportCheckBoxes[1]);

		// Put a border on the panel
		//
		checkBoxPanel.setBorder(new TitledBorder(LineBorder.createBlackLineBorder(),"Obs Reports"));

		// Now add it to the content panel
		//
		contentPane.add(checkBoxPanel);

		// Create a panel for the output file name
		//
		JPanel filenamePanel = new JPanel(new FlowLayout());

		// Put a border on the panel
		// Set the filename to editable
		// Add a default output file name to the text area
		//
		filenamePanel.setBorder(new TitledBorder(LineBorder.createBlackLineBorder(),"Output Name"));
		filename.isEditable();
		filenamePanel.add(filename);

		// add the filename panel to the content panel
		//
		contentPane.add(filenamePanel);

		// Create the accept and cancel buttons
		//
		JButton accept = new JButton("Accept");
		JButton cancel = new JButton("Cancel");

		// Add the listeners for the buttons
		//
		accept.addActionListener(new AcceptButtonListener());
		cancel.addActionListener(new CancelButtonListener());

	    // Create a panel for the buttons
		// Add the buttons to the panel
		// Add the button panel to the content panel
		JPanel acceptAndCancelPanel = new JPanel();

		acceptAndCancelPanel.add(accept);
		acceptAndCancelPanel.add(cancel);

		contentPane.add(acceptAndCancelPanel);

		return contentPane;

	} // end method createContentPane

	//*********************************************************

	protected static void createAndShowGUI() {

		// Create and set up the content pane.
		DDSParserTool ddsFrame   = new DDSParserTool("DDS Parser Tool");

		ddsFrame.addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Setup the other window attributes
		//
		ddsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		ddsFrame.setContentPane(ddsFrame.createContentPane());
		ddsFrame.displayFileChooser();

		//Size and position the window
		//
		ddsFrame.setSize(275,225);
		ddsFrame.setLocation(300,200);

	} // end method createAndShowGUI

	//*********************************************************

	public static void main(String[] args) {

		// Start the tool
		// Schedule a job for the event-dispatching thread:
		//   creating and showing this Waveform Injector GUI.
		//
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	} // end method main

	//************************************************************************

	private class AcceptButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			// When the accept button is pressed on this display

			// Get the selected checkbox information
			//
			for (int i = 0; i < reportCheckBoxes.length; i++) {

				isCheckBoxChecked[i] = reportCheckBoxes[i].isSelected();
			}

			String filenameText = filename.getText();
			String[] tokens = filenameText.split("[^a-zA-Z0-9]");

            // Verify operator input for the filename
			//
			try {

				if ((Character.isDigit(filenameText.charAt(0))) || (tokens.length > 1)) {

					JOptionPane.showMessageDialog( // Indicate the an error occurred during filename processing
							null,
							"Filename must be AlphaNumeric characters only. No spaces or leading numerics",   // memory has filled up
							"User Input Error",
							JOptionPane.ERROR_MESSAGE);
				}

			} catch (StringIndexOutOfBoundsException exception) { // No filename entered

				JOptionPane.showMessageDialog( // Indicate the an error occurred during filename processing
						null,
						"No Filename was entered",
						"User Input Error",
						JOptionPane.ERROR_MESSAGE);
			}

			// Now start processing
			if (theFile.isDirectory()) {

				// Process a Directory and all it's sub-directories
				//
				FileAndDirectoryManager.processDirectory(theFile,
						                                 filenameText,
						                                 isCheckBoxChecked);

			} else {

				// Process a single file
				//
				FileAndDirectoryManager.processFile(theFile,
						                            filenameText,
						                            isCheckBoxChecked);
			}

			// Indicate that parsing has completed
			//
			JOptionPane.showMessageDialog( // Indicate the an error occurred during filename processing
					null,
					"Parsing has completed",   // memory has filled up
					"Parsing Completed",
					JOptionPane.INFORMATION_MESSAGE);

			// No longer display this window
			//   Put the Filechooser back up for operator interaction
			//
			setVisible(false);
			displayFileChooser();

		} // end method ActionPerformed

	} // end private class AcceptButtonListener

	//************************************************************************

	private class CancelButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			// Turn all the Menu Items back on for user interaction
			// Hide this display
			//
			System.exit(0);

		} // end method ActionPerformed

	} // end private class CancelButtonListener

} // end class DDSParserTool
