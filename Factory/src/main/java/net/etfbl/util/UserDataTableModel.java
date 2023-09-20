package net.etfbl.util;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import net.etfbl.model.User;

@SuppressWarnings("serial")
public class UserDataTableModel extends AbstractTableModel {
	private ArrayList<User> users;
	
	public UserDataTableModel(ArrayList<User> users) {
		this.users = users;
	}
	
	@Override
	public int getRowCount() {
		return users.size();
	}

	@Override
    public String getColumnName(int column) {
        String name = "??";
        switch (column) {
            case 0:
                name = "Name";
                break;
            case 1:
                name = "Username";
                break;
            case 2:
                name = "Address";
                break;
            case 3:
            	name = "Phone";
            	break;
            case 4:
            	name = "Blocked";
            	break;
        }
        return name;
    }
	
	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		User user = users.get(rowIndex);
		Object value = null;
		switch (columnIndex) {
	        case 0:
	            value = user.getName();
	            break;
	        case 1:
	            value = user.getUsername();
	            break;
	        case 2:
	            value = user.getAddress();
	            break;
	        case 3:
	        	value = user.getPhone();
	        	break;
	        case 4:
	        	value = user.isBlocked();
		}
		return value;
	}
}
