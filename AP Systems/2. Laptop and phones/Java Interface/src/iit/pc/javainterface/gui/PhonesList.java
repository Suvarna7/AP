package iit.pc.javainterface.gui;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import iit.pc.javainterface.usb.USB_PCHost;

public class PhonesList {
	private static final String _PHONE_1 = "pink_phone";
	public static final String _ID_1 =  "044330fb0950bd90";
	private static final String _PHONE_2 = "red_phone";
	public static final String _ID_2 =  "044330fb0950bd90";
	private static final String _PHONE_3 = "yellow_phone";
	public static final String _ID_3 =  "06ac3cce13c86914";
	private static final String _PHONE_4 = "motorola_phone";
	public final static String _ID_4 =  "ZY224336GR";
	private static final String _OTHER = "other";

	private String[] modes = { _PHONE_1, _PHONE_2, _PHONE_3, _PHONE_4, _OTHER};
	private String[] ids = { _ID_1, _ID_2, _ID_3, _ID_4};

	private HashMap<String, String> phonesID;

	private USB_PCHost usbHost;
	private int connectionIndex;

	private JComboBox comboBox;
	JTextField new_phone;
	JTextField new_id;
	private JFrame nPhone;

	//ACTIONS
	private static final String _NEW_PHONE = "new_phone";
	private static final String _SELECT_PHONE = "select_phone";

	public PhonesList(USB_PCHost host, int connectionIndex){
		usbHost  = host;
		this.connectionIndex=connectionIndex;

		updateSelectList(modes);

		phonesID = new HashMap<String, String>();
		for (int i =0; i< ids.length; i ++ ){
			phonesID.put(modes[i], ids[i]);
		}
	}

	private void updateSelectList(String [] list){
		comboBox = new JComboBox(list);
		comboBox.setSelectedIndex(1);
		comboBox.setActionCommand(_SELECT_PHONE);
		comboBox.addActionListener(phonesListener);
	}

	ActionListener phonesListener  = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();

			if (actionCommand.equals(_SELECT_PHONE)){
				//When a phone is selected from the phone list
				String newMode = (String)comboBox.getSelectedItem();

				if (newMode.equals(_OTHER)) {
					//Open new frame to input new phone information
					openNewPhoneFrame();
				} else {
					//Set current phone id
					usbHost.setPhone(connectionIndex, phonesID.get(newMode));
				}
			}else if (actionCommand.equals(_NEW_PHONE)){
				//When OK is pressed in the new phone frame
				phonesID.put(new_phone.getText(), new_id.getText());
				updateSelectList(phonesID.keySet().toArray(new String [phonesID.keySet().size()]));
				usbHost.setPhone(connectionIndex, phonesID.get(new_phone.getText()));
				if (nPhone != null){
					nPhone.dispose();
					nPhone = null;
				}
			}

		}
	};
	
	/**
	 * When New phone is selected, open a frame to manually enter
	 * - phone name
	 * - phone id
	 */

	private void openNewPhoneFrame(){
		nPhone =  new JFrame("Input new phone info");
		JPanel panel = new JPanel(new GridLayout(2,1));

		JPanel input = new JPanel(new GridLayout(1,4));
		Label l = new Label("Phone name:");
		input.add(l);
		new_phone  = new JTextField("new_phone");
		input.add(new_phone);
		l = new Label("Phone id:");
		input.add(l);
		new_id  = new JTextField("nnnnnnnnnnnnnn");
		input.add(new_id);
		panel.add(input);

		JButton b = new JButton ("OK");
		b.setActionCommand(_NEW_PHONE);
		b.addActionListener(phonesListener);
		panel.add(b);

		nPhone.add(panel);
		nPhone.pack();
		nPhone.setVisible(true);
		//nPhone.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




	}

	public JComboBox getPhonesList(){
		return comboBox;
	}

}
